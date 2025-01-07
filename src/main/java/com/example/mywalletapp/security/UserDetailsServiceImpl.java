package com.example.mywalletapp.security;



import com.example.mywalletapp.model.User;
import com.example.mywalletapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException
    {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException
                        ("User Not Found with this email: " + email)
                );
        if (user.getRoles().isEmpty()) { logger.warn("User has no roles assigned!"); }
        return UserDetailsImpl.build(user);
    }
}