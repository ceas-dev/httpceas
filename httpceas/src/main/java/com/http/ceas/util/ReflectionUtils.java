package com.http.ceas.util;

import android.os.Build;
import com.http.ceas.core.HttpConnection;
import com.http.ceas.core.annotation.verbs.DELETE;
import com.http.ceas.core.annotation.verbs.GET;
import com.http.ceas.core.annotation.verbs.HEAD;
import com.http.ceas.core.annotation.verbs.OPTIONS;
import com.http.ceas.core.annotation.verbs.PATCH;
import com.http.ceas.core.annotation.verbs.POST;
import com.http.ceas.core.annotation.verbs.PUT;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public final class ReflectionUtils {

    public static final Class[] VERBS_ANNOTATIONS = new Class[]{
            DELETE.class, GET.class, HEAD.class, OPTIONS.class, PATCH.class, POST.class, PUT.class
    };

    public static boolean containsAnnotation(AccessibleObject object, Class<? extends Annotation>... annotations) {
        for (Class<? extends Annotation> annotation : annotations) {
            if (object.isAnnotationPresent(annotation)) return true;
        }
        return false;
    }

    public static Class<?> getReturnType(Method method) {
        return method.getReturnType();
    }

    public static boolean checkReturnType(Method method, Class<?>... typesReturn) {
        for (Class<?> type : typesReturn) {
            if (getReturnType(method).equals(type)) return true;
        }
        return false;
    }

    public static boolean isHttpMethod(Method method) {
        return containsAnnotation(method, VERBS_ANNOTATIONS) && checkReturnType(method, HttpConnection.class);
    }


    public static String getFirstAnnotationName(AccessibleObject object, Class<? extends Annotation>... annotations){
        Annotation annotation = getFirstAnnotation(object, annotations);
        return (annotation == null) ? null : annotation.annotationType().getSimpleName();
    }
    public static Annotation getFirstAnnotation(AccessibleObject object, Class<? extends Annotation>... annotations) {
        for (Annotation objectAnnotation : object.getAnnotations()) {
           for(Class<? extends Annotation> annotation : annotations) {
               if(objectAnnotation.annotationType().isAssignableFrom(annotation)){
                   return objectAnnotation;
               }
           }
        }
        return null;
    }

    public static <T extends Annotation> T getAnnotation(AccessibleObject object, Class<T> typeAnnotation) {
        return object.getAnnotation(typeAnnotation);
    }

}
