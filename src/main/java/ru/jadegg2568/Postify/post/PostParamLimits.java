package ru.jadegg2568.Postify.post;

public class PostParamLimits {
    public static class Min {
        public static final int TITLE = 1;
        public static final int CONTENT = 1;
    }

    public static class Max {
        public static final int TITLE = 255;
        public static final int CONTENT = 10000;
    }
}

