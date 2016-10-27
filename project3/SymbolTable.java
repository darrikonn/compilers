import java.util.*;

public class SymbolTable {
  private static Hashtable<String, SymbolTableEntry> _lookupList = 
      new Hashtable<String, SymbolTableEntry>();
  private static ArrayList<SymbolTableEntry> _seqList = new ArrayList<SymbolTableEntry>();

  public static SymbolTableEntry lookup(String lexeme) {
    return _lookupList.get(lexeme);
  }

  public static SymbolTableEntry insert(String lexeme) {
    SymbolTableEntry symTabEntry = new SymbolTableEntry(lexeme);
    _lookupList.put(lexeme, symTabEntry);
    _seqList.add(symTabEntry);
    return symTabEntry;
  }

  public static int size() {
    return _seqList.size();
  }

  public static SymbolTableEntry getEntry(int index) {
    return _seqList.get(index);
  }
}
