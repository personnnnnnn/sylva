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
            STR("Hello, World!")
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
