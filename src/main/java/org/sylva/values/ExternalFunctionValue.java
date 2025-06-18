package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.ExternalFunction;
import org.sylva.Value;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Result;

import java.util.List;

public record ExternalFunctionValue(@Nullable String name, @NotNull ExternalFunction function) implements Value {
    @Override
    public @NotNull String typeName() {
        return "function";
    }

    @Override
    public @NotNull Result<Value, SylvaError> call(@NotNull List<Value> arguments) {
        return function.call(arguments);
    }

    @Override
    public @NotNull String toString() {
        return "<external function" + (name != null ? " " + name : "") +">";
    }
}
