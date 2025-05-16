package org.sylva.values;

import org.jetbrains.annotations.NotNull;
import org.sylva.Value;
import org.sylva.errors.SylvaError;
import org.sylva.errors.errors.MathError;
import org.sylva.util.results.Err;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

public record FloatValue(double n) implements Value {
    @Override
    public @NotNull String typeName() {
        return "int";
    }

    @Override
    public @NotNull String toString() {
        return n + "";
    }

    @Override
    public @NotNull Result<Boolean, SylvaError> bool() {
        return new Ok<>(n != 0);
    }

    @Override
    public @NotNull Result<Value, SylvaError> addValue(@NotNull Value other) {
        if (other instanceof IntValue(int n1)) {
            return new Ok<>(new FloatValue(n + n1));
        }
        if (other instanceof FloatValue(double f)) {
            return new Ok<>(new FloatValue(n + f));
        }
        return Value.super.addValue(other);
    }

    @Override
    public @NotNull Result<Value, SylvaError> subValue(@NotNull Value other) {
        if (other instanceof IntValue(int n1)) {
            return new Ok<>(new FloatValue(n - n1));
        }
        if (other instanceof FloatValue(double f)) {
            return new Ok<>(new FloatValue(n - f));
        }
        return Value.super.addValue(other);
    }

    @Override
    public @NotNull Result<Value, SylvaError> mulValue(@NotNull Value other) {
        if (other instanceof IntValue(int n1)) {
            return new Ok<>(new FloatValue(n * n1));
        }
        if (other instanceof FloatValue(double f)) {
            return new Ok<>(new FloatValue(n * f));
        }
        return Value.super.addValue(other);
    }

    @Override
    public @NotNull Result<Value, SylvaError> divValue(@NotNull Value other) {
        if (other instanceof IntValue(int n1)) {
            if (n1 == 0) {
                return new Err<>(new MathError("Cannot divide by 0"));
            }
            return new Ok<>(new FloatValue(n / n1));
        }
        if (other instanceof FloatValue(double f)) {
            if (f == 0) {
                return new Err<>(new MathError("Cannot divide by 0"));
            }
            return new Ok<>(new FloatValue(n / f));
        }
        return Value.super.addValue(other);
    }

    @Override
    public @NotNull Result<Value, SylvaError> modValue(@NotNull Value other) {
        if (other instanceof IntValue(int n1)) {
            if (n1 == 0) {
                return new Err<>(new MathError("Cannot mod by 0"));
            }
            return new Ok<>(new FloatValue(n % n1));
        }
        if (other instanceof FloatValue(double f)) {
            if (f == 0) {
                return new Err<>(new MathError("Cannot mod by 0"));
            }
            return new Ok<>(new FloatValue(n % f));
        }
        return Value.super.addValue(other);
    }

    @Override
    public @NotNull Result<Value, SylvaError> powValue(@NotNull Value other) {
        if (other instanceof IntValue(int n1)) {
            return new Ok<>(new FloatValue((int) Math.pow(n, n1)));
        }
        if (other instanceof FloatValue(double f)) {
            return new Ok<>(new FloatValue(Math.pow(n, f)));
        }
        return Value.super.addValue(other);
    }

    @Override
    public @NotNull Result<Value, SylvaError> umn() {
        return new Ok<>(new FloatValue(-n));
    }
}
