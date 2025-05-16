package org.sylva.util.results;

import org.jetbrains.annotations.NotNull;

public final class Err<T, E> implements Result<T, E> {
    public @NotNull E error;
    public Err(@NotNull E error) {
        this.error = error;
    }

    @Override
    public @NotNull T ok() {
        throw new RuntimeException("Value was not OK");
    }

    @Override
    public @NotNull E error() {
        return error;
    }
}
