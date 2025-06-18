package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.Value;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;
import org.sylva.values.Limit;
import org.sylva.values.ArrayValue;

import java.util.ArrayList;
import java.util.List;

public record SpreadArg(@NotNull String name, int varId) implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        List<Value> arguments = new ArrayList<>();

        while (true) {
            var arg = manager.popValue();
            if (arg instanceof Limit) {
                break;
            }
            arguments.add(arg);
        }

        var list = new ArrayValue(arguments);
        manager.getVariableContext().setVar(varId, list);

        return new Ok<>(null);
    }
}
