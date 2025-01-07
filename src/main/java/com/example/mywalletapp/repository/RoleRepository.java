package com.example.mywalletapp.repository;


import com.example.mywalletapp.model.ERole;
import com.example.mywalletapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(ERole name);
}
