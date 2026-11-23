package com.codingshuttle.project.uber.uberApp.services.impl;

import com.codingshuttle.project.uber.uberApp.dto.DriverDto;
import com.codingshuttle.project.uber.uberApp.dto.RiderDto;
import com.codingshuttle.project.uber.uberApp.entities.Driver;
import com.codingshuttle.project.uber.uberApp.entities.Rating;
import com.codingshuttle.project.uber.uberApp.entities.Ride;
import com.codingshuttle.project.uber.uberApp.entities.Rider;
import com.codingshuttle.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.codingshuttle.project.uber.uberApp.repositories.DriverRepository;
import com.codingshuttle.project.uber.uberApp.repositories.RatingRepository;
import com.codingshuttle.project.uber.uberApp.repositories.RiderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private RiderRepository riderRepository;
    @Mock
    private ModelMapper modelMapper;

    private RatingServiceImpl ratingService;
    private Ride ride;
    private Driver driver;
    private Rider rider;

    @BeforeEach
    void setUp() {
        ratingService = new RatingServiceImpl(ratingRepository, driverRepository, riderRepository, modelMapper);

        driver = Driver.builder().id(1L).rating(4.0).build();
        rider = Rider.builder().id(1L).rating(4.0).build();
        ride = new Ride();
        ride.setId(10L);
        ride.setDriver(driver);
        ride.setRider(rider);
    }

    @Test
    void rateDriverAveragesAllDriverRatings() {
        Rating existing = Rating.builder().ride(ride).driver(driver).rider(rider).build();
        when(ratingRepository.findByRider(ride)).thenReturn(Optional.of(existing));
        when(ratingRepository.findByDriver(driver)).thenReturn(List.of(
                Rating.builder().driverRating(5).build(),
                Rating.builder().driverRating(4).build(),
                Rating.builder().driverRating(3).build()
        ));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(modelMapper.map(driver, DriverDto.class)).thenReturn(new DriverDto());

        ratingService.rateDriver(ride, 5);

        assertEquals(5, existing.getDriverRating());
        assertEquals(4.0, driver.getRating(), 1e-9);
        verify(ratingRepository).save(existing);
        verify(driverRepository).save(driver);
    }

    @Test
    void rateDriverRejectsSecondRatingForSameRide() {
        Rating alreadyRated = Rating.builder().ride(ride).driver(driver).rider(rider).driverRating(4).build();
        when(ratingRepository.findByRider(ride)).thenReturn(Optional.of(alreadyRated));

        assertThrows(RuntimeConflictException.class, () -> ratingService.rateDriver(ride, 5));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void rateRiderAveragesAllRiderRatings() {
        Rating existing = Rating.builder().ride(ride).driver(driver).rider(rider).build();
        when(ratingRepository.findByRider(ride)).thenReturn(Optional.of(existing));
        when(ratingRepository.findByRider(rider)).thenReturn(List.of(
                Rating.builder().riderRating(5).build(),
                Rating.builder().riderRating(3).build()
        ));
        when(riderRepository.save(rider)).thenReturn(rider);
        when(modelMapper.map(rider, RiderDto.class)).thenReturn(new RiderDto());

        ratingService.rateRider(ride, 5);

        assertEquals(5, existing.getRiderRating());
        assertEquals(4.0, rider.getRating(), 1e-9);
        verify(ratingRepository).save(existing);
        verify(riderRepository).save(rider);
    }

    @Test
    void createNewRatingSavesRatingLinkedToRideRiderAndDriver() {
        ratingService.createNewRating(ride);

        ArgumentCaptor<Rating> captor = ArgumentCaptor.forClass(Rating.class);
        verify(ratingRepository).save(captor.capture());
        Rating saved = captor.getValue();
        assertSame(ride, saved.getRide());
        assertSame(rider, saved.getRider());
        assertSame(driver, saved.getDriver());
    }
}
