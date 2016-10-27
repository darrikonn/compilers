class Quadruple {
  // This class should hold the data for one TAC instruction.  It should have members for the TacCode, param1, param2 and result.  Param1, param2 and result should all be of the type SymbolTableEntry.
  private SymbolTableEntry _param1, _param2, _result;
  private TacCode _tacCode;

  public Quadruple(TacCode tc, SymbolTableEntry p1, SymbolTableEntry p2, SymbolTableEntry result) {
    _tacCode = tc;
    _param1 = p1;
    _param2 = p2;
    _result = result;
  }

  public TacCode getTacCode() {
    return _tacCode;
  }

  public SymbolTableEntry getParam1() {
    return _param1;
  }

  public SymbolTableEntry getParam2() {
    return _param2;
  }

  public SymbolTableEntry getResult() {
    return _result;
  }
}
