package org.sylva.errors.errors;

import org.jetbrains.annotations.NotNull;

public class ArgumentError extends GeneralError {
    public ArgumentError(@NotNull String message) {
        super(message);
    }
}
