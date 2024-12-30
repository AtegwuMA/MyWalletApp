package com.example.mywalletapp.service.serviceimpl;


import com.example.mywalletapp.dto.requestdto.LoginRequestDto;
import com.example.mywalletapp.dto.requestdto.SignUpRequestDto;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import com.example.mywalletapp.dto.responsedto.UserDTOMapper;
import com.example.mywalletapp.dto.responsedto.UserSignInResponse;
import com.example.mywalletapp.dto.responsedto.UserSignUpResponse;
import com.example.mywalletapp.exception.CustomException;
import com.example.mywalletapp.exception.RoleNotFoundException;
import com.example.mywalletapp.factory.RoleFactory;
import com.example.mywalletapp.model.Role;
import com.example.mywalletapp.model.User;
import com.example.mywalletapp.model.Wallet;
import com.example.mywalletapp.model.WalletTier;
import com.example.mywalletapp.repository.UserRepository;
import com.example.mywalletapp.repository.WalletRepository;
import com.example.mywalletapp.security.UserDetailsImpl;
import com.example.mywalletapp.security.jwt.JwtUtils;
import com.example.mywalletapp.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper userDTOMapper;
    private final RoleFactory roleFactory;

    private final JwtUtils jwtUtil;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           WalletRepository walletRepository,
                           PasswordEncoder passwordEncoder,
                           UserDTOMapper userDTOMapper,
                           RoleFactory roleFactory,
                           JwtUtils jwtUtil
    ) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDTOMapper = userDTOMapper;
        this.roleFactory = roleFactory;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

    }

    public GenericResponse signUp(SignUpRequestDto signUpRequest) {

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
            return new GenericResponse(01, "Email is already in use",
                                            HttpStatus.BAD_REQUEST);
        }
        if (!signUpRequest.getWalletTier().equals("BASIC") &&
                        !signUpRequest.getWalletTier().equals("SILVER") &&
                        !signUpRequest.getWalletTier().equals("GOLD")
        ) {
            throw new CustomException("Invalid tier name: " + signUpRequest.getWalletTier() +
                    ". Allowed values are: BASIC, SILVER, GOLD.");
        }

        // Create and save a new user
        try {

            List<Wallet> wallets = new ArrayList<>();

            User newUser = createUser(signUpRequest);
            User savedUser = userRepository.save(newUser);

            Wallet newWallet = createWalletForUser(savedUser, signUpRequest.getWalletTier());
            walletRepository.save(newWallet);

            wallets.add(newWallet);
            newUser.setWallets(wallets);

            User user = userRepository.save(newUser); // Save user to the database

            UserSignUpResponse response = userDTOMapper.apply(user);
            return new GenericResponse(00, "User registered successfully",
                                             HttpStatus.CREATED, response);

        } catch (RoleNotFoundException e) {
            return new GenericResponse(01, "Role not found",
                                            HttpStatus.BAD_REQUEST);
        }

    }

    private Wallet createWalletForUser(User newUser, String walletTier) {
        Wallet wallet = new Wallet();
        wallet.setTier(walletTier);
        wallet.setBalance(0.0);
        wallet.setUser(newUser); // Set the user for the wallet if needed
        return wallet;
    }

    private User createUser(SignUpRequestDto signUpRequestDto) throws RoleNotFoundException {
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
                .roles(determineRoles(signUpRequestDto.getRoles()))
                .build();
    }

    private Set<Role> determineRoles(Set<String> strRoles) throws RoleNotFoundException {
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

    public GenericResponse login(LoginRequestDto loginRequest) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                                loginRequest.getPassword()));
            logger.warn("take1");

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);
            logger.warn("take2");

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

logger.warn("take here3");
            UserSignInResponse response = UserSignInResponse.builder()
                    .email(userDetails.getEmail())
                    .id(userDetails.getId())
                    .token(jwt)
                    .type("Bearer")
                    .roles(roles)
                    .build();
logger.warn("take to4");
            // Return response with token
            return new GenericResponse(00, "Login successful",
                                             HttpStatus.OK, response);
        }catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage(), e);
            return new GenericResponse(02, "Invalid email or password",
                                             HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
