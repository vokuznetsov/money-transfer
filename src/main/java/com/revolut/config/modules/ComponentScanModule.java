package com.revolut.config.modules;

import com.google.inject.AbstractModule;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reflections.Reflections;

/**
 * Bind classes with specific annotations.
 */
public class ComponentScanModule extends AbstractModule {

    private final String packageName;
    private final Set<Class<? extends Annotation>> bindingAnnotations;

    @SafeVarargs
    public ComponentScanModule(String packageName, final Class<? extends Annotation>... bindingAnnotations) {
        this.packageName = packageName;
        this.bindingAnnotations = new HashSet<>(Arrays.asList(bindingAnnotations));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure() {
        Reflections packageReflections = new Reflections(packageName);
        Set<Class<?>> classes = bindingAnnotations.stream()
                .map(packageReflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        for (Class aClass : classes) {
            Class<?>[] interfaces = aClass.getInterfaces();
            if (interfaces.length == 0) {
                bind(aClass);
            } else {
                Stream.of(aClass.getInterfaces()).forEach(inter -> bind(inter).to(aClass));
            }
        }
    }
}
