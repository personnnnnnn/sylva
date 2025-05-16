package org.sylva.errors;

import org.jetbrains.annotations.NotNull;

public interface SylvaError {
    @NotNull String message();

    default @NotNull String type() {
        return getClass().getSimpleName();
    }

    default @NotNull String fullMessage() {
        return type() + ": " + message();
    }
}
