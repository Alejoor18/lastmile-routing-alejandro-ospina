package com.alejandro.lastmile.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GeoUtilsTest {

    @Test
    void haversineReturnsExpectedDistanceAroundMedellin() {
        double distance = GeoUtils.haversineKm(6.2442, -75.5812, 6.2088, -75.5677);

        assertThat(distance).isBetween(4.0, 4.4);
    }

    @Test
    void validatesCoordinates() {
        assertThat(GeoUtils.isValidLatitude(6.2442)).isTrue();
        assertThat(GeoUtils.isValidLatitude(120.0)).isFalse();
        assertThat(GeoUtils.isValidLongitude(-75.5812)).isTrue();
        assertThat(GeoUtils.isValidLongitude(-200.0)).isFalse();
    }
}
