import java.util.ArrayList;

public class QuadrupleList {
  private ArrayList<Quadruple> _quadruples;

  public QuadrupleList() {
    _quadruples = new ArrayList<Quadruple>();
  }

  public void addQuadruple(TacCode code, SymbolTableEntry param1, SymbolTableEntry param2, SymbolTableEntry result) {
    _quadruples.add(new Quadruple(code, param1, param2, result));
  }

  public ArrayList<Quadruple> getQuadruples() {
    return _quadruples;
  }
}
