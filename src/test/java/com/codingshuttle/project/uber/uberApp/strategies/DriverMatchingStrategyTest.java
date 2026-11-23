package com.codingshuttle.project.uber.uberApp.strategies;

import com.codingshuttle.project.uber.uberApp.entities.Driver;
import com.codingshuttle.project.uber.uberApp.entities.RideRequest;
import com.codingshuttle.project.uber.uberApp.repositories.DriverRepository;
import com.codingshuttle.project.uber.uberApp.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.codingshuttle.project.uber.uberApp.strategies.impl.DriverMatchingNearestDriverStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverMatchingStrategyTest {

    @Mock
    private DriverRepository driverRepository;

    private RideRequest rideRequest;
    private Point pickup;

    @BeforeEach
    void setUp() {
        pickup = new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(77.2, 28.6));
        rideRequest = new RideRequest();
        rideRequest.setPickupLocation(pickup);
    }

    @Test
    void nearestStrategyQueriesNearestDriversByPickupLocation() {
        List<Driver> expected = List.of(Driver.builder().id(1L).build());
        when(driverRepository.findTenNearestDrivers(pickup)).thenReturn(expected);

        List<Driver> actual = new DriverMatchingNearestDriverStrategy(driverRepository)
                .findMatchingDriver(rideRequest);

        assertEquals(expected, actual);
        verify(driverRepository).findTenNearestDrivers(pickup);
        verify(driverRepository, never()).findTenNearByTopRatedDrivers(any());
    }

    @Test
    void highestRatedStrategyQueriesTopRatedDriversByPickupLocation() {
        List<Driver> expected = List.of(Driver.builder().id(2L).rating(4.9).build());
        when(driverRepository.findTenNearByTopRatedDrivers(pickup)).thenReturn(expected);

        List<Driver> actual = new DriverMatchingHighestRatedDriverStrategy(driverRepository)
                .findMatchingDriver(rideRequest);

        assertEquals(expected, actual);
        verify(driverRepository).findTenNearByTopRatedDrivers(pickup);
        verify(driverRepository, never()).findTenNearestDrivers(any());
    }
}
