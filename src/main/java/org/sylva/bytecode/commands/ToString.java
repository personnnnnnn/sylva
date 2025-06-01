package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;
import org.sylva.values.StringValue;

public record ToString() implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var value = manager.popValue();
        manager.pushValue(new StringValue(value.toString()));
        return new Ok<>(null);
    }
}
