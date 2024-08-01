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
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * Provides reflection methods.
 */
public final class Reflector {
    private static final Objenesis OBJENESIS = new ObjenesisStd();
    private static final Pattern PATTERN = Pattern.compile("\\.");

    private final ClassLoader loader;
    private final MethodHandles.Lookup lookup;
    private final ConcurrentMap<Class<?>, ObjectInstantiator<?>> cache;

    /**
     * Constructs a new reflector.
     *
     * @param loader A class loader.
     * @param lookup A lookup object.
     */
    public Reflector(ClassLoader loader, MethodHandles.Lookup lookup) {
        this.loader = loader;
        this.lookup = lookup;
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Stub.
     *
     * @param type     Stub.
     * @param typeName Stub.
     * @param <E>      Stub.
     * @return Stub.
     */
    public <E> ObjectInstantiator<E> getInstantiator(Class<E> type, String typeName) {
        checkInstantiable(type, typeName);
        ObjectInstantiator<?> instantiator = cache.computeIfAbsent(type, OBJENESIS::getInstantiatorOf);
        return uncheckedCast(instantiator);
    }

    /**
     * Stub.
     *
     * @param type     Stub.
     * @param typeName Stub.
     * @return Stub.
     */
    public MethodHandle getCreator(Class<?> type, String typeName) {
        checkInstantiable(type, typeName);
        Constructor<?> constructor;
        try {
            constructor = type.getDeclaredConstructor();
        } catch (NoSuchMethodException exception) {
            throw new ReflectionException("Class %s must have a no-args constructor (but not necessarily public)".formatted(typeName));
        }
        if (!Modifier.isPublic(constructor.getModifiers())) {
            constructor.setAccessible(true);
        }
        return unreflectConstructor(constructor);
    }

    void checkInstantiable(Class<?> type, String typeName) {
        String message = getNonInstantiableMessage(type, typeName);
        if (message != null) {
            throw new ReflectionException(message);
        }
    }

    MethodHandle unreflectConstructor(Constructor<?> constructor) {
        MethodHandle creator;
        try {
            creator = lookup.unreflectConstructor(constructor);
        } catch (IllegalAccessException exception) {
            throw new AssertionError(exception);
        }
        return creator;
    }

    /**
     * Stub.
     *
     * @param field Stub.
     * @return Stub.
     */
    public MethodHandle unreflectGetter(Field field) {
        MethodHandle getter;
        try {
            getter = lookup.unreflectGetter(field);
        } catch (IllegalAccessException exception) {
            throw new AssertionError(exception);
        }
        return getter;
    }

    /**
     * Stub.
     *
     * @param field Stub.
     * @return Stub.
     */
    public MethodHandle unreflectSetter(Field field) {
        MethodHandle setter;
        try {
            setter = lookup.unreflectSetter(field);
        } catch (IllegalAccessException exception) {
            throw new AssertionError(exception);
        }
        return setter;
    }

    /**
     * Stub.
     *
     * @param method Stub.
     * @return Stub.
     */
    public MethodHandle unreflect(Method method) {
        MethodHandle handle;
        try {
            handle = lookup.unreflect(method);
        } catch (IllegalAccessException exception) {
            throw new AssertionError(exception);
        }
        return handle;
    }

    /**
     * Stub.
     *
     * @param creator Stub.
     * @param args    Stub.
     * @param <E>     Stub.
     * @return Stub.
     */
    public <E> E invokeCreator(MethodHandle creator, Object... args) {
        Object instance;
        try {
            instance = creator.invoke(args);
        } catch (Throwable throwable) {
            throw new AssertionError(throwable);
        }
        return uncheckedCast(instance);
    }

    /**
     * Stub.
     *
     * @param getter   Stub.
     * @param instance Stub.
     * @param <F>      Stub.
     * @return Stub.
     */
    public <F> F invokeGetter(MethodHandle getter, Object instance) {
        Object value;
        try {
            value = getter.invoke(instance);
        } catch (Throwable throwable) {
            throw new AssertionError(throwable);
        }
        return uncheckedCast(value);
    }

    /**
     * Stub.
     *
     * @param setter   Stub.
     * @param instance Stub.
     * @param value    Stub.
     */
    public void invokeSetter(MethodHandle setter, Object instance, Object value) {
        try {
            setter.invoke(instance, value);
        } catch (Throwable throwable) {
            throw new AssertionError(throwable);
        }
    }

    /**
     * Stub.
     *
     * @param handle   Stub.
     * @param instance Stub.
     * @param args     Stub.
     * @param <R>      Stub.
     * @return Stub.
     */
    public <R> R invoke(MethodHandle handle, Object instance, Object... args) {
        Object value;
        try {
            value = handle.invoke(instance, args);
        } catch (Throwable throwable) {
            throw new AssertionError(throwable);
        }
        return uncheckedCast(value);
    }

    /**
     * Stub.
     *
     * @param handle   Stub.
     * @param instance Stub.
     * @param args     Stub.
     * @param <R>      Stub.
     * @return Stub.
     */
    public <R> R invokeWithArguments(MethodHandle handle, Object instance, Object... args) {
        Object value;
        try {
            value = handle.invokeWithArguments(instance, args);
        } catch (Throwable throwable) {
            throw new AssertionError(throwable);
        }
        return uncheckedCast(value);
    }

    /**
     * Stub.
     *
     * @param packageName Stub.
     * @param superType   Stub.
     * @param <T>         Stub.
     * @return Stub.
     */
    public <T> Iterable<Class<? extends T>> getInstantiableSubTypes(String packageName, Class<T> superType) {
        Objects.requireNonNull(packageName, "Package name cannot be null");

        Queue<Class<? extends T>> queue = new LinkedList<>();

        Stack<String> stack = new Stack<>();
        stack.push(PATTERN.matcher(packageName).replaceAll("/"));

        return () -> new Iterator<>() {
            @Override
            public boolean hasNext() {
                return !(queue.isEmpty() && stack.isEmpty());
            }

            @Override
            public Class<? extends T> next() {
                if (queue.isEmpty() && !stack.isEmpty()) {
                    String name = stack.pop();

                    URL url = loader.getResource(name);
                    if (url != null) {

                        File file = new File(url.getPath());
                        if (file.canRead()) {

                            String[] baseNames = file.list();
                            if (baseNames == null) {
                                Class<? extends T> subType = getInstantiableSubType(name, superType);
                                if (subType != null) {
                                    queue.add(subType);
                                }
                            } else {
                                for (String baseName : baseNames) {
                                    stack.push("%s/%s".formatted(name, baseName));
                                }
                            }
                        }
                    }
                }

                return queue.remove();
            }
        };
    }

    private <T> Class<? extends T> getInstantiableSubType(String name, Class<T> superType) {
        if (name.endsWith(".class")) {
            String typeName = name.substring(0, name.lastIndexOf('.'));
            try {
                Class<?> type = Class.forName(typeName, true, loader);
                if (superType.isAssignableFrom(type) && getNonInstantiableMessage(type, typeName) == null) {
                    return uncheckedCast(type);
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    private String getNonInstantiableMessage(Class<?> type, String typeName) {
        if (type.isAnnotation()) {
            return "Type %s cannot be an annotation".formatted(typeName);
        }
        if (type.isAnonymousClass()) {
            return "Type %s cannot be an anonymous class".formatted(typeName);
        }
        if (type.isArray()) {
            return "Type %s cannot be an array class".formatted(typeName);
        }
        if (type.isEnum()) {
            return "Type %s cannot be an enum".formatted(typeName);
        }
        if (type.isHidden()) {
            return "Type %s cannot be hidden".formatted(typeName);
        }
        if (type.isInterface()) {
            return "Type %s cannot be an interface".formatted(typeName);
        }
        if (type.isLocalClass()) {
            return "Type %s cannot be a local class".formatted(typeName);
        }
        if (type.isMemberClass()) {
            return "Type %s cannot be a member class".formatted(typeName);
        }
        if (type.isPrimitive()) {
            return "Type %s cannot be primitive".formatted(typeName);
        }
        if (type.isSynthetic()) {
            return "Type %s cannot be synthetic".formatted(typeName);
        }
        if (type.getTypeParameters().length > 0) {
            return "Type %s cannot be generic".formatted(typeName);
        }
        if (Modifier.isAbstract(type.getModifiers())) {
            return "Type %s cannot be an abstract class".formatted(typeName);
        }
        return null;
    }

    /**
     * Stub.
     *
     * @param instance  Stub.
     * @param rootType  Stub.
     * @param rootIndex Stub.
     * @param <T>       Stub.
     * @param <S>       Stub.
     * @return Stub.
     */
    public <T, S extends T> Type getSpecificType(S instance, Class<T> rootType, int rootIndex) {
        Class<?> type = instance.getClass();

        TypeVariable<?>[] typeVariables;

        Stack<Node> stack = new Stack<>();
        stack.push(new Node(null, type));

        while (!stack.isEmpty()) {
            Node node = stack.peek();

            if (node.moveToNext()) {
                Class<?> superType = node.getSuperType();

                if (superType != null) {
                    if (superType.equals(rootType)) {
                        int index = rootIndex;

                        while (node != null) {
                            ParameterizedType genericSuperType = node.getGenericSuperType();
                            Type[] types = genericSuperType.getActualTypeArguments();
                            Type specificType = types[index];

                            if (!(specificType instanceof TypeVariable)) {
                                return specificType;
                            }

                            typeVariables = node.getTypeParameters();
                            index = 0;
                            while (!specificType.equals(typeVariables[index])) {
                                index++;
                            }

                            node = node.getSubNode();
                        }
                    } else {
                        stack.push(new Node(node, superType));
                    }
                }
            } else {
                stack.pop();
            }
        }

        typeVariables = rootType.getTypeParameters();
        String typeVariableName = typeVariables[rootIndex].getName();
        throw new ReflectionException("Class %s must specify type %s of %s".formatted(type.getName(), typeVariableName, rootType.getName()));
    }

    private static class Node {
        private final Node subNode;
        private final Class<?> type;
        private final Class<?>[] interfaces;
        private final Type[] genericInterfaces;
        private int index;

        private Node(Node subNode, Class<?> type) {
            this.subNode = subNode;
            this.type = type;
            this.interfaces = type.getInterfaces();
            this.genericInterfaces = type.getGenericInterfaces();
            this.index = -2;
            // superclass == -1
            // interfaces >= 0
        }

        private Node getSubNode() {
            return subNode;
        }

        private TypeVariable<?>[] getTypeParameters() {
            return type.getTypeParameters();
        }

        private boolean moveToNext() {
            index++;
            return index < interfaces.length;
        }

        private Class<?> getSuperType() {
            Class<?> superType;
            if (index == -1) {
                superType = type.getSuperclass();
            } else {
                superType = interfaces[index];
            }
            return superType;
        }

        private ParameterizedType getGenericSuperType() {
            Type genericSuperType;
            if (index == -1) {
                genericSuperType = type.getGenericSuperclass();
            } else {
                genericSuperType = genericInterfaces[index];
            }
            return (ParameterizedType) genericSuperType;
        }
    }

    /**
     * Casts a given object to a generic type.
     *
     * @param instance The object.
     * @param <T>      The type.
     * @return The object cast to the type.
     */
    public <T> T uncheckedCast(Object instance) {
        @SuppressWarnings("unchecked")
        T uncheckedInstance = (T) instance;
        return uncheckedInstance;
    }
}
