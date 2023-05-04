package com.codeforces.iomarkup.test;

import com.codeforces.commons.properties.PropertiesUtil;

import java.nio.file.Path;

public class Profile {
    private static final String FILENAME = "profile.properties";

    private Profile() {
    }

    public static String getPolygonLogin() {
        return PropertiesUtil.getProperty("test.polygon.login", "", FILENAME);
    }

    public static String getPolygonPassword() {
        return PropertiesUtil.getProperty("test.polygon.password", "", FILENAME);
    }

    public static Path getCachePath() {
        return Path.of(
                PropertiesUtil.getProperty("test.cache.dir", "./cache", FILENAME)
        );
    }
}
