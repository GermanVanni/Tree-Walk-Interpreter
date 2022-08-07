package me.germanvanni.nox.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class AstGenerator {
    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.err.println("ast_generator <output directory>");
            System.exit(64);
        }
        String outputDirectory = args[0];

        defineAst(outputDirectory, "Expr", Arrays.asList(
                "Assign   : Token name, Expr value",
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right",
                "Variable : Token name",
                "Logical  : Expr left, Token operator, Expr right"
        ));

        defineAst(outputDirectory, "Stmt", Arrays.asList(
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Block      : List<Stmt> statements",
                "Expression : Expr expression",
                "Print      : Expr expression",
                "Var        : Token name, Expr initializer",
                "While      : Expr Condition, Stmt body"
        ));
    }

    private static void defineAst(String outputDirectory, String baseName, List<String> types) throws IOException{
        String path = outputDirectory + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package me.germanvanni.nox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + "{");

        //base accept()
        writer.println();
        writer.println(" abstract <R> R accept(Visitor<R> visitor);");

        defineVisitor(writer, baseName, types);

        for(String type : types){
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);

        }


        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList){
        writer.println("static class " + className + " extends " + baseName + " {");
        writer.println();
        String[] fields = fieldList.split(", ");
        //fields declaration

        for(String field : fields){
            writer.println("    final " + field + ";");
        }
        writer.println();


        //Constructor
        writer.println("    " + className + "(" + fieldList + ") {");
        //inside the constructor, we assign the parameters to it's corresponding fields
        for(String field : fields){
            String name = field.split(" ")[1];
            writer.println("        this." + name + " = " + name + ";");
        }
        //End constructor
        writer.println("    }");
        writer.println();

        //Visitor
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("        return visitor.visit" + className + baseName + "(this);");
        writer.println("    }");
        writer.println();


        //End class
        writer.println("}");
        writer.println();

    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types){
        writer.println("    interface Visitor<R> {");
        for(String type : types){
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" +
            typeName + " " + baseName.toLowerCase() + ");");

        }
        writer.println("    }");
    }
}
