package org.example.delivery;

import jakarta.persistence.Embeddable;

@Embeddable
public class GeoPoint {

    private double latitude;
    private double longitude;
}
