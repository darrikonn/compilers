import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class Parser {
    private Lexer _lexer;
    private Token _token, _tmp;
    private String _prevText;
    private TokenCode _lookahead;
    private OpType _opType;

    private ArrayList<TokenCode> _expression, _statementList, _types;
    private HashMap<Integer, ArrayList<ErrorObject>> _errorMap;
    private String _filename;

    // FOLLOWs
    private ArrayList<TokenCode> _followParameters, _followVariableDeclaration, _followExpression,
            _followStatement, _followParameterList, _followVariable,
                _followMethodReturnType, _followMethodDeclarations, _followVariableList,
                _followStatementBlock, _followParenthesizedExpressionList,
                _followParenthesizedExpression, _followVariableLoc2, _followNoneSuchType;

    // FIRSTs
    private ArrayList<OpType> _firstOpExpression;
    private ArrayList<TokenCode> _firstVariableMethodDeclarations, _firstVariableDeclarationsStatementList, 
            _firstVariableLoc, _firstParameters, _firstParameterList2, _firstExpression,
            _firstStatementBlock, _firstVariableLoc2;

    public Parser(Lexer lexer, String filename) throws IOException {
        _filename = filename;
        _lexer = lexer;
        _token = lexer.yylex();
        _tmp = _token;
        _prevText = "";
        _lookahead = _token.getTokenCode();
        _opType = _token.getOpType();
        _errorMap = new HashMap<Integer, ArrayList<ErrorObject>>();

        // used for validation
        _expression = new ArrayList<TokenCode> (Arrays.asList(
            TokenCode.IF, TokenCode.ADDOP, TokenCode.NOT, TokenCode.LPAREN, 
            TokenCode.NUMBER, TokenCode.IDENTIFIER 
        ));
        _statementList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF, TokenCode.FOR, 
            TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK, 
            TokenCode.CONTINUE
        ));
        _types = new ArrayList<TokenCode> (Arrays.asList(TokenCode.INT, TokenCode.REAL));

        // FIRST
        _firstExpression = new ArrayList<TokenCode> (Arrays.asList(TokenCode.NOT, TokenCode.LPAREN,
                    TokenCode.NUMBER, TokenCode.IDENTIFIER));

        _firstVariableLoc2 = new ArrayList<TokenCode> (Arrays.asList(TokenCode.LBRACKET,
                    TokenCode.ASSIGNOP, TokenCode.INCDECOP));

        _firstParameterList2 = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN, 
                    TokenCode.COMMA));

        _firstVariableDeclarationsStatementList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF,
                    TokenCode.FOR, TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK,
                    TokenCode.CONTINUE, TokenCode.RBRACE));

        _firstStatementBlock = new ArrayList<TokenCode> (Arrays.asList(TokenCode.LBRACE));

        _firstOpExpression = new ArrayList<OpType> (Arrays.asList(OpType.MINUS, OpType.PLUS));

        _firstParameters = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN));
        _firstParameters.addAll(_types);

        _firstVariableMethodDeclarations = new ArrayList<TokenCode> (Arrays.asList(TokenCode.STATIC, 
                    TokenCode.IDENTIFIER, TokenCode.RBRACE));
        _firstVariableMethodDeclarations.addAll(_types);

        // FOLLOW
        _followMethodReturnType = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IDENTIFIER));

        _followVariable = new ArrayList<TokenCode> (Arrays.asList(TokenCode.COMMA, TokenCode.SEMICOLON));

        _followParameterList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN,
                    TokenCode.COMMA));

        _followExpression = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN, TokenCode.RBRACKET,
                    TokenCode.SEMICOLON, TokenCode.COMMA));

        _followMethodDeclarations = new ArrayList<TokenCode>(Arrays.asList(TokenCode.RBRACE,
                    TokenCode.STATIC));

        _followNoneSuchType = new ArrayList<TokenCode>();
        _followNoneSuchType.addAll(_types);
        _followNoneSuchType.addAll(_followMethodDeclarations);

        _followParenthesizedExpressionList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.MULOP,
                    TokenCode.ADDOP, TokenCode.RELOP, TokenCode.RPAREN, TokenCode.SEMICOLON, 
                    TokenCode.COMMA, TokenCode.RBRACKET, TokenCode.IDENTIFIER, TokenCode.NUMBER,
                    TokenCode.LPAREN, TokenCode.NOT));
        
        _followVariableLoc2 = new ArrayList<TokenCode> (Arrays.asList(TokenCode.ASSIGNOP, 
                    TokenCode.INCDECOP));
        _followVariableLoc2.addAll(_followParenthesizedExpressionList);

        _followParenthesizedExpression = new ArrayList<TokenCode> (Arrays.asList(TokenCode.LBRACE));
        _followParenthesizedExpression.addAll(_followParenthesizedExpressionList);

        _followParameters = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN));

        _followVariableList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.SEMICOLON));

        _followStatement = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF, TokenCode.FOR, 
            TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK, 
            TokenCode.CONTINUE, TokenCode.RBRACE));

        _followStatementBlock = new ArrayList<TokenCode>(Arrays.asList(TokenCode.ELSE));
        _followStatementBlock.addAll(_followStatement);

        _followVariableDeclaration = new ArrayList<TokenCode> (Arrays.asList(TokenCode.STATIC));
        _followVariableDeclaration.addAll(_followStatement);
    }

    public static void main(String[] args) throws IOException {
        Parser p = new Parser(new Lexer(new FileReader(args[0])), args[0]);
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
        while (!followList.contains(_lookahead) && _lookahead != TokenCode.EOF) {
            readNextToken();
        }
    }

    private void recoverExpression() {
        while (!_firstExpression.contains(_lookahead) && !_firstOpExpression.contains(_opType) && 
                _lookahead != TokenCode.EOF) {
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
    private void program() {
        if (!match(TokenCode.CLASS) || !match(TokenCode.IDENTIFIER) || !match(TokenCode.LBRACE)) {
            recoverError(_firstVariableMethodDeclarations);
        }

        variableDeclarations(true);
        methodDeclarations();
        match(TokenCode.RBRACE);
    }

    private void variableDeclarations(boolean outSideOfScope) { // Epsilon x
        if (isType()) {
            type();
            variableList();
            if (!match(TokenCode.SEMICOLON)) {
                recoverError(_firstVariableDeclarationsStatementList);
            }
            variableDeclarations(outSideOfScope);
        } else if (outSideOfScope && _lookahead == TokenCode.IDENTIFIER) {
            setError(_token, "^ None such type");
            recoverError(_followNoneSuchType);
            variableDeclarations(outSideOfScope);
        } else if (!_followVariableDeclaration.contains(_lookahead)) {
            setError(_token, "^ Expected a type");
            recoverError(_followVariableDeclaration);
        }
    }

    private void type() { // don't need error recovery because isType is always called
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
                recoverError(_followVariable);
            }
        }
    }

    private void methodDeclarations() {
        if (_lookahead == TokenCode.STATIC) {
            methodDeclaration();
            moreMethodDeclarations();
        } else {
            setError(_token, "^ Expected 'static'");
            recoverError(_followMethodDeclarations);
        }
    }

    private void moreMethodDeclarations() { // Epsilon x
        if (_lookahead == TokenCode.STATIC) {
            methodDeclarations();
        }
    }

    private void methodDeclaration() {
        match(TokenCode.STATIC); // always checked if static, no need for recovery
        methodReturnType();
        if (!match(TokenCode.IDENTIFIER) || !match(TokenCode.LPAREN)) {
            recoverError(_firstParameters);
        }
        parameters();
        if (!match(TokenCode.RPAREN) || !match(TokenCode.LBRACE)) {
            recoverError(_firstVariableDeclarationsStatementList);
        }
        variableDeclarations(false);
        statementList();
        if (!match(TokenCode.RBRACE)) {
            recoverError(_followMethodDeclarations);
        }
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
            if (!match(TokenCode.IDENTIFIER)) {
                recoverError(_firstParameterList2);
            }
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
                if (!match(TokenCode.ASSIGNOP)) {
                    recoverExpression();
                }
                expression();
                if (!match(TokenCode.SEMICOLON)) {
                    recoverExpression();
                }
                expression();
                if (!match(TokenCode.SEMICOLON)) {
                    recoverError(_followMethodReturnType);
                }
                incrDecrVar();
                if (!match(TokenCode.RPAREN)) {
                    recoverError(_firstStatementBlock);
                }
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
                    recoverError(_followVariableList);
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
                        recoverError(_followVariableList);
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
                recoverError(_followVariableList);
        }
    }

    private void optionalExpression() { // Epsilon x
        if (isExpression()) {
            expression();
        }
    }

    private void statementBlock() {
        if (!match(TokenCode.LBRACE)) {
            recoverError(_followStatement);
        }
        statementList();
        if (!match(TokenCode.RBRACE)) {
            recoverError(_followStatementBlock);
        }
    }

    private void incrDecrVar() {
        variableLoc();
        if (!match(TokenCode.INCDECOP)) {
            recoverError(_followParameters);
        }
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
        match(TokenCode.LPAREN); // no need for error recovery or check
        expressionList();
        if (!match(TokenCode.RPAREN)) {
            recoverError(_followParenthesizedExpressionList);
        }
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
        if (!match(TokenCode.LPAREN)) {
            recoverExpression();
        }
        expression();
        if (!match(TokenCode.RPAREN)) {
            recoverError(_followParenthesizedExpression);
        }
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
        if (!match(TokenCode.IDENTIFIER)) {
            recoverError(_firstVariableLoc2);
        }
        variableLoc2();
    }

    private void variableLoc2() { // Epsilon x
        if (_lookahead == TokenCode.LBRACKET) {
            match(TokenCode.LBRACKET);
            expression();
            if (!match(TokenCode.RBRACKET)) {
                recoverError(_followVariableLoc2);
            }
        }
    }

    /*
     * Lookahead checks
     */
    private boolean isType() {
        return _types.contains(_lookahead);
    }

    private boolean isExpression() {
        return _expression.contains(_lookahead);
    }

    private boolean isStatementList() {
        return _statementList.contains(_lookahead);
    }
}
// I'm Mr Mezeeks, look at me;
