package com.educast.ems.models;

public enum Shift {
    CUSTOM,            // active at random time
    MORNING_FIRST_HALF,  // 08:00 - 12:59
    MORNING_SECOND_HALF, // 13:00 - 16:59
    NIGHT_FIRST_HALF,    // 20:00 - 23:59
    NIGHT_SECOND_HALF    // 00:00 - 04:59
}

