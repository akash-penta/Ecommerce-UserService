package com.akash.userservice.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenResponseDto {
    private UUID id;
    private String email;
    private List<String> roles = new ArrayList<>();
    private Date createdAt;
    private Date expireAt;
}
