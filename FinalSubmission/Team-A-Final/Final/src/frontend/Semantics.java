package frontend;

import java.util.ArrayList;

import antlr4.*;

import intermediate.symtab.*;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.*;
import intermediate.util.*;

import static backend.compiler.Instruction.LDC;
import static frontend.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.symtab.SymtabEntry.Routine.DECLARED;
import static intermediate.type.Typespec.Form.*;
import static intermediate.util.BackendMode.EXECUTOR;

/**
 * Semantic operations.
 * Perform type checking and create symbol tables.
 */
public class Semantics extends CKBaseVisitor<Object>
{
    private BackendMode mode;
    private SymtabStack symtabStack;
    private SymtabEntry programId;
    private SemanticErrorHandler error;
    
    public static ArrayList<ArrayList<String>> variables;
    
    public Semantics(BackendMode mode)
    {
        // Create and initialize the symbol table stack.
        this.symtabStack = new SymtabStack();
        Predefined.initialize(symtabStack);
        
        this.mode = mode;
        this.error = new SemanticErrorHandler();
        this.variables = new ArrayList<ArrayList<String>>();
    }
    
    public SymtabEntry getProgramId() { return programId; }
    public int getErrorCount() { return error.getCount(); };
    
    /**
     * Return the default value for a data type.
     * @param type the data type.
     * @return the default value.
     */
    public static Object defaultValue(Typespec type)
    {
        type = type.baseType();

        if      (type == Predefined.integerType) return Integer.valueOf(0);
        else if (type == Predefined.realType)    return Float.valueOf(0.0f);
        else if (type == Predefined.booleanType) return Boolean.valueOf(false);
        else if (type == Predefined.charType)    return Character.valueOf('#');
        else /* string */                        return String.valueOf("#");
    }

    @Override 
    public Object visitProgram(CKParser.ProgramContext ctx) 
    { 
        visit(ctx.programHeader());
        visit(ctx.compoundStatement());
        
        // Print the cross-reference table.
        CrossReferencer crossReferencer = new CrossReferencer();
        crossReferencer.print(symtabStack);

        return null;
    }
    
    @Override 
    public Object visitProgramHeader(CKParser.ProgramHeaderContext ctx) 
    { 
        CKParser.ProgramIdentifierContext idCtx = ctx.programIdentifier();
        String programName = idCtx.IDENTIFIER().getText();  // don't shift case
        
        programId = symtabStack.enterLocal(programName, PROGRAM);
        programId.setRoutineSymtab(symtabStack.push());
        
        symtabStack.setProgramId(programId);
        symtabStack.getLocalSymtab().setOwner(programId);
        
        idCtx.entry = programId;
        return null;
    }
    
    @Override 
    public Object visitVariableDeclarationStatement(
                                CKParser.VariableDeclarationStatementContext ctx) 
    { 
    	CKParser.TypeIdentifierContext typeCtx = ctx.typeIdentifier();
    	visit(ctx.typeIdentifier());
        CKParser.VariableIdentifierContext idCtx = ctx.variableIdentifier();
    	
        int lineNumber = idCtx.getStart().getLine();
        String variableName = idCtx.getText().toLowerCase();
        SymtabEntry variableId = symtabStack.lookupLocal(variableName);
    	
        if (variableId == null)
        {
            variableId = symtabStack.enterLocal(variableName, VARIABLE);
            variableId.setType(typeCtx.type);
            
            // Assign slot numbers to local variables.
            Symtab symtab = variableId.getSymtab();
            if (symtab.getNestingLevel() > 1)
            {
                variableId.setSlotNumber(symtab.nextSlotNumber());
            }
            
            idCtx.entry = variableId;
        }
        else
        {
            error.flag(REDECLARED_IDENTIFIER, ctx);
        }
        
        variableId.appendLineNumber(lineNumber);  
        
        return null;
    }

