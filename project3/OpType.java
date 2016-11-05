public enum OpType {
  NONE, INC, DEC, EQUAL, NOT_EQUAL, LT, GT, LTE, GTE, 
  PLUS, MINUS, OR, MULT, DIV, MOD, AND, ASSIGN;

  public TacCode map() {
    switch(this) {
      case EQUAL: return TacCode.NE;
      case NOT_EQUAL: return TacCode.EQ;
      case LT: return TacCode.GE;
      case LTE: return TacCode.GT;
      case GT: return TacCode.LE;
      case GTE: return TacCode.LT;
      case PLUS: return TacCode.ADD;
      case MINUS: return TacCode.SUB;
      default: return TacCode.NOOP;
    }
  }
}
