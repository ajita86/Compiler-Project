package backend.compiler;

import antlr4.CKParser;
import intermediate.symtab.*;
import intermediate.type.*;
import intermediate.type.Typespec.Form;

import static intermediate.type.Typespec.Form.*;
import static backend.compiler.Instruction.*;

/**
 * <h1>ExpressionGenerator</h1>
 *
 * <p>Generate code for an expression.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ExpressionGenerator extends CodeGenerator
{
    /**
     * Constructor.
     * @param parent the parent executor.
     */
    public ExpressionGenerator(CodeGenerator parent, Compiler compiler)
    {
        super(parent, compiler);
    }
    
    /**
     * Emit code for an expression.
     * @param ctx the ExpressionContext.
     */
    public void emitExpression(CKParser.ExpressionContext ctx)
    {
        CKParser.RelationExpressionContext relationCtx1 = ctx.relationExpression().get(0);
        CKParser.CypherOpContext cypherOpCtx = ctx.cypherOp();
        emitRelationExpression(relationCtx1);

        if (cypherOpCtx != null)
        {
            String op = cypherOpCtx.getText();
            CKParser.RelationExpressionContext relationCtx2 = ctx.relationExpression().get(1);
            if(op.equals(">>"))
            {
                emitRelationExpression(relationCtx2);
                emit(INVOKESTATIC, "cypher/shift(Ljava/lang/String;I)Ljava/lang/String;");
                //TODO: Emit proper library call
            }
            else
            {
                emitRelationExpression(relationCtx2);
                emit(INVOKESTATIC, "cypher/poly(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
                //TODO: Emit proper library call
            }
        }
    }

    /**
     * Emit code for an expression.
     * @param ctx the ExpressionContext.
     */
    public void emitRelationExpression(CKParser.RelationExpressionContext ctx)
    {
        CKParser.SimpleExpressionContext simpleCtx1 =
                ctx.simpleExpression().get(0);
        CKParser.RelOpContext relOpCtx = ctx.relOp();
        Typespec type1 = simpleCtx1.type;
        emitSimpleExpression(simpleCtx1);

        // More than one simple expression?
        if (relOpCtx != null) {
            String op = relOpCtx.getText();
            CKParser.SimpleExpressionContext simpleCtx2 =
                    ctx.simpleExpression().get(1);
            Typespec type2 = simpleCtx2.type;

            boolean integerMode = false;
            boolean realMode = false;
            boolean characterMode = false;

            if ((type1 == Predefined.integerType)
                    && (type2 == Predefined.integerType)) {
                integerMode = true;
            } else if ((type1 == Predefined.realType)
                    || (type2 == Predefined.realType)) {
                realMode = true;
            } else if ((type1 == Predefined.charType)
                    && (type2 == Predefined.charType)) {
                characterMode = true;
            }

            Label trueLabel = new Label();
            Label exitLabel = new Label();

            if (integerMode || characterMode) {
                emitSimpleExpression(simpleCtx2);

                if (op.equals("==")) emit(IF_ICMPEQ, trueLabel);
                else if (op.equals("<>")) emit(IF_ICMPNE, trueLabel);
                else if (op.equals("<")) emit(IF_ICMPLT, trueLabel);
                else if (op.equals("<=")) emit(IF_ICMPLE, trueLabel);
                else if (op.equals(">")) emit(IF_ICMPGT, trueLabel);
                else if (op.equals(">=")) emit(IF_ICMPGE, trueLabel);
            } else if (realMode) {
                if (type1 == Predefined.integerType) emit(I2F);
                emitSimpleExpression(simpleCtx2);
                if (type2 == Predefined.integerType) emit(I2F);

                emit(FCMPG);

                if (op.equals("==")) emit(IFEQ, trueLabel);
                else if (op.equals("<>")) emit(IFNE, trueLabel);
                else if (op.equals("<")) emit(IFLT, trueLabel);
                else if (op.equals("<=")) emit(IFLE, trueLabel);
                else if (op.equals(">")) emit(IFGT, trueLabel);
                else if (op.equals(">=")) emit(IFGE, trueLabel);
            } else  // stringMode
            {
                emitSimpleExpression(simpleCtx2);
                emit(INVOKEVIRTUAL,
                        "java/lang/String.compareTo(Ljava/lang/String;)I");
                localStack.decrease(1);

                if (op.equals("==")) emit(IFEQ, trueLabel);
                else if (op.equals("<>")) emit(IFNE, trueLabel);
                else if (op.equals("<")) emit(IFLT, trueLabel);
                else if (op.equals("<=")) emit(IFLE, trueLabel);
                else if (op.equals(">")) emit(IFGT, trueLabel);
                else if (op.equals(">=")) emit(IFGE, trueLabel);
            }

            emit(ICONST_0); // false
            emit(GOTO, exitLabel);
            emitLabel(trueLabel);
            emit(ICONST_1); // true
            emitLabel(exitLabel);

            localStack.decrease(1);  // only one branch will be taken
        }
    }
    
    /**
     * Emit code for a simple expression.
     * @param ctx the SimpleExpressionContext.
     */
    public void emitSimpleExpression(CKParser.SimpleExpressionContext ctx)
    {
        int count = ctx.term().size();
        Boolean negate =    (ctx.sign() != null) 
                         && ctx.sign().getText().equals("-");
        
        // First term.
        CKParser.TermContext termCtx1 = ctx.term().get(0);
        Typespec type1 = termCtx1.type;
        emitTerm(termCtx1);
        
        if (negate) emit(type1 == Predefined.integerType ? INEG : FNEG);
        
        // Loop over the subsequent terms.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.addOp().get(i-1).getText().toLowerCase();
            CKParser.TermContext termCtx2 = ctx.term().get(i);
            Typespec type2 = termCtx2.type;

            boolean integerMode = false;
            boolean realMode    = false;
            boolean booleanMode = false;

            if (   (type1 == Predefined.integerType)
                && (type2 == Predefined.integerType)) 
            {
                integerMode = true;
            }
            else if (   (type1 == Predefined.realType) 
                     || (type2 == Predefined.realType))
            {
                realMode = true;
            }
            else if (   (type1 == Predefined.booleanType) 
                     && (type2 == Predefined.booleanType))
            {
                booleanMode = true;
            }
                            
            if (integerMode)
            {
                emitTerm(termCtx2);
                
                if (op.equals("+")) emit(IADD);
                else                emit(ISUB);
            }
            else if (realMode)
            {
                if (type1 == Predefined.integerType) emit(I2F);
                emitTerm(termCtx2);
                if (type2 == Predefined.integerType) emit(I2F);
                
                if (op.equals("+")) emit(FADD);
                else                emit(FSUB);
            }
            else if (booleanMode)
            {
                emitTerm(termCtx2);
                emit(IOR);
            }
            else  // stringMode
            {
                emit(NEW, "java/lang/StringBuilder");
                emit(DUP_X1);             
                emit(SWAP);                  
                emit(INVOKESTATIC, "java/lang/String/valueOf(Ljava/lang/Object;)" +
                                   "Ljava/lang/String;");
                emit(INVOKESPECIAL, "java/lang/StringBuilder/<init>" +
                                    "(Ljava/lang/String;)V");
                localStack.decrease(1);
                
                emitTerm(termCtx2);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/append(Ljava/lang/String;)" +
                                    "Ljava/lang/StringBuilder;");
                localStack.decrease(1);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/toString()" +
                                    "Ljava/lang/String;");
                localStack.decrease(1);
            }
        }
    }
    
    /**
     * Emit code for a term.
     * @param ctx the TermContext.
     */
    public void emitTerm(CKParser.TermContext ctx)
    {
        int count = ctx.factor().size();
        
        // First factor.
        CKParser.FactorContext factorCtx1 = ctx.factor().get(0);
        Typespec type1 = factorCtx1.type;
        compiler.visit(factorCtx1);
        
        // Loop over the subsequent factors.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.mulOp().get(i-1).getText().toLowerCase();
            CKParser.FactorContext factorCtx2 = ctx.factor().get(i);
            Typespec type2 = factorCtx2.type;

            boolean integerMode = false;
            boolean realMode    = false;

            if (   (type1 == Predefined.integerType)
                && (type2 == Predefined.integerType)) 
            {
                integerMode = true;
            }
            else if (   (type1 == Predefined.realType) 
                     || (type2 == Predefined.realType))
            {
                realMode = true;
            }
                
            if (integerMode)
            {
                compiler.visit(factorCtx2);            

                if      (op.equals("*"))   emit(IMUL);
                else if (op.equals("/"))   emit(FDIV);
                else if (op.equals("div")) emit(IDIV);
                else if (op.equals("mod")) emit(IREM);
            }
            else if (realMode)
            {
                if (type1 == Predefined.integerType) emit(I2F);
                compiler.visit(factorCtx2); 
                if (type2 == Predefined.integerType) emit(I2F);
                
                if      (op.equals("*")) emit(FMUL);
                else if (op.equals("/")) emit(FDIV);
            }
            else  // booleanMode
            {
                compiler.visit(factorCtx2);                 
                emit(IAND);
            }
        }
    }
    
    /**
     * Emit code for NOT.
     * @param ctx the NotFactorContext.
     */
    public void emitNotFactor(CKParser.NotFactorContext ctx)
    {
        compiler.visit(ctx.factor());
        emit(ICONST_1);
        emit(IXOR);
    }

    public void emitStringAnalysis(CKParser.StringAnalysisContext ctx)
    {
        emitLoadVariable(ctx.variable());
        emit(INVOKESTATIC, "cypher/analysis(Ljava/lang/String;)Ljava/lang/String;");
        //TODO: emit proper function call
    }

    /**
     * Emit code to load a scalar variable's value 
     * or a structured variable's address.
     * @param varCtx the VariableContext.
     */
    public void emitLoadValue(CKParser.VariableContext varCtx)
    {
        // Load the scalar value or structure address.
        Typespec variableType = emitLoadVariable(varCtx);
    }
    
    /**
     * Emit code to load a scalar variable's value 
     * or a structured variable's address.
     * @param varCtx the variable node.
     * @return the datatype of the variable.
     */
    public Typespec emitLoadVariable(CKParser.VariableContext varCtx)
    {
        SymtabEntry variableId = varCtx.entry;
        Typespec variableType = variableId.getType();
        
        // Scalar value or structure address.
        emitLoadValue(variableId);

        return variableType;
    }
    
    /**
     * Emit code to load an integer constant.
     * @parm intCtx the IntegerConstantContext.
     */
    public void emitLoadIntegerConstant(CKParser.NumberContext intCtx)
    {
        int value = Integer.parseInt(intCtx.getText());
        emitLoadConstant(value);
    }
    
    /**
     * Emit code to load real constant.
     * @parm intCtx the IntegerConstantContext.
     */
    public void emitLoadRealConstant(CKParser.NumberContext realCtx)
    {
        float value = Float.parseFloat(realCtx.getText());
        emitLoadConstant(value);
    }
}
