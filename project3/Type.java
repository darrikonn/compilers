public enum Type {
  INT, REAL, TRUE, FALSE, VOID;

  @Override
  public String toString() {
    switch(this) {
      case VOID: return "^ Expected no return type";
      case INT: return "^ Expected an integer";
      case REAL: return "^ Expected a real";
      case TRUE: return "^ Expected 'true'";
      case FALSE: return "^ Expected 'false'";
      default: return "Expected something amazing";
    }
  }
}
