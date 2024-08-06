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

import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * Provides reflection objects.
 */
public final class Reflection {
    private static final Reflection INSTANCE = new Reflection();

    /**
     * Obtains the reflector instance.
     *
     * @return The current instance.
     */
    public static Reflector getReflector() {
        return INSTANCE.reflector;
    }

    /**
     * Obtains the parser factory.
     *
     * @return The current factory.
     */
    public static ParserFactory getParserFactory() {
        return INSTANCE.parserFactory;
    }

    /**
     * Obtains the converter factory.
     *
     * @return The current factory.
     */
    public static ConverterFactory getConverterFactory() {
        return INSTANCE.converterFactory;
    }

    /**
     * Replaces the class loader.
     *
     * @param loader The new loader.
     */
    public static void setLoader(ClassLoader loader) {
        Objects.requireNonNull(loader, "Class loader cannot be null");
        INSTANCE.reflector.setLoader(loader);
    }

    /**
     * Replaces the lookup object.
     *
     * @param lookup The new lookup.
     */
    public static void setLookup(MethodHandles.Lookup lookup) {
        Objects.requireNonNull(lookup, "Lookup object cannot be null");
        INSTANCE.reflector.setLookup(lookup);
    }

    private final Reflector reflector;
    private final ParserFactory parserFactory;
    private final ConverterFactory converterFactory;

    private Reflection() {
        Reflector reflector = new Reflector();

        this.reflector = reflector;
        this.parserFactory = new ParserFactory(reflector);
        this.converterFactory = new ConverterFactory(reflector);
    }
}
