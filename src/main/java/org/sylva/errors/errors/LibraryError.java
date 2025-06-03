package org.sylva.errors.errors;

import org.jetbrains.annotations.NotNull;

public class LibraryError extends GeneralError {
    public LibraryError(@NotNull String message) {
        super(message);
    }
}
