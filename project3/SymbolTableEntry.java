public class SymbolTableEntry {
  private Object _lexeme;
  private Type _type; 
  private int _params;

  public SymbolTableEntry(Object lexeme, Type type, int params) {
    _lexeme = lexeme;
    _type = type;
    _params = params;
  }

  public SymbolTableEntry(Object lexeme, Type type) {
    _lexeme = lexeme;
    _type = type;
    _params = 0;
  }

  public SymbolTableEntry(Object lexeme) {
    _lexeme = lexeme;
    _type = null;
    _params = 0;
  }

  public void setParams(int params) {
    _params = params;
  }

  public int getParams() {
    return _params;
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
