package org.sylva.bytecode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sylva.Value;
import org.sylva.bytecode.commands.*;
import org.sylva.bytecode.commands.Set;
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
        var children = program.command();

        Map<String, Integer> codeLocations = new HashMap<>();

        var commands = new ArrayList<Command>();

        var childIndex = 0;
        for (var child : children) {
            if (child instanceof SylvaBytecodeParser.LocationContext location) {
                String id = location.ID().getText();
                codeLocations.put(id, childIndex);
            } else {
                childIndex++;
            }
        }

        for (var child : children) {
            if (child instanceof SylvaBytecodeParser.LocationContext) {
                continue;
            }
            var append = switch (child) {
                case SylvaBytecodeParser.AddContext ignored -> new Add();
                case SylvaBytecodeParser.SubContext ignored -> new Sub();
                case SylvaBytecodeParser.MulContext ignored -> new Mul();
                case SylvaBytecodeParser.DivContext ignored -> new Div();
                case SylvaBytecodeParser.ModContext ignored -> new Mod();
                case SylvaBytecodeParser.CallContext ignored -> new Call();
                case SylvaBytecodeParser.UmnContext ignored -> new Umn();
                case SylvaBytecodeParser.ToStringContext ignored -> new ToString();
                case SylvaBytecodeParser.RemContext ignored -> new Rem();
                case SylvaBytecodeParser.ReturnContext ignored -> new ReturnCommand();
                case SylvaBytecodeParser.FltContext fltContext -> new Flt(Double.parseDouble(fltContext.FLOAT().getText()));
                case SylvaBytecodeParser.SkipContext ignored -> new Skip();
                case SylvaBytecodeParser.NilContext ignored -> new NilCommand();
                case SylvaBytecodeParser.IntContext intContext -> new Int(Integer.parseInt(intContext.INTEGER().getText()));
                case SylvaBytecodeParser.StrContext strContext -> new Str(
                        strContext.STRING()
                                .getText()
                                .replaceAll("^\"|\"$", "")
                                .translateEscapes() // thanks java :)
                );
                case SylvaBytecodeParser.NopContext ignored -> new Noop();
                case SylvaBytecodeParser.DupContext ignored -> new Dup();
                case SylvaBytecodeParser.JmpContext jmpContext -> {
                    var loc = jmpContext.codelocation().ID() == null
                            ? Integer.parseInt(jmpContext.codelocation().INTEGER().getText())
                            : codeLocations.get(jmpContext.codelocation().ID().getText());
                    yield new Jump(loc);
                }
                case SylvaBytecodeParser.FunctionContext functionContext -> {
                    var loc = functionContext.codelocation().ID() == null
                            ? Integer.parseInt(functionContext.codelocation().INTEGER().getText())
                            : codeLocations.get(functionContext.codelocation().ID().getText());
                    yield new FunctionCommand(loc);
                }
                case SylvaBytecodeParser.PushContext ignored -> new Push();
                case SylvaBytecodeParser.PopContext ignored -> new Pop();
                case SylvaBytecodeParser.SetContext setContext -> {
                    var id = Integer.parseInt(setContext.INTEGER().getFirst().getText());
                    var level = Integer.parseInt(setContext.INTEGER().getLast().getText());
                    yield new Set(id, level);
                }
                case SylvaBytecodeParser.GetContext getContext -> {
                    var id = Integer.parseInt(getContext.INTEGER().getFirst().getText());
                    var level = Integer.parseInt(getContext.INTEGER().getLast().getText());
                    yield new Get(id, level);
                }
                default -> throw new IllegalStateException("Unexpected value: " + child);
            };
            commands.add(append);
            childIndex++;
        }

        return new StateManager(commands);
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
