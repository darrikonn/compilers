Letter = [A-Z] | [a-z]
Identifier = {Letter}+

Digit = [0-9]
Number = {Digit}+
Whitespace = [ \n\t\r]+
Unknown = .

{Identifier}    { System.out.println("Identifier: " + yytext()); }
{Number}        { System.out.println("Digit: " + yytext()); }
{Whitespace}    { /* Do Nothing */ }
{Unknown}       { System.out.println("Unrecognized characters: " + yytext()); } 

//java -jar jflex/lib/jflex-1.6.1.jar lexer.flex
