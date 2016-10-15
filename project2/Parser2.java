import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class Parser2 {
    private Lexer _lexer;
    private Token _token, _tmp;
    private String _prevText;
    private TokenCode _lookahead;
    private OpType _opType;

    private ArrayList<TokenCode> _expression, _statementList;
    private HashMap<Integer, ArrayList<ErrorObject>> _errorMap;
    private String _filename;

    // follows
    private ArrayList<TokenCode> _followParameters, _followVariableDeclaration, _followExpression,
            _followStatement, _followParameterList, _followStatement2, _followProgram, _followVariable,
                _followMethodReturnType, _followMethodDeclarations, _followVariableList;

    // first
    private ArrayList<TokenCode> _firstVariableMethodDeclarations, _firstVariable2, _firstVariableDeclarationsStatementList, _firstVariableLoc, _firstParameters;

    public Parser2(Lexer lexer, String filename) throws IOException {
        _filename = filename;
        _lexer = lexer;
        _token = lexer.yylex();
        _tmp = _token;
        _prevText = "";
        _lookahead = _token.getTokenCode();
        _opType = _token.getOpType();

        // used for validation
        _expression = new ArrayList<TokenCode> (Arrays.asList(
            TokenCode.IF, TokenCode.ADDOP, TokenCode.NOT, TokenCode.LPAREN, 
            TokenCode.NUMBER, TokenCode.IDENTIFIER 
        ));
        _statementList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF, TokenCode.FOR, 
            TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK, 
            TokenCode.CONTINUE
        ));

        // first
        _firstVariableMethodDeclarations = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IDENTIFIER));
        _firstParameters = new ArrayList<TokenCode> (Arrays.asList(TokenCode.INT, TokenCode.REAL,
                    TokenCode.RPAREN));
        _firstVariableMethodDeclarations = new ArrayList<TokenCode> (Arrays.asList(TokenCode.INT,
                    TokenCode.REAL, TokenCode.STATIC));
        _firstVariable2 = new ArrayList<TokenCode> (Arrays.asList(TokenCode.LBRACKET, TokenCode.COMMA,
                    TokenCode.SEMICOLON));
        _firstVariableDeclarationsStatementList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF,
                    TokenCode.FOR, TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK,
                    TokenCode.CONTINUE, TokenCode.RBRACE));

        // follow
        _followVariableList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.SEMICOLON));
        _followMethodDeclarations = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RBRACE,
                    TokenCode.STATIC));
        _followMethodReturnType = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IDENTIFIER));
        _followVariable = new ArrayList<TokenCode> (Arrays.asList(TokenCode.COMMA, TokenCode.SEMICOLON));
        _followProgram = new ArrayList<TokenCode> (Arrays.asList(TokenCode.STATIC, TokenCode.INT, 
                    TokenCode.REAL));
        _followParameters = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN));
        _followParameterList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN,
                    TokenCode.COMMA));
        _followExpression = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN, TokenCode.RBRACKET,
                    TokenCode.SEMICOLON, TokenCode.COMMA));
        _followStatement = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF, TokenCode.FOR, 
            TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK, 
            TokenCode.CONTINUE, TokenCode.RBRACE, TokenCode.SEMICOLON
        ));
        _followStatement2 = new ArrayList<TokenCode> (Arrays.asList(TokenCode.SEMICOLON));
        _followVariableDeclaration = new ArrayList<TokenCode> (Arrays.asList(TokenCode.STATIC, 
                    TokenCode.IF, TokenCode.FOR, TokenCode.LBRACE, TokenCode.IDENTIFIER, 
                    TokenCode.RETURN, TokenCode.BREAK, TokenCode.CONTINUE, TokenCode.RBRACE
        ));

        _errorMap = new HashMap<Integer, ArrayList<ErrorObject>>();
    }

    public static void main(String[] args) throws IOException {
        Parser2 p = new Parser2(new Lexer(new FileReader(args[0])), args[0]);
        p.parse();
    }

    public void parse() {
        program();
        getErrors();
    }

    private boolean match(TokenCode code) {
        if (_lookahead == code) {
            readNextToken();
            return true;
        }

        String message = code == TokenCode.SEMICOLON 
            ? StringUtils.repeat(" ", _prevText.length()) + code.toString()
            : code.toString();
        setError(code == TokenCode.SEMICOLON ? _tmp : _token, message);

        return false;
    }

    private boolean match(OpType type) {
        if (_opType == type) {
            readNextToken();
            return true;
        }

        setError(_token, "^ Expected '" + type + "'");
        return false;
    }

    private void recoverError(ArrayList<TokenCode> followList) {
        System.out.println("I'm Mr Mezeeks, look at me");
        while (!followList.contains(_lookahead) && _lookahead != TokenCode.EOF) {
            readNextToken();
        }
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
                            System.out.println(leadString);
                            System.out.println(StringUtils.repeat(" ", 
                                    err.Column+6-s.indexOf(code)) + err.Message);
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
            _prevText = _lexer.yytext();
            _token = _lexer.yylex();

            _lookahead = _token.getTokenCode();
            _opType = _token.getOpType();

            if (_lookahead == TokenCode.ERR_ILL_CHAR) {
                setError(_token, "^ Illegal character");
                _token = _lexer.yylex();
                _lookahead = _token.getTokenCode();
                _opType = _token.getOpType();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /*
     * Grammar
     */
    /*
     * paela i thvi ad error recovera strax eftir semicolon en ekki fyrir hitt
     */
    private void program() {
        if (!match(TokenCode.CLASS) || !match(TokenCode.IDENTIFIER) || !match(TokenCode.LBRACE)) {
            recoverError(_firstVariableMethodDeclarations);
        }

        variableDeclarations();
        methodDeclarations();
        match(TokenCode.RBRACE);
    }

    private void variableDeclarations() { // Epsilon x
        if (isType()) {
            type();
            variableList();
            match(TokenCode.SEMICOLON);
            variableDeclarations();
        } else if (!_followVariableDeclaration.contains(_lookahead)) {
            setError(_token, "^ Expected a type");
            recoverError(_followVariableDeclaration);
        }
    }

    private void type() {
        if (_lookahead == TokenCode.INT) {
            match(TokenCode.INT);
        } else if (_lookahead == TokenCode.REAL) {
            match(TokenCode.REAL);
        }
    }

    private void variableList() {
        variable();
        variableList2();
    }

    private void variableList2() { // Epsilon x
        if (_lookahead == TokenCode.COMMA) {
            match(TokenCode.COMMA);
            variableList();
        } else if (!_followVariableList.contains(_lookahead)) {
            setError(_token, "^ Expected a comma");
            recoverError(_followVariableList);
        }
    }

    private void variable() {
        if (_lookahead == TokenCode.IDENTIFIER) {
            match(TokenCode.IDENTIFIER);
            variable2();
        } else {
            setError(_token, "^ Expected an identifier");
            recoverError(_followVariable);
        }
    }

    private void variable2() { // Epsilon x
        if (_lookahead == TokenCode.LBRACKET) {
            if (!match(TokenCode.LBRACKET) || !match(TokenCode.NUMBER) || !match(TokenCode.RBRACKET)) {
                // recover first, no error
                recoverError(_followVariable);
            }
        }
    }

    private void methodDeclarations() {
        if (_lookahead == TokenCode.STATIC) {
            methodDeclaration();
            moreMethodDeclarations();
        } else {
            setError(_token, "^ Expected a variable or a method declaration");
            recoverError(_followMethodDeclarations);
        }
    }

    private void moreMethodDeclarations() { // Epsilon x
        if (_lookahead == TokenCode.STATIC) {
            methodDeclarations();
        }
    }

    private void methodDeclaration() {
        match(TokenCode.STATIC);
        methodReturnType();
        if (!match(TokenCode.IDENTIFIER) || !match(TokenCode.LPAREN)) {
            recoverError(_firstParameters);
        }
        parameters();
        if (!match(TokenCode.RPAREN) || !match(TokenCode.LBRACE)) {
            recoverError(_firstVariableDeclarationsStatementList);
        }
        variableDeclarations();
        statementList();
        match(TokenCode.RBRACE);
    }

    private void methodReturnType() {
        if (isType()) {
            type();
        } else if (_lookahead == TokenCode.VOID) {
            match(TokenCode.VOID);
        } else {
            setError(_token, "^ Expected a method return type");
            recoverError(_followMethodReturnType);
        }
    }

    private void parameters() { // Epsilon x
        if (_lookahead != TokenCode.RPAREN) {
            parameterList();
        }
    }

    private void parameterList() {
        if (isType()) {
            type();
            match(TokenCode.IDENTIFIER);
        } else {
            setError(_token, "^ Expected a type");
            recoverError(_followParameterList);
        }
        parameterList2();
    }

    private void parameterList2() { // Epsilon x
        if (_lookahead == TokenCode.COMMA) {
            match(TokenCode.COMMA);
            parameterList();
        } else if (!_followParameters.contains(_lookahead)) {
            setError(_token, "^ Expected a comma");
            recoverError(_followParameters);
        }
    }

    private void statementList() { // Epsilon x
        if (isStatementList()) {
            statement();
            statementList();
        }
    }

    private void statement() {
        switch (_lookahead) {
            case IF:
                match(TokenCode.IF);
                parenthesizedExpression();
                statementBlock();
                optionalElse();
                break;
            case FOR:
                if (!match(TokenCode.FOR) || !match(TokenCode.LPAREN)) {
                    recoverError(_firstVariableLoc);
                }
                variableLoc();
                match(TokenCode.ASSIGNOP);
                expression();
                match(TokenCode.SEMICOLON);
                expression();
                match(TokenCode.SEMICOLON);
                incrDecrVar();
                match(TokenCode.RPAREN);
                statementBlock();
                break;
            case LBRACE:
                statementBlock();
                break;
            default:
                statement2();
                if (!match(TokenCode.SEMICOLON)) {
                    recoverError(_followStatement);
                }
                break;
        }
    }

    private void statement2() {
        switch (_lookahead) {
            case IDENTIFIER:
                match(TokenCode.IDENTIFIER);
                if (_lookahead == TokenCode.IDENTIFIER) {
                    setError(_tmp, "^ None such type");
                    recoverError(_followStatement2);
                } else if (_lookahead == TokenCode.LPAREN) {
                    parenthesizedExpressionList();
                } else {
                    variableLoc2();
                    if (_lookahead == TokenCode.ASSIGNOP) {
                        match(TokenCode.ASSIGNOP);
                        expression();
                    } else if (_lookahead == TokenCode.INCDECOP) {
                        match(TokenCode.INCDECOP);
                    } else {
                        setError(_token, "^ Invalid statement");
                        recoverError(_followStatement2);
                    }
                }
                break;
            case RETURN:
                match(TokenCode.RETURN);
                optionalExpression();
                break;
            case BREAK:
                match(TokenCode.BREAK);
                break;
            case CONTINUE:
                match(TokenCode.CONTINUE);
                break;
            default:
                setError(_token, "^ Invalid statement");
                recoverError(_followStatement2);
        }
    }

    private void optionalExpression() { // Epsilon x
        if (isExpression()) {
            expression();
        }
    }

    private void statementBlock() {
        match(TokenCode.LBRACE);
        statementList();
        match(TokenCode.RBRACE);
    }

    private void incrDecrVar() {
        variableLoc();
        match(TokenCode.INCDECOP);
    }

    private void optionalElse() { // Epsilon x
        if (_lookahead == TokenCode.ELSE) {
            match(TokenCode.ELSE);
            statementBlock();
        }
    }

    private void expressionList() { // Epsilon x
        if (isExpression()) {
            expression();
            moreExpressions();
        }
    }

    private void parenthesizedExpressionList() {
        match(TokenCode.LPAREN);
        expressionList();
        match(TokenCode.RPAREN);
    }


    private void moreExpressions() { // Epsilon x
        if (_lookahead == TokenCode.COMMA) {
            match(TokenCode.COMMA);
            expressionList();
        }
    }

    private void expression() {
        simpleExpression();
        expression2();
    }

    private void parenthesizedExpression() {
        match(TokenCode.LPAREN);
        expression();
        match(TokenCode.RPAREN);
    }

    private void expression2() { // Epsilon x
        if (_lookahead == TokenCode.RELOP) {
            match(TokenCode.RELOP);
            simpleExpression();
        }
    }

    private void simpleExpression() {
        if (_opType == OpType.PLUS) {
            match(OpType.PLUS);
        } else if (_opType == OpType.MINUS) {
            match(OpType.MINUS);
        }
        term();
        simpleExpression2();
    }

    private void simpleExpression2() { // Epsilon x
        if (_lookahead == TokenCode.ADDOP) {
            match(TokenCode.ADDOP);
            term();
            simpleExpression2();
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
                parenthesizedExpression();
                break;
            case NUMBER:
                match(TokenCode.NUMBER);
                break;
            case IDENTIFIER:
                match(TokenCode.IDENTIFIER);
                if (_lookahead == TokenCode.LPAREN) {
                    parenthesizedExpressionList();
                } else {
                    variableLoc2();
                }
                break;
            default:
                setError(_token, "^ Expected an expression");
                recoverError(_followExpression);
                break;
        }
    }

    private void variableLoc() {
        match(TokenCode.IDENTIFIER);
        variableLoc2();
    }

    private void variableLoc2() { // Epsilon x
        if (_lookahead == TokenCode.LBRACKET) {
            match(TokenCode.LBRACKET);
            expression();
            match(TokenCode.RBRACKET);
        }
    }

    /*
     * Lookahead checks
     */
    private boolean isType() {
        return _lookahead == TokenCode.INT || _lookahead == TokenCode.REAL;
    }

    private boolean isExpression() {
        return _expression.contains(_lookahead);
    }

    private boolean isStatementList() {
        return _statementList.contains(_lookahead);
    }
}
