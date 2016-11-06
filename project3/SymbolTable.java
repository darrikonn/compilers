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

  public static SymbolTableEntry smartLookup(String lexeme) {
    if (_hasGlobalEnded) {
      return _lookupList_local.get(lexeme);
    }
    return _lookupList_global.get(lexeme);
  }

  public static SymbolTableEntry lookupGlobal(String lexeme) {
    return _lookupList_global.get(lexeme);
  }

  public static SymbolTableEntry insert(String lexeme, Type type, int params) {
    SymbolTableEntry symTabEntry = new SymbolTableEntry(lexeme, type, params);
    insertEntry(lexeme, symTabEntry);
    return symTabEntry;
  }

  public static SymbolTableEntry insert(String lexeme, Type type) {
    SymbolTableEntry symTabEntry = new SymbolTableEntry(lexeme, type, 0);
    insertEntry(lexeme, symTabEntry);
    return symTabEntry;
  }

  public static SymbolTableEntry insert(String lexeme) {
    SymbolTableEntry symTabEntry = new SymbolTableEntry(lexeme, null, 0);
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

  public static int localSize() {
    return _seqList_local.size();
  }

  public static int globalSize() {
    return _seqList_global.size();
  }

  public static SymbolTableEntry getLocalEntry(int index) {
    return _seqList_local.get(index);
  }

  public static SymbolTableEntry getGlobalEntry(int index) {
    return _seqList_global.get(index);
  }

  public static void resetLocalTable() {
    _lookupList_local.clear(); 
    _seqList_local.clear();
  }

  public static void printl() {
    for (SymbolTableEntry entry : _seqList_local) {
      System.out.print(entry.getLexeme() + " ");
    }
    System.out.println();
  }
  public static void printg() {
    for (SymbolTableEntry entry : _seqList_global) {
      System.out.print(entry.getLexeme() + " ");
    }
    System.out.println();
  }
}
