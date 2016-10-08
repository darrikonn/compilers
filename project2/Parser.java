import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    private Lexer _lexer;
    private Token _token;
    private TokenCode _lookahead;
    private OpType _opType;

    private ArrayList<TokenCode> _expression, _statementList;
    private String _decafCode;
    private int _lineNumber;

    public Parser(Lexer lexer) {
        _lexer = lexer;

        // used for validation
        _expression = new ArrayList<TokenCode> (Arrays.asList(
            TokenCode.IF, TokenCode.ADDOP, TokenCode.NOT, TokenCode.LPAREN, 
            TokenCode.NUMBER, TokenCode.IDENTIFIER 
        ));
        _statementList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF, TokenCode.FOR, 
            TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK, 
            TokenCode.CONTINUE
        ));

        _decafCode = "";
        _lineNumber = 0;
    }

    public static void main(String[] args) throws IOException {
        Parser p = new Parser(new Lexer(new FileReader(args[0])));
        p.parse();
    }

    public void parse() {
        readNextToken();
        program();
    }

    private void match(TokenCode code) { // nota yypushback
        if (_lookahead == code) {
            readNextToken();
            return;
        }

        printError(code, _lookahead);
    }

    private void match(OpType type) {
        if (_opType == type) {
            readNextToken();
            return;
        }

        printError(type, _opType);
    }

    private void printError(Object expected, Object value) {
        System.out.println(_token.getLineNumber() + " : " + _token.getColumnNumber() + " " + 
                _lexer + "Error: expected \'" + expected + "\', got \'" + value + "\'");
        System.out.println(_lexer.yytext());
        System.out.println(_lexer.yylength());
        System.exit(-1);
    }

    private void readNextToken() {
        try {
            _token = _lexer.yylex();

            _lookahead = _token.getTokenCode();
            _opType = _token.getOpType();


            int currentLine = _token.getLineNumber();
            if (currentLine > _lineNumber) {
                _lineNumber = currentLine;
                _decafCode = "";
            } else {
                
            }


            if (_lookahead == TokenCode.EOF) {
                System.out.println("No errors");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /*
     * Grammar
     */
    private void program() {
        match(TokenCode.CLASS);
        match(TokenCode.IDENTIFIER);
        match(TokenCode.LBRACE);
        variable_declarations();
        method_declarations();
        match(TokenCode.RBRACE);
    }

    private void variable_declarations() { // Epsilon x
        if (is_type()) {
            type();
            variable_list();
            match(TokenCode.SEMICOLON);
            variable_declarations();
        }
    }

    private void type() {
        if (_lookahead == TokenCode.INT) {
            match(TokenCode.INT);
        } else {
            match(TokenCode.REAL);
        }
    }

    private void variable_list() {
        variable();
        variable_list2();
    }

    private void variable_list2() { // Epsilon x
        if (_lookahead == TokenCode.COMMA) {
            match(TokenCode.COMMA);
            variable_list();
        }
    }

    private void variable() {
        match(TokenCode.IDENTIFIER);
        variable2();
    }

    private void variable2() { // Epsilon x
        if (_lookahead == TokenCode.LBRACKET) {
            match(TokenCode.LBRACKET);
            match(TokenCode.NUMBER);
            match(TokenCode.RBRACKET);
        }
    }

    private void method_declarations() {
        method_declaration();
        more_method_declarations();
    }

    private void more_method_declarations() { // Epsilon x
        if (_lookahead == TokenCode.STATIC) {
            method_declarations();
        }
    }

    private void method_declaration() {
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

    private void method_return_type() {
        if (is_type()) {
            type();
        } else {
            match(TokenCode.VOID);
        }
    }

    private void parameters() { // Epsilon x
        if (is_type()) {
            parameter_list();
        }
    }

    private void parameter_list() {
        type();
        match(TokenCode.IDENTIFIER);
        parameter_list2();
    }

    private void parameter_list2() { // Epsilon x
        if (_lookahead == TokenCode.COMMA) {
            match(TokenCode.COMMA);
            parameter_list();
        }
    }

    private void statement_list() { // Epsilon x
        if (is_statement_list()) {
            statement();
            statement_list();
        }
    }

    private void statement() {
        if (_lookahead == TokenCode.IF) {
            match(TokenCode.IF);
            parenthesized_expression();
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

    private void statement2() {
        if (_lookahead == TokenCode.IDENTIFIER) {
            match(TokenCode.IDENTIFIER);
            if (_lookahead == TokenCode.LPAREN) {
                parenthesized_expression_list();
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
        }
    }

    private void optional_expression() { // Epsilon x
        if (is_expression()) {
            expression();
        }
    }

    private void statement_block() {
        match(TokenCode.LBRACE);
        statement_list();
        match(TokenCode.RBRACE);
    }

    private void incr_decr_var() {
        variable_loc();
        match(TokenCode.INCDECOP);
    }

    private void optional_else() { // Epsilon x
        if (_lookahead == TokenCode.ELSE) {
            match(TokenCode.ELSE);
            statement_block();
        }
    }

    private void expression_list() { // Epsilon x
        if (is_expression()) {
            expression();
            more_expressions();
        }
    }

    private void parenthesized_expression_list() {
        match(TokenCode.LPAREN);
        expression_list();
        match(TokenCode.RPAREN);
    }


    private void more_expressions() { // Epsilon x
        if (_lookahead == TokenCode.COMMA) {
            match(TokenCode.COMMA);
            expression_list();
        }
    }

    private void expression() {
        simple_expression();
        expression2();
    }

    private void parenthesized_expression() {
        match(TokenCode.LPAREN);
        expression();
        match(TokenCode.RPAREN);
    }

    private void expression2() { // Epsilon x
        if (_lookahead == TokenCode.RELOP) {
            match(TokenCode.RELOP);
            simple_expression();
        }
    }

    private void simple_expression() {
        if (_opType == OpType.MINUS || _opType == OpType.PLUS) {
            sign();
        }
        term();
        simple_expression2();
    }

    private void simple_expression2() { // Epsilon x
        if (_lookahead == TokenCode.ADDOP) {
            match(TokenCode.ADDOP);
            term();
            simple_expression2();
        }
    }

    private void term() {
        factor();
        term2();
    }

    private void term2() { // Epsilon x
        if (_lookahead == TokenCode.MULOP) {
            match(TokenCode.MULOP);
            term();
        }
    }

    private void factor() {
        if (_lookahead == TokenCode.NOT) {
            match(TokenCode.NOT);
            factor();
        } else if (_lookahead == TokenCode.LPAREN) {
            parenthesized_expression();
        } else if (_lookahead == TokenCode.NUMBER) {
            match(TokenCode.NUMBER);
        } else {
            match(TokenCode.IDENTIFIER);
            if (_lookahead == TokenCode.LPAREN) {
                parenthesized_expression_list();
            } else {
                variable_loc2();
            }
        }
    }

    private void variable_loc() {
        match(TokenCode.IDENTIFIER);
        variable_loc2();
    }

    private void variable_loc2() { // Epsilon x
        if (_lookahead == TokenCode.LBRACKET) {
            match(TokenCode.LBRACKET);
            expression();
            match(TokenCode.RBRACKET);
        }
    }

    private void sign() {
        if (_opType == OpType.PLUS) {
            match(OpType.PLUS);
        } else {
            match(OpType.MINUS);
        }
    }

    /*
     * Lookahead checks
     */
    private boolean is_type() {
        return _lookahead == TokenCode.INT || _lookahead == TokenCode.REAL;
    }

    private boolean is_expression() {
        return _expression.contains(_lookahead);
    }

    private boolean is_statement_list() {
        return _statementList.contains(_lookahead);
    }
}
