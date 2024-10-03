package org.example.delivery.location;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/*
 * GeoPoint encloses geographic coordinates.
 * It is used on JPA entities to add latitude, longitude columns.
 * It is also used on DTOs to avoid having a duplicate class.
 */

@Embeddable
public class GeoPoint {

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    public GeoPoint() {
    }

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static GeoPoint random() {
        double f = Math.random() / Math.nextDown(1.0);
        double lat = -90 * (1.0 - f) + 90 * f;      // [-90..90]
        double lon = -180 * (1.0 - f) + 180 * f;    // [-180..180]
        return new GeoPoint(lat, lon);
    }
}
