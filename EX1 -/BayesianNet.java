import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class BayesianNet<T> {
 public Hashtable<String, node> AllNodes;
 public List<Integer> Options = Arrays.asList(0, 1, 10, 11, 100, 101, 110, 111, 1000, 1001, 1010, 1011, 1100, 1101, 1110, 1111,
   10000, 10001, 10010, 10011, 10100, 10101, 10110, 10111, 11000, 11001, 11010, 11011, 11100, 11101, 11110, 11111);
 

 public BayesianNet() {
  AllNodes = new Hashtable<>();
 }

 public node GetNode(String Nname) {
  return AllNodes.get(Nname);
 }

 public node GetNode(Object Nname) {
  return AllNodes.get(Nname);
 }

 public void PrintAllNode() {
  for (String s : AllNodes.keySet()) {
   AllNodes.get(s).ToString();
  }
 }

 public void BuildCPT(node Ncpt){
  int row = 0;
  int col = 0;
  System.out.println("Curr Ncpt: "+ Ncpt.name);
  for (Object option : Ncpt.Outcome) {
   for (Object temp : Ncpt.Parents) {
    System.out.println("Parents: " + temp.toString());
    node parent = GetNode(temp.toString());
    for (int i = 0; i < Ncpt.Parents.size(); i++) {

    }
   }
  }
 }
}
