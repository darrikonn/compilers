public enum OpType {
  NONE, INC, DEC, EQUAL, NOT_EQUAL, LT, GT, LTE, GTE, 
  PLUS, MINUS, OR, MULT, DIV, MOD, AND, ASSIGN;

  public TacCode map() {
    switch(this) {
      case EQUAL: return TacCode.EQ;
      case NOT_EQUAL: return TacCode.NE;
      case LT: return TacCode.LT;
      case LTE: return TacCode.LE;
      case GT: return TacCode.GT;
      case GTE: return TacCode.GE;
      case PLUS: return TacCode.ADD;
      case MINUS: return TacCode.SUB;
      default: return TacCode.NOOP;
    }
  }
}
