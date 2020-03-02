package com.arafeh.jsf.service;

import com.arafeh.jsf.bll.GraphNodeBll;
import com.arafeh.jsf.bll.NodeBll;
import com.arafeh.jsf.bll.ProjectBll;
import com.arafeh.jsf.config.AppProperties;
import com.arafeh.jsf.model.Project;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.geojson.Point;
import dynamicore.DynamicNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

import static com.arafeh.jsf.core.utils.Extensions.isNullOrEmpty;

/**
 * this class is a singleton for the whole service, it existence is for one and only
 * one purpose, execute pending, resume running DynamicNetwork projects.
 * currently whenever a network didn't execute we can stop its execution, but one
 * it start executing forget about it. need to fix anyone but not now
 */
@Singleton
@LocalBean
public class GeoCoder implements Serializable {

    @PostConstruct
    public void init() {
        MapboxGeocoding.builder()
                .accessToken(AppProperties.getInstance().getMapboxAccesstocken())
                .query("1600 Pennsylvania Ave NW")
                .build();
    }

    public LatLong decode(String address) {
        MapboxGeocoding request = MapboxGeocoding.builder()
                .accessToken(AppProperties.getInstance().getMapboxAccesstocken())
                .query(address)
                .build();
        try {
            Point point = (Point) Objects.requireNonNull(request.executeCall().body()).features().get(0).geometry();
            return new LatLong(point.latitude(), point.longitude());
        } catch (Exception e) {
            return LatLong.nullValue();
        }
    }


    public static class LatLong {
        public static LatLong nullValue() {
            return new LatLong(null, null);
        }

        private final Double lat;
        private final Double lng;

        public LatLong(Double lat, Double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

        public boolean hasValue() {
            return lat != null && lng != null;
        }
    }

}
