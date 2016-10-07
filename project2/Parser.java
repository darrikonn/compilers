import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    private static Lexer _lexer;
    private static Token _token;
    private static TokenCode _lookahead;
    private static ArrayList<TokenCode> _expression;

    public Parser() {
        _expression = new ArrayList<TokenCode> (Arrays.asList(
            TokenCode.IF, TokenCode.ADDOP, TokenCode.NOT, TokenCode.LPAREN, 
            TokenCode.NUMBER, TokenCode.IDENTIFIER 
        ));
    }

    public static void main(String[] args) throws IOException {
        _lexer = new Lexer(new FileReader(args[0]));
        readNextToken();
        program();
    }

    private static void readNextToken() {
        try {
            _token = _lexer.yylex();

            _lookahead = _token.getTokenCode();
            if (_lookahead == TokenCode.EOF) {
                System.out.println("No errors");
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    private static void match(TokenCode code) { // nota yypushback
        if (_lookahead == code) {
            readNextToken();
            return;
        }

        System.out.println(_token.getLineNumber() + " : " + _token.getColumnNumber() + " " + _lexer + "Error: expected \'" + _lookahead + "\', got \'" + code + "\'");
        //System.out.println(_lexer.yylex());
        //System.out.println(_lexer.yylval());
        System.out.println(_lexer.yytext());
        System.out.println(_lexer.yylength());
        System.exit(-1);
    }

    private static void program() {
        match(TokenCode.CLASS);
        match(TokenCode.IDENTIFIER);
        match(TokenCode.LBRACE);
        variable_declarations();
        method_declarations();
        match(TokenCode.RBRACE);
    }

    private static void variable_declarations() { // Epsilon x
        if (isType()) {
            type();
            variable_list();
            match(TokenCode.SEMICOLON);
            variable_declarations();
        }
    }

    private static void type() {
        if (_lookahead == TokenCode.INT) {
            match(TokenCode.INT);
        } else {
            match(TokenCode.REAL);
        }
    }

    private static boolean isType() {
        return _lookahead == TokenCode.INT || _lookahead == TokenCode.REAL;
    }

    private static void variable_list() {
        variable();
        variable_list2();
    }

    private static void variable_list2() { // Epsilon x
        if (isComma()) {
            match(TokenCode.COMMA);
            variable();
            variable_list2();
        }
    }

    private static void variable() {
        match(TokenCode.IDENTIFIER);
        variable2();
    }

    private static void variable2() { // Epsilon x
        if (_lookahead == TokenCode.LBRACKET) {
            match(TokenCode.LBRACKET);
            match(TokenCode.NUMBER);
            match(TokenCode.RBRACKET);
        }
    }

    private static void method_declarations() {
        method_declaration();
        more_method_declarations();
    }

    private static void more_method_declarations() { // Epsilon x
        if (isStatic()) {
            method_declaration();
            more_method_declarations();
        }
    }

    private static boolean isStatic() {
        return _lookahead == TokenCode.STATIC;
    }

    private static void method_declaration() {
        match(TokenCode.STATIC);
        method_return_type();
        match(TokenCode.IDENTIFIER);
        match(TokenCode.LPAREN);
        parameters();
        match(TokenCode.RPAREN);
        match(TokenCode.LBRACE);
        variable_declarations();
        statement_list();
        match(TokenCode.RBRACE);
    }

    private static void method_return_type() {
        if (_lookahead == TokenCode.VOID) {
            match(TokenCode.VOID);
        } else {
            type();
        }
    }

    private static void parameters() { // Epsilon x
        if (isType()) {
            parameter_list();
        }
    }

    private static void parameter_list() {
        type();
        match(TokenCode.IDENTIFIER);
        parameter_list2();
    }

    private static void parameter_list2() { // Epsilon x
        if (isComma()) {
            match(TokenCode.COMMA);
            type();
            match(TokenCode.IDENTIFIER);
            parameter_list2();
        }
    }

    private static boolean isComma() {
        return _lookahead == TokenCode.COMMA;
    }

    private static void statement_list() { // Epsilon x
        if (_lookahead == TokenCode.IF || _lookahead == TokenCode.FOR || _lookahead == TokenCode.LBRACE ||
                _lookahead == TokenCode.IDENTIFIER || _lookahead == TokenCode.RETURN || 
                _lookahead == TokenCode.BREAK || _lookahead == TokenCode.CONTINUE) {
            statement();
            statement_list();
        }
    }

    private static void statement() {
        if (_lookahead == TokenCode.IF) {
            match(TokenCode.IF);
            match(TokenCode.LPAREN);
            expression();
            match(TokenCode.RPAREN);
            statement_block();
            optional_else();
        } else if (_lookahead == TokenCode.FOR) {
            match(TokenCode.FOR);
            match(TokenCode.LPAREN);
            variable_loc();
            match(TokenCode.ASSIGNOP);
            expression();
            match(TokenCode.SEMICOLON);
            expression();
            match(TokenCode.SEMICOLON);
            incr_decr_var();
            match(TokenCode.RPAREN);
            statement_block();
        } else if (_lookahead == TokenCode.LBRACE) {
            statement_block();
        } else {
            statement2();
            match(TokenCode.SEMICOLON);
        }
    }

    private static void statement2() {
        if (_lookahead == TokenCode.IDENTIFIER) {
            match(TokenCode.IDENTIFIER);
            if (_lookahead == TokenCode.LPAREN) {
                match(TokenCode.LPAREN);
                expression_list();
                match(TokenCode.RPAREN);
            } else {
                variable_loc2();
                if (_lookahead == TokenCode.ASSIGNOP) {
                    match(TokenCode.ASSIGNOP);
                    expression();
                } else {
                    match(TokenCode.INCDECOP);
                }
            }
        } else if (_lookahead == TokenCode.RETURN) {
            match(TokenCode.RETURN);
            optional_expression();
        } else if (_lookahead == TokenCode.BREAK) {
            match(TokenCode.BREAK);
        } else if (_lookahead == TokenCode.CONTINUE) {
            match(TokenCode.CONTINUE);
        } else {
        } 
    }

    private static void optional_expression() { // Epsilon x
        if (isExpression()) {
            expression();
        }
    }

    private static void statement_block() {
        match(TokenCode.LBRACE);
        statement_list();
        match(TokenCode.RBRACE);
    }

    private static void incr_decr_var() {
        variable_loc();
        match(TokenCode.INCDECOP);
    }

    private static void optional_else() { // Epsilon x
        if (_lookahead == TokenCode.ELSE) {
            match(TokenCode.ELSE);
            statement_block();
        }
    }

    private static void expression_list() { // Epsilon x
        if (isExpression()) {
            expression();
            more_expressions();
        }
    }

    private static void more_expressions() { // Epsilon x
        if (isComma()) {
            match(TokenCode.COMMA);
            expression();
            more_expressions();
        }
    }

    private static void expression() {
        simple_expression();
        expression2();
    }

    private static void expression2() { // Epsilon x
        if (_lookahead == TokenCode.RELOP) {
            match(TokenCode.RELOP);
            simple_expression();
        }
    }

    private static void simple_expression() {
        if (_lookahead == TokenCode.ADDOP) {
            sign();
        }
        term();
        simple_expression2();
    }

    private static void simple_expression2() { // Epsilon x
        if (_lookahead == TokenCode.ADDOP) {
            match(TokenCode.ADDOP);
            term();
            simple_expression2();
        }
    }

    private static void term() {
        factor();
        term2();
    }

    private static void term2() { // Epsilon x
        if (_lookahead == TokenCode.MULOP) {
            match(TokenCode.MULOP);
            factor();
            term2();
        }
    }

    private static void factor() {
        if (_lookahead == TokenCode.NOT) {
            match(TokenCode.NOT);
            factor();
        } else if (_lookahead == TokenCode.LPAREN) {
            match(TokenCode.LPAREN);
            expression();
            match(TokenCode.RPAREN);
        } else if (_lookahead == TokenCode.NUMBER) {
            match(TokenCode.NUMBER);
        } else {
            match(TokenCode.IDENTIFIER);
            if (_lookahead == TokenCode.LPAREN) {
                match(TokenCode.LPAREN);
                expression_list();
                match(TokenCode.RPAREN);
            } else {
                variable_loc2();
            }
        }
    }

    private static void variable_loc() {
        match(TokenCode.IDENTIFIER);
        variable_loc2();
    }

    private static void variable_loc2() { // Epsilon x
        if (_lookahead == TokenCode.LBRACKET) {
            match(TokenCode.LBRACKET);
            expression();
            match(TokenCode.RBRACKET);
        }
    }

    private static void sign() {
        match(TokenCode.ADDOP);
    }

    // lookahead check functions
    private static boolean isExpression() {
        return _lookahead == TokenCode.IF || _lookahead == TokenCode.ADDOP || 
            _lookahead == TokenCode.NOT || _lookahead == TokenCode.LPAREN || 
            _lookahead == TokenCode.NUMBER || _lookahead == TokenCode.IDENTIFIER; 
        //return _expression.contains(_lookahead);
    }
}
