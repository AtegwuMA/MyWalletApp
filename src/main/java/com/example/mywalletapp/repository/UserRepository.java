package com.example.mywalletapp.repository;




import com.example.mywalletapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    User findByVerificationToken(String token);


//    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :userId")
//    Optional<User> findByIdWithRoles(@Param("userId") Long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    List<User> findByIsLocked(boolean b);

    Optional<User> findByResetToken(String token);
}

