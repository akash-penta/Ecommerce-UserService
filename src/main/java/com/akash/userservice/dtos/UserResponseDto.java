package com.akash.userservice.dtos;

import com.akash.userservice.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponseDto {
    private String email;
    private List<String> roles = new ArrayList<>();

    public static UserResponseDto from(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail(user.getEmail());

        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getRole()));
        userResponseDto.setRoles(roles);

        return userResponseDto;
    }
}
