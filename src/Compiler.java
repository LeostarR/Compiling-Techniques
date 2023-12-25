import GrammaticalTools.Parser;
import IRTools_llvm.MidOptimizer;
import IRTools_llvm.Visitor;
import LexicalTools.LexType;
import LexicalTools.Lexer;
import LexicalTools.wordTuple;
import IRTools_llvm.Value.Module;
import MIPSTools.EndOptimizer;
import MIPSTools.LLVMParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) {
        ArrayList<wordTuple> wordList = new ArrayList<>();
        String source;
        try {
            File file = new File("testfile.txt");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\Z");
            source = scanner.next();
            scanner.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Lexer lexer = new Lexer(source);
        while (!lexer.isEnd()) {
            lexer.next();
            if (!lexer.isEnd() && !lexer.isAnnotation()) {
                String wordName = lexer.getToken();
                LexType wordType = lexer.getLexType();
                Object wordValue = lexer.getValue();
                int wordLine = lexer.getLineNum();
                boolean legitimate = lexer.isLegitimate();
                wordTuple wordTuple = new wordTuple(wordName, wordType, wordValue, wordLine, legitimate);
                wordList.add(wordTuple);
            }
        }
        try {
            FileOutputStream fileOutputStream;
            PrintStream printStream;
            String outputName = "output.txt";
            String errName = "error.txt";
            String llvmName = "llvm_ir.txt";
            String mipsName = "mips.txt";

            Module module = Module.getInstance();
            fileOutputStream = new FileOutputStream(errName);
            printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
            Parser parser = new Parser(wordList);
            File errFile = new File(errName);
            if (errFile.exists() && errFile.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(errFile))) {
                    String firstLine = reader.readLine();
                    if (firstLine != null && !firstLine.trim().isEmpty()) {
                        return;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Visitor visitor = new Visitor(parser.getCompUnit());
            visitor.visitNodeCompUnit();
            fileOutputStream = new FileOutputStream(llvmName);
            printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
            MidOptimizer midOptimizer = new MidOptimizer(module, true);
            System.out.println(midOptimizer.getModule());

            LLVMParser llvmParser = new LLVMParser();
            llvmParser.visitModule();
            fileOutputStream = new FileOutputStream(mipsName);
            printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
            EndOptimizer endOptimizer = new EndOptimizer(llvmParser.getMipsModule(), true);
            System.out.println(endOptimizer.getMipsModule());

            printStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}