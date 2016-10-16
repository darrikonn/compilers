// first
_firstOpExpression = new ArrayList<OpType> (Arrays.asList(
            OpType.MINUS, 
            OpType.PLUS
            );


_firstExpression = (
            TokenCode.NOT, 
            TokenCode.LPAREN,
            TokenCode.NUMBER, 
            TokenCode.IDENTIFIER
            );

_firstVariableLoc2 = (
            TokenCode.LBRACKET,
            TokenCode.ASSIGNOP, 
            TokenCode.INCDECOP
            );

_firstStatementBlock = (
            TokenCode.LBRACE
            );

_firstParameterList2 = (
            TokenCode.RPAREN, 
            TokenCode.COMMA
            );

_firstIncrDecrVar = _followMethodReturnType = (
            TokenCode.IDENTIFIER
            );

_types = (
        TokenCode.INT, 
        TokenCode.REAL
        );

_firstParameters = _types + (
            TokenCode.RPAREN
            );

_firstVariableMethodDeclarations = _types + (
            TokenCode.RBRACE
            TokenCode.IDENTIFIER
            TokenCode.STATIC
            );

_firstVariable2 = (
            TokenCode.LBRACKET, 
            TokenCode.COMMA,
            TokenCode.SEMICOLON
            );

_firstVariableDeclarationsStatementList = (
            TokenCode.IF,
            TokenCode.FOR, 
            TokenCode.LBRACE, 
            TokenCode.IDENTIFIER, 
            TokenCode.RETURN, 
            TokenCode.BREAK,
            TokenCode.CONTINUE, 
            TokenCode.RBRACE
            );

// follow
_followNoneSuchType = _types + (
            TokenCode.STATIC,
            TokenCode.RBRACE
            );

_followVariableLoc2 = _followParenthesizedExpressionList + (
            TokenCode.ASSIGNOP, 
            TokenCode.INCDECOP
            );

_followParenthesizedExpression = _followParenthesizedExpressionList + (
            TokenCode.LBRACE
            );

_followParenthesizedExpressionList = (
            TokenCode.MULOP,
            TokenCode.ADDOP, 
            TokenCode.RELOP, 
            TokenCode.RPAREN, 
            TokenCode.SEMICOLON, 
            TokenCode.COMMA, 
            TokenCode.RBRACKET, 
            TokenCode.IDENTIFIER, 
            TokenCode.NUMBER,
            TokenCode.LPAREN, 
            TokenCode.NOT
            );

_followIncrDecrVar = _followParameters = (
            TokenCode.RPAREN
            );


_followMethodDeclarations = (
            TokenCode.RBRACE,
            TokenCode.STATIC
            );

_followVariable = (
            TokenCode.COMMA, 
            TokenCode.SEMICOLON
            );

_followProgram = _types + (
            TokenCode.STATIC, 
            );

_followParameterList = (
            TokenCode.RPAREN,
            TokenCode.COMMA
            );

_followExpression = (
            TokenCode.RPAREN, 
            TokenCode.RBRACKET,
            TokenCode.SEMICOLON, 
            TokenCode.COMMA
            );

_followStatement = (
            TokenCode.IF, 
            TokenCode.FOR, 
            TokenCode.LBRACE, 
            TokenCode.IDENTIFIER, 
            TokenCode.RETURN, 
            TokenCode.BREAK, 
            TokenCode.CONTINUE, 
            TokenCode.RBRACE
            );

_followStatementBlock = _followStatement + (
            TokenCode.ELSE
            );

_followVariableList = _followStatement2 = (
            TokenCode.SEMICOLON
            );

_followVariableDeclaration = _followStatement + (
            TokenCode.STATIC, 
            );
