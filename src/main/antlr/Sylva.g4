grammar Sylva;

AND : '&&' ;
QMARK : '?' ;
OR : '||' ;
NOT : '!' ;
NEQ : '!=' ;
EQ : '==' ;
EQ_SIGN : '=' ;
ARR : '=>' ;
COMMA : ',' ;
SEMI : ';' ;
DDOT : '..' ;
LPAREN : '(' ;
RPAREN : ')' ;
LCURLY : '{' ;
RCURLY : '}' ;
TDOT : '...' ;
STAR : '*' ;
SLASH : '/' ;
PERC : '%' ;
LET : 'let' ;
PUB : 'pub' ;
POKE : 'poke' ;
FN : 'fn' ;
IN : 'in' ;
HAS : 'has' ;
FOR : 'for' ;
IF : 'if' ;
UNLESS : 'unless' ;
WHILE : 'while' ;
UNTIL : 'until' ;
CONTINUE : 'continue' ;
BREAK : 'break' ;
RETURN : 'return' ;
BY : 'by' ;
TO : 'to' ;
COPYOF : 'copyof' ;
AS : 'as' ;
INDEFINITELY : 'indefinitely' ;
WITH : 'with' ;
ELSE : 'else' ;
TOSTRING : 'tostring' ;
TRUE : 'true' ;
FALSE : 'false' ;
NIL : 'nil' ;
PLUS : '+' ;
MINUS : '-' ;
DOT : '.' ;
LBRACK : '[' ;
RBRACK : ']' ;
HASH : '#' ;
STRING
    : '\''(~[\\'\n\r]|'\\'~[\n\r])*'\''
    | '"'(~[\\"\n\r]|'\\'~[\n\r])*'"'
    ;
MULTILINESTRING
    : '\'\'\''.*?'\'\'\''
    | '"""'.*?'"""'
    ;

INT : [0-9]+ ;
FLOAT : [0-9]+'.' | '.'[0-9]+ | [0-9]+'.'[0-9]+ ;
ID: [a-zA-Z_][a-zA-Z0-9]* ;
WS: [ \t\n\r\f]+ -> skip ;
COMMENT : '//' ~[\n\r]* -> skip ;
MULTILINECOMMENT : '/*' .*? '*/' -> skip ;

program
    : withentry* stmt* EOF # FullProgram
    ;

idaccess
    : expr '.' ID # AttributeSet
    | expr '[' expr ']' # IndexSet
    | ID # VarSet
    ;

varSetValues : idaccess (',' idaccess)* # SetValues ;

defvalues : ID (',' ID)* ;

stmt: 'let' (PUB|POKE)? defvalues '=' expr # LetValueStatement
    | 'let' (PUB|POKE)? defvalues # LetStatement
    | 'return' expr # ReturnValueStatement
    | 'return' # SimpleReturnStatement
    | ifstmt # IfStatement
    | forstmt # ForStatement
    | funcdef # FunctionDefinitionStatement
    | whilestmt # WhileStatement
    | indefinitestmt # IndefiniteStatement
    | varSetValues '=' expr # SetStatement
    | expr # ExpressionStatement
    | 'break' # BreakStatement
    | 'continue' # ContinueStatement
    ;

expr: expr 'to' expr ('by' expr)? # RangeExpression
    | op=('!'|'+'|'-'|'#'|'copyof'|'tostring'|'typeof') expr # UnaryOpExpr
    | expr '..' expr # ConcatExpr
    | expr op=('*'|'/'|'%') expr # OtherOpExpr
    | expr op=('+'|'-') expr # AddSubOpExpr
    | expr '?' expr # OptionalExpr
    | expr op=('has'|'in') expr # HasExpr
    | expr op=('=='|'!='|'<'|'<='|'>'|'>=') expr # ComparisonExpr
    | expr op=('&&'|'||') expr # LogicExpr
    | value # ValueExpr
    ;

tableItem
   : ID '=' expr # TableEntryId
   | '[' expr ']' '=' expr # TableEntryValue
   | '...' expr # TableEntrySpread
   ;

value
    : INT # IntValue
    | FLOAT # FloatValue
    | MULTILINESTRING # MultilineStringValue
    | STRING # StringValue
    | TRUE # TrueValue
    | FALSE # FalseValue
    | NIL # NilValue
    | 'fn' genbody # FnValue
    | '[' ('...'? expr (',' '...'? expr)* ','?)? ']' # ArrayValue
    | '{' (tableItem (',' tableItem)* ','?)? '}' # TableValue
    | ID # VarAccess
    | funcval # FunctionValue
    | ifexpr # IfExprValue
    | '(' expr ')' # ParensValue
    | value '.' ID # AttributeValue
    | value '[' expr ']' # IndexValue
    | value callargs # CallExpr
    ;

arg : '...' ID # ExpandedArg
    | ID '=' expr # ArgWithDefault
    | ID # NormalArg
    ;

args : '(' (arg (',' arg)* ','?)? ')' # FnArgumentList ;

callargs : '(' ('...'? expr (',' '...'? expr)* ','?)? ')' # CallArgumentList ;

funcdef : PUB? 'fn' name=ID args fnbody # FunctionDefintion ;

funcval
    : 'fn' args fnbody # AnonFunctionValue
    | 'fn' name=ID args fnbody # NamedFunctionValue
    ;

genbody : '{' stmt* '}' # GeneralBody ;

fnbody
    : genbody # BodyEval
    | '=>' expr # ExprEval
    ;

ifexpr
    : type=('if'|'unless') '(' expr ')' fnbody 'else' ifexpr # IfExprContinuation
    | type=('if'|'unless') '(' expr ')' fnbody 'else' fnbody # IfExprElse
    | type=('if'|'unless') '(' expr ')' fnbody # IfExprSimple
    ;

ifstmt
    : type=('if'|'unless') '(' expr ')' genbody 'else' ifstmt # IfStatementContinuation
    | type=('if'|'unless') '(' expr ')' genbody 'else' genbody # IfStatementElse
    | type=('if'|'unless') '(' expr ')' genbody # IfStatementSimple
    ;

forstmt : 'for' '(' defvalues 'in' expr ')' genbody # For ;

whilestmt : type=('while'|'until') '(' expr ')' genbody # While ;

indefinitestmt : 'indefinitely' genbody # Indefinite ;

withentry
    : 'with' withstmt # SingleImport
    | 'with' '{' withstmt* '}' # MultipleImport
    ;

withname : ID ('.' ID)* # ImportName ;

withstmt
    : withname 'as' ID # RenameImport
    | withname '{' withstmt* '}' # MassImport
    | withname # Import
    ;
