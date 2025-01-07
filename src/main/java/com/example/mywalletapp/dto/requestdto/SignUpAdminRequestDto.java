package com.example.mywalletapp.dto.requestdto;



import lombok.*;

import java.util.Set;

//@Data(callSuper = true) // Equals will check both SignUpAdminRequestDto and SignUpRequestDto fields
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpAdminRequestDto extends SignUpRequestDto {

    private Set<String> roles;
}

