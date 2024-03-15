package com.roseno.curbcrime.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private long idx;
    private String id;
    private String password;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private boolean isDeleted;
    private LocalDateTime deleteAt;
}
