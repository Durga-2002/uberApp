package com.codingshuttle.project.uber.uberApp.services;

import com.codingshuttle.project.uber.uberApp.entities.RideRequest;

public interface RideRequestService {

    RideRequest findRiderRequestById(Long rideRequestId);

    void update(RideRequest rideRequest);
}
