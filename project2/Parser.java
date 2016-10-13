import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class Parser {
    private Lexer _lexer;
    private Token _token, _tmp;
    private TokenCode _lookahead;
    private OpType _opType;

    private ArrayList<TokenCode> _expression, _statementList;
    private HashMap<Integer, ArrayList<ErrorObject>> _errorMap;
    private String _filename;

    // follows
    private ArrayList<TokenCode> _follow_parameters, _follow_variable_declaration, _follow_expression,
            _follow_statement, _follow_parameter_list, _follow_statement2;

    public Parser(Lexer lexer, String filename) {
        _filename = filename;
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

        _follow_parameters = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN));
        _follow_parameter_list = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN,
                    TokenCode.COMMA));
        _follow_expression = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN, TokenCode.RBRACKET,
                    TokenCode.SEMICOLON, TokenCode.COMMA));
        _follow_statement = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF, TokenCode.FOR, 
            TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK, 
            TokenCode.CONTINUE, TokenCode.RBRACE, TokenCode.SEMICOLON
        ));
        _follow_statement2 = new ArrayList<TokenCode> (Arrays.asList(TokenCode.SEMICOLON));
        _follow_variable_declaration = new ArrayList<TokenCode> (Arrays.asList(TokenCode.STATIC, 
                    TokenCode.IF, TokenCode.FOR, TokenCode.LBRACE, TokenCode.IDENTIFIER, 
                    TokenCode.RETURN, TokenCode.BREAK, TokenCode.CONTINUE, TokenCode.RBRACE
        ));

        _errorMap = new HashMap<Integer, ArrayList<ErrorObject>>();
    }

    public static void main(String[] args) throws IOException {
        Parser p = new Parser(new Lexer(new FileReader(args[0])), args[0]);
        p.parse();
    }

    public void parse() {
        readNextToken();
        program();
        getErrors();
    }

    private void match(TokenCode code) {
        if (_lookahead == code) {
            readNextToken();
            return;
        }

        setError(code == TokenCode.SEMICOLON ? _tmp : _token, "Expected '" + code + "'");
    }

    private void match(OpType type) {
        if (_opType == type) {
            readNextToken();
            return;
        }

        setError(_token, "err");
    }

    private void recover_error(Token token, ArrayList<TokenCode> followList, String message) {
        setError(token, message);
        System.out.println("I'm Mr Mezeeks, look at me");
        while (!followList.contains(_lookahead)) {
            System.out.println("does not contain: " + _lookahead);
            readNextToken();
        }
        
        System.out.println("Current token: " + _lookahead);
    }

    private void setError(Token token, String message) {
        int line = token.getLineNumber();
        if (!_errorMap.containsKey(line)) {
            _errorMap.put(line, new ArrayList<ErrorObject>());
        }
        _errorMap.get(line).add(new ErrorObject(token.getColumnNumber(), message));
    }

    private void getErrors() {
        if (_errorMap.size() == 0) {
            System.out.println("No errors");
        } else {
            try {
                int cnt = 0;
                BufferedReader br = new BufferedReader(new FileReader(_filename));
                String s = br.readLine();
                for (int i = 0; s != null; i++, s = br.readLine()) {
                    if (_errorMap.containsKey(i)) {
                        String code = s.trim();
                        String leadString = StringUtils.leftPad(Integer.toString(i+1), 
                                3, ' ') + " : " + code;
                        ArrayList<ErrorObject> obj = _errorMap.get(i);
                        for (ErrorObject err : obj) {
                            cnt++;
                            int len = err.Message.contains("';'") 
                                ? code.substring(code.lastIndexOf(" ")+1).length()
                                : 0;
                            System.out.println(leadString);
                            System.out.println(StringUtils.repeat(" ", 
                                    err.Column+6-s.indexOf(code)) + 
                                    StringUtils.repeat(" ", len) + "^ " + err.Message);
                        }
                    }
                }
                System.out.println("Number of errors: " + cnt);
                br.close();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(-1);
            }
        }
    }

    private void readNextToken() {
        try {
            _tmp = _token;
            _token = _lexer.yylex();

            _lookahead = _token.getTokenCode();
            //System.out.println(_lookahead);
            _opType = _token.getOpType();

            if (_lookahead == TokenCode.ERR_ILL_CHAR) {
                setError(_token, "Illegal character");
                readNextToken();
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
        } else if (!_follow_variable_declaration.contains(_lookahead)) {
            recover_error(_token, _follow_variable_declaration, "Expected a type");
        }
    }

    private void type() {
        if (_lookahead == TokenCode.INT) {
            match(TokenCode.INT);
        } else if (_lookahead == TokenCode.REAL) {
            match(TokenCode.REAL);
        } else {
            setError(_token, "Invalid type");
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
        if (_lookahead != TokenCode.RPAREN) {
            parameter_list();
        }
    }

    private void parameter_list() {
        if (is_type()) {
            type();
            match(TokenCode.IDENTIFIER);
        } else {
            recover_error(_token, _follow_parameter_list, "Expected a type");
        }
        parameter_list2();
    }

    private void parameter_list2() { // Epsilon x
        if (_lookahead == TokenCode.COMMA) {
            match(TokenCode.COMMA);
            parameter_list();
        } else if (!_follow_parameters.contains(_lookahead)) {
            recover_error(_token, _follow_parameters, "Expected ','");
        }
    }

    private void statement_list() { // Epsilon x
        if (is_statement_list()) {
            statement();
            statement_list();
        }
    }

    private void statement() {
        switch (_lookahead) {
            case IF:
                match(TokenCode.IF);
                parenthesized_expression();
                statement_block();
                optional_else();
                break;
            case FOR:
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
                break;
            case LBRACE:
                statement_block();
                break;
            default:
                statement2();
                match(TokenCode.SEMICOLON);
                break;
        }
    }

    private void statement2() {
        switch (_lookahead) {
            case IDENTIFIER:
                match(TokenCode.IDENTIFIER);
                if (_lookahead == TokenCode.IDENTIFIER) {
                    recover_error(_tmp, _follow_statement2, "None such type");
                } else if (_lookahead == TokenCode.LPAREN) {
                    parenthesized_expression_list();
                } else {
                    variable_loc2();
                    if (_lookahead == TokenCode.ASSIGNOP) {
                        match(TokenCode.ASSIGNOP);
                        expression();
                    } else if (_lookahead == TokenCode.INCDECOP) {
                        match(TokenCode.INCDECOP);
                    } else {
                        recover_error(_token, _follow_statement2, "Invalid statement");
                    }
                }
                break;
            case RETURN:
                match(TokenCode.RETURN);
                optional_expression();
                break;
            case BREAK:
                match(TokenCode.BREAK);
                break;
            case CONTINUE:
                match(TokenCode.CONTINUE);
                break;
            default:
                recover_error(_token, _follow_statement2, "Invalid statement");
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
        if (_opType == OpType.PLUS) {
            match(OpType.PLUS);
        } else if (_opType == OpType.MINUS) {
            match(OpType.MINUS);
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
        switch (_lookahead) {
            case NOT:
                match(TokenCode.NOT);
                factor();
                break;
            case LPAREN:
                parenthesized_expression();
                break;
            case NUMBER:
                match(TokenCode.NUMBER);
                break;
            case IDENTIFIER:
                match(TokenCode.IDENTIFIER);
                if (_lookahead == TokenCode.LPAREN) {
                    parenthesized_expression_list();
                } else {
                    variable_loc2();
                }
                break;
            default:
                recover_error(_token, _follow_expression, "Expected an expression");
                break;
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
