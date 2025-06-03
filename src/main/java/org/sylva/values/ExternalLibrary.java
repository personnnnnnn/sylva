package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.ExternalFunction;
import org.sylva.Value;

import java.util.HashMap;

public class ExternalLibrary implements Value {
    public final @NotNull String name;
    public final @NotNull HashMap<String, Value> values = new HashMap<>();

    public ExternalLibrary(@NotNull String name) {
        this.name = name;
    }

    @Override
    public @NotNull String typeName() {
        return "library";
    }

    public void addValue(@NotNull String name, @Nullable Value value) {
        values.put(name, value);
    }

    public void addSubLibrary(@NotNull ExternalLibrary library) {
        addValue(library.name, library);
    }

    public void addFunction(@NotNull String name, @NotNull ExternalFunction function) {
        addValue(name, new ExternalFunctionValue(name, function));
    }
}
