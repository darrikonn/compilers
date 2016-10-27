/* 
  Reference implementation of a Lexical Analyser for Decaf
  Compiler Course, T-603-THYDE - Fall 2014
  Author: Fridjon Gudjohnsen & Thorgeir Audunn Karlsson
*/



%%
%class Lexer
%unicode
%line
%column
%type Token
// %standalone
// %debug

%eofval{
  return createRawToken(TokenCode.EOF);
%eofval}

Letter 			   = [a-z] | [A-Z]
Letter_ 		   = {Letter} | [_]
Digit 			   = [0-9]
Identifier 		   = {Letter_} ({Letter_} | {Digit})*
IntNum 			   = {Digit}+
OptionalFraction   = (\.{IntNum})?
OptionalExponent   = ("E" ( "+" | "-" )? {IntNum})?
RealNum            = {IntNum} {OptionalFraction} {OptionalExponent}

IncDecOp = "++" | "--"
RelOp    = "==" | "!=" | "<" | ">" | "<=" | ">="
AddOp    = "+"  | "-"  | "||"
MulOp    = "*"  | "/"  | "%" | "&&"
AssignOp = "="

// Reserved words
Class    = "class"
Void     = "void"
If       = "if"
Else     = "else"
For      = "for"
Return   = "return"
Break    = "break"
Continue = "continue"
Int      = "int"
Real     = "real"
Static   = "static"


LBrace   = "{"
RBrace   = "}"
LBracket = "["
RBracket = "]"
LParen   = "("
RParen   = ")"

// Other tokens
Semicolon = ";"
Comma     = ","
Not       = "!"

Comment = "/*" ~"*/" 
WS      = [ \t\r\n]

%{
  private SymbolTableEntry createOrInsertSymTabEntry() {
    SymbolTableEntry symTabEntry = SymbolTable.lookup(yytext());
    if (symTabEntry == null)
      symTabEntry = SymbolTable.insert(yytext());
    return symTabEntry;
  } 

  private Token createRawToken(TokenCode tcode) {
    Token retVal = Token.createRaw(tcode);
    retVal.setLineColumn(yyline, yycolumn);
    return retVal;
  }

  private Token createRealToken(SymbolTableEntry entry) {
    Token retVal = Token.createReal(entry);
    retVal.setLineColumn(yyline, yycolumn);
    return retVal;
  }

  private Token createIntToken(SymbolTableEntry entry) {
    Token retVal = Token.createInt(entry);
    retVal.setLineColumn(yyline, yycolumn);
    return retVal;
  }

  private Token createIdToken(SymbolTableEntry entry) {
    Token retVal = Token.createId(entry);
    retVal.setLineColumn(yyline, yycolumn);
    return retVal;
  }

  private Token createOpToken(TokenCode tcode, OpType opType) {
    Token retVal = Token.createOp(tcode, opType);
    retVal.setLineColumn(yyline, yycolumn);
    return retVal;
  }

  private Token createRelOpToken(String str) {
    Token retVal = Token.createRelOp(str);
    retVal.setLineColumn(yyline, yycolumn);
    return retVal;
  }

  private Token createMulOpToken(String str) {
    Token retVal = Token.createMulOp(str);
    retVal.setLineColumn(yyline, yycolumn);
    return retVal;
  }

  private Token createAddOpToken(String str) {
    Token retVal = Token.createAddOp(str);
    retVal.setLineColumn(yyline, yycolumn);
    return retVal;
  }
%}

%%
{Comment}       { /* Ignore comments   */ }
{WS}            { /* Ignore whitespace */ }

{IncDecOp}      { 
                  OpType op = yytext().equals("++") ? OpType.INC : OpType.DEC;
                  return createOpToken(TokenCode.INCDECOP, op); 
                }

{RelOp}         { return createRelOpToken(yytext()); }

{MulOp}         { return createMulOpToken(yytext()); }
{AddOp}         { return createAddOpToken(yytext()); }

{AssignOp}      { return createOpToken(TokenCode.ASSIGNOP, OpType.ASSIGN); }


{Class}         { return createRawToken(TokenCode.CLASS); }
{Void}          { return createRawToken(TokenCode.VOID); }
{If}            { return createRawToken(TokenCode.IF); }
{Else}          { return createRawToken(TokenCode.ELSE); }
{For}           { return createRawToken(TokenCode.FOR); }
{Return}        { return createRawToken(TokenCode.RETURN); }
{Break}         { return createRawToken(TokenCode.BREAK); }
{Continue}      { return createRawToken(TokenCode.CONTINUE); }
{Int}           { return createRawToken(TokenCode.INT); }
{Real}          { return createRawToken(TokenCode.REAL); }
{Static}        { return createRawToken(TokenCode.STATIC); }

{LBrace}        { return createRawToken(TokenCode.LBRACE); }
{RBrace}        { return createRawToken(TokenCode.RBRACE); }
{LBracket}      { return createRawToken(TokenCode.LBRACKET); }
{RBracket}      { return createRawToken(TokenCode.RBRACKET); }
{LParen}        { return createRawToken(TokenCode.LPAREN); }
{RParen}        { return createRawToken(TokenCode.RPAREN); }

{Semicolon}     { return createRawToken(TokenCode.SEMICOLON); }
{Comma}         { return createRawToken(TokenCode.COMMA); }
{Not}           { return createRawToken(TokenCode.NOT); }

{Identifier}    {
        					if (yytext().length() <= 32)
        						return createIdToken(createOrInsertSymTabEntry());
        					else 
        						return createRawToken(TokenCode.ERR_LONG_ID);
                }

{IntNum}        {
                  return createIntToken(createOrInsertSymTabEntry());
                }

{RealNum}       {
                  return createRealToken(createOrInsertSymTabEntry());
                }

[^]             { 
                  Token retVal = new Token(TokenCode.ERR_ILL_CHAR);
                  retVal.setLineColumn(yyline, yycolumn);
                  return retVal;
                }
