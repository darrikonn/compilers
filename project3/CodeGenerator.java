import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

class CodeGenerator {
  private QuadrupleList _quadrupleList;
  public CodeGenerator() {
    _quadrupleList = new QuadrupleList();
  }

  public void generate(TacCode code, SymbolTableEntry param1, SymbolTableEntry param2, SymbolTableEntry result) {
  _quadrupleList.addQuadruple(code, param1, param2, result);
  }

  public void print() {
    ArrayList<Quadruple> quads = _quadrupleList.getQuadruples();

    System.out.format("%15s%15s%15s%15s\n", "Op", "Param1", "Param2", "Result");
    System.out.println(StringUtils.repeat("-", 60));
    for (Quadruple quad : quads) {
      SymbolTableEntry tmp;
      String p1 = (tmp = quad.getParam1()) != null ? tmp.getLexeme().toString() : "";
      String p2 = (tmp = quad.getParam2()) != null ? tmp.getLexeme().toString() : "";
      String res = (tmp = quad.getResult()) != null ? tmp.getLexeme().toString() : "";
      System.out.format("%15s%15s%15s%15s\n", quad.getTacCode(), p1, p2, res);
    }
  }
}
