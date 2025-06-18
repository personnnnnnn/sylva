package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.errors.errors.ArgumentError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;
import org.sylva.values.Limit;

public record NoMoreArguments() implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var arg = manager.peekValue();
        if (!(arg instanceof Limit)) {
            return new Err<>(new ArgumentError("Got too many arguments"));
        }
        manager.popValue();
        return new Ok<>(null);
    }
}
