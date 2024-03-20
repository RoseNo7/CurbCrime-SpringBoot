package com.roseno.curbcrime.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "notices")
public class Notice {
    @Id
    private long id;
    private String title;
    private String content;
    private int viewCount;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;

    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deleteAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
