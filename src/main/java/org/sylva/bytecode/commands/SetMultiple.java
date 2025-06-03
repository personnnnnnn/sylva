package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.Value;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.errors.errors.UnpackingError;
import org.sylva.util.Pair;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

import java.util.ArrayList;
import java.util.List;

public record SetMultiple(List<Pair<Integer, Integer>> variables) implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var iterable = manager.popValue();
        var result = iterable.iter();
        if (result.isError()) {
            return new Err<>(result.error());
        }
        var iterator = result.ok();

        var items = new ArrayList<Value>();
        iterator.forEachRemaining(items::add);

        if (items.size() != variables.size()) {
            if (items.size() < variables.size()) {
                return new Err<>(new UnpackingError("Got too little values to unpack (expected " + variables.size() + ", got " + items.size() + ")"));
            } else {
                return new Err<>(new UnpackingError("Got too many values to unpack (expected " + variables.size() + ", got " + items.size() + ")"));
            }
        }

        for (var i = 0; i < variables.size(); i++) {
            var variable = variables.get(i);
            var value = items.get(i);
            manager.getVariableContext().setVar(variable.a, value, variable.b);
        }

        return new Ok<>(null);
    }
}