    @Override
    public Object visitTypeIdentifier(CKParser.TypeIdentifierContext ctx)
    {
        String typeName = ctx.IDENTIFIER().getText().toLowerCase();
        SymtabEntry typeId = symtabStack.lookup(typeName);

        if (typeId != null)
        {
            if (typeId.getKind() != TYPE)
            {
                error.flag(INVALID_TYPE, ctx);
                ctx.type = Predefined.integerType;
            }
            else
            {
                ctx.type = typeId.getType();
            }

            typeId.appendLineNumber(ctx.start.getLine());
        }
        else
        {
            error.flag(UNDECLARED_IDENTIFIER, ctx);
            ctx.type = Predefined.integerType;
        }

        ctx.entry = typeId;
        return null;
    }
    
    @Override 
    public Object visitAssignmentStatement(
                                    CKParser.AssignmentStatementContext ctx) 
    {
        CKParser.LhsContext lhsCtx = ctx.lhs();
        CKParser.RhsContext rhsCtx = ctx.rhs();
        
        visitChildren(ctx);
        
        Typespec lhsType = lhsCtx.type;
        Typespec rhsType = rhsCtx.expression().type;
        
        if (!TypeChecker.areAssignmentCompatible(lhsType, rhsType))
        {
            error.flag(INCOMPATIBLE_ASSIGNMENT, rhsCtx);
        }
        
        return null;
    }

    @Override 
    public Object visitLhs(CKParser.LhsContext ctx) 
    {
        CKParser.VariableContext varCtx = ctx.variable();
        visit(varCtx);
        ctx.type = varCtx.type;

        return null;
    }

    @Override 
    public Object visitIfStatement(CKParser.IfStatementContext ctx) 
    {
        CKParser.ExpressionContext     exprCtx  = ctx.expression();
        CKParser.TrueStatementContext  trueCtx  = ctx.trueStatement();
        CKParser.FalseStatementContext falseCtx = ctx.falseStatement();
        
        visit(exprCtx);
        Typespec exprType = exprCtx.type;
        
        if (!TypeChecker.isBoolean(exprType))
        {
            error.flag(TYPE_MUST_BE_BOOLEAN, exprCtx);
        }
        
        visit(trueCtx);
        if (falseCtx != null) visit(falseCtx);
        
        return null;
    }

    @Override 
    public Object visitWhileStatement(CKParser.WhileStatementContext ctx) 
    {
        CKParser.ExpressionContext exprCtx = ctx.expression();
        visit(exprCtx);
        Typespec exprType = exprCtx.type;
        
        if (!TypeChecker.isBoolean(exprType))
        {
            error.flag(TYPE_MUST_BE_BOOLEAN, exprCtx);
        }
        
        visit(ctx.statement());
        return null;
    }
    
    @Override 
    public Object visitFunctionDefinitionStatement(CKParser.FunctionDefinitionStatementContext ctx) 
    {
        CKParser.TypeIdentifierContext typeIdCtx = ctx.typeIdentifier();
        CKParser.FunctionNameContext functNameCtx = ctx.functionName();
        CKParser.DefArgumentListContext parameters = ctx.defArgumentList();
        Typespec returnType = null;
        String functionName;
        
        functionName = functNameCtx.IDENTIFIER().getText().toLowerCase();
        SymtabEntry functionId = symtabStack.lookupLocal(functionName);
        
        if (functionId != null)
        {
            error.flag(REDECLARED_IDENTIFIER, 
                       ctx.getStart().getLine(), functionName);
            return null;
        }

        functionId = symtabStack.enterLocal(
                        functionName, FUNCTION);
        functionId.setRoutineCode(DECLARED);
        functNameCtx.entry = functionId;
        int lineNumber = ctx.getStart().getLine(); 
        functionId.appendLineNumber(lineNumber);
        
        // Append to the parent routine's list of subroutines.
        SymtabEntry parentId = symtabStack.getLocalSymtab().getOwner();
        parentId.appendSubroutine(functionId);
        
        functionId.setRoutineSymtab(symtabStack.push());
        functNameCtx.entry = functionId;
        
        Symtab symtab = symtabStack.getLocalSymtab();
        symtab.setOwner(functionId);
        
        if (parameters != null)
        {
            @SuppressWarnings("unchecked")
			ArrayList<SymtabEntry> parameterIds = (ArrayList<SymtabEntry>) 
                                visit(parameters);
            functionId.setRoutineParameters(parameterIds);
            
            for (SymtabEntry parmId : parameterIds)
            {
                parmId.setSlotNumber(symtab.nextSlotNumber());
            }
        }

        visit(typeIdCtx);
        returnType = typeIdCtx.type;
            
        if (returnType.getForm() != SCALAR)
        {
            error.flag(INVALID_RETURN_TYPE, typeIdCtx);
            returnType = Predefined.integerType;
        }
            
        functionId.setType(returnType);
        functNameCtx.type = returnType;
        ctx.type = returnType;
        
        // Enter the function's associated variable into its symbol table.
        SymtabEntry assocVarId = 
                                symtabStack.enterLocal(functionName, VARIABLE);
        assocVarId.setSlotNumber(symtab.nextSlotNumber());
        assocVarId.setType(returnType);
        
        visit(ctx.statement());
        functionId.setExecutable(ctx.statement());
        ctx.entry = functionId;
        
        symtabStack.pop();
        return null;
    }
    
