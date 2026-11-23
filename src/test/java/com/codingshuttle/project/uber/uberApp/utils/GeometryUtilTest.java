package com.codingshuttle.project.uber.uberApp.utils;

import com.codingshuttle.project.uber.uberApp.dto.PointDto;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeometryUtilTest {

    @Test
    void createsValidPointWithCoordinatesAndSrid4326() {
        PointDto pointDto = new PointDto(new double[]{77.2, 28.6});

        Point point = GeometryUtil.createPoint(pointDto);

        assertEquals(77.2, point.getX(), 1e-9);
        assertEquals(28.6, point.getY(), 1e-9);
        assertEquals(4326, point.getSRID());
        assertTrue(point.isValid());
    }
}
