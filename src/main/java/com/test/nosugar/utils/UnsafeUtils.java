package com.test.nosugar.utils;

import com.test.nosugar.NoSugar;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

//なんか名前がかっこよくて使ってみたかっただけなんや....
//絶対使わなくていいよな:skull:
public class UnsafeUtils {
    private static final Unsafe UNSAFE;
    public static final boolean SUCCESS;

    static {
        Unsafe temp = null;
        boolean s = false;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            temp = (Unsafe) f.get(null);
            s = true;
            NoSugar.LOGGER.info("Unsafe is available");
        } catch (Exception e) {
            NoSugar.LOGGER.warn("Could not get Unsafe instance.", e);
            e.printStackTrace();
        }
        UNSAFE = temp;
        SUCCESS = s;
    }

    public static long getFieldOffset(Class<?> clazz, String... fieldNames) {
        if (!SUCCESS) return -1;
        for (String name : fieldNames) {
            try {
                Field f = clazz.getDeclaredField(name);
                return UNSAFE.objectFieldOffset(f);
            } catch (NoSuchFieldException e) {
            }
        }
        NoSugar.LOGGER.warn("Could not find field of {}.", clazz.getSimpleName());
        return -1;
    }

    public static Object getObject(Object instance, long offset) {
        return SUCCESS && offset != -1 ? UNSAFE.getObject(instance, offset) : null;
    }

    public static void setField(Object instance, long offset, Object value) {
        if (SUCCESS && offset != -1) {
            try {
                UNSAFE.putObject(instance, offset, value);
            }
            catch (Exception e) {
                NoSugar.LOGGER.warn("Could not set field " + offset + " in class " + instance.getClass().getName(), e);
            }
        }
    }
}