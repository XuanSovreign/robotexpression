package com.space.hwexpression;

import android.text.TextUtils;

import java.lang.reflect.Field;

/**
 * Created by licht on 2019/9/10.
 */

public class ValueArg {
    public static Object getValue(Object object, String fieldName) {
        if (object == null || TextUtils.isEmpty(fieldName)) {
            return null;
        }
        Class<?> clazz = object.getClass();
        Field field = null;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
