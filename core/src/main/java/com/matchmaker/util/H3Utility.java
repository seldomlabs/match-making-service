package com.matchmaker.util;

import com.uber.h3core.H3Core;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class H3Utility {

    private static final Logger logger = LogManager.getLogger(H3Utility.class);

    public static final H3Core h3 = getH3CoreInstance();

    private static H3Core getH3CoreInstance() {
        try {
            if (h3 == null) {
                return H3Core.newInstance();
            } else {
                return h3;
            }
        } catch (Exception e) {
            logger.error("Exception in getH3CoreInstance", e);
        }
        return null;
    }

    public static String latLonToH3(double lat, double lon, int resolution) {
        assert h3 != null;
        return h3.latLngToCellAddress(lat, lon, resolution);
    }

    public static List<String> getH3IndicesInRadius(double lat, double lon, int resolution, int radius) {
        String h3Index = latLonToH3(lat, lon, resolution);
        assert h3 != null;
        return h3.gridDisk(h3Index, radius);
    }
}