grammar SylvaBytecode;

AT : '@' ;
COMMA : ',' ;

LPAREN : '(' ;
RPAREN : ')' ;

STRING  : '"' (~[\\"] | '\\' .)* '"' ;
INTEGER : [0-9]+ ;
FLOAT : INTEGER'.'INTEGER ;
BOOL : 'true' | 'false' ;

ID : [a-zA-Z_][a-zA-Z_0-9\-]* ;

COMMENT : ';' ~[\n]* -> skip ;
WHITESPACE  : [ \r\f\n\t]+ -> skip ;

program : command* EOF ;

codelocation : '@' ID | INTEGER ;

command
    : '@' ID # Location
    | 'STR' '(' STRING ')' # Str
    | 'INT' '(' INTEGER ')' # Int
    | 'FLT' '(' FLOAT ')' # Flt
    | 'NIL' # Nil
    | 'TRUE' # BTrue
    | 'FALSE' # BFalse
    | 'RETURN' # Return
    | 'JMP' '(' codelocation ')' # Jmp
    | 'SKP' # Skip
    | 'NSKP' # NSkip
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
    | 'FUNCTION' '(' codelocation ',' INTEGER ',' INTEGER ',' BOOL ')' # Function
    | 'GET' '(' INTEGER ',' INTEGER ')' # Get
    | 'SET' '(' INTEGER ',' INTEGER ')' # Set
    | 'PUSH' # Push
    | 'POP' # Pop
    | 'GETATTR' '(' STRING ')' # GetAttr
    | 'SETATTR' '(' STRING ')' # SetAttr
    | 'GETIDX' # GetIdx
    | 'SETIDX' # SetIdx
    | 'NOP' # Nop
    | 'REM' # Rem
    | 'DUP' # Dup
    | 'TOSTRING' # ToString
    ;
