public enum TacCode {
  LABEL, UMINUS, ASSIGN, ADD, SUB, MULT, DIVIDE, DIV, MOD, OR, AND, NOT,
  LT, LE, GT, GE, EQ, NE, GOTO, CALL, APARAM, FPARAM, VAR, RETURN, NOOP
};

/*
      if (_token.getOpType() == OpType.AND) {
        // &&
        // prenta fyrri ut
        SymbolTableEntry label1 = newLabel();
        SymbolTableEntry label2 = newLabel();
        SymbolTableEntry tmp = newTemp();
        _codeGen.generate(tc, prevEntry, factorEntry, label1);
        _codeGen.generate(TacCode.ASSIGN, SymbolTable.lookup("1"), null, label1);
        _codeGen.generate(TacCode.GOTO, null, null, label2);
        _codeGen.generate(TacCode.LABEL, null, null, label1);
        _codeGen.generate(TacCode.LABEL, null, null, label2);
*/
