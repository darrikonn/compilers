/*********** Import statements  ***********/

/*********** Header  ***********/
%%
%class Lexer    // The class name of the generated Lexical Analyzer class
%unicode
%line           // Used to get the line number
%column         // Used to get the column number
%type Token     // The class name of the class returned by the Lexical
                // Analyzer class when yylex() is called
// %debug       // Uncomment to trouble shoot your definitions

%{
    private SymbolTableEntry addSymbolTableEntry() {
        SymbolTableEntry tableEntry;
        if ((tableEntry = SymbolTable.lookup(yytext())) == null) {
            return SymbolTable.insert(yytext());
        }

        return tableEntry;
    }
%}

%eofval{
    return new Token(TokenCode.EOF, OpType.NONE, DataType.NONE, yyline, yycolumn);
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
"="         { return new Token(TokenCode.ASSIGNOP, OpType.ASSIGN, DataType.OP, yyline, yycolumn); }

/* Arithmetic operators */
"+"         { return new Token(TokenCode.ADDOP, OpType.PLUS, DataType.OP, yyline, yycolumn); }
"-"         { return new Token(TokenCode.ADDOP, OpType.MINUS, DataType.OP, yyline, yycolumn); }
"*"         { return new Token(TokenCode.MULOP, OpType.MULT, DataType.OP, yyline, yycolumn); }
"/"         { return new Token(TokenCode.MULOP, OpType.DIV, DataType.OP, yyline, yycolumn); }
"%"         { return new Token(TokenCode.MULOP, OpType.MOD, DataType.OP, yyline, yycolumn); }
"++"        { return new Token(TokenCode.INCDECOP, OpType.INC, DataType.OP, yyline, yycolumn); }
"--"        { return new Token(TokenCode.INCDECOP, OpType.DEC, DataType.OP, yyline, yycolumn); }

/* Relational operators */
"=="        { return new Token(TokenCode.RELOP, OpType.EQUAL, DataType.OP, yyline, yycolumn); }
"!="        { return new Token(TokenCode.RELOP, OpType.NOT_EQUAL, DataType.OP, yyline, yycolumn); }
"<"         { return new Token(TokenCode.RELOP, OpType.LT, DataType.OP, yyline, yycolumn); }
"<="        { return new Token(TokenCode.RELOP, OpType.LTE, DataType.OP, yyline, yycolumn); }
">"         { return new Token(TokenCode.RELOP, OpType.GT, DataType.OP, yyline, yycolumn); }
">="        { return new Token(TokenCode.RELOP, OpType.GTE, DataType.OP, yyline, yycolumn); }

/* Logical operators */
"||"        { return new Token(TokenCode.ADDOP, OpType.OR, DataType.OP, yyline, yycolumn); }
"&&"        { return new Token(TokenCode.MULOP, OpType.AND, DataType.OP, yyline, yycolumn); }

/* Key words */
"class"     { return new Token(TokenCode.CLASS, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }      // instances
"int"       { return new Token(TokenCode.INT, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }        // type
"real"      { return new Token(TokenCode.REAL, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }       // type
"void"      { return new Token(TokenCode.VOID, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }       // return type
"if"        { return new Token(TokenCode.IF, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }         // statement
"else"      { return new Token(TokenCode.ELSE, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }       // statement
"for"       { return new Token(TokenCode.FOR, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }        // loop
"return"    { return new Token(TokenCode.RETURN, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }     // actions
"break"     { return new Token(TokenCode.BREAK, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }      // actions
"continue"  { return new Token(TokenCode.CONTINUE, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }   // actions

/* Modifiers */
"static"    { return new Token(TokenCode.STATIC, OpType.NONE, DataType.KEYWORD, yyline, yycolumn); }

/* Symbols */
"{"         { return new Token(TokenCode.LBRACE, OpType.NONE, DataType.NONE, yyline, yycolumn); }
"}"         { return new Token(TokenCode.RBRACE, OpType.NONE, DataType.NONE, yyline, yycolumn); }
"("         { return new Token(TokenCode.LPAREN, OpType.NONE, DataType.NONE, yyline, yycolumn); }
")"         { return new Token(TokenCode.RPAREN, OpType.NONE, DataType.NONE, yyline, yycolumn); }
"]"         { return new Token(TokenCode.RBRACKET, OpType.NONE, DataType.NONE, yyline, yycolumn); }
"["         { return new Token(TokenCode.LBRACKET, OpType.NONE, DataType.NONE, yyline, yycolumn); }
"!"         { return new Token(TokenCode.NOT, OpType.NONE, DataType.NONE, yyline, yycolumn); }
","         { return new Token(TokenCode.COMMA, OpType.NONE, DataType.NONE, yyline, yycolumn); }
";"         { return new Token(TokenCode.SEMICOLON, OpType.NONE, DataType.NONE, yyline, yycolumn); }

/* Identifier */
{Identifier}    { 
                    if (yytext().length() > 32) {
                        // return a symbol table entry dummy to show incorrect symbol.
                        // it is not added to the symbol table entry list.
                        return new Token(TokenCode.ERR_LONG_ID, OpType.NONE, DataType.ID, 
                                new SymbolTableEntry(yytext()), yyline, yycolumn);
                    }
                    return new Token(TokenCode.IDENTIFIER, OpType.NONE, DataType.ID, 
                            addSymbolTableEntry(), yyline, yycolumn); 
                }

/* Number */
{RealNumber}    { return new Token(TokenCode.NUMBER, OpType.NONE, DataType.REAL, 
                          addSymbolTableEntry(), yyline, yycolumn); }
{IntNumber}     { return new Token(TokenCode.NUMBER, OpType.NONE, DataType.INT,
                          addSymbolTableEntry(), yyline, yycolumn); }

/* Other */
{WhiteSpace}    { /* Do Nothing */ }
{Comment}       { /* Do Nothing */ }
{Unknown}       { 
                    // return a symbol table entry dummy to show incorrect symbol.
                    // it is not added to the symbol table entry list.
                    return new Token(TokenCode.ERR_ILL_CHAR, OpType.NONE, DataType.NONE, 
                            new SymbolTableEntry(yytext()), yyline, yycolumn); 
                } 


// Hester meistaramalari!
