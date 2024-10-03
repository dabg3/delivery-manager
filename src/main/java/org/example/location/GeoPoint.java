package org.example.location;

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
}
