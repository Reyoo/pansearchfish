package com.libbytian.pan.system.aop;

@FunctionalInterface
public interface CacheSelector<T> {
    T select() throws Exception;
}
