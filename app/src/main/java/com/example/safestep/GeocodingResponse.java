package com.example.safestep;

import java.util.List;

public class GeocodingResponse {
    public List<Result> results;

    public class Result {
        public Geometry geometry;

        public class Geometry {
            public Location location;

            public class Location {
                public double lat;
                public double lng;
            }
        }
    }
}
