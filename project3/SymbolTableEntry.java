public class SymbolTableEntry {
  private Object _lexeme;
  private Type _type; 

  public SymbolTableEntry(Object lexeme, Type type) {
    _lexeme = lexeme;
    _type = type;
  }

  public SymbolTableEntry(Object lexeme) {
    _lexeme = lexeme;
    _type = null;
  }

  public void setType(Type type) {
    _type = type;
  }

  public Type getType() {
    return _type;
  }

  public Object getLexeme() {
    return _lexeme;
  }
}
