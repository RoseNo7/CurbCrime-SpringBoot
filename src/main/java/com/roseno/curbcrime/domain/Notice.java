package com.roseno.curbcrime.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notice {
    private long id;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private boolean isDeleted;
    private LocalDateTime deleteAt;

    private User user;
}
