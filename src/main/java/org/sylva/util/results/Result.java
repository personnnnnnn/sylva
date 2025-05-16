package org.sylva.util.results;

import org.jetbrains.annotations.NotNull;

public interface Result<T, E> {
    @NotNull T ok();
    @NotNull E error();

    default boolean isOk() {
        return this instanceof Ok<T,E>;
    }

    default boolean isError() {
        return this instanceof Err<T,E>;
    }
}
