package com.example.mywalletapp.dto.requestdto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.Set;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    private String middleName;

    @Size(max = 100)
    @NotBlank(message = "Email Name is required")
    @Email(message = "A valid email is required")
    private String email;

    @NotNull
    @NotBlank(message = "Password Name is required")
    @Size(max = 20, message = "Password must be less than or equal to 20 characters")
    @Size(min = 8, message = "Password must be more than or equal to 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()-+=]).{8,}$",
            message = "Password must contain at least 8 characters, one digit, " +
                       "one lowercase letter, one uppercase letter, and one special character")
    private String password;

    @NotBlank(message = "Date of Birth is required")
    private String dateOfBirth;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "BVN is required")
    private String bvn; // Bank Verification Number

    private Set<String> roles;

    private String walletTier;

}
