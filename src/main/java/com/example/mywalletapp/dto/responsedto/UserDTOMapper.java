package com.example.mywalletapp.dto.responsedto;


import com.example.mywalletapp.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserDTOMapper implements Function<User, UserSignUpResponse> {
    @Override
    public UserSignUpResponse apply(User user){

        List<WalletResponseDto> walletDtos = user.getWallets().stream()
                .map(wallet -> {
                    WalletResponseDto walletDto = new WalletResponseDto();
                    walletDto.setId(wallet.getId());
                    walletDto.setTier(wallet.getTier());
                    walletDto.setBalance(wallet.getBalance());
                    return walletDto;
                })
                .collect(Collectors.toList());

        return UserSignUpResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .bvn(user.getBvn())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .wallets(walletDtos)
                .build();
    }

}
