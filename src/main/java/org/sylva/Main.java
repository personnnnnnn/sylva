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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        StringBuilder code = new StringBuilder();
        try {
            File myObj = new File("testing/sylva/proper-array-definition/main.sylva");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                code.append(myReader.nextLine()).append("\n");
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return;
        }

        System.out.println(code);
        System.out.println();

        SylvaLexer sylvaLexer = new SylvaLexer(CharStreams.fromString(code.toString()));
        CommonTokenStream sylvaTokens = new CommonTokenStream(sylvaLexer);
        SylvaParser sylvaParser = new SylvaParser(sylvaTokens);

        ParseTree sylvaTree = sylvaParser.program();

        BytecodeGenerator generator = new BytecodeGenerator();

        var bytecode = generator.visit(sylvaTree);

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
                System.out.println("Error");
                System.out.println();
                System.out.println(manager.getInstructionPointer());
                System.out.println(manager.getCurrentCommand());
                System.out.println(res.error().fullMessage());
                break;
            }
        }

        System.out.println();
        System.out.println("Program end");
        System.out.println();

        System.out.println(manager.getValueStack());
    }
}
