package org.sylva;

import com.sun.jdi.event.StepEvent;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.NotNull;
import org.sylva.generated.SylvaBaseVisitor;
import org.sylva.generated.SylvaParser;

import java.util.HashSet;
import java.util.Stack;

public class BytecodeGenerator extends SylvaBaseVisitor<String> {
    private int labelIndex = 0;
    public int getNewLabelID() {
        return labelIndex++;
    }

    private Stack<HashSet<String>> variableIDStack = new Stack<>();
    private void pushVariableIDStack() {
        variableIDStack.push(new HashSet<>());
    }
    private void popVariableIDStack() {
        variableIDStack.pop();
    }
    private void createVariable(@NotNull String name) {
        variableIDStack.peek().add(name);
    }
    private @NotNull String getVariableIDStackRepresentation() {
        StringBuilder str = new StringBuilder();
        var prefix = "";

        for (var variable : variableIDStack.peek()) {
            str.append(prefix).append(variable);
            prefix = " ";
        }

        return str.toString();
    }

    @Override
    public String visitIntValue(SylvaParser.@NotNull IntValueContext ctx) {
        var intStr = ctx.INT().getText();
        var str = "";
        str += "INT(" + intStr + ")\n";
        return str;
    }

    @Override
    public String visitFloatValue(SylvaParser.@NotNull FloatValueContext ctx) {
        var floatStr = ctx.FLOAT().getText();
        if (floatStr.charAt(0) == '.') {
            floatStr = '0' + floatStr;
        }
        if (floatStr.charAt(floatStr.length() - 1) == '.') {
            floatStr += '0';
        }
        var str = "";
        str += "FLT(" + floatStr + ")\n";
        return str;
    }

    @Override
    public String visitStringValue(SylvaParser.@NotNull StringValueContext ctx) {
        var string = ctx.STRING().getText();
        string = string.replaceAll("^['\"]|['\"]$", "");
        int backSlashCount = 0;
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\"' || string.charAt(i) == '\'') {
                if (backSlashCount % 2 == 0) {
                    newString.append('\\');
                }
            } else if (string.charAt(i) == '\\') {
                backSlashCount++;
            } else {
                backSlashCount = 0;
            }
            newString.append(string.charAt(i));
        }
        var str = "";
        str += "STR(\"" + newString + "\")\n";
        return str;
    }

    @Override
    public String visitMultilineStringValue(SylvaParser.@NotNull MultilineStringValueContext ctx) {
        var string = ctx.MULTILINESTRING().getText();
        string = string.replaceAll("^['\"]{3}|['\"]{3}$", "");
        int backSlashCount = 0;
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\"' || string.charAt(i) == '\'') {
                if (backSlashCount % 2 == 0) {
                    newString.append('\\');
                }
            }

            if (string.charAt(i) == '\\') {
                backSlashCount++;
            } else {
                backSlashCount = 0;
            }

            if (string.charAt(i) != '\n') {
                newString.append(string.charAt(i));
            } else {
                newString.append("\\n");
            }
        }
        var str = "";
        str += "STR(\"" + newString + "\")\n";
        return str;
    }

    @Override
    public String visitTrueValue(SylvaParser.TrueValueContext ctx) {
        return "TRUE\n";
    }

    @Override
    public String visitFalseValue(SylvaParser.FalseValueContext ctx) {
        return "FALSE\n";
    }

    @Override
    public String visitNilValue(SylvaParser.NilValueContext ctx) {
        return "NIL\n";
    }

    @Override
    public String visitAttributeValue(SylvaParser.@NotNull AttributeValueContext ctx) {
        var str = "";
        str += visit(ctx.value());
        str += "GET_ATTR(\"" + ctx.ID().getText() + "\")\n";
        return str;
    }

    @Override
    public String visitIndexValue(SylvaParser.@NotNull IndexValueContext ctx) {
        var str = "";
        str += visit(ctx.value());
        str += "GET_IDX\n";
        return str;
    }

    @Override
    public String visitCallExpr(SylvaParser.@NotNull CallExprContext ctx) {
        StringBuilder str = new StringBuilder();
        str.append("ARGUMENTS\n");

        boolean isSpread = false;
        for (var child : ctx.callargs().children) {
            if (child instanceof TerminalNode terminalNode && terminalNode.getSymbol().getType() == SylvaParser.TDOT) {
                isSpread = true;
                continue;
            }
            if (child instanceof SylvaParser.ExprContext exprContext) {
                str.append(visit(exprContext));
                if (isSpread) {
                    str.append("SPREAD\n");
                }
            }
            isSpread = false;
        }

        str.append(visit(ctx.value()));
        str.append("CALL\n");

        return str.toString();
    }

    @Override
    public String visitValueExpr(SylvaParser.@NotNull ValueExprContext ctx) {
        return visit(ctx.value());
    }

    @Override
    public String visitBodyEval(SylvaParser.@NotNull BodyEvalContext ctx) {
        var str = visit(ctx.genbody());
        if ((!str.matches("RETURN\\s*$") || !str.matches("JMP\\(.*\\)\\s*$")) || str.matches("SKP\\s+JMP\\(.*\\)\\s*$")) {
            str += "NIL\n";
            str += "RETURN\n";
        }
        return str;
    }

    @Override
    public String visitExprEval(SylvaParser.@NotNull ExprEvalContext ctx) {
        var str = "";
        pushVariableIDStack();
        var contents = visit(ctx.expr());
        var representation = getVariableIDStackRepresentation();
        popVariableIDStack();
        contents += "RETURN\n";
        str += "(" + representation + ") {\n";
        str += contents;
        str += "}\n";
        return str;
    }

    @Override
    public String visitExpressionStatement(SylvaParser.@NotNull ExpressionStatementContext ctx) {
        return visit(ctx.expr()) + "REM\n";
    }

    @Override
    public String visitGeneralBody(SylvaParser.@NotNull GeneralBodyContext ctx) {
        var str = "";
        pushVariableIDStack();
        var contents = new StringBuilder();
        for (var stmt : ctx.stmt()) {
            contents.append(visit(stmt));
        }
        var representation = getVariableIDStackRepresentation();
        popVariableIDStack();

        str += "(" + representation + ") {\n";
        str += contents;
        str += "}\n";

        return str;
    }

    @Override
    public String visitLocalFunctionValue(SylvaParser.@NotNull LocalFunctionValueContext ctx) {
        var str = "";
        var id = getNewLabelID();
        var end = "@end#" + id;

        if (ctx.name == null) {
            str += "NIL\n";
        } else {
            str += "STR(\"" + ctx.name.getText() + "\")\n";
        }

        str += "FUNCTION(" + end + ")\n";
        str += visit(ctx.fnbody());
        str += end + "\n";

        return str;
    }
}
