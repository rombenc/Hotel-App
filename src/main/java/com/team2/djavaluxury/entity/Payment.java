package com.team2.djavaluxury.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team2.djavaluxury.constant.TableConstant;
import com.team2.djavaluxury.constant.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = TableConstant.TABLE_PAYMENT)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "token")
    private String token;

    @Column(name = "redirect_url", nullable = false)
    private String redirectUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @Column(name = "created_at")
    @Temporal(TemporalType.DATE)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date created_at;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Method untuk mengupdate waktu updatedAt setiap kali entitas di-persist atau update
    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        updatedAt = LocalDateTime.now();  // Set updatedAt to current time
    }

    @OneToOne(mappedBy = "payment") // ini harus sama dengan attribute di booking
    private Booking booking;
}

