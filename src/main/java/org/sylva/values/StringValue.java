package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.sylva.Value;

public record StringValue(@NotNull String s) implements Value {
    @Override
    public @NotNull String typeName() {
        return "string";
    }

    @Override
    public @NotNull String toString() {
        return s;
    }
}
