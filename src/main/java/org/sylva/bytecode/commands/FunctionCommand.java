package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;
import org.sylva.values.FunctionValue;
import org.sylva.values.Nil;

public record FunctionCommand(int next) implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var nameValue = manager.popValue();
        var name = nameValue instanceof Nil ? null : nameValue.toString();
        manager.pushValue(new FunctionValue(name, manager.getInstructionPointer() + 1, manager));
        manager.goTo(next);
        return new Ok<>(null);
    }
}
