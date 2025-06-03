package org.sylva.bytecode.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.bytecode.Command;
import org.sylva.bytecode.StateManager;
import org.sylva.errors.SylvaError;
import org.sylva.errors.errors.LibraryError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

public record GetLibrary(@NotNull String libraryName) implements Command {
    @Override
    public @NotNull Result<@Nullable Object, SylvaError> run(@NotNull StateManager manager) {
        if (!manager.libraries.containsKey(libraryName)) {
            return new Err<>(new LibraryError("There is no library '" + libraryName + "'"));
        }
        manager.pushValue(manager.libraries.get(libraryName));
        return new Ok<>(null);
    }
}
