package org.sylva.errors.errors;

import org.jetbrains.annotations.NotNull;

public class UnpackingError extends GeneralError {
    public UnpackingError(@NotNull String message) {
        super(message);
    }
}
