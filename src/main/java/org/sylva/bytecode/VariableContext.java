package org.sylva.bytecode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.Value;
import org.sylva.errors.SylvaError;
import org.sylva.errors.errors.GeneralError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

import java.util.HashMap;
import java.util.Map;

public class VariableContext {
    private final Map<Integer, Value> variables = new HashMap<>();
    private final Map<Integer, VariableContext> contextCache = new HashMap<>();
    public final @Nullable VariableContext parent;

    public void setVar(int id, @NotNull Value value) {
        variables.put(id, value);
    }

    public @NotNull Result<Value, SylvaError> getVar(int id) {
        if (!variables.containsKey(id)) {
            return new Err<>(new GeneralError("Variable with id " + id + " does not exist"));
        }
        return new Ok<>(variables.get(id));
    }

    public @NotNull Result<@Nullable Object, SylvaError> setVar(int id, @NotNull Value value, int level) {
        var res = getContext(level);
        if (res.isError()) {
            return new Err<>(res.error());
        }
        res.ok().setVar(id, value);
        return new Ok<>(null);
    }

    public @NotNull Result<Value, SylvaError> getVar(int id, int level) {
        var res = getContext(level);
        if (res.isError()) {
            return new Err<>(res.error());
        }
        return res.ok().getVar(id);
    }

    public VariableContext(@NotNull VariableContext parent) {
        this.parent = parent;
    }

    public VariableContext() {
        this.parent = null;
    }

    public Result<VariableContext, SylvaError> getContext(int stepAmount) {
        if (stepAmount == 0) {
            return new Ok<>(this);
        }

        if (contextCache.containsKey(stepAmount)) {
            return new Ok<>(contextCache.get(stepAmount));
        }

        var ctx = this;
        for (int i = 0; i < stepAmount; i++) {
            ctx = ctx.parent;
            if (ctx == null) {
                return new Err<>(new GeneralError("there is no upper parent that many levels up"));
            }
        }

        contextCache.put(stepAmount, ctx);

        return new Ok<>(ctx);
    }
}
