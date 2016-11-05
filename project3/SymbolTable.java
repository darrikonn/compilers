import java.util.*;

public class SymbolTable {
  // global
  private static Hashtable<String, SymbolTableEntry> _lookupList_global = 
      new Hashtable<String, SymbolTableEntry>();
  private static ArrayList<SymbolTableEntry> _seqList_global = new ArrayList<SymbolTableEntry>();

  // local
  private static Hashtable<String, SymbolTableEntry> _lookupList_local = 
      new Hashtable<String, SymbolTableEntry>();
  private static ArrayList<SymbolTableEntry> _seqList_local = new ArrayList<SymbolTableEntry>();

  private static boolean _hasGlobalEnded = false;

  public static void endGlobal() {
    _hasGlobalEnded = true;
  }

  public static void startGlobal() {
    _hasGlobalEnded = false;
  }

  public static SymbolTableEntry lookup(String lexeme) {
    SymbolTableEntry entry;
    if ((entry = _lookupList_local.get(lexeme)) == null) {
      return _lookupList_global.get(lexeme);
    }
    return entry;
  }

  public static SymbolTableEntry insert(String lexeme, Type type) {
    SymbolTableEntry symTabEntry = new SymbolTableEntry(lexeme, type);
    insertEntry(lexeme, symTabEntry);
    return symTabEntry;
  }

  public static SymbolTableEntry insert(String lexeme) {
    SymbolTableEntry symTabEntry = new SymbolTableEntry(lexeme, null);
    insertEntry(lexeme, symTabEntry);
    return symTabEntry;
  }

  private static void insertEntry(String lexeme, SymbolTableEntry entry) {
    if (_hasGlobalEnded) {
      _lookupList_local.put(lexeme, entry);
      _seqList_local.add(entry);
    } else {
      _lookupList_global.put(lexeme, entry);
      _seqList_global.add(entry);
    }
  }

  public static int size() {
    if (_hasGlobalEnded) {
      return _seqList_local.size();
    }
    return _seqList_global.size();
  }

  public static SymbolTableEntry getEntry(int index) {
    if (_hasGlobalEnded) {
      return _seqList_local.get(index);
    }
    return _seqList_global.get(index);
  }

  public static void resetLocalTable() {
    _lookupList_local.clear(); 
    _seqList_local.clear();
  }
}
