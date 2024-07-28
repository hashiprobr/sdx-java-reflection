/*
 * Copyright (c) 2024 Marcelo Hashimoto
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package br.pro.hashi.sdx.reflection;

import br.pro.hashi.sdx.reflection.exception.ReflectionException;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Stub.
 */
public class ParserFactory {
    private final Reflector reflector;
    private final ConcurrentMap<Class<?>, Function<String, ?>> cache;

    /**
     * Stub.
     *
     * @param reflector Stub.
     */
    public ParserFactory(Reflector reflector) {
        ConcurrentMap<Class<?>, Function<String, ?>> cache = new ConcurrentHashMap<>();
        cache.put(boolean.class, Boolean::parseBoolean);
        cache.put(byte.class, Byte::parseByte);
        cache.put(short.class, Short::parseShort);
        cache.put(int.class, Integer::parseInt);
        cache.put(long.class, Long::parseLong);
        cache.put(float.class, Float::parseFloat);
        cache.put(double.class, Double::parseDouble);
        cache.put(char.class, this::parseChar);
        cache.put(Character.class, this::parseChar);
        cache.put(BigInteger.class, BigInteger::new);
        cache.put(BigDecimal.class, BigDecimal::new);
        cache.put(String.class, (valueString) -> valueString);
        this.reflector = reflector;
        this.cache = cache;
    }

    char parseChar(String valueString) {
        if (valueString.isEmpty()) {
            throw new IllegalArgumentException("Value string cannot be empty");
        }
        if (valueString.length() > 1) {
            throw new IllegalArgumentException("Value string must have only one character");
        }
        return valueString.charAt(0);
    }

    /**
     * Stub.
     *
     * @param type Stub.
     * @param <K>  Stub.
     * @return Stub.
     */
    public <K> Function<String, K> get(Class<K> type) {
        @SuppressWarnings("unchecked")
        Function<String, K> parser = (Function<String, K>) cache.computeIfAbsent(type, this::compute);
        return parser;
    }

    private <K> Function<String, K> compute(Class<K> type) {
        String typeName = type.getName();
        Method method;
        try {
            method = type.getDeclaredMethod("valueOf", String.class);
        } catch (NoSuchMethodException exception) {
            throw new ReflectionException("Class %s must have a valueOf(String) method".formatted(typeName));
        }
        if (!method.getReturnType().equals(type)) {
            throw new ReflectionException("Method valueOf(String) of class %s must return an instance of this class".formatted(typeName));
        }
        int modifiers = method.getModifiers();
        if (!(Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers))) {
            throw new ReflectionException("Method valueOf(String) of class %s must be public and static".formatted(typeName));
        }
        for (Class<?> exceptionType : method.getExceptionTypes()) {
            if (!RuntimeException.class.isAssignableFrom(exceptionType)) {
                throw new ReflectionException("Method valueOf(String) of class %s can only throw unchecked exceptions".formatted(typeName));
            }
        }
        MethodHandle handle = reflector.unreflect(method);
        return (valueString) -> invoke(handle, valueString);
    }

    <K> K invoke(MethodHandle handle, String valueString) {
        try {
            @SuppressWarnings("unchecked")
            K value = (K) handle.invoke(valueString);
            return value;
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException exception) {
                throw exception;
            }
            throw new AssertionError(throwable);
        }
    }
}
