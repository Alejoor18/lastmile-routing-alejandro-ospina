package com.alejandro.lastmile.util;

public final class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0088;

    private GeoUtils() {
    }

    public static double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double startLat = Math.toRadians(lat1);
        double endLat = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(startLat) * Math.cos(endLat) * Math.pow(Math.sin(dLng / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public static boolean isValidLatitude(Double latitude) {
        return latitude != null && latitude >= -90 && latitude <= 90;
    }

    public static boolean isValidLongitude(Double longitude) {
        return longitude != null && longitude >= -180 && longitude <= 180;
    }
}
