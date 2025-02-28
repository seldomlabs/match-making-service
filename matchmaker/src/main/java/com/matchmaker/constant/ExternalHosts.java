package com.matchmaker.constant;

import com.matchmaker.util.ApplicationProperties;

public class ExternalHosts {

    public static final String USER_SERVICE_HOST = ApplicationProperties.getInstance()
            .getProperty("external", "user.service.host", "http://3.185.76:3000/");
}
