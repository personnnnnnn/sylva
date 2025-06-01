package org.sylva;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sylva.bytecode.StateManager;
import org.sylva.generated.SylvaBytecodeLexer;
import org.sylva.generated.SylvaBytecodeParser;

public class Main {
    public static void main(String[] args) {
        var code = """
        PUSH
            NIL FUNCTION(@end)
                OPTIONAL_ARG("x", 0, @if-given)
                STR("string") SET(0, 0)
                @if-given
                NO_MORE_ARGUMENTS
                GET(0, 0) RETURN
            @end SET(0, 0)
            ARGUMENTS STR("abcd") GET(0, 0) CALL
        POP
        \s""";

        SylvaBytecodeLexer lexer = new SylvaBytecodeLexer(CharStreams.fromString(code));
        CommonTokenStream stream = new CommonTokenStream(lexer);

        SylvaBytecodeParser parser = new SylvaBytecodeParser(stream);

        var tree = parser.program();

        var manager = StateManager.from(tree);

        while (!manager.isDone()) {
            var res = manager.step();
            if (res.isError()) {
                System.out.println(res.error().fullMessage());
                break;
            }
        }
        System.out.println(manager.getValueStack());
    }
}
