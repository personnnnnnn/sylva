package org.sylva.errors.errors;

import org.jetbrains.annotations.NotNull;
import org.sylva.errors.SylvaError;

public class UnsupportedUnaryOpError implements SylvaError {
    private final @NotNull String val, op;
    public UnsupportedUnaryOpError(
            @NotNull String val,
            @NotNull String op
    ) {
        this.val = val;
        this.op = op;
    }

    @Override
    public @NotNull String type() {
        return "UnsupportedOperationError";
    }

    @Override
    public @NotNull String message() {
        return "can't perform operation '"
                + op + "' on type '"
                + val + "'";
    }
}
