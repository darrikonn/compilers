/* 
  Reference implementation of a Lexical Analyser for Decaf
  Compiler Course, T-603-THYDE - Fall 2014
  Author: Fridjon Gudjohnsen
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
  return Token.createRaw(TokenCode.EOF);
%eofval}

Letter           = [a-z] | [A-Z]
Digit            = [0-9]
Identifier       = {Letter} ({Letter} | {Digit})*
Digits           = {Digit}+
IntNum           = {Digits}
OptionalFraction = (\.{IntNum})?
OptionalExponent = ( "E" ( "+" | "-" )? {IntNum} )?
RealNum          = {IntNum} {OptionalFraction} {OptionalExponent}


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

WS = [ \n\t]+

%{
  private SymbolTableEntry createOrInsertSymTabEntry() {
    SymbolTableEntry symTabEntry = SymbolTable.lookup(yytext());
    if (symTabEntry == null)
      symTabEntry = SymbolTable.insert(yytext());
    return symTabEntry;
  } 
%}

%%

{IncDecOp}      { 
                  OpType op = yytext().equals("++") ? OpType.INC : OpType.DEC;
                  return Token.createOp(TokenCode.INCDECOP, op); 
                }

{RelOp}         { return Token.createRelOp(yytext()); }

{MulOp}         { return Token.createMulOp(yytext()); }
{AddOp}         { return Token.createAddOp(yytext()); }

{AssignOp}      { return Token.createOp(TokenCode.ASSIGNOP, OpType.ASSIGN); }


{Class}         { return Token.createRaw(TokenCode.CLASS); }
{Void}          { return Token.createRaw(TokenCode.VOID); }
{If}            { return Token.createRaw(TokenCode.IF); }
{Else}          { return Token.createRaw(TokenCode.ELSE); }
{For}           { return Token.createRaw(TokenCode.FOR); }
{Return}        { return Token.createRaw(TokenCode.RETURN); }
{Break}         { return Token.createRaw(TokenCode.BREAK); }
{Continue}      { return Token.createRaw(TokenCode.CONTINUE); }
{Int}           { return Token.createRaw(TokenCode.INT); }
{Real}          { return Token.createRaw(TokenCode.REAL); }

{LBrace}        { return Token.createRaw(TokenCode.LBRACE); }
{RBrace}        { return Token.createRaw(TokenCode.RBRACE); }
{LBracket}      { return Token.createRaw(TokenCode.LBRACKET); }
{RBracket}      { return Token.createRaw(TokenCode.RBRACKET); }
{LParen}        { return Token.createRaw(TokenCode.LPAREN); }
{RParen}        { return Token.createRaw(TokenCode.RPAREN); }

{Semicolon}     { return Token.createRaw(TokenCode.SEMICOLON); }
{Comma}         { return Token.createRaw(TokenCode.COMMA); }
{Not}           { return Token.createRaw(TokenCode.NOT); }



{Identifier}    {
                  return Token.createId(createOrInsertSymTabEntry());
                }

{IntNum}        {
                  return Token.createInt(createOrInsertSymTabEntry());
                }

{RealNum}       {
                  return Token.createReal(createOrInsertSymTabEntry());
                }


{Comment}          { /* Ignore comments */}
{WS}               { /* Ignore whitespace */ }
[^]             { return new Token(TokenCode.ERR_ILL_CHAR); }
