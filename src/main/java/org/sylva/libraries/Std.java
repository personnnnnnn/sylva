package org.sylva.libraries;

import org.sylva.util.results.Ok;
import org.sylva.values.ExternalLibrary;
import org.sylva.values.Nil;

public class Std {
    public static final ExternalLibrary std = new ExternalLibrary("std");

    static {
        std.addFunction("print", (args) -> {
            for (var arg : args) {
                System.out.println(arg);
            }
            return new Ok<>(new Nil());
        });
    }
}
