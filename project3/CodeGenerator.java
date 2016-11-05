import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

class CodeGenerator {
  private QuadrupleList _quadrupleList;
  private int _maxLabel;
  public CodeGenerator() {
    _quadrupleList = new QuadrupleList();
    _maxLabel = 0;
  }

  private void setMaxLabel(String lexeme) {
    int length = lexeme.length();
    if (length > _maxLabel) {
      _maxLabel = length;
    }
  }

  public void generate(TacCode code, SymbolTableEntry param1, SymbolTableEntry param2, 
      SymbolTableEntry result) {
    if (code == TacCode.LABEL) {
      setMaxLabel(result.getLexeme().toString());
    }
    _quadrupleList.addQuadruple(code, param1, param2, result);
  }

  public void print() {
    ArrayList<Quadruple> quads = _quadrupleList.getQuadruples();

    _maxLabel++;
    System.out.format("%" + _maxLabel + "s%10s%15s%15s%15s\n", "", "Op", "Param1", "Param2", "Result");
    System.out.println(StringUtils.repeat("-", 55 + _maxLabel));
    boolean setLabel = false;
    String lbl = "";
    for (Quadruple quad : quads) {
      SymbolTableEntry tmp;
      String res = (tmp = quad.getResult()) != null ? tmp.getLexeme().toString() : "";
      TacCode tc = quad.getTacCode();
      if (tc == TacCode.LABEL) {
        lbl = res + ":";
        setLabel = true;
        continue;
      }

      String p1 = (tmp = quad.getParam1()) != null ? tmp.getLexeme().toString() : "";
      String p2 = (tmp = quad.getParam2()) != null ? tmp.getLexeme().toString() : "";

      System.out.format("%" + _maxLabel + "s%10s%15s%15s%15s\n", lbl, tc, p1, p2, res);

      if (setLabel) {
        setLabel = false;
        lbl = "";
      }
    }

/*
    System.out.format("%15s%15s%15s%15s\n", "Op", "Param1", "Param2", "Result");
    System.out.println(StringUtils.repeat("-", 60));
    for (Quadruple quad : quads) {
      SymbolTableEntry tmp;
      String p1 = (tmp = quad.getParam1()) != null ? tmp.getLexeme().toString() : "";
      String p2 = (tmp = quad.getParam2()) != null ? tmp.getLexeme().toString() : "";
      String res = (tmp = quad.getResult()) != null ? tmp.getLexeme().toString() : "";
      System.out.format("%15s%15s%15s%15s\n", quad.getTacCode(), p1, p2, res);
    }
    */
  }
}
