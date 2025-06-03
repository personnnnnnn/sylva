package org.sylva;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sylva.bytecode.StateManager;
import org.sylva.generated.SylvaBytecodeLexer;
import org.sylva.generated.SylvaBytecodeParser;
import org.sylva.generated.SylvaLexer;
import org.sylva.generated.SylvaParser;
import org.sylva.libraries.Std;

public class Main {
    public static void main(String[] args) {

        var code = """
        fn sayHello(target = "World") => "Hello, "..target.."!"
        \s""";

        System.out.println(code);
        System.out.println();

        SylvaLexer sylvaLexer = new SylvaLexer(CharStreams.fromString(code));
        CommonTokenStream sylvaTokens = new CommonTokenStream(sylvaLexer);
        SylvaParser sylvaParser = new SylvaParser(sylvaTokens);

        ParseTree sylvaTree = sylvaParser.program();

        BytecodeGenerator generator = new BytecodeGenerator();

        var bytecode = generator.visit(sylvaTree);

//        var bytecode = """
//        PUSH (&x &y) {
//            NIL SET(&x)
//            GET(&x) SET(&y)
//            GET(&x) GET(&y)
//        } POP
//        \s""";

        System.out.println(bytecode);

        SylvaBytecodeLexer lexer = new SylvaBytecodeLexer(CharStreams.fromString(bytecode));
        CommonTokenStream stream = new CommonTokenStream(lexer);

        SylvaBytecodeParser parser = new SylvaBytecodeParser(stream);

        var tree = parser.program();

        var manager = StateManager.from(tree);

        manager.addLibrary(Std.std);

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
