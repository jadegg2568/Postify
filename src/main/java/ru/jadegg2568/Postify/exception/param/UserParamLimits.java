package ru.jadegg2568.Postify.exception.param;

public class UserParamLimits {

    public static class Min {
        public static final int LOGIN = 8;
        public static final int MAIL = 6;
        public static final int PASSWORD = 8;
        public static final int NAME = 2;
        public static final int DISPLAY_NAME = 1;
        public static final int DESCRIPTION = 0;
    }

    public static class Max {
        public static final int LOGIN = 32;
        public static final int MAIL = 64;
        public static final int PASSWORD = 64;
        public static final int NAME = 50;
        public static final int DISPLAY_NAME = 100;
        public static final int DESCRIPTION = 2000;
    }
}

