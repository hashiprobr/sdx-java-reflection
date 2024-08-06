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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stub.
 */
public final class ConverterFactory {
    private final Reflector reflector;
    private final ConcurrentMap<Class<? extends Converter<?, ?>>, Converter<?, ?>> cache;

    ConverterFactory(Reflector reflector) {
        this.reflector = reflector;
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Stub.
     *
     * @param type Stub.
     * @return Stub.
     */
    public Converter<?, ?> get(Class<? extends Converter<?, ?>> type) {
        return cache.computeIfAbsent(type, this::compute);
    }

    private Converter<?, ?> compute(Class<? extends Converter<?, ?>> type) {
        Converter<?, ?> converter;
        String typeName = type.getName();
        MethodHandle creator = reflector.getCreator(type, typeName);
        try {
            converter = (Converter<?, ?>) creator.invoke();
        } catch (Throwable throwable) {
            throw new ReflectionException("Class %s could not be instantiated".formatted(typeName), throwable);
        }
        return converter;
    }
}