    @Override 
    public Object visitDefArgumentList(
                            CKParser.DefArgumentListContext ctx)
    {
        ArrayList<SymtabEntry> parameterList = new ArrayList<>();
        
        // Loop over the parameter declarations.
        for (int i = 0; i < ctx.variable().size(); i++)
        {
        	CKParser.VariableContext varCtx = ctx.variable(i);
            CKParser.TypeIdentifierContext typeCtx = ctx.typeIdentifier(i);
                
            visit(typeCtx);
            Typespec varType = typeCtx.type;
                
            int lineNumber = varCtx.getStart().getLine();   
            String varName = varCtx.variableIdentifier().IDENTIFIER().getText().toLowerCase();
            SymtabEntry varId = symtabStack.lookupLocal(varName);
                    
            if (varId == null)
            {
                varId = symtabStack.enterLocal(varName, VALUE_PARAMETER);
                varId.setType(varType);
            }
            else
            {
                error.flag(REDECLARED_IDENTIFIER, varCtx);
            }
                    
            varCtx.entry = varId;
            varCtx.type  = varType;
                    
            parameterList.add(varId);
            varId.appendLineNumber(lineNumber);    
            }
        
        return parameterList;
    }

    @Override 
    public Object visitFunctionCallFactor(
                                    CKParser.FunctionCallFactorContext ctx) 
    {
        CKParser.FunctionCallContext callCtx = ctx.functionCall();
        CKParser.FunctionNameContext nameCtx = callCtx.functionName();
        CKParser.ArgumentListContext listCtx = callCtx.argumentList();
        String name = callCtx.functionName().getText().toLowerCase();
        SymtabEntry functionId = symtabStack.lookup(name);
        boolean badName = false;
        
        ctx.type = Predefined.integerType;

        if (functionId == null)
        {
            error.flag(UNDECLARED_IDENTIFIER, nameCtx);
            badName = true;
        }
        else if (functionId.getKind() != FUNCTION)
        {
            error.flag(NAME_MUST_BE_FUNCTION, nameCtx);
            badName = true;
        }
        
        // Bad function name. Do a simple arguments check and then leave.
        if (badName)
        {
            for (CKParser.ArgumentContext exprCtx : listCtx.argument())
            {
                visit(exprCtx);
            }
        }
        
        // Good function name.
        else
        {
            ArrayList<SymtabEntry> parameters = functionId.getRoutineParameters();
            checkCallArguments(listCtx, parameters);
            ctx.type = functionId.getType();
        }
        
        nameCtx.entry = functionId;
        nameCtx.type  = ctx.type;

        return null;
    }
    
