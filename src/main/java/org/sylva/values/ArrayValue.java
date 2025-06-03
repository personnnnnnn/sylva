package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.sylva.Value;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

import java.util.Iterator;
import java.util.List;

public record ArrayValue(List<Value> items) implements Value {
    @Override
    public @NotNull String typeName() {
        return "array";
    }

    @Override
    public @NotNull Result<Iterator<Value>, SylvaError> iter() {
        return new Ok<>(items.iterator());
    }
}
