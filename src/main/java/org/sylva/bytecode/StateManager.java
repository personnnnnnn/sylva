package org.sylva.bytecode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.Value;
import org.sylva.errors.SylvaError;
import org.sylva.generated.SylvaBytecodeParser;
import org.sylva.util.results.Ok;
import org.sylva.util.results.Result;

import java.util.*;

public class StateManager {
    private int instructionPointer = 0;
    private final @NotNull List<Command> commands;
    private boolean moved = false;
    private @NotNull VariableContext variableContext = new VariableContext();

    public int getInstructionPointer() {
        return instructionPointer;
    }

    public static @NotNull StateManager from(SylvaBytecodeParser.@NotNull ProgramContext program) {
        return new StateCreator().stateManagerFrom(program);
    }

    private final @NotNull Stack<Value> valueStack = new Stack<>();
    private final @NotNull Stack<Integer> callStack = new Stack<>();

    public @NotNull Stack<Integer> getCallStack() {
        return callStack;
    }

    public void goSub(int loc) {
        callStack.push(instructionPointer);
        instructionPointer = loc;
    }

    public void ret() {
        instructionPointer = callStack.pop();
    }

    public @NotNull Stack<Value> getValueStack() {
        return valueStack;
    }

    public void pushValue(@NotNull Value value) {
        valueStack.push(value);
    }

    public @NotNull Value popValue() {
        return valueStack.pop();
    }

    public @NotNull Value peekValue() {
        return valueStack.peek();
    }

    public StateManager(@NotNull List<Command> commands) {
        this.commands = commands;
    }

    public void goTo(int codeLocation) {
        instructionPointer = codeLocation;
        moved = true;
    }

    public void forward(int amt) {
        instructionPointer += amt;
        moved = true;
    }

    public @NotNull Command getCurrentCommand() {
        return commands.get(instructionPointer);
    }

    public boolean isDone() {
        return instructionPointer >= commands.size();
    }

    public @NotNull Result<@Nullable Object, SylvaError> step() {
        if (isDone()) {
            return new Ok<>(null);
        }
        var command = getCurrentCommand();
        var res = command.run(this);
        if (moved) {
            moved = false;
        } else {
            instructionPointer++;
        }
        return res;
    }

    public @NotNull VariableContext getVariableContext() {
        return variableContext;
    }

    public void pushContext() {
        variableContext = new VariableContext(variableContext);
    }

    public void popContext() {
        if (variableContext.parent != null) {
            variableContext = variableContext.parent;
        }
    }
}
