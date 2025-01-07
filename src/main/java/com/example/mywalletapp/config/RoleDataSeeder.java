package com.example.mywalletapp.config;


import com.example.mywalletapp.model.ERole;
import com.example.mywalletapp.model.Role;
import com.example.mywalletapp.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleDataSeeder {
    // Add a logger
    private static final Logger logger = LoggerFactory.getLogger(RoleDataSeeder.class);

    private final RoleRepository roleRepository;

    @EventListener
    @Transactional
    public void LoadRoles(ContextRefreshedEvent event) {

        logger.info("Loading roles into the database as the context has refreshed: {}", event);
        List<ERole> roles = Arrays.stream(ERole.values()).toList();

        for(ERole erole: roles) {
            if (roleRepository.findByName(erole)==null) {
                roleRepository.save(new Role(erole));
            }
        }

    }

}