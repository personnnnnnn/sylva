package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;
import org.sylva.values.Limit;

public record OptionalArg(@NotNull String name, int varId, int goTo) implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var arg = manager.peekValue();

        if (!(arg instanceof Limit)) {
            manager.getVariableContext().setVar(varId, manager.popValue());
            manager.goTo(goTo);
        }

        return new Ok<>(null);
    }
}