    /**
     * Perform semantic operations on procedure and function call arguments.
     * @param listCtx the ArgumentListContext.
     * @param parameters the arraylist of parameters to fill.
     */
    private void checkCallArguments(CKParser.ArgumentListContext listCtx,
                                    ArrayList<SymtabEntry> parameters)
    {
        int parmsCount = parameters.size();
        int argsCount = listCtx != null ? listCtx.argument().size() : 0;
        
        if (parmsCount != argsCount)
        {
            error.flag(ARGUMENT_COUNT_MISMATCH, listCtx);
            return;
        }
        
        // Check each argument against the corresponding parameter.
        for (int i = 0; i < parmsCount; i++)
        {
            CKParser.ArgumentContext argCtx = listCtx.argument().get(i);
            CKParser.ExpressionContext exprCtx = argCtx.expression();
            visit(exprCtx);
            
            SymtabEntry parmId = parameters.get(i);
            Typespec parmType = parmId.getType();
            Typespec argType  = exprCtx.type;
            
            // For a VAR parameter, the argument must be a variable
            // with the same datatype.
            if (parmId.getKind() == REFERENCE_PARAMETER)
            {
                if (expressionIsVariable(exprCtx))
                {
                    if (parmType != argType)
                    {
                        error.flag(TYPE_MISMATCH, exprCtx);
                    }
                }
                else
                {
                    error.flag(ARGUMENT_MUST_BE_VARIABLE, exprCtx);
                }
            }
            
            // For a value parameter, the argument type must be
            // assignment compatible with the parameter type.
            else if (!TypeChecker.areAssignmentCompatible(parmType, argType))
            {
                error.flag(TYPE_MISMATCH, exprCtx);
            }
        }
    }

    /**
     * Determine whether or not an expression is a variable only.
     * @param exprCtx the ExpressionContext.
     * @return true if it's an expression only, else false.
     */
    private boolean expressionIsVariable(CKParser.ExpressionContext exprCtx)
    {
        if (exprCtx.relationExpression().size() == 1) {
                CKParser.RelationExpressionContext relCtx =
                        exprCtx.relationExpression().get(0);
                // Only a single simple expression?
                if (relCtx.simpleExpression().size() == 1) {
                    CKParser.SimpleExpressionContext simpleCtx =
                            relCtx.simpleExpression().get(0);
                    // Only a single term?
                    if (simpleCtx.term().size() == 1) {
                        CKParser.TermContext termCtx = simpleCtx.term().get(0);

                        // Only a single factor?
                        if (termCtx.factor().size() == 1) {
                            return termCtx.factor().get(0) instanceof
                                    CKParser.VariableFactorContext;
                        }
                    }
                }
        }
        
        return false;
    }

    @Override 
    public Object visitExpression(CKParser.ExpressionContext ctx) 
    {
        CKParser.RelationExpressionContext relCtx1 =
                                                ctx.relationExpression().get(0);

        // First relation expression.
        visit(relCtx1);
        
        Typespec relType1 = relCtx1.type;
        ctx.type = relType1;
        
        CKParser.CypherOpContext cyphOpCtx = ctx.cypherOp();
        
        // Second relation expression?
        if (cyphOpCtx != null)
        {
            CKParser.RelationExpressionContext relCtx2 = 
                                                ctx.relationExpression().get(1);
            visit(relCtx2);
            
            Typespec relType2 = relCtx2.type;

            if ((cyphOpCtx.getText() == ">>" && !(relType1 == Predefined.stringType && relType2 == Predefined.integerType)) || (cyphOpCtx.getText() == "<<" && !(relType1 == Predefined.stringType && relType2 == Predefined.stringType)))
            {
                error.flag(INCOMPATIBLE_ASSIGNMENT, ctx);
            }
            
            ctx.type = Predefined.stringType;
        }
        
        return null;
    }
    
