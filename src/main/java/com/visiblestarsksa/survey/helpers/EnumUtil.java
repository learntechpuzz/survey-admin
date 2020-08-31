package com.visiblestarsksa.survey.helpers;

public class EnumUtil {

    public static <E extends Enum<E>> E value(Class<E> clz, String name, E defaultValue) {
        try {
            return Enum.valueOf(clz, name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return defaultValue;
        }
    }
}
