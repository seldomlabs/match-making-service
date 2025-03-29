package com.matchmaker.common.constants;

public class MatchmakingConstants {

    public static final Integer H3_RESOLUTION = 8;

    public enum MatchStatus {
        CANCELED, PENDING, CONFIRMED, MET
    }

    public static final Integer CANCEL_MATCH_CHECK_DAYS_THRESHOLD = 3;
}
