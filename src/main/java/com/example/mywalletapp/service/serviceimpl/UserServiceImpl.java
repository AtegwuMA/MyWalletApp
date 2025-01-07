package com.example.mywalletapp.service.serviceimpl;


import com.example.mywalletapp.dto.requestdto.*;
import com.example.mywalletapp.dto.responsedto.*;
import com.example.mywalletapp.exception.CustomException;
import com.example.mywalletapp.exception.RoleNotFoundException;
import com.example.mywalletapp.factory.RoleFactory;
import com.example.mywalletapp.model.Role;
import com.example.mywalletapp.model.User;
import com.example.mywalletapp.model.Wallet;
import com.example.mywalletapp.repository.UserRepository;
import com.example.mywalletapp.repository.WalletRepository;
import com.example.mywalletapp.security.UserDetailsImpl;
import com.example.mywalletapp.security.UserDetailsServiceImpl;
import com.example.mywalletapp.security.jwt.JwtUtils;
import com.example.mywalletapp.service.EmailService;
import com.example.mywalletapp.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final int MAX_LOGIN_ATTEMPTS = 3; // Maximum allowed failed attempts
    private static final Duration LOCK_DURATION = Duration.ofHours(1); // Lock duration
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper userDTOMapper;
    private final EmailService emailService;
    private final RoleFactory roleFactory;
    private final AdminDTOMapper adminDTOMapper;
    private final JwtUtils jwtUtil;
    private final Validator validator;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public UserServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           WalletRepository walletRepository,
                           PasswordEncoder passwordEncoder,
                           UserDTOMapper userDTOMapper,
                           EmailService emailService,
                           RoleFactory roleFactory,
                           AdminDTOMapper adminDTOMapper,
                           JwtUtils jwtUtil,
                           Validator validator, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDTOMapper = userDTOMapper;
        this.emailService = emailService;
        this.roleFactory = roleFactory;
        this.adminDTOMapper = adminDTOMapper;
        this.jwtUtil = jwtUtil;
        this.validator = validator;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    public GenericResponse userSignUp(SignUpRequestDto signUpRequest) {
        SignUpAdminRequestDto newUserSignUp = new SignUpAdminRequestDto();
        // Use BeanUtils to copy properties from SignUpRequestDto to User
        BeanUtils.copyProperties(signUpRequest, newUserSignUp);
//        logger.info(passwordEncoder.encode("adminPass1"));
        newUserSignUp.setRoles(null);
        return signUp(newUserSignUp);
    }

    @Override
    public GenericResponse adminSignUp(SignUpAdminRequestDto signUpRequestDto) {
        // Ensure to assign ROLE_ADMIN here or handle it in the service
        signUpRequestDto.setRoles(Set.of("admin")); // Set the role specifically for admin creation
        return signUp(signUpRequestDto);
    }

    public GenericResponse signUp(SignUpAdminRequestDto signUpRequest) {

        LocalDateTime now = LocalDateTime.now();
        logger.info("Take to: {}", signUpRequest);


        // Validate fields using the validator
        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(signUpRequest);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<SignUpRequestDto> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException(sb.toString().trim());
        }

        // Check if the email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new GenericResponse(1, "Email is already in use",
                    HttpStatus.BAD_REQUEST);
        }

        // Create and save a new user
        try {

            User newUser = createUser(signUpRequest);

            if (newUser.getRoles().contains(roleFactory.getInstance("admin"))){

                User adminUser = userRepository.save(newUser);
                UserSignUpResponse response = adminDTOMapper.apply(adminUser);
                return new GenericResponse(0, "Admin registered successfully",
                            HttpStatus.CREATED, response
                );
            }

            String token = generateVerificationToken(newUser, now);
            userRepository.save(newUser);
            emailService.sendVerificationEmail(newUser.getEmail(), token);
            return new GenericResponse(0,
                    "Registration successful! Please check your email to verify.",
                            HttpStatus.OK
            );

        } catch (RoleNotFoundException e) {
            return new GenericResponse(1, "Role not found",
                            HttpStatus.BAD_REQUEST
            );
        }
    }

    private User createUser(SignUpAdminRequestDto signUpRequestDto)
        throws RoleNotFoundException {
        return User.builder()
                .firstName(signUpRequestDto.getFirstName())
                .lastName(signUpRequestDto.getLastName())
                .middleName(signUpRequestDto.getMiddleName())
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .dateOfBirth(signUpRequestDto.getDateOfBirth())
                .address(signUpRequestDto.getAddress())
                .bvn(signUpRequestDto.getBvn())
                .enabled(true)
                .roles(determineRoles(signUpRequestDto.getRoles())) // Default to ROLE_USER
                .walletTiers(signUpRequestDto.getWalletTier())
                .build();
    }

    private Set<Role> determineRoles(Set<String> strRoles)
            throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(roleFactory.getInstance("user"));
        } else {
            for (String role : strRoles) {
                roles.add(roleFactory.getInstance(role));
            }
        }
        return roles;
    }


    @Override
    @Transactional
    public GenericResponse verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user == null) {
            logger.warn("Invalid verification token: {}", token);
            throw new CustomException("Invalid or expired verification token.");
        }

        if (user.getTokenCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new CustomException("Verification token has expired.");
        }

        user.setVerified(true);
        clearVerificationToken(user);

        return createWalletForVerifiedUser(user);
    }


    @Override
    public GenericResponse resendVerificationEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            logger.warn("Resend request for non-existent email: {}", email);
            throw new CustomException("Email not found.");
        }

        User user = optionalUser.get();
        if (user.isVerified()) {
            logger.warn("Resend request for already verified email: {}", email);
            throw new CustomException("Email is already verified.");
        }

        String token = generateVerificationToken(user, LocalDateTime.now());
        userRepository.save(user);
        emailService.sendVerificationEmail(email, token);
        logger.info("Resend verification email sent to: {}", email);
        return new GenericResponse(0,
                "Verification email resent. Please check your email.",
                HttpStatus.OK
        );
    }


    private String generateVerificationToken(User user, LocalDateTime now) {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenCreatedAt(now);
        return token;
    }

    private void clearVerificationToken(User user) {
        user.setVerificationToken(null);
        user.setTokenCreatedAt(null);
    }


    private GenericResponse createWalletForVerifiedUser(User verifiedUser){
        Wallet wallet = new Wallet();
        wallet.setTier(verifiedUser.getWalletTiers());
        wallet.setBalance(0.0);
        wallet.setUser(verifiedUser);
        walletRepository.save(wallet);

        List<Wallet> wallets = new ArrayList<>();
        wallets.add(wallet);
        verifiedUser.setWallets(wallets);

        User user = userRepository.save(verifiedUser); // Save user to the database

        UserSignUpResponse response = userDTOMapper.apply(user);
        logger.info("Email verified successfully for: {}", user.getEmail());
        return new GenericResponse(0, "Email verified successfully!",
                HttpStatus.CREATED, response
        );
    }


    @Override
    public GenericResponse login(LoginRequestDto loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));

        if (user.isLocked()) {
            return new GenericResponse(2,
                    "Account is locked due to too many failed attempts",
                             HttpStatus.UNAUTHORIZED, null);
        }

        try {

            Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                                loginRequest.getPassword()));

            if (authentication != null) {
                logger.info("Take-to: {}", authentication.getName());
                logger.info("Take-to1: {}", authentication.getAuthorities());
            }

            user.setLoginAttempts(0);
            userRepository.save(user);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            assert authentication != null;


            String accessToken = jwtUtil.generateJwtToken(authentication); // Generate access token
            String refreshToken = jwtUtil.generateRefreshToken(authentication); // Generate refresh token

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            UserSignInResponse response = UserSignInResponse.builder()
                    .email(userDetails.getEmail())
                    .id(userDetails.getId())
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .roles(roles)
                    .build();

        // Return response with token
            return new GenericResponse(0, "Login successful",
                                             HttpStatus.OK, response
            );
        }catch (Exception e) {
              return   lockUser(user, e);

        }
    }

    @Override
    public GenericResponse forgotPassword(PasswordRecoveryRequest request) {
        // Validate the user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        // Generate a token and send the email
        String token = UUID.randomUUID().toString(); // Simple UUID as token
        user.setResetToken(token);
        userRepository.save(user); // Save the token in your user entity

        emailService.sendPasswordResetEmail(user.getEmail(), token);
        return new GenericResponse(0, "Password reset email has been sent, check your mail.",
                HttpStatus.OK
        );
    }

    @Transactional
    @Override
    public GenericResponse resetPassword(String token, ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        // Set the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword())); // Don't forget to hash the password
        user.setResetToken(null); // Clear the reset token after usage
        userRepository.save(user);

        return new GenericResponse(0, "Password reset successful", HttpStatus.OK);
    }


    public GenericResponse lockUser(User user, Exception e) {
        // Increment login attempts
        incrementLoginAttempts(user);
        if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
            user.setLocked(true);
            user.setLockTimestamp(LocalDateTime.now()); // Set the current time as the lock timestamp
            userRepository.save(user);
            logger.warn("Account locked for user: {}", user.getEmail());
            return new GenericResponse(2, "Your account is locked due to 3 failed attempts",
                    HttpStatus.UNAUTHORIZED, e.getMessage()
            );
        }
        logger.error("Authentication failed: {}", e.getMessage(), e);
        return new GenericResponse(2, "Invalid email or password",
                HttpStatus.UNAUTHORIZED, e.getMessage()
        );
    }

    private void incrementLoginAttempts(User user) {
        user.setLoginAttempts(user.getLoginAttempts() + 1);
    }

    @Scheduled(fixedRate = 3600000) // For example, every hour
    public void unlockAccounts() {
        List<User> lockedUsers = userRepository.findByIsLocked(true);
        LocalDateTime now = LocalDateTime.now(); // Get the current time
        for (User user : lockedUsers) {
            if (user.getLockTimestamp() != null && now.isAfter(user.getLockTimestamp().plus(LOCK_DURATION))) {
                user.setLocked(false);
                user.setLoginAttempts(0); // Reset attempts
                user.setLockTimestamp(null);
                userRepository.save(user);
                logger.info("Account unlocked for user: {}", user.getEmail());
            }
        }
    }



    @Override
    public GenericResponse refreshAccessToken(RefreshTokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();

        // Validate the refresh token and generate a new access token
        if (refreshToken == null || !jwtUtil.validateJwtToken(refreshToken)) {
            return new GenericResponse(2, "Invalid refresh token",
                                                    HttpStatus.UNAUTHORIZED
            );
        }

        String email = jwtUtil.getUserNameFromJwtToken(refreshToken);
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);

        // Generate a new access token based on the user's details
        String newAccessToken = jwtUtil.generateJwtToken((Authentication) userDetails);

        return new GenericResponse(0, "Token refreshed",
                                                    HttpStatus.OK, newAccessToken
        );
    }

}



