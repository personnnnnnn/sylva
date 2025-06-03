grammar SylvaBytecode;

AT : '@' ;
COMMA : ',' ;

LPAREN : '(' ;
RPAREN : ')' ;

STRING  : '"' (~[\\"] | '\\' .)* '"' ;
INTEGER : [0-9]+ ;
FLOAT : INTEGER'.'INTEGER ;
BOOL : 'true' | 'false' ;

ID : [a-zA-Z_][a-zA-Z_0-9\-#]* ;

COMMENT : ';' ~[\n]* -> skip ;
WHITESPACE  : [ \r\f\n\t]+ -> skip ;

program : command* EOF ;

varid : '&'ID | INTEGER ',' INTEGER;

codelocation : '@' ID | INTEGER ;

command
    : '@' ID # Location
    | '(' ('&'ID)* ')' '{' command* '}' # VariableManager
    | 'STR' '(' STRING ')' # Str
    | 'INT' '(' INTEGER ')' # Int
    | 'FLT' '(' FLOAT ')' # Flt
    | 'NIL' # Nil
    | 'TRUE' # BTrue
    | 'FALSE' # BFalse
    | 'RETURN' # Return
    | 'JMP' '(' codelocation ')' # Jmp
    | 'SKP' # Skip
    | 'BNOT' # BNot
    | 'UMN' # Umn
    | 'EQ' # Eq
    | 'LT' # Lt
    | 'GT' # Gt
    | 'ADD' # Add
    | 'SUB' # Sub
    | 'MUL' # Mul
    | 'DIV' # Div
    | 'MOD' # Mod
    | 'ARGUMENTS' # Arguments
    | 'CALL' # Call
    | 'FUNCTION' '(' codelocation ')' # Function
    | 'GET' '(' varid ')' # Get
    | 'SET' '(' varid ')' # Set
    | 'PUSH' # Push
    | 'POP' # Pop
    | 'GET_ATTR' '(' STRING ')' # GetAttr
    | 'SET_ATTR' '(' STRING ')' # SetAttr
    | 'GET_IDX' # GetIdx
    | 'SET_IDX' # SetIdx
    | 'NOP' # Nop
    | 'REM' # Rem
    | 'DUP' # Dup
    | 'TO_STRING' # ToString
    | 'NEEDED_ARG' '(' STRING ',' varid ')' # NeededArg
    | 'OPTIONAL_ARG' '(' STRING ',' varid ',' codelocation ')' # OptionalArg
    | 'SPREAD_ARG' '(' STRING ',' varid ')' # SpreadArg
    | 'NO_MORE_ARGUMENTS' # NoMoreArguments
    | 'SPREAD' # Spread
    | 'SET_MULTIPLE' '(' varid (',' varid)+ ')' # SetMultiple
    | 'GET_LIBRARY' '(' STRING ')' # GetLibrary
    ;
