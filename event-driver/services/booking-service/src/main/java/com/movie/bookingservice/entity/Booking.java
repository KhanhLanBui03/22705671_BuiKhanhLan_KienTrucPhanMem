package com.movie.bookingservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    private String id;
    private Long movieId;
    private String userId;
    private String status;

    // Phụ trợ nếu Lombok lỗi trong runtime
    public void setStatus(String status) {
        this.status = status;
    }
}
