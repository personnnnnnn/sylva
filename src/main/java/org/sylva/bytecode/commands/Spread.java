package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

public record Spread() implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var iterable = manager.popValue();
        var result = iterable.iter();
        if (result.isError()) {
            return new Err<>(result.error());
        }
        var iterator = result.ok();

        while (iterator.hasNext()) {
            var v = iterator.next();
            manager.pushValue(v);
        }

        return new Ok<>(null);
    }
}
