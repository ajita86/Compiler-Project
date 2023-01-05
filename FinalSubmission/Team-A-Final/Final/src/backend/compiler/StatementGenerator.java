package backend.compiler;

import java.util.*;

import antlr4.CKParser;
import antlr4.CKParser.VariableIdentifierContext;
import intermediate.symtab.*;
import intermediate.type.*;
import intermediate.type.Typespec.Form;
import intermediate.util.BackendMode;
import frontend.Semantics;

import static intermediate.type.Typespec.Form.*;
import static backend.compiler.Instruction.*;
import static frontend.SemanticErrorHandler.Code.UNDECLARED_IDENTIFIER;

/**
 * <h1>StatementGenerator</h1>
 *
 * <p>Emit code for executable statements.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class StatementGenerator extends CodeGenerator
{
	
    /**
     * Constructor.
     * @param parent the parent generator.
     * @param compiler the compiler to use.
     */
    public StatementGenerator(CodeGenerator parent, Compiler compiler)
    {
        super(parent, compiler);
    }

    /**
     * Emit code for an assignment statement.
     * @param ctx the AssignmentStatementContext.
     */
    public void emitAssignment(CKParser.AssignmentStatementContext ctx)
    {
        CKParser.VariableContext   varCtx  = ctx.lhs().variable();
        CKParser.ExpressionContext exprCtx = ctx.rhs().expression();
        SymtabEntry varId = varCtx.entry;
        Typespec varType  = varCtx.type;
        Typespec exprType = exprCtx.type;
        
        // Emit code to evaluate the expression.
        compiler.visit(exprCtx);
        
        // float variable := integer constant
        if (   (varType == Predefined.realType)
            && (exprType.baseType() == Predefined.integerType)) emit(I2F);
        
        // Emit code to store the expression value into the target variable.
        emitStoreValue(varId, varId.getType());
    }

    /**
     * Emit code for an IF statement.
     * @param ctx the IfStatementContext.
     */
    public void emitIf(CKParser.IfStatementContext ctx)
    {
        Label met = new Label();
        Label end = new Label();
        compiler.visit(ctx.expression());
        emit(IFNE, met);
        if(ctx.falseStatement() != null)
        {
            compiler.visit(ctx.falseStatement());
        }
        emit(GOTO, end);
        emitLabel(met);
        compiler.visit(ctx.trueStatement());
        emitLabel(end);
    }
    
    /**
     * Emit code for a WHILE statement.
     * @param ctx the WhileStatementContext.
     */
    public void emitWhile(CKParser.WhileStatementContext ctx)
    {
        Label loopTopLabel  = new Label();
        Label loopExitLabel = new Label();

        emitLabel(loopTopLabel);

        compiler.visit(ctx.expression());
        emit(IFEQ, loopExitLabel); // if equal continue if not exit loop

        compiler.visit(ctx.statement());
        emit(GOTO, loopTopLabel);
        emitLabel(loopExitLabel);
    }
    
    /**
     * Emit code for a function call statement.
     * @param ctx the FunctionCallContext.
     */
    public void emitFunctionCall(CKParser.FunctionCallContext ctx)
    {
        emitCall(ctx.functionName().entry, ctx.argumentList());
    }
    
    /**
     * Emit a call to a procedure or a function.
     * @param routineId the routine name's symbol table entry.
     * @param argListCtx the ArgumentListContext.
     */
    private void emitCall(SymtabEntry routineId,
                          CKParser.ArgumentListContext argListCtx)
    {
        if (argListCtx != null)			//visit argument expressions
        {
        	for(int i = 0; i < argListCtx.argument().size(); i++) 
        	{
        		CKParser.ExpressionContext expression = argListCtx.argument(i).expression();
        		
        		compiler.visit(expression);
        		
        		if (expression.type == Predefined.integerType && routineId.getRoutineParameters().get(i).getType() == Predefined.realType)
        		{
        			expression.type = Predefined.realType;
        			emit(I2F);
        		} else if (expression.type == Predefined.realType && routineId.getRoutineParameters().get(i).getType() == Predefined.integerType)
        		{
        			expression.type = Predefined.integerType;
        			emit(F2I);
        		}
        	}
        }
        
        String invokeCall = routineId.getSymtab().getOwner().getName() + "/" + routineId.getName() + "(";
        
        if (routineId.getRoutineParameters() != null)			//parameters
        {
        	for (int i = 0; i < routineId.getRoutineParameters().size(); i++)
        	{
        		invokeCall = invokeCall + typeDescriptor(routineId.getRoutineParameters().get(i));
        	}
        }
        
        invokeCall = invokeCall + ")" + typeDescriptor(routineId);		
        
        emit(INVOKESTATIC, invokeCall);		// invokestatic programName/<functionName or procedureName>(paramTypes)returnType
        
        localStack.increase(INVOKESTATIC.stackUse);
    }

    /**
     * Emit code for a print statement.
     * @param ctx the PrintStatementContext.
     */
    public void emitPrint(CKParser.PrintStatementContext ctx, Typespec printType)
    {
        if(printType == Predefined.integerType)
        {
            emitPrintInteger(ctx);
        }
        else if(printType == Predefined.realType)
        {
            emitPrintReal(ctx);
        }
        else
        {
            emitPrint(ctx);
        }
    }

    /**
     * Emit code for a call to WRITE or WRITELN.
     * @param argsCtx the WriteArgumentsContext.
     */
    private void emitPrint(CKParser.PrintStatementContext argsCtx)
    {
        emit(GETSTATIC, "java/lang/System/out", "Ljava/io/PrintStream;");

        // WRITELN with no arguments.
        if (argsCtx == null)
        {
            emit(INVOKEVIRTUAL, "java/io/PrintStream.println()V");
            localStack.decrease(1);
        }

        // Generate code for the arguments.
        else
        {
            StringBuffer format = new StringBuffer();
            createWriteFormat(argsCtx, format, false);

            String text = format.toString();
//            emit(LDC, text);

            emit(INVOKEVIRTUAL, "java/io/PrintStream/print(Ljava/lang/String;)V");

            localStack.decrease(2);
        }
    }

    /**
     * Emit code for a call to WRITE or WRITELN.
     * @param argsCtx the WriteArgumentsContext.
     */
    private void emitPrintInteger(CKParser.PrintStatementContext argsCtx)
    {
        emit(GETSTATIC, "java/lang/System/out", "Ljava/io/PrintStream;");

        // WRITELN with no arguments.
        if (argsCtx == null)
        {
            emit(INVOKEVIRTUAL, "java/io/PrintStream.println()V");
            localStack.decrease(1);
        }

        // Generate code for the arguments.
        else
        {
            StringBuffer format = new StringBuffer();
            createWriteFormat(argsCtx, format, false);

            String text = format.toString();
//            emit(LDC, text);

            emit(INVOKEVIRTUAL, "java/io/PrintStream/print(I)V");

            localStack.decrease(2);
        }
    }

    /**
     * Emit code for a call to WRITE or WRITELN.
     * @param argsCtx the WriteArgumentsContext.
     */
    private void emitPrintReal(CKParser.PrintStatementContext argsCtx)
    {
        emit(GETSTATIC, "java/lang/System/out", "Ljava/io/PrintStream;");

        // WRITELN with no arguments.
        if (argsCtx == null)
        {
            emit(INVOKEVIRTUAL, "java/io/PrintStream.println()V");
            localStack.decrease(1);
        }

        // Generate code for the arguments.
        else
        {
            StringBuffer format = new StringBuffer();
            createWriteFormat(argsCtx, format, false);

            String text = format.toString();
//            emit(LDC, text);

            emit(INVOKEVIRTUAL, "java/io/PrintStream/print(F)V");

            localStack.decrease(2);
        }
    }
    
    /**
     * Create the printf format string.
     * @param printCtx the WriteArgumentsContext.
     * @param format the format string to create.
     * @return the count of expression arguments.
     */
    private int createWriteFormat(CKParser.PrintStatementContext printCtx,
                                  StringBuffer format, boolean needLF)
    {
        int exprCount = 0;
        format.append("\"");
        

        Typespec type = printCtx.expression().type;
        String printText = printCtx.expression().getText();
            
        // Append any literal strings.
        if (printText.charAt(0) == '\'')    //string
        {
            format.append(convertString(printText));
            emit(LDC, printText.replace("'", "\""));
        } 
        else   //variable
        {
        	String variableName = printCtx.expression().getText().toLowerCase();
            try
            {
                CKParser.VariableFactorContext fc = (CKParser.VariableFactorContext) printCtx.expression().relationExpression(0).simpleExpression(0).term(0).getChild(0);
                CKParser.VariableContext vc = fc.variable();
                emitLoadValue(vc.entry);
            }
            catch(Exception e)
            {

            }

        	String x = "";
        	for (ArrayList<String> l : Semantics.variables) 
			{
				if (l.contains(variableName))
				{
					x = l.get(1);
					format.append(x);
				}
			}
        	
        }
                
        format.append(needLF ? "\\n\"" : "\"");
 
        return exprCount;
    }
}
