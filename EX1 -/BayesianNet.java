import java.util.Hashtable;

public class BayesianNet<T> {
 public Hashtable<String, node> AllNodes;

 public BayesianNet() {
  AllNodes = new Hashtable<>();
 }

 public node GetNode(String Nname) {
  return AllNodes.get(Nname);
 }

 public void PrintAllNode() {
  for (String s : AllNodes.keySet()) {
   AllNodes.get(s).ToString();
  }
 }
}
