package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.Value;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;
import org.sylva.values.Limit;

import java.util.ArrayList;
import java.util.List;

public record Call() implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var newIP = manager.getInstructionPointer() + 1;
        var v = manager.popValue();

        List<Value> arguments = new ArrayList<>();

        while (true) {
            var poppedValue = manager.popValue();
            if (poppedValue instanceof Limit) {
                break;
            }
            arguments.add(poppedValue);
        }

        var res = v.call(arguments.reversed());

        if (res.isError()) {
            return new Err<>(res.error());
        }
        manager.pushValue(res.ok());

        manager.goTo(newIP);

        return new Ok<>(null);
    }
}
