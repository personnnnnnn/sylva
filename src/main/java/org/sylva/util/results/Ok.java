package org.sylva.util.results;

import org.jetbrains.annotations.NotNull;

public record Ok<T, E>(T value) implements Result<T, E> {
    @Override
    public @NotNull T ok() {
        return value;
    }

    @Override
    public @NotNull E error() {
        throw new RuntimeException("Value was not error");
    }
}
