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
    private SymbolTableEntry AddSymbolTableEntry(Object entry) {
        SymbolTableEntry tableEntry;
        if ((tableEntry = sym.findSymbolTableEntry(entry)) == null) {
            return sym.addSymbolTableEntry(yytext());
        }

        return tableEntry;
    }
%}

%eofval{
    return new Token(TokenCode.EOF);
%eofval}

/*********** Definitions for patterns of tokens  ***********/
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
WhiteSpace     = {LineTerminator} | [ \t\f]
Identifier  = [:jletter:][:jletterdigit:]*
Sign        = (\-|\+)?
Integer     = 0 | [1-9][0-9]*
Fraction    = (\.{Integer}+)?
Exponent    = (E{Sign}{Integer})?
Number      = {Integer}{Fraction}{Exponent}
Unknown     = .

%%

/*********** Translation rules ***********/
/* Assignment operators */
"="         { return new Token(TokenCode.ASSIGNOP); }

/* Arithmetic operators */
"+"         { return new Token(TokenCode.ADDOP, OpType.PLUS); }
"-"         { return new Token(TokenCode.ADDOP, OpType.MINUS); }
"*"         { return new Token(TokenCode.MULOP, OpType.MUL); }
"/"         { return new Token(TokenCode.MULOP, OpType.DIV); }
"%"         { return new Token(TokenCode.MULOP, OpType.MOD); }
"++"        { return new Token(TokenCode.INCDECOP, OpType.INC); }
"--"        { return new Token(TokenCode.INCDECOP, OpType.DEC); }

/* Relational operators */
"=="        { return new Token(TokenCode.RELOP, OpType.EQ); }
"!="        { return new Token(TokenCode.RELOP, OpType.NE); }
"<"         { return new Token(TokenCode.RELOP, OpType.LT); }
"<="        { return new Token(TokenCode.RELOP, OpType.LE); }
">"         { return new Token(TokenCode.RELOP, OpType.GT); }
">="        { return new Token(TokenCode.RELOP, OpType.GE); }

/* Logical operators */
"||"        { return new Token(TokenCode.ADDOP, OpType.OR); }
"&&"        { return new Token(TokenCode.MULOP, OpType.AND); }
"!"         { return new Token(TokenCode.NOT, OpType.NOT); }

/* Key words */
"class"     { return new Token(TokenCode.CLASS); }      // instances
"int"       { return new Token(TokenCode.INT); }        // type
"real"      { return new Token(TokenCode.REAL); }       // type
"void"      { return new Token(TokenCode.VOID); }       // return type
"if"        { return new Token(TokenCode.IF); }         // statement
"else"      { return new Token(TokenCode.ELSE); }       // statement
"for"       { return new Token(TokenCode.FOR); }        // loop
"return"    { return new Token(TokenCode.RETURN); }     // actions
"break"     { return new Token(TokenCode.BREAK); }      // actions
"continue"  { return new Token(TokenCode.CONTINUE); }   // actions

/* Modifiers */
"static"    { return new Token(TokenCode.STATIC); }

/* Symbols */
"{"         { return new Token(TokenCode.LBRACE); }
"}"         { return new Token(TokenCode.RBRACE); }
"("         { return new Token(TokenCode.LPAREN); }
")"         { return new Token(TokenCode.RPAREN); }
","         { return new Token(TokenCode.COMMA); }
";"         { return new Token(TokenCode.SEMICOLON); }

/* EOF */
"EOF"       { return new Token(TokenCode.EOF); }

/* Identifier */
{Identifier}    { 
                    if (yytext().length() > 32) {
                        return new Token(TokenCode.ERR_LONG_ID);
                    }
                    SymbolTableEntry entry = AddSymbolTableEntry(yytext());
                    return new Token(TokenCode.IDENTIFIER, DataType.REAL, entry); 
                }

/* Number */
{Number}        { 
                    SymbolTableEntry entry = AddSymbolTableEntry(yytext());
                    return new Token(TokenCode.NUMBER, DataType.INT, entry); 
                }

/* Other */
{WhiteSpace}    { /* Do Nothing */ }
{Comment}       { /* Do Nothing */ }
{Unknown}       { return new Token(TokenCode.ERR_ILL_CHAR); } // do not have to return symbol


// Hester will be dead by daylight!
