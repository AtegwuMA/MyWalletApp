package com.example.mywalletapp.factory;


import com.example.mywalletapp.exception.RoleNotFoundException;
import com.example.mywalletapp.model.ERole;
import com.example.mywalletapp.model.Role;
import com.example.mywalletapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class RoleFactory {
    @Autowired
    RoleRepository roleRepository;

    public Role getInstance(String role) throws RoleNotFoundException {
        switch (role) {
            case "admin" -> {
                return roleRepository.findByName(ERole.ROLE_ADMIN);
            }
            case "user" -> {
                return roleRepository.findByName(ERole.ROLE_USER);
            }

            default -> throw  new RoleNotFoundException("No role found for " +  role);
        }
    }

}