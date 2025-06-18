package org.sylva.bytecode;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.sylva.bytecode.commands.*;
import org.sylva.bytecode.commands.Set;
import org.sylva.generated.SylvaBytecodeParser;
import org.sylva.util.Pair;

import java.util.*;

public class StateCreator {
    private Map<String, Integer> codeLocations;
    private Stack<HashMap<String, Integer>> variableContexts;
    private ArrayList<Command> commands;
    private int childIndex;

    public @NotNull StateManager stateManagerFrom(SylvaBytecodeParser.@NotNull ProgramContext program) {
        List<SylvaBytecodeParser.CommandContext> children = program.command();

        codeLocations = new HashMap<>();
        commands = new ArrayList<>();
        childIndex = 0;
        variableContexts = new Stack<>();

        storeCodeLocations(children);
        loopOverCommands(children);
        return new StateManager(commands);
    }

    private void storeCodeLocations(@NotNull List<SylvaBytecodeParser.CommandContext> commands) {
        for (var child : commands) {
            if (child instanceof SylvaBytecodeParser.LocationContext location) {
                String id = location.ID().getText();
                codeLocations.put(id, childIndex);
            } else if (child instanceof SylvaBytecodeParser.VariableManagerContext managerContext) {
                storeCodeLocations(managerContext.command());
            } else {
                childIndex++;
            }
        }
    }

    @Contract("_ -> new")
    private @NotNull Pair<Integer, Integer> getRelativeVariableLocation(@NotNull String name) {
        int depth = 0;
        int id = 0;
        int i = variableContexts.size() - 1;
        while (i >= 0) {
            var ctx = variableContexts.get(i);
            if (ctx.containsKey(name)) {
                id = ctx.get(name);
                break;
            }
            depth++;
            i--;
        }
        return new Pair<>(id, depth);
    }

    private @NotNull Pair<Integer, Integer> getVariableLocation(@NotNull SylvaBytecodeParser.VaridContext ctx) {
        if (ctx.ID() != null) {
            return getRelativeVariableLocation(ctx.ID().getText());
        }
        return new Pair<>(Integer.parseInt(ctx.INTEGER(0).getText()), Integer.parseInt(ctx.INTEGER(1).getText()));
    }

    private void loopOverCommands(@NotNull List<SylvaBytecodeParser.CommandContext> commands) {
        for (var child : commands) {
            if (child instanceof SylvaBytecodeParser.LocationContext) {
                continue;
            }
            if (child instanceof SylvaBytecodeParser.VariableManagerContext ctx) {
                var newContext = new HashMap<String, Integer>();
                variableContexts.push(newContext);
                var varId = 0;
                for (var variable : ctx.ID()) {
                    newContext.put(variable.getText(), varId++);
                }
                loopOverCommands(ctx.command());
                variableContexts.pop();
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
                    var loc = getVariableLocation(setContext.varid());
                    yield new Set(loc.a, loc.b);
                }
                case SylvaBytecodeParser.GetContext getContext -> {
                    var loc = getVariableLocation(getContext.varid());
                    yield new Get(loc.a, loc.b);
                }
                case SylvaBytecodeParser.LimitContext ignored -> new Limit();
                case SylvaBytecodeParser.NoMoreArgumentsContext ignored -> new NoMoreArguments();
                case SylvaBytecodeParser.NeededArgContext neededArgContext -> new NeededArg(neededArgContext.STRING().getText().replaceAll("\"", ""), getVariableLocation(neededArgContext.varid()).a);
                case SylvaBytecodeParser.SpreadArgContext spreadArgContext -> new SpreadArg(spreadArgContext.STRING().getText().replaceAll("\"", ""), getVariableLocation(spreadArgContext.varid()).a);
                case SylvaBytecodeParser.OptionalArgContext optionalArgContext -> {
                    var loc = optionalArgContext.codelocation().ID() == null
                            ? Integer.parseInt(optionalArgContext.codelocation().INTEGER().getText())
                            : codeLocations.get(optionalArgContext.codelocation().ID().getText());
                    var id = getVariableLocation(optionalArgContext.varid()).a;
                    yield new OptionalArg(optionalArgContext.STRING().getText().replaceAll("\"", ""), id, loc);
                }
                case SylvaBytecodeParser.SetMultipleContext setMultipleContext -> {
                    ArrayList<Pair<Integer, Integer>> variables = new ArrayList<>();
                    for (var variableDefinition : setMultipleContext.varid()) {
                        var varData = getVariableLocation(variableDefinition);
                        variables.add(varData);
                    }

                    yield new SetMultiple(variables);
                }
                case SylvaBytecodeParser.SpreadContext ignored -> new Spread();
                case SylvaBytecodeParser.GetLibraryContext getLibraryContext -> new GetLibrary(getLibraryContext.STRING().getText().replaceAll("^\"|\"$", ""));
                case SylvaBytecodeParser.GetAttrContext getAttrContext -> new GetAttr(getAttrContext.STRING().getText().replaceAll("^\"|\"$", ""));
                case SylvaBytecodeParser.SpreadIntoContext spreadIntoContext -> new SpreadInto(Integer.parseInt(spreadIntoContext.INTEGER().getText()));
                default -> throw new IllegalStateException("Unexpected value: " + child);
            };
            this.commands.add(append);
            childIndex++;
        }
    }
}
