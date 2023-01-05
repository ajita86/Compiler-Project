grammar CK;

@header {
    package antlr4;
    import java.util.HashMap;
    import intermediate.symtab.SymtabEntry;
    import intermediate.type.Typespec;
}

program           : programHeader compoundStatement ;
programHeader     : PROGRAM programIdentifier '[' programParameters ']';
programParameters : IDENTIFIER ( ',' IDENTIFIER )* ;

programIdentifier   locals [ SymtabEntry entry = null ]
    : IDENTIFIER ;

statement : compoundStatement
          | assignmentStatement
          | ifStatement
          | whileStatement
          | printStatement
          | functionDefinitionStatement
          | functionCallStatement
          | variableDeclarationStatement
          | emptyStatement
          ;

variableDeclarationStatement : typeIdentifier variableIdentifier;
typeIdentifier      locals [ Typespec type = null, SymtabEntry entry = null ]
    : IDENTIFIER ;
variableIdentifier locals [ Typespec type = null, SymtabEntry entry = null ]
    : IDENTIFIER;

compoundStatement : '{' statementList '}' ;
emptyStatement : ;
     
statementList       : statement ( ';' statement )* ;
assignmentStatement : lhs '=' rhs ;

lhs locals [ Typespec type = null ] 
    : variable ;
rhs locals [ Typespec type = null ]
    : expression ;

ifStatement    : IF '[' expression ']' trueStatement ( ELSE falseStatement )? ;
trueStatement  : statement ;
falseStatement : statement ;

whileStatement  : WHILE '[' expression ']' statement ;

functionCallStatement : functionCall ;

argumentList : argument ( ',' argument )* ;
argument     : expression ;

printStatement   : PRINT '[' expression ']' ;

expression          locals [ Typespec type = null ] 
    : relationExpression (cypherOp relationExpression)? ;

relationExpression        locals [ Typespec type = null ]
    : simpleExpression (relOp simpleExpression)? ;

sign : '-' | '+' ;
    
simpleExpression    locals [ Typespec type = null ] 
    : sign? term (addOp term)* ;
    
term                locals [ Typespec type = null ]
    : factor (mulOp factor)* ;

factor              locals [ Typespec type = null ] 
    : variable             # variableFactor
    | number               # numberFactor
    | characterConstant    # characterFactor
    | stringConstant       # stringFactor
    | functionCall         # functionCallFactor
    | NOT factor           # notFactor
    | '[' expression ']'   # bracketedFactor
    | variable '@'         # stringAnalysis
    ;

variable            locals [ Typespec type = null, SymtabEntry entry = null ] 
    : variableIdentifier ;

functionDefinitionStatement     locals [ Typespec type = null, SymtabEntry entry = null ]
    : FUNCTION typeIdentifier functionName '[' defArgumentList? ']' statement ;
defArgumentList : typeIdentifier variable (',' typeIdentifier variable)* ;
functionCall : functionName '[' argumentList? ']' ;
functionName        locals [ Typespec type = null, SymtabEntry entry = null ] 
    : IDENTIFIER ;
     
number          : sign? unsignedNumber ;
unsignedNumber  : integerConstant | doubleConstant ;
integerConstant : INTEGER ;
doubleConstant    : DOUBLE;

characterConstant : CHARACTER ;
stringConstant    : STRING ;
       
relOp : '==' | '!=' | '<' | '<=' | '>' | '>=' ;
addOp : '+' | '-' | OR ;
mulOp : '*' | '/' | DIV | MOD | AND ;
cypherOp : '>>' | '<<' ;

fragment A : ('a' | 'A') ;
fragment B : ('b' | 'B') ;
fragment C : ('c' | 'C') ;
fragment D : ('d' | 'D') ;
fragment E : ('e' | 'E') ;
fragment F : ('f' | 'F') ;
fragment G : ('g' | 'G') ;
fragment H : ('h' | 'H') ;
fragment I : ('i' | 'I') ;
fragment J : ('j' | 'J') ;
fragment K : ('k' | 'K') ;
fragment L : ('l' | 'L') ;
fragment M : ('m' | 'M') ;
fragment N : ('n' | 'N') ;
fragment O : ('o' | 'O') ;
fragment P : ('p' | 'P') ;
fragment Q : ('q' | 'Q') ;
fragment R : ('r' | 'R') ;
fragment S : ('s' | 'S') ;
fragment T : ('t' | 'T') ;
fragment U : ('u' | 'U') ;
fragment V : ('v' | 'V') ;
fragment W : ('w' | 'W') ;
fragment X : ('x' | 'X') ;
fragment Y : ('y' | 'Y') ;
fragment Z : ('z' | 'Z') ;

PROGRAM   : P ;
CONST     : C O N S T ;
DIV       : D I V ;
MOD       : M O D ;
AND       : A N D ;
OR        : O R ;
NOT       : N O T ;
IF        : I F ;
THEN      : T H E N ;
ELSE      : E L S E ;
WHILE     : W H I L E ;
PRINT     : P R I N T ;
FUNCTION  : F ;

IDENTIFIER : [a-zA-Z][a-zA-Z0-9]* ;
INTEGER    : [0-9]+ ;

DOUBLE     : INTEGER '.' INTEGER
           | INTEGER ('e' | 'E') ('+' | '-')? INTEGER
           | INTEGER '.' INTEGER ('e' | 'E') ('+' | '-')? INTEGER
           ;

NEWLINE : '\r'? '\n' -> skip  ;
WS      : [ \t]+ -> skip ; 

QUOTE     : '\'' ;
CHARACTER : QUOTE CHARACTER_CHAR QUOTE ;
STRING    : QUOTE STRING_CHAR* QUOTE ;

fragment CHARACTER_CHAR : ~('\'')   // any non-quote character
                        ;

fragment STRING_CHAR : QUOTE QUOTE  // two consecutive quotes
                     | ~('\'')      // any non-quote character
                     ;

COMMENT : '%' COMMENT_CHARACTER* '%' -> skip ;

fragment COMMENT_CHARACTER : ~('%') ;
                     
