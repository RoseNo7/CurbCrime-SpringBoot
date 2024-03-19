package com.roseno.curbcrime.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "users")
public class User {
    @Id
    private long idx;

    private String id;
    private String password;
    private String name;
    private String email;
    private String role;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;

    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deleteAt;
}
