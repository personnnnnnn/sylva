package org.sylva.bytecode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Result;

public interface Command {
    @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager);
}
