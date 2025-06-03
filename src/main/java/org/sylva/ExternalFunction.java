package org.sylva;

import org.jetbrains.annotations.NotNull;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Result;

import java.util.List;

public interface ExternalFunction {
    @NotNull Result<Value, SylvaError> call(List<Value> arguments);
}
