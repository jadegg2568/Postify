package ru.jadegg2568.Postify.user;

public class UserParamLimits {

    public static class Min {
        public static final int LOGIN = 8;
        public static final int MAIL = 6;
        public static final int PASSWORD = 8;
        public static final int NAME = 2;
        public static final int DISPLAY_NAME = 1;
        public static final int DESCRIPTION = 0;
        public static final int TOKEN = 128;
    }

    public static class Max {
        public static final int LOGIN = 32;
        public static final int MAIL = 32;
        public static final int PASSWORD = 32;
        public static final int NAME = 24;
        public static final int DISPLAY_NAME = 100;
        public static final int DESCRIPTION = 2000;
        public static final int TOKEN = 512;
    }
}

