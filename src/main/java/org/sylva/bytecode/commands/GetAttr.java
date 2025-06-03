package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;
import org.sylva.values.StringValue;

public record GetAttr(@NotNull String attrName) implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        var res = manager.popValue().get(new StringValue(attrName));
        if (res.isError()) {
            return new Err<>(res.error());
        }
        manager.pushValue(res.ok());
        return new Ok<>(null);
    }
}
