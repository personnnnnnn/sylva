package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.Value;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;
import org.sylva.values.ArrayValue;
import org.sylva.values.Limit;

import java.util.ArrayList;

public record ArrayCommand() implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        ArrayList<Value> items = new ArrayList<>();
        while (true) {
            var poppedValue = manager.popValue();
            if (poppedValue instanceof Limit) {
                break;
            }
            items.add(poppedValue);
        }
        manager.pushValue(new ArrayValue(items.reversed()));
        return new Ok<>(null);
    }
}
