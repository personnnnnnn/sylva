package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.sylva.Value;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

public class Nil implements Value {
    @Override
    public @NotNull String typeName() {
        return "nil";
    }

    @Override
    public @NotNull Result<Boolean, SylvaError> bool() {
        return new Ok<>(false);
    }
}
