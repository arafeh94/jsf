package com.arafeh.jsf.dal;

import com.arafeh.jsf.core.utils.StaticResources;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import dynamicore.input.Breadcrumb;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GeoTest {
    @Test
    public void test1() throws IOException {
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoiYXJhZmVoMTk4IiwiYSI6ImNqdnA4NmtvMjIwZHA0NG1sM2h5bGd6amUifQ.7nfl_L1gR-u9XxYvTD7fvA")
                .query("1600 Pennsylvania Ave NW")
                .build();
        System.out.println(((Point) mapboxGeocoding.executeCall().body().features().get(0).geometry()));
    }

}
