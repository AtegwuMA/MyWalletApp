package com.example.mywalletapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String password;
    private String dateOfBirth;
    private String address;
    private String bvn;
    private LocalDateTime createdAt;
    private boolean enabled;
    private String walletTiers;
    private String verificationToken;
    private boolean verified = false;
    private LocalDateTime tokenCreatedAt;
    private LocalDateTime lastVerificationRequestTime;
    private int verificationRequestCount;
    private String resetToken;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int loginAttempts;

    @Column(nullable = false)
    private boolean isLocked;

    private LocalDateTime lockTimestamp; // Field to store when the account was locked

    // Corrected the mappedBy attribute to reference the correct property name
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wallet> wallets; // Should map to 'user' in Wallet

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


}