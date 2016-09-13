/*********** Import statements  ***********/

/*********** Header  ***********/
%%
%class Lexer    // The class name of the generated Lexical Analyzer class
%unicode
%line
%column
%type Token     // The class name of the class returned by the Lexical
                // Analyzer class when yylex() is called
// %debug       // Uncomment to trouble shoot your definitions

%{
    SymbolTable sym = new SymbolTable();
    private SymbolTableEntry addSymbolTableEntry(Object entry) {
        SymbolTableEntry tableEntry;
        if ((tableEntry = sym.findSymbolTableEntry(entry)) == null) {
            return sym.addSymbolTableEntry(yytext());
        }

        return tableEntry;
    }
%}

%eofval{
    return new Token(TokenCode.EOF, OpType.NONE, DataType.NONE);
%eofval}

/*********** Definitions for patterns of tokens  ***********/
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
WhiteSpace  = {LineTerminator} | [ \t\f]
Letter      = [A-Za-z] | _
Digit       = [0-9]
Identifier  = {Letter}({Letter}|{Digit})* 
//Identifier  = [:jletter:][:jletterdigit:]*
Sign        = (\-|\+)?
Integer     = 0 | [1-9][0-9]*
Fraction    = (\.{Integer}+)
Exponent    = (E{Sign}{Integer})?
RealNumber  = {Integer}{Fraction}{Exponent}
IntNumber   = {Integer}{Exponent}
Unknown     = .

%%

/*********** Translation rules ***********/
/* Assignment operators */
"="         { return new Token(TokenCode.ASSIGNOP, OpType.ASSIGN, DataType.OP); }

/* Arithmetic operators */
"+"         { return new Token(TokenCode.ADDOP, OpType.PLUS, DataType.OP); }
"-"         { return new Token(TokenCode.ADDOP, OpType.MINUS, DataType.OP); }
"*"         { return new Token(TokenCode.MULOP, OpType.MULT, DataType.OP); }
"/"         { return new Token(TokenCode.MULOP, OpType.DIV, DataType.OP); }
"%"         { return new Token(TokenCode.MULOP, OpType.MOD, DataType.OP); }
"++"        { return new Token(TokenCode.INCDECOP, OpType.INC, DataType.OP); }
"--"        { return new Token(TokenCode.INCDECOP, OpType.DEC, DataType.OP); }

/* Relational operators */
"=="        { return new Token(TokenCode.RELOP, OpType.EQUAL, DataType.OP); }
"!="        { return new Token(TokenCode.RELOP, OpType.NOT_EQUAL, DataType.OP); }
"<"         { return new Token(TokenCode.RELOP, OpType.LT, DataType.OP); }
"<="        { return new Token(TokenCode.RELOP, OpType.LTE, DataType.OP); }
">"         { return new Token(TokenCode.RELOP, OpType.GT, DataType.OP); }
">="        { return new Token(TokenCode.RELOP, OpType.GTE, DataType.OP); }

/* Logical operators */
"||"        { return new Token(TokenCode.ADDOP, OpType.OR, DataType.OP); }
"&&"        { return new Token(TokenCode.MULOP, OpType.AND, DataType.OP); }

/* Key words */
"class"     { return new Token(TokenCode.CLASS, OpType.NONE, DataType.KEYWORD); }      // instances
"int"       { return new Token(TokenCode.INT, OpType.NONE, DataType.KEYWORD); }        // type
"real"      { return new Token(TokenCode.REAL, OpType.NONE, DataType.KEYWORD); }       // type
"void"      { return new Token(TokenCode.VOID, OpType.NONE, DataType.KEYWORD); }       // return type
"if"        { return new Token(TokenCode.IF, OpType.NONE, DataType.KEYWORD); }         // statement
"else"      { return new Token(TokenCode.ELSE, OpType.NONE, DataType.KEYWORD); }       // statement
"for"       { return new Token(TokenCode.FOR, OpType.NONE, DataType.KEYWORD); }        // loop
"return"    { return new Token(TokenCode.RETURN, OpType.NONE, DataType.KEYWORD); }     // actions
"break"     { return new Token(TokenCode.BREAK, OpType.NONE, DataType.KEYWORD); }      // actions
"continue"  { return new Token(TokenCode.CONTINUE, OpType.NONE, DataType.KEYWORD); }   // actions

/* Modifiers */
"static"    { return new Token(TokenCode.STATIC, OpType.NONE, DataType.KEYWORD); }

/* Symbols */
"{"         { return new Token(TokenCode.LBRACE, OpType.NONE, DataType.NONE); }
"}"         { return new Token(TokenCode.RBRACE, OpType.NONE, DataType.NONE); }
"("         { return new Token(TokenCode.LPAREN, OpType.NONE, DataType.NONE); }
")"         { return new Token(TokenCode.RPAREN, OpType.NONE, DataType.NONE); }
"]"         { return new Token(TokenCode.RBRACKET, OpType.NONE, DataType.NONE); }
"["         { return new Token(TokenCode.LBRACKET, OpType.NONE, DataType.NONE); }
"!"         { return new Token(TokenCode.NOT, OpType.NONE, DataType.NONE); }
","         { return new Token(TokenCode.COMMA, OpType.NONE, DataType.NONE); }
";"         { return new Token(TokenCode.SEMICOLON, OpType.NONE, DataType.NONE); }

/* Identifier */
{Identifier}    { 
                    if (yytext().length() > 32) {
                        // return a symbol table entry dummy to show incorrect symbol.
                        // it is not added to the symbol table entry list.
                        return new Token(TokenCode.ERR_LONG_ID, OpType.NONE, DataType.ID, 
                                new SymbolTableEntry(yytext()));
                    }
                    return new Token(TokenCode.IDENTIFIER, OpType.NONE, DataType.ID, 
                            addSymbolTableEntry(yytext())); 
                }

/* Number */
{RealNumber}    { return new Token(TokenCode.NUMBER, OpType.NONE, DataType.REAL, 
                          addSymbolTableEntry(yytext())); }
{IntNumber}     { return new Token(TokenCode.NUMBER, OpType.NONE, DataType.INT,
                          addSymbolTableEntry(yytext())); }

/* Other */
{WhiteSpace}    { /* Do Nothing */ }
{Comment}       { /* Do Nothing */ }
{Unknown}       { 
                    // return a symbol table entry dummy to show incorrect symbol.
                    // it is not added to the symbol table entry list.
                    return new Token(TokenCode.ERR_ILL_CHAR, OpType.NONE, DataType.NONE, 
                            new SymbolTableEntry(yytext())); 
                } 


// Hester will be dead by daylight!
