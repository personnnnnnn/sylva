package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.sylva.Value;

public class ArgumentDelimiter implements Value {
    @Override
    public @NotNull String typeName() {
        return "argument delimiter";
    }
}
