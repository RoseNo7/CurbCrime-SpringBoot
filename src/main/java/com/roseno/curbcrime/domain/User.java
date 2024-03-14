package com.roseno.curbcrime.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    long idx;
    String id;
    String password;
    String name;
    String email;
    String role;
    LocalDateTime createAt;
    LocalDateTime updateAt;
    boolean isDeleted;
    LocalDateTime deleteAt;
}
