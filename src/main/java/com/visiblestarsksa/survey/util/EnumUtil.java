package com.visiblestarsksa.survey.util;

public class EnumUtil {

    public static <E extends Enum<E>> E value(Class<E> clz, String name, E defaultValue) {
        try {
            return Enum.valueOf(clz, name);
        } catch (IllegalArgumentException | NullPointerException e) {
            if (defaultValue == null) throw e;
            else return defaultValue;
        }
    }
}
