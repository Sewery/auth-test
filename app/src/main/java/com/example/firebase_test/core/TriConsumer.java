package com.example.firebase_test.core;

import androidx.annotation.Nullable;

@FunctionalInterface
public interface TriConsumer<A,B,C> {
    void accept(A a,@Nullable B b,@Nullable C c);
}