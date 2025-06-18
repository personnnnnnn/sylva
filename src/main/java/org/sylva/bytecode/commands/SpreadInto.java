package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.errors.errors.UnpackingError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

public record SpreadInto(int count) implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var iterable = manager.popValue();
        var result = iterable.iter();
        if (result.isError()) {
            return new Err<>(result.error());
        }
        var iterator = result.ok();

        int itemCount = 0;
        while (iterator.hasNext()) {
            var v = iterator.next();
            manager.pushValue(v);
            itemCount++;
        }

        if (itemCount != count) {
            if (itemCount < count) {
                return new Err<>(new UnpackingError("Got too little values to unpack (expected " + count + ", got " + itemCount + ")"));
            } else {
                return new Err<>(new UnpackingError("Got too many values to unpack (expected " + count + ", got " + itemCount + ")"));
            }
        }

        return new Ok<>(null);
    }
}
