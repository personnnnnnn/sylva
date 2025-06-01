package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.sylva.Value;

import java.util.List;

public record ArrayValue(List<Value> items) implements Value {
    @Override
    public @NotNull String typeName() {
        return "array";
    }
}
