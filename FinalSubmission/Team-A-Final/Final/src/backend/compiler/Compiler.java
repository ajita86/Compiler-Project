package backend.compiler;

import antlr4.*;

import intermediate.symtab.*;
import intermediate.symtab.Predefined;
import intermediate.type.Typespec;

/**
 * Compile Pascal to Jasmin assembly language.
 */
public class Compiler extends CKBaseVisitor<Object>
{
    private SymtabEntry programId;  // symbol table entry of the program name
    private String programName;     // the program name
    
    private CodeGenerator       code;            // base code generator
    private ProgramGenerator    programCode;     // program code generator
    private StatementGenerator  statementCode;   // statement code generator
    private ExpressionGenerator expressionCode;  // expression code generator
    
    /**
     * Constructor for the base compiler.
     * @param programId the symtab entry for the program name.
     */
    public Compiler(SymtabEntry programId)
    {
        this.programId = programId;        
        programName = programId.getName();
        
        code = new CodeGenerator(programName, "j", this);
    }
    
    /**
     * Constructor for child compilers of procedures and functions.
     * @param parent the parent compiler.
     */
    public Compiler(Compiler parent)
    {
        this.code        = parent.code;
        this.programCode = parent.programCode;
        this.programId   = parent.programId;
        this.programName = parent.programName;
    }
    
    /**
     * Constructor for child compilers of records.
     * @param parent the parent compiler.
     * @param recordId the symbol table entry of the name of the record to compile.
     */
    public Compiler(Compiler parent, SymtabEntry recordId)
    {        
        String recordTypePath = recordId.getType().getRecordTypePath();
        code = new CodeGenerator(recordTypePath, "j", this);
        createNewGenerators(code);
        
        programCode.emitRecord(recordId, recordTypePath);
    }
    
    /**
     * Create new child code generators.
     * @param parentGenerator the parent code generator.
     */
    private void createNewGenerators(CodeGenerator parentGenerator)
    {
        programCode    = new ProgramGenerator(parentGenerator, this);
        statementCode  = new StatementGenerator(programCode, this);
        expressionCode = new ExpressionGenerator(programCode, this);
    }

    /**
     * Get the name of the object (Jasmin) file.
     * @return the name.
     */
    public String getObjectFileName() { return code.getObjectFileName(); }
    
    @Override 
    public Object visitProgram(CKParser.ProgramContext ctx)
    { 
        createNewGenerators(code);
        programCode.emitProgram(ctx);
        return null;
    }

    @Override
    public Object visitStatement(CKParser.StatementContext ctx)
    {
        if (   (ctx.compoundStatement() == null) 
            && (ctx.emptyStatement() == null))
        {
            statementCode.emitComment(ctx.getText());
        }
        
        return visitChildren(ctx);
    }

    @Override 
    public Object visitAssignmentStatement(
                                    CKParser.AssignmentStatementContext ctx)
    {
        statementCode.emitAssignment(ctx);
        return null;
    }

    @Override 
    public Object visitIfStatement(CKParser.IfStatementContext ctx)
    {
        statementCode.emitIf(ctx);
        return null;
    }

    @Override
    public Object visitWhileStatement(CKParser.WhileStatementContext ctx)
    {
        statementCode.emitWhile(ctx);
        return null;
    }

    @Override
    public Object visitExpression(CKParser.ExpressionContext ctx)
    {
        expressionCode.emitExpression(ctx);
        try
        {
            CKParser.FactorContext f = ctx.relationExpression(0).simpleExpression(0).term(0).factor(0);
            return f.type;
        }
        catch(Exception e)
        {

        }
        return null;
    }

    @Override 
    public Object visitVariableFactor(CKParser.VariableFactorContext ctx)
    {
        expressionCode.emitLoadValue(ctx.variable());
        return null;
    }

    @Override 
    public Object visitVariable(CKParser.VariableContext ctx)
    {
        expressionCode.emitLoadVariable(ctx);        
        return null;
    }

    @Override 
    public Object visitNumberFactor(CKParser.NumberFactorContext ctx)
    {
        if (ctx.type == Predefined.integerType) 
        {
            expressionCode.emitLoadIntegerConstant(ctx.number());
        }
        else
        {
            expressionCode.emitLoadRealConstant(ctx.number());
        }
        
        return null;
    }

    @Override 
    public Object visitCharacterFactor(CKParser.CharacterFactorContext ctx)
    {
        char ch = ctx.getText().charAt(1);
        expressionCode.emitLoadConstant(ch);

        return null;
    }

    @Override 
    public Object visitStringFactor(CKParser.StringFactorContext ctx)
    {
        String jasminString = convertString(ctx.getText());
        expressionCode.emitLoadConstant(jasminString);
        
        return null;
    }
    
    /**
     * Convert a Pascal string to a Java string.
     * @param pascalString the Pascal string.
     * @return the Java string.
     */
    String convertString(String pascalString)
    {
        String unquoted = pascalString.substring(1, pascalString.length()-1);
        return unquoted.replace("''", "'").replace("\"", "\\\"");
    }

    @Override 
    public Object visitFunctionCallFactor(
                                    CKParser.FunctionCallFactorContext ctx)
    {
        statementCode.emitFunctionCall(ctx.functionCall());
        return null;
    }

    @Override
    public Object visitFunctionDefinitionStatement(
            CKParser.FunctionDefinitionStatementContext ctx)
    {
        return null;
    }

    @Override 
    public Object visitNotFactor(CKParser.NotFactorContext ctx)
    {
        expressionCode.emitNotFactor(ctx);
        return null;
    }

    @Override
    public Object visitStringAnalysis(CKParser.StringAnalysisContext ctx)
    {
        expressionCode.emitStringAnalysis(ctx);
        return null;
    }

    @Override 
    public Object visitBracketedFactor(
                                    CKParser.BracketedFactorContext ctx)
    {
        return visitExpression(ctx.expression());
    }

    @Override 
    public Object visitPrintStatement(CKParser.PrintStatementContext ctx)
    {
        Typespec pType = ctx.expression().type;
        statementCode.emitPrint(ctx, pType);
        return null;
    }
}