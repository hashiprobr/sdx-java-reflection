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

/**
 * <p>
 * Implemented to convert objects of a given source type to and from objects of
 * a given target type.
 * </p>
 * <p>
 * The idea is that the source type is not supported by a third-party library
 * but the target type is (possibly via other converters).
 * </p>
 *
 * @param <S> The source type.
 * @param <T> The target type.
 */
public interface Converter<S, T> {
    /**
     * Converts an object of the source type to an object of the target type.
     *
     * @param source An object of type {@code S}.
     * @return An object of type {@code T}.
     */
    T to(S source);

    /**
     * Converts an object of the source type from an object of the target type.
     *
     * @param target An object of type {@code T}.
     * @return An object of type {@code S}.
     */
    S from(T target);
}
