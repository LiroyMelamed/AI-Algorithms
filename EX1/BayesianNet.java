import java.util.Hashtable;
import java.util.LinkedList;

public class BayesianNet<T> {
 public static Hashtable<String, node> AllNodes;

 /**
  * My BayesianNet Class store all the nodes that are in My net
  * And represents the probabilistic relationships between different variables
  */
  
 public BayesianNet() {
  AllNodes = new Hashtable<>();
 }

 public static node GetNode(String Nname) {
  return AllNodes.get(Nname);
 }

 public static node GetNode(Object Nname) {
  return AllNodes.get(Nname);
 }

 public void PrintAllNode() {
  for (String s : AllNodes.keySet()) {
   AllNodes.get(s).ToString();
  }
 }

 /**
  * BuiltCPT is a function that build the CPT table for each node in the our net
  * 
  * @param Ncpt the current node to build his CPT table
  */

 public void BuildCPT(node Ncpt) {
  int row = 0;
  int temp = Ncpt.Outcome.size();
  // String array of the parents of the current node
  String[] Parents = new String[Ncpt.Parents.size()];
  // puts the name of the current node in the last col -1
  Ncpt.CPT[row][Ncpt.Parents.size()] = Ncpt.name;
  // loop for putting the outcomes of the current node
  while (row < Ncpt.Probability.size()) {
   for (Object string : Ncpt.Outcome) {
    row++;
    Ncpt.CPT[row][Ncpt.Parents.size()] = string.toString();
   }
  }
  // loop for puting the parents in the string array
  for (int i = 0; i < Ncpt.Parents.size();) {
   for (Object string : Ncpt.Parents) {
    node parent = GetNode(string);
    Parents[i] = parent.name;
    i++;
   }
  }
  // initialize the row to 0
  row = 0;
  // loop for puting the parents in the currect place
  for (int i = Parents.length - 1; i >= 0; i--) {
   // init list of outcome for each parent
   LinkedList<String> Outcome = new LinkedList<>();
   // putting the parent name in the first row
   Ncpt.CPT[row][i] = Parents[i];
   // loop for adding the outcome of the current parent
   for (Object string : GetNode(Parents[i]).Outcome) {
    Outcome.add(string.toString());
   }
   // sending to our builtOptions function
   builtOptions(Ncpt, Outcome, i, temp);
   // the number of time to put each outcome * the current num of outcome
   temp = temp * Outcome.size();
  }

  // loop to put the probabilty in the last col
  for (Object Prob : Ncpt.Probability) {
   row++;
   Ncpt.CPT[row][Ncpt.Parents.size() + 1] = Prob.toString();
  }

  buildFastCPT(Ncpt);
 }

 /**
  * buildOptions is a Function that puts the outcome for each given parent in the
  * parent col
  * 
  * @param child   node of the current node that we build the CPT table for
  * @param outcome LinkedList of outcomes of the current parent
  * @param col     the column of the parent
  * @param temp    the number of time each outcome need to be written
  */
 public void builtOptions(node child, LinkedList<String> outcome, int col, int temp) {
  // initalize the row number to 1
  int row = 1;
  // while the row number is smaller then the probabilty options
  while (row < child.Probability.size()) {
   // for each outcome of the parent
   for (String string : outcome) {
    // loop of how mant time to put the specific outcome
    for (int i = 0; i < temp; i++) {
     child.CPT[row][col] = string;
     row++;
    }
   }
  }
 }

 /**
  * buildFactCpt is a function that build an HashMap and puts inside the querrys as an key and the
  * probabilty as a value
  *
  * @param NCPT node to build his CPT
  */

 public void buildFastCPT(node NCPT) {
  for (int i = 1; i < NCPT.CPT.length; i++) {
   String querry = "";
   for (int j = 0; j < NCPT.CPT[0].length; j++) {
    if (j != NCPT.CPT[0].length - 1) {
     querry = querry + NCPT.CPT[i][j];
    }
    if (j == NCPT.CPT[0].length - 1) {
     NCPT.FastCPT.put(querry, NCPT.CPT[i][j]);
    }
   }
  }
 }
}
