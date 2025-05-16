package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

public record Skip() implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var res = manager.popValue().bool();
        if (res.isError()) {
            return new Err<>(res.error());
        }

        if (res.ok()) {
            // skip next command
            manager.forward(2);
        }

        return new Ok<>(null);
    }
}
