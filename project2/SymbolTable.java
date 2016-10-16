import java.util.ArrayList;

class SymbolTable {
  // possibly change to a hashtable
  private ArrayList<SymbolTableEntry> _symbolTableEntries;

  public SymbolTable() {
    _symbolTableEntries = new ArrayList<SymbolTableEntry>();
  }

  // easier lookup with a hashtable
  public SymbolTableEntry findSymbolTableEntry(Object lexeme) {
    for (SymbolTableEntry s : _symbolTableEntries) {
      if (s.getLexeme().equals(lexeme)) {
        return s;
      }
    }

    return null;
  }

  public SymbolTableEntry addSymbolTableEntry(Object lexeme) {
    SymbolTableEntry entry = new SymbolTableEntry(lexeme);
    _symbolTableEntries.add(entry);

    return entry;
  }

  public ArrayList<SymbolTableEntry> getSymbolTableEntries() {
    return _symbolTableEntries;
  }
}
