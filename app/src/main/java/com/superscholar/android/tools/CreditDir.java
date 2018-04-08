package com.superscholar.android.tools;

/**
 * @author zhqy
 * @date 2018/4/8
 */

public class CreditDir {
    public static final double LEVEL_LOWEST = 50;
    public static final double LEVEL_LOWER = 150;
    public static final double LEVEL_MEDIUM = 300;
    public static final double LEVEL_HIGHER = 500;
    public static final double LEVEL_HIGHEST = 999;

    public static class ConcentrationTime{
        public static final double MORTGAGE = -3;
        public static final double RETURN_LOW = 3;
        public static final double RETURN_MIDDLE = 5;
        public static final double RETURN_HIGH = 7;
        public static final double RETURN_HIGHEST = 9;
    }

    public static class Target{
        public static final double PUNISH = -5;
        public static final double GRADE_PER_DAY = 2;
    }

    public static class Event{
        public static final double INVEST_PER_HOUR = 1;
        public static final double WASTE_PER_HOUR = -1;
        public static final double SLEEP_PER_HOUR = -1;
        public static final double REGULAR_PER_HOUR = -1;
    }
}
