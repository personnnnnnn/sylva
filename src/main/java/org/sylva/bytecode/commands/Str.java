package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;
import org.sylva.values.StringValue;

public record Str(@NotNull String s) implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        manager.pushValue(new StringValue(s));
        return new Ok<>(null);
    }
}
