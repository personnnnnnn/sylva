package org.sylva.errors.errors;

import org.jetbrains.annotations.NotNull;
import org.sylva.errors.SylvaError;

public class GeneralError implements SylvaError {
    private final @NotNull String message;

    public GeneralError(@NotNull String message) {
        this.message = message;
    }

    @Override
    public @NotNull String message() {
        return message;
    }
}