    @Override 
    public Object visitRelationExpression(CKParser.RelationExpressionContext ctx) 
    {
        int count = ctx.simpleExpression().size();
        CKParser.SimpleExpressionContext simpExprCtx1 = ctx.simpleExpression().get(0);
        
        // First simple expression.
        visit(simpExprCtx1);
        Typespec simpExprType1 = simpExprCtx1.type;    
        ctx.type = simpExprType1;
        
        // Loop over any subsequent simple expressions.
        for (int i = 1; i < count; i++)
        {
        	CKParser.RelOpContext relOpCtx = ctx.relOp();
            
            if (relOpCtx != null)
            {
            	CKParser.SimpleExpressionContext simpExprCtx2 = ctx.simpleExpression().get(i);
            	visit(simpExprCtx2);
                Typespec simpExprType2 = simpExprCtx2.type;

                if (!TypeChecker.areComparisonCompatible(simpExprType1, simpExprType2))
                {
                    error.flag(INCOMPATIBLE_COMPARISON, ctx);
                }
                
                ctx.type = Predefined.booleanType;
            }
        }

        return null;
    }

    @Override 
    public Object visitSimpleExpression(CKParser.SimpleExpressionContext ctx)
    {
        int count = ctx.term().size();
        CKParser.SignContext signCtx = ctx.sign();
        Boolean hasSign = signCtx != null;
        CKParser.TermContext termCtx1 = ctx.term().get(0);
        
        if (hasSign)
        {
            String sign = signCtx.getText();
            if (sign.equals("+") && sign.equals("-"))
            {
                error.flag(INVALID_SIGN, signCtx);
            }
        }
        
        // First term.
        visit(termCtx1);
        Typespec termType1 = termCtx1.type;        
        
        // Loop over any subsequent terms.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.addOp().get(i-1).getText().toLowerCase();
            CKParser.TermContext termCtx2 = ctx.term().get(i);
            visit(termCtx2);
            Typespec termType2 = termCtx2.type;
            
            // Both operands boolean ==> boolean result. Else type mismatch.
            if (op.equals("or"))
            {
                if (!TypeChecker.isBoolean(termType1)) 
                {
                    error.flag(TYPE_MUST_BE_BOOLEAN, termCtx1);
                }
                if (!TypeChecker.isBoolean(termType2)) 
                {
                    error.flag(TYPE_MUST_BE_BOOLEAN, termCtx2);
                }
                if (hasSign)
                {
                    error.flag(INVALID_SIGN, signCtx);
                }
                
                termType2 = Predefined.booleanType;
            }
            else if (op.equals("+"))
            {
                // Both operands integer ==> integer result
                if (TypeChecker.areBothInteger(termType1, termType2)) 
                {
                    termType2 = Predefined.integerType;
                }

                // Both real operands ==> real result 
                // One real and one integer operand ==> real result
                else if (TypeChecker.isAtLeastOneReal(termType1, termType2)) 
                {
                    termType2 = Predefined.realType;
                }
                
                // Both operands string ==> string result
                else if (TypeChecker.areBothString(termType1, termType2))
                {
                    if (hasSign) error.flag(INVALID_SIGN, signCtx);                    
                    termType2 = Predefined.stringType;
                }

                // Type mismatch.
                else
                {
                    if (!TypeChecker.isIntegerOrReal(termType1))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, termCtx1);
                        termType2 = Predefined.integerType;
                    }
                    if (!TypeChecker.isIntegerOrReal(termType2))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, termCtx2);
                        termType2 = Predefined.integerType;
                    }
                }
            }
            else  // -
            {
                // Both operands integer ==> integer result
                if (TypeChecker.areBothInteger(termType1, termType2)) 
                {
                    termType2 = Predefined.integerType;
                }

                // Both real operands ==> real result 
                // One real and one integer operand ==> real result
                else if (TypeChecker.isAtLeastOneReal(termType1, termType2)) 
                {
                    termType2 = Predefined.realType;
                }
                
                // Type mismatch.
                else
                {
                    if (!TypeChecker.isIntegerOrReal(termType1))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, termCtx1);
                        termType2 = Predefined.integerType;
                    }
                    if (!TypeChecker.isIntegerOrReal(termType2))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, termCtx2);
                        termType2 = Predefined.integerType;
                    }
                }
            }
            
            termType1 = termType2;
        }
        
        ctx.type = termType1;
        return null;
    }

    @Override 
    public Object visitTerm(CKParser.TermContext ctx)
    {
        int count = ctx.factor().size();
        CKParser.FactorContext factorCtx1 = ctx.factor().get(0);
        
        // First factor.
        visit(factorCtx1);
        Typespec factorType1 = factorCtx1.type; 
        
        // Loop over any subsequent factors.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.mulOp().get(i-1).getText().toLowerCase();
            CKParser.FactorContext factorCtx2 = ctx.factor().get(i);
            visit(factorCtx2);
            Typespec factorType2 = factorCtx2.type;
            
            if (op.equals("*"))
            {
                // Both operands integer  ==> integer result
                if (TypeChecker.areBothInteger(factorType1, factorType2)) 
                {
                    factorType2 = Predefined.integerType;
                }

                // Both real operands ==> real result 
                // One real and one integer operand ==> real result
                else if (TypeChecker.isAtLeastOneReal(factorType1, factorType2)) 
                {
                    factorType2 = Predefined.realType;
                }
                
                // Type mismatch.
                else
                {
                    if (!TypeChecker.isIntegerOrReal(factorType1))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx1);
                        factorType2 = Predefined.integerType;
                    }
                    if (!TypeChecker.isIntegerOrReal(factorType2))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx2);
                        factorType2 = Predefined.integerType;
                    }
                }
            }
            else if (op.equals("/"))
            {
                // All integer and real operand combinations ==> real result
                if (   TypeChecker.areBothInteger(factorType1, factorType2)
                    || TypeChecker.isAtLeastOneReal(factorType1, factorType2))
                {
                    factorType2 = Predefined.realType;
                }
                
                // Type mismatch.
                else 
                {
                    if (!TypeChecker.isIntegerOrReal(factorType1))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx1);
                        factorType2 = Predefined.integerType;
                    }
                    if (!TypeChecker.isIntegerOrReal(factorType2))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx2);
                        factorType2 = Predefined.integerType;
                    }
                }
            }
            else if (op.equals("div") || op.equals("mod"))
            {
                // Both operands integer ==> integer result. Else type mismatch.
                if (!TypeChecker.isInteger(factorType1))
                {
                    error.flag(TYPE_MUST_BE_INTEGER, factorCtx1);
                    factorType2 = Predefined.integerType;
                }
                if (!TypeChecker.isInteger(factorType2))
                {
                    error.flag(TYPE_MUST_BE_INTEGER, factorCtx2);
                    factorType2 = Predefined.integerType;
                }
            }
            else if (op.equals("and"))
            {
                // Both operands boolean ==> boolean result. Else type mismatch.
                if (!TypeChecker.isBoolean(factorType1))
                {
                    error.flag(TYPE_MUST_BE_BOOLEAN, factorCtx1);
                    factorType2 = Predefined.booleanType;
                }
                if (!TypeChecker.isBoolean(factorType2))
                {
                    error.flag(TYPE_MUST_BE_BOOLEAN, factorCtx2);
                    factorType2 = Predefined.booleanType;
                }
            }
            
            factorType1 = factorType2;
        }

        ctx.type = factorType1;
        return null;
    }

    @Override 
    public Object visitVariableFactor(CKParser.VariableFactorContext ctx)
    {
        CKParser.VariableContext varCtx = ctx.variable();
        visit(varCtx);
        ctx.type = varCtx.type;

        return null;
    }

    @Override 
    public Object visitVariable(CKParser.VariableContext ctx)
    {
        CKParser.VariableIdentifierContext varIdCtx =
                                                    ctx.variableIdentifier();
        
        visit(varIdCtx);
        ctx.entry = varIdCtx.entry;
        ctx.type  = variableDatatype(ctx, varIdCtx.type);

        return null;
    }

    @Override 
    public Object visitVariableIdentifier(
                                    CKParser.VariableIdentifierContext ctx)
    {
        String variableName = ctx.IDENTIFIER().getText().toLowerCase();
        SymtabEntry variableId = symtabStack.lookup(variableName);
        
        if (variableId != null)
        {
            int lineNumber = ctx.getStart().getLine();
            ctx.type = variableId.getType();
            ctx.entry = variableId;
            variableId.appendLineNumber(lineNumber);
            
            Kind kind = variableId.getKind();
            switch (kind)
            {
                case TYPE:
                case PROGRAM:
                case PROGRAM_PARAMETER:
                case PROCEDURE:
                case UNDEFINED:
                    error.flag(INVALID_VARIABLE, ctx);
                    break;
                    
                default: break;
            }
        }
        else
        {
            error.flag(UNDECLARED_IDENTIFIER, ctx);
            ctx.type = Predefined.integerType;
        }

        return null;
    }

    /**
     * Determine the datatype of a variable that can have modifiers.
     * @param varCtx the VariableContext.
     * @param varType the variable's datatype without the modifiers.
     * @return the datatype with any modifiers.
     */
    private Typespec variableDatatype(
                        CKParser.VariableContext varCtx, Typespec varType)
    {
        Typespec type = varType;
        
        return type;
    }
    
    @Override 
    public Object visitNumberFactor(CKParser.NumberFactorContext ctx)
    {
        CKParser.NumberContext          numberCtx   = ctx.number();
        CKParser.UnsignedNumberContext  unsignedCtx = numberCtx.unsignedNumber();
        CKParser.IntegerConstantContext integerCtx  = unsignedCtx.integerConstant();

        ctx.type = (integerCtx != null) ? Predefined.integerType
                                        : Predefined.realType;
        
        return null;
    }

    @Override 
    public Object visitCharacterFactor(
                                    CKParser.CharacterFactorContext ctx)
    {
        ctx.type = Predefined.charType;
        return null;
    }

    @Override
    public Object visitStringFactor(CKParser.StringFactorContext ctx)
    {
        ctx.type = Predefined.stringType;
        return null;
    }

    @Override
    public Object visitStringAnalysis(CKParser.StringAnalysisContext ctx)
    {
        visit(ctx.variable());
        ctx.type = Predefined.stringType;
        return null;
    }

    @Override 
    public Object visitNotFactor(CKParser.NotFactorContext ctx)
    {
        CKParser.FactorContext factorCtx = ctx.factor();
        visit(factorCtx);
        
        if (factorCtx.type != Predefined.booleanType)
        {
            error.flag(TYPE_MUST_BE_BOOLEAN, factorCtx);
        }
        
        ctx.type = Predefined.booleanType;
        return null;
    }

    @Override 
    public Object visitBracketedFactor(
                                    CKParser.BracketedFactorContext ctx)
    {
        CKParser.ExpressionContext exprCtx = ctx.expression();
        visit(exprCtx);
        ctx.type = exprCtx.type;

        return null;
    }
    
    @Override 
    public Object visitPrintStatement(
                                    CKParser.PrintStatementContext ctx)
    {
        visit(ctx.expression());
    	String print = "";
    	if (!(ctx.expression().getText().substring(0,1).equals("'")))		//print variable value
        {
    		String variableName = ctx.expression().getText().toLowerCase();
    		SymtabEntry variableId = symtabStack.lookup(variableName);
    		
    		if (variableId != null)
    		{
    			print = (String) variableId.getValue();

    			boolean alreadyInVariables = false;
    			for (ArrayList<String> l : variables) 
    			{
    				if (l.contains(variableName))
    				{
    					l.set(1, print);
    					alreadyInVariables = true;
    				}
    			}
    			if (!alreadyInVariables)
    			{
        			ArrayList<String> v = new ArrayList<String>();
        			v.add(variableName);
        			v.add(print);
        			variables.add(v);
    			}
    			return print;
    		}
    		else
    		{
    			error.flag(UNDECLARED_IDENTIFIER, ctx);
    		}
        } else     //print text
        {
        	print = (String) ctx.expression().getText();
        	return print;
        }

        return null;
    }
}