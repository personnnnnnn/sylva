package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.Value;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

public record FunctionValue(@Nullable String name, int codeLocation, @NotNull StateManager manager) implements Value {
    @Override
    public @NotNull String typeName() {
        return "function";
    }

    @Override
    public @NotNull Result<Value, SylvaError> call() {
        int calledAt = manager.getCallStack().size();
        manager.goSub(codeLocation);
        while (!manager.isDone() && manager.getCallStack().size() != calledAt) {
            var res = manager.step();
            if (res.isError()) {
                return new Err<>(res.error());
            }
        }
        return new Ok<>(manager.popValue());
    }
}
