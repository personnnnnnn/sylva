package org.sylva.errors.errors;

import org.jetbrains.annotations.NotNull;
import org.sylva.errors.SylvaError;

public class UnsupportedBinOpError implements SylvaError {
    private final @NotNull String left, right, op;
    public UnsupportedBinOpError(
            @NotNull String left,
            @NotNull String right,
            @NotNull String op
    ) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public @NotNull String type() {
        return "UnsupportedOperationError";
    }

    @Override
    public @NotNull String message() {
        return "can't perform operation '"
                + op + "' on types '"
                + left + "' and '"
                + right + "'";
    }
}
