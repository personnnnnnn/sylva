package org.sylva;

import org.jetbrains.annotations.NotNull;
import org.sylva.errors.SylvaError;
import org.sylva.errors.errors.UnsupportedBinOpError;
import org.sylva.errors.errors.UnsupportedUnaryOpError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

import java.util.Iterator;
import java.util.List;

public interface Value {
    @NotNull String typeName();

    default @NotNull Result<Boolean, SylvaError> bool() {
        return new Ok<>(true);
    }

    default @NotNull Result<Value, SylvaError> addValue(@NotNull Value other) {
        return new Err<>(new UnsupportedBinOpError(typeName(), other.typeName(), "+"));
    }

    default @NotNull Result<Value, SylvaError> subValue(@NotNull Value other) {
        return new Err<>(new UnsupportedBinOpError(typeName(), other.typeName(), "-"));
    }

    default @NotNull Result<Value, SylvaError> mulValue(@NotNull Value other) {
        return new Err<>(new UnsupportedBinOpError(typeName(), other.typeName(), "*"));
    }

    default @NotNull Result<Value, SylvaError> divValue(@NotNull Value other) {
        return new Err<>(new UnsupportedBinOpError(typeName(), other.typeName(), "/"));
    }

    default @NotNull Result<Value, SylvaError> modValue(@NotNull Value other) {
        return new Err<>(new UnsupportedBinOpError(typeName(), other.typeName(), "%"));
    }

    default @NotNull Result<Value, SylvaError> powValue(@NotNull Value other) {
        return new Err<>(new UnsupportedBinOpError(typeName(), other.typeName(), "math.pow"));
    }

    default @NotNull Result<Value, SylvaError> umn() {
        return new Err<>(new UnsupportedUnaryOpError(typeName(), "unary -"));
    }

    default @NotNull Result<Value, SylvaError> call(@NotNull List<Value> arguments) {
        return new Err<>(new UnsupportedUnaryOpError(typeName(), "function call"));
    }

    default @NotNull Result<Iterator<Value>, SylvaError> iter() {
        return new Err<>(new UnsupportedUnaryOpError(typeName(), "iter"));
    }

    default @NotNull Result<Value, SylvaError> get(@NotNull Value attribute) {
        return new Err<>(new UnsupportedUnaryOpError(typeName(), "get attribute"));
    }

    default @NotNull Result<Value, SylvaError> set(@NotNull Value attribute, @NotNull Value value) {
        return new Err<>(new UnsupportedUnaryOpError(typeName(), "set attribute"));
    }

    default @NotNull Result<Value, SylvaError> getIndex(@NotNull Value index) {
        return new Err<>(new UnsupportedUnaryOpError(typeName(), "get index"));
    }

    default @NotNull Result<Value, SylvaError> setIndex(@NotNull Value index, @NotNull Value value) {
        return new Err<>(new UnsupportedUnaryOpError(typeName(), "set index"));
    }
}
