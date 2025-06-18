package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.Value;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

import java.util.List;

public record FunctionValue(@Nullable String name, int codeLocation, @NotNull StateManager manager) implements Value {
    @Override
    public @NotNull String typeName() {
        return "function";
    }

    @Override
    public @NotNull Result<Value, SylvaError> call(@NotNull List<Value> arguments) {
        int calledAt = manager.getCallStack().size();
        manager.goSub(codeLocation);

        manager.pushValue(new Limit());
        for (var argument : arguments.reversed()) {
            manager.pushValue(argument);
        }

        while (!manager.isDone() && manager.getCallStack().size() != calledAt) {
            var res = manager.step();
            if (res.isError()) {
                return new Err<>(res.error());
            }
        }

        return new Ok<>(manager.popValue());
    }

    @Override
    public @NotNull String toString() {
        return "<function" + (name != null ? " " + name : "") +">";
    }
}
