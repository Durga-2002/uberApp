package com.codingshuttle.project.uber.uberApp.strategies;

import com.codingshuttle.project.uber.uberApp.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.codingshuttle.project.uber.uberApp.strategies.impl.DriverMatchingNearestDriverStrategy;
import com.codingshuttle.project.uber.uberApp.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import com.codingshuttle.project.uber.uberApp.strategies.impl.RiderFareDefaultFareCalculationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class RideStrategyManagerTest {

    @Mock
    private DriverMatchingHighestRatedDriverStrategy highestRatedDriverStrategy;
    @Mock
    private DriverMatchingNearestDriverStrategy nearestDriverStrategy;
    @Mock
    private RideFareSurgePricingFareCalculationStrategy surgePricingStrategy;
    @Mock
    private RiderFareDefaultFareCalculationStrategy defaultFareStrategy;

    private RideStrategyManager rideStrategyManager;

    @BeforeEach
    void setUp() {
        rideStrategyManager = new RideStrategyManager(
                highestRatedDriverStrategy,
                nearestDriverStrategy,
                surgePricingStrategy,
                defaultFareStrategy
        );
    }

    @ParameterizedTest
    @ValueSource(doubles = {4.8, 4.9, 5.0})
    void ridersWithHighRatingGetTopRatedDrivers(double riderRating) {
        assertSame(highestRatedDriverStrategy, rideStrategyManager.driverMatchingStrategy(riderRating));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 4.5, 4.79})
    void ridersBelowThresholdGetNearestDrivers(double riderRating) {
        assertSame(nearestDriverStrategy, rideStrategyManager.driverMatchingStrategy(riderRating));
    }
}
