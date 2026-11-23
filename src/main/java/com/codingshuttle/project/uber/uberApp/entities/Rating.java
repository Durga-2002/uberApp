package com.codingshuttle.project.uber.uberApp.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(
        indexes = {
                @Index(name = "idx_rating_rider", columnList = "ride_id"),
                @Index(name = "idx_rating_driver", columnList = "driver_id")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    private Integer riderRating;

    private Integer driverRating;

    @OneToOne
    private Ride ride;

    @ManyToOne
    private Rider rider;

    @ManyToOne
    private Driver driver;
}
