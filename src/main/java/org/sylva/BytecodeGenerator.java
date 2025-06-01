package org.sylva;

import org.jetbrains.annotations.NotNull;
import org.sylva.generated.SylvaBaseVisitor;
import org.sylva.generated.SylvaParser;

public class BytecodeGenerator extends SylvaBaseVisitor<String> {
    private int labelIndex = 0;
    public int getNewLabelID() {
        return labelIndex++;
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
        str += "STR(\"" + newString.toString() + "\")\n";
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
}
