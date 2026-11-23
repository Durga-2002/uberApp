package com.codingshuttle.project.uber.uberApp.strategies;

import com.codingshuttle.project.uber.uberApp.entities.RideRequest;
import com.codingshuttle.project.uber.uberApp.services.DistanceService;
import com.codingshuttle.project.uber.uberApp.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import com.codingshuttle.project.uber.uberApp.strategies.impl.RiderFareDefaultFareCalculationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideFareCalculationStrategyTest {

    @Mock
    private DistanceService distanceService;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private RideRequest rideRequest;
    private Point pickup;
    private Point dropOff;

    @BeforeEach
    void setUp() {
        pickup = geometryFactory.createPoint(new Coordinate(77.2, 28.6));
        dropOff = geometryFactory.createPoint(new Coordinate(77.3, 28.7));
        rideRequest = new RideRequest();
        rideRequest.setPickupLocation(pickup);
        rideRequest.setDropOffLocation(dropOff);
    }

    @Test
    void defaultFareIsDistanceTimesMultiplier() {
        when(distanceService.calculateDistance(pickup, dropOff)).thenReturn(12.5);

        double fare = new RiderFareDefaultFareCalculationStrategy(distanceService).calculateFare(rideRequest);

        assertEquals(125.0, fare, 1e-9);
        verify(distanceService).calculateDistance(pickup, dropOff);
    }

    @Test
    void surgeFareIsTwiceTheDefaultFare() {
        when(distanceService.calculateDistance(pickup, dropOff)).thenReturn(12.5);

        double fare = new RideFareSurgePricingFareCalculationStrategy(distanceService).calculateFare(rideRequest);

        assertEquals(250.0, fare, 1e-9);
        verify(distanceService).calculateDistance(pickup, dropOff);
    }

    @Test
    void zeroDistanceProducesZeroFare() {
        when(distanceService.calculateDistance(pickup, dropOff)).thenReturn(0.0);

        double fare = new RiderFareDefaultFareCalculationStrategy(distanceService).calculateFare(rideRequest);

        assertEquals(0.0, fare, 1e-9);
    }
}
