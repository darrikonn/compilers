import java.io.*;
import java.util.ArrayList;

public class Parser {
    private static Lexer _lexer;
    private static Token _token;
    private static TokenCode _lookahead;

    private static void readNextToken() {
        try {
            _token = _lexer.yylex();
        } catch (Exception e) {
            System.out.println("Could not get next yylex");
            System.exit(-1);
        }
        _lookahead = _token.getTokenCode();
        if (_lookahead == TokenCode.EOF) {
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        _lexer = new Lexer(new FileReader(args[0]));
        readNextToken();
        program();
    }

    private static void match(TokenCode code) {
        System.out.println(_lookahead + " == " + code);
        if (_lookahead == code) {
            readNextToken();
            return;
        }

        System.out.println("Error: expected \'" + _lookahead + "\', got \'" + code + "\'");
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

    private static void variable_declarations() {
        type();
        variable_list();
        variable_declarations();
    }

    private static void type() {
        if (TokenCode.INT == TokenCode.INT) {
            match(TokenCode.INT);
        } else {
            match(TokenCode.REAL);
        }
    }

    private static void variable_list() {
        variable();
        variable_list2();
    }

    private static void variable_list2() {
        match(TokenCode.COMMA);
        variable();
        variable_list2();
    }

    private static void variable(){
        match(TokenCode.IDENTIFIER);
        variable2();
    }

    private static void variable2() { 
        if (_lookahead == TokenCode.LBRACKET) {
            match(TokenCode.LBRACKET);
            match(TokenCode.NUMBER);
            match(TokenCode.RBRACKET);
        }
    }

    private static void method_declarations() { 
        more_method_declarations();
        method_declaration();
    }

    private static void more_method_declarations() { 
        method_declaration();
        more_method_declarations();
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

    private static void parameters() { 
        parameter_list();
    }

    private static void parameter_list() {
        type();
        match(TokenCode.IDENTIFIER);
        parameter_list2();
    }

    private static void parameter_list2() { 
        match(TokenCode.COMMA);
        type();
        match(TokenCode.IDENTIFIER);
        parameter_list2();
    }

    private static void statement_list() { 
        statement();
        statement_list();
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
            match(TokenCode.LPAREN);
            expression_list();
            match(TokenCode.RPAREN);
        } else if (_lookahead == TokenCode.RETURN) {
            match(TokenCode.RETURN);
            optional_expression();
        } else if (_lookahead == TokenCode.BREAK) {
            match(TokenCode.BREAK);
        } else if (_lookahead == TokenCode.CONTINUE) {
            match(TokenCode.CONTINUE);
        } else {
            variable_loc();
            if (_lookahead == TokenCode.ASSIGNOP) {
                match(TokenCode.ASSIGNOP);
                expression();
            } else {
                match(TokenCode.INCDECOP);;
            }
        } 
    }

    private static void optional_expression() { 
        expression();
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

    private static void optional_else() { 
        match(TokenCode.ELSE);
        statement_block();
    }

    private static void expression_list() { 
        expression();
        more_expressions();
    }

    private static void more_expressions() { 
        match(TokenCode.COMMA);
        expression();
        more_expressions();
    }

    private static void expression() {
        simple_expression();
        expression2();
    }

    private static void expression2() {
        match(TokenCode.RELOP);
        simple_expression();
    }

    private static void simple_expression() {
        if (_lookahead == TokenCode.ADDOP) {
            sign();
        }
        term();
        simple_expression2();
    }

    private static void simple_expression2() { 
        match(TokenCode.ADDOP);
        term();
        simple_expression2();
    }

    private static void term() {
        factor();
        term2();
    }

    private static void term2() { 
        match(TokenCode.MULOP);
        factor();
        term2();
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

    private static void variable_loc2() {
        match(TokenCode.LBRACKET);
        expression();
        match(TokenCode.RBRACKET);
    }

    private static void sign() {
        match(TokenCode.ADDOP);
    }
}
