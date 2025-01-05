package com.team2.djavaluxury.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team2.djavaluxury.constant.TableConstant;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = TableConstant.TABLE_ROOM)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "room_number", nullable = false)
    private String roomNumber;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "price_per_night", nullable = false)
    private Double pricePerNight;
    @JoinColumn(name = "room_image_id", unique = true)
    @OneToOne
    private Image image;
    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Tambahkan ini
    @JsonIgnore // Hindari serialisasi referensi ke Booking
    private List<Booking> booking = new ArrayList<>();
}
