package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

public record Call() implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        // temp
        var newIP = manager.getInstructionPointer() + 1;
        var v = manager.popValue();
        var res = v.call();

        if (res.isError()) {
            return new Err<>(res.error());
        }
        manager.pushValue(res.ok());

        manager.goTo(newIP);

        return new Ok<>(null);
    }
}
