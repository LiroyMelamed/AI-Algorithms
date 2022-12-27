import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Algorithms {

  /**
   * My SimpleDeduction Function represent Algorithm number 1 (SD).
   * The Algorithm and the Function works that way
   * 1. take all the variables in the querry split them into Root and Given and find all the other variables
   * 2. find all the quests of the Numerator and solve them
   * 3. find all the quests of the Denominator and solve them
   * 4. divide the Numerator by Numerator + Denominator
   * 
   * @param querry the full querry from the input file
   */

 public static void SimpleDeduction(String[] querry) {
  int plus = -1;                // Stores the number of time we preform plus (+)
  double multipal = 0;          // Stores the number of time we preform mulitple (*)

  HashMap<String, String> Given = new HashMap<String, String>(); // The names of the Evidence in the querry
  HashMap<String, String> Root = new HashMap<String, String>();  // The name of the Root in the querry
  ArrayList<String> Hidden = new ArrayList<String>();            // All the hidden variables in the Net

  String[] Quests;
  double Numerator = 0;     // Store the Numerator
  double Denominator = 0;   // Store the Denominator

  String[] RootSplit = querry[0].trim().split("="); // Splitting the querry
  Root.put(RootSplit[0], RootSplit[1]);
  String[] array = querry[1].trim().split(",");
  for (String string : array) {
   String[] Splited = string.trim().split("=");
   Given.put(Splited[0], Splited[1]);
  }

  String check = ""; // In case that the querry is in the root CPT
  for (String string : Given.keySet()) {
    check += Given.get(string);
  }
  check += RootSplit[1];
  if(BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.containsKey(RootSplit[1]) || (CheckCPT(Root, Given) && BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.containsKey(check))){ // In case that the querry is in the root CPT
    if (BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.containsKey(RootSplit[1])) {
      DecimalFormat df = new DecimalFormat("#0.00000");
      String writeToFile = df.format(Double.parseDouble(BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.get(RootSplit[1]).toString())) + "," + Integer.toString(0) + "," + Integer.toString((int) 0);
      try (FileWriter fw = new FileWriter("output.txt", true);
        BufferedWriter bw = new BufferedWriter(fw)) {
        bw.write(writeToFile);
        bw.newLine();
      } catch (IOException e) {
          e.printStackTrace();
      }
    }
    else{
      DecimalFormat df = new DecimalFormat("#0.00000");
      String writeToFile = df.format(Double.parseDouble(BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.get(check).toString())) + "," + Integer.toString(0) + "," + Integer.toString((int) 0);
      try (FileWriter fw = new FileWriter("output.txt", true);
        BufferedWriter bw = new BufferedWriter(fw)) {
        bw.write(writeToFile);
        bw.newLine();
      } catch (IOException e) {
          e.printStackTrace();
      }
    }
  }
  else{ // The querry isnt in the root CPT Table
    Hidden = FindHidden(Root, Given);           // Sending to function that find all the hidden
    Quests = MakeQuests(Root, Given, Hidden);   // Makes all the posiible quests with all the variables
    
    for (int i = 0; i < Quests.length; i++) {   // Solving every Quest Of the Numerator solo
      double[] answer = SolveQuerry(Quests[i], multipal);
      Numerator += answer[0];
      multipal += answer[1];
      plus++;
    }

    Hidden = FindHidden(Root, Given);
    Quests = MakeQuestsDenominator(Root, Given, Hidden);
    for (int i = 0; i < Quests.length; i++) {   // Solving every Quest Of the Denominator solo
      double[] answer = SolveQuerry(Quests[i], multipal);
      Denominator += answer[0];
      multipal += answer[1];
      plus++;
    }
    DecimalFormat df = new DecimalFormat("#0.00000");
    double FinalSoulotion = Numerator / (Numerator + Denominator);  // Solving the final soulotion
    String writeToFile = df.format(FinalSoulotion) + "," + Integer.toString(plus) + "," + Integer.toString((int) multipal);
    try (FileWriter fw = new FileWriter("output.txt", true);
      BufferedWriter bw = new BufferedWriter(fw)) {
      bw.write(writeToFile);
      bw.newLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
  }

 /**
  * The FindHidden function finds all the variables that not in the querry
  *
  * @param Root the variable that we asked about
  * @param Given the evidence variable
  * @return ArrayList of all the hidden variables
  */

 public static ArrayList<String> FindHidden(HashMap Root, HashMap Given) {
  Hashtable<String, node> Temp = new Hashtable<>();
  Temp.putAll(BayesianNet.AllNodes);
  ArrayList<String> Hidden = new ArrayList<String>();
  for (Object key : Root.keySet()) {
   if (Temp.containsKey(key)) {
    Temp.remove(key);
   }
  }
  for (Object key : Given.keySet()) {
   if (Temp.containsKey(key)) {
    Temp.remove(key);
   }
  }
  for (Object key : Temp.keySet()) {
   Hidden.add(BayesianNet.GetNode(key).name);
  }
  // System.out.println(Temp);
  return Hidden;
 }

 /**
  * The MakeQuests Function make all the avaible permotation of the Numerator
  *
  * @param Root the variable that we asked about
  * @param Given the evidence variable
  * @param Hidden all the hidden variables
  * @return String[] with all the possible quests
  */

 public static String[] MakeQuests(HashMap Root, HashMap Given, ArrayList Hidden) {
  int NumOfSubQuest = 1;
  int temp = 1;
  for (int i = 0; i < Hidden.size(); i++) {
   NumOfSubQuest *= BayesianNet.AllNodes.get(Hidden.get(i)).Outcome.size();
  }
  String[] Quests = new String[NumOfSubQuest];
  String Mat[][] = new String[NumOfSubQuest][Hidden.size()];
  for (int i = 0; i < Hidden.size(); i++) {
   ArrayList<String> Outcomes = new ArrayList<String>();
   for (Object string : BayesianNet.AllNodes.get(Hidden.get(i)).Outcome) {
    Outcomes.add(string.toString());
   }
   Mat = builtOptions(Mat, Outcomes, i, temp, NumOfSubQuest);
   temp *= Outcomes.size();
  }
  for (int i = 0; i < NumOfSubQuest; i++) {
   String quest = "";
   for (Object string : Root.keySet()) {
    quest += string + "=" + Root.get(string);
   }
   for (Object string : Given.keySet()) {
    quest += "," + string + "=" + Given.get(string);
   }
   for (int j = 0; j < Mat[0].length; j++) {
    quest += "," + Hidden.get(j) + "=" + Mat[i][j];
   }
   Quests[i] = quest;
  }
  return Quests;
 }

  /**
  * The MakeQuestsDenominator Function make all the avaible permotation of the Denominator
  *
  * @param Root the variable that we asked about
  * @param Given the evidence variable
  * @param Hidden all the hidden variables
  * @return String[] with all the possible quests
  */

 public static String[] MakeQuestsDenominator(HashMap Root, HashMap Given, ArrayList Hidden) {
  int NumOfSubQuest = 1;
  int temp = 1;
  for (Object object : Root.keySet()) {
   NumOfSubQuest *= BayesianNet.AllNodes.get(object).Outcome.size() - 1;
  }
  for (int i = 0; i < Hidden.size(); i++) {
   NumOfSubQuest *= BayesianNet.AllNodes.get(Hidden.get(i)).Outcome.size();
  }
  String[] Quests = new String[NumOfSubQuest];
  String Mat[][] = new String[NumOfSubQuest][Hidden.size() + 1];
  for (int i = 0; i < Hidden.size(); i++) {
   ArrayList<String> Outcomes = new ArrayList<String>();
   for (Object string : BayesianNet.AllNodes.get(Hidden.get(i)).Outcome) {
    Outcomes.add(string.toString());
   }
   Mat = builtOptions(Mat, Outcomes, i, temp, NumOfSubQuest);
   temp *= Outcomes.size();
  }
  for (Object object : Root.keySet()) {
   ArrayList<String> Outcomes = new ArrayList<String>();
   for (Object string : BayesianNet.AllNodes.get(object).Outcome) {
    if (!Root.get(object).equals(string)) {
     Outcomes.add(string.toString());
    }
   }
   Mat = builtOptions(Mat, Outcomes, Hidden.size(), temp, NumOfSubQuest);
   temp *= Outcomes.size();
  }
  for (int i = 0; i < NumOfSubQuest; i++) {
   String quest = "";
   for (Object string : Root.keySet()) {
    quest += string + "=" + Mat[i][Hidden.size()];
   }
   for (Object string : Given.keySet()) {
    quest += "," + string + "=" + Given.get(string);
   }
   for (int j = 0; j < Mat[0].length - 1; j++) {
    quest += "," + Hidden.get(j) + "=" + Mat[i][j];
   }

   Quests[i] = quest;
  }
  return Quests;
 }

 /**
  * The builtOptions Function built each coloum of the Mat with the outcome of the current node
  *
  * @param Mat the matrix we are working on
  * @param outcome the outcome of the variable
  * @param col the index of the variable in the coluom
  * @param temp the number of time each outcome need to be written
  * @param NumOfSubQuest the number of quests in the matrix
  * @return the changed matrix
  */

 public static String[][] builtOptions(String Mat[][], ArrayList<String> outcome, int col, int temp,
   int NumOfSubQuest) {
  // initalize the row number to 1
  int row = 0;
  // while the row number is smaller then the probabilty options
  while (row < NumOfSubQuest) {
   // for each outcome of the parent
   for (String string : outcome) {
    // loop of how mant time to put the specific outcome
    for (int i = 0; i < temp; i++) {
     Mat[row][col] = string;
     row++;
    }
   }
  }
  return Mat;
 }

 /**
  * The SolveQuerry Function solve the querrys
  *
  * @param querry the current querry
  * @param multipal the number of time we use multiply (*)
  * @return the answer of the query and the number of time we use multiply as a double[]
  */

 public static double[] SolveQuerry(String querry, double multipal) {
  double Prob = 1;
  double[] answer= new double[2];
  HashMap<String, String> Variable = new HashMap<String, String>();
  String[] Splitted = querry.trim().split(",");
  for (int i = 0; i < Splitted.length; i++) {
   String[] SplitEqual = Splitted[i].trim().split("=");
   Variable.put(SplitEqual[0], SplitEqual[1]);
  }
  multipal = Variable.size()-1;
  for (String string : Variable.keySet()) {
   if (BayesianNet.GetNode(string).Parents.isEmpty()) {
    Prob *= Double.parseDouble(BayesianNet.GetNode(string).FastCPT.get(Variable.get(string)).toString());
   } else {
    String QuestWithParents = "";
    for (Object parent : BayesianNet.GetNode(string).Parents) {
     QuestWithParents += Variable.get(parent.toString()).toString();
    }
    QuestWithParents += Variable.get(string);
    Prob *= Double.parseDouble(BayesianNet.GetNode(string).FastCPT.get(QuestWithParents).toString());
   }
  }
  answer[0] = Prob;
  answer[1] = multipal;
  return answer;
 }

// End of SimpleDeduction algorithm


/**
 * My VariableElimination Function represent Algorithm number 2 (VE) and Algorithm number 3 by diffrent sorting
 * The Algorithm and the Function works that way:
 * 
 * Find all the hidden variable that suppose to be in the querry
 * Build a List of Factors
 * Place all the evidence in every Factor CPT
 * If the CPT is empty or has just 1 outcome remove the Factor from the list of Factors
 * 
 * Now for Every Hidden ->
 * 1. Join all the Factor that contains this hidden
 * 2. Eliminate the hidden from the Joined Factor
 * 3. Do it for all the Factor that contains this hidden
 * 
 * Then Normalize the relevent Factor
 * 
 * @param querry the full querry from the input file
 */
 public static void VariableElimination(String[] querry){
  int plus = -1; 
  int multipal = 0;

  HashMap<String, String> Given = new HashMap<String, String>();
  HashMap<String, String> Root = new HashMap<String, String>();
  ArrayList<String> Hidden = new ArrayList<String>();

  String[] RootSplit = querry[0].trim().split("=");
  Root.put(RootSplit[0], RootSplit[1]);
  String[] array = querry[1].trim().split(",");
  for (String string : array) {
   String[] Splited = string.trim().split("=");
   Given.put(Splited[0], Splited[1]);
  }

  String check = ""; // In case that the querry is in the root CPT
  for (String string : Given.keySet()) {
    check += Given.get(string);
  }
  check += RootSplit[1];
  if(BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.containsKey(RootSplit[1]) || (CheckCPT(Root, Given) && BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.containsKey(check))){ // In case that the querry is in the root CPT
    if (BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.containsKey(RootSplit[1])) {
      DecimalFormat df = new DecimalFormat("#0.00000");
      String writeToFile = df.format(Double.parseDouble(BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.get(RootSplit[1]).toString())) + "," + Integer.toString(0) + "," + Integer.toString((int) 0);
      try (FileWriter fw = new FileWriter("output.txt", true);
        BufferedWriter bw = new BufferedWriter(fw)) {
        bw.write(writeToFile);
        bw.newLine();
      } catch (IOException e) {
          e.printStackTrace();
      }
    }
    else{
      DecimalFormat df = new DecimalFormat("#0.00000");
      String writeToFile = df.format(Double.parseDouble(BayesianNet.AllNodes.get(RootSplit[0]).FastCPT.get(check).toString())) + "," + Integer.toString(0) + "," + Integer.toString((int) 0);
      try (FileWriter fw = new FileWriter("output.txt", true);
        BufferedWriter bw = new BufferedWriter(fw)) {
        bw.write(writeToFile);
        bw.newLine();
      } catch (IOException e) {
          e.printStackTrace();
      }
    }
  }
  else{ // The querry isnt in the root CPT
    Hidden = FindHiddenFather(Root, Given);
    List<Factor> Factors = new ArrayList<Factor>();
    Factors = BuildFactorList(Root, Given, Hidden);

    for (Object Evidence : Given.keySet()) {
      List<Factor> FactorsToEliminate = new ArrayList<Factor>();
      for (int i = 0; i < Factors.size(); i++) {
        if(Factors.get(i).containsVariable(BayesianNet.GetNode(Evidence))){
          FactorsToEliminate.add(Factors.get(i));
          Factors.remove(Factors.get(i));
          i--;
        }
      }
      for (int i = 0; i < FactorsToEliminate.size(); i++) {
        if (FactorsToEliminate.get(i).FactorCPT[0].length > 2) {
          Factors.add(EliminateGiven(FactorsToEliminate.get(i), Evidence.toString(), Given.get(Evidence)));
        }
        else{
          Factors.add(EliminateSmallGiven(FactorsToEliminate.get(i), Evidence.toString(), Given.get(Evidence)));
        }
      }
    }

    for (int i = 0; i < Factors.size(); i++) {
      if (Factors.get(i).FactorCPT.length == 2) {
        Factors.remove(i);
        i--;
      }
    }

    if(querry[2].contains("2")){ // Algorithm number 2 Sort it Alphabtic
      Collections.sort(Hidden);
    }

    
    if(querry[2].contains("3")){ // Algorithm number 3 Sort the hidden by the sum of the times they apper in each Fator CPT table that contain the hidden
      HashMap<String,Integer> map = new HashMap<>();
      for (int i = 0; i < Hidden.size(); i++) {
        int numberOftime = 0;
        List<Factor> TempList = new ArrayList<Factor>();
        TempList.addAll(Factors);
        for (int j = 0; j < TempList.size(); j++) {
          if (TempList.get(j).containsVariable(BayesianNet.GetNode(Hidden.get(i)))){
            numberOftime += TempList.get(j).FactorCPT.length;
            TempList.remove(TempList.get(j));
            j--;
          }
        }
        map.put(Hidden.get(i),numberOftime);
      }
      Map<String, Integer> sortedMap = map.entrySet().stream()
      .sorted(Map.Entry.comparingByValue())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
              (oldValue, newValue) -> oldValue, LinkedHashMap::new));
      int i = 0;
      for (String hidden : sortedMap.keySet()) {
        Hidden.set(i, hidden);
        i++;
      }
    }

    while(!Hidden.isEmpty()){ // Hidden Join&Elimination loop
      String currentVariable = Hidden.get(0);
      List<Factor> FactorsToJoin = new ArrayList<Factor>();
      for (int i = 0; i < Factors.size(); i++) {
        if (Factors.get(i).containsVariable(BayesianNet.GetNode(currentVariable))){
          FactorsToJoin.add(Factors.get(i));
          Factors.remove(Factors.get(i));
          i--;
        }
      }
      Collections.sort(FactorsToJoin);
      if (FactorsToJoin.size()>1) {
        int i = 0;
        for (int j = 1; j < FactorsToJoin.size(); j++) {
          HashMap<Factor,Integer> JoinAns = new HashMap<>();
          JoinAns = Join(FactorsToJoin.get(i),FactorsToJoin.get(j));
          Factor JoinedFactor = new Factor();
          for (Factor factor : JoinAns.keySet()) {
            JoinedFactor = factor;
            multipal += JoinAns.get(factor);
          }
          JoinedFactor.buildFastCPT(JoinedFactor);
          FactorsToJoin.set(i, JoinedFactor);
        }
      }
      HashMap<String[][],Integer> answer = new HashMap<>();
      if(FactorsToJoin.get(0).variables.size()>1){
        answer = Eliminate(FactorsToJoin.get(0), currentVariable);
        Factors.add(FactorsToJoin.get(0));
      }

      for (String[][] mat : answer.keySet()) {
        FactorsToJoin.get(0).FactorCPT = mat;
        plus += answer.get(mat);
      }
      FactorsToJoin.get(0).buildFastCPT(FactorsToJoin.get(0));
      Hidden.remove(0);
    } // End of Hidden Join&Elimination loop
    
    while(Factors.size() != 1) {
      HashMap<Factor,Integer> JoinAns = new HashMap<>();
      JoinAns = Join(Factors.get(0),Factors.get(1));
      for (Factor f1 : JoinAns.keySet()) {
        Factors.add(f1);
        multipal += JoinAns.get(f1);
      }
      Factors.remove(Factors.get(0));
      Factors.remove(Factors.get(0));
    }
    String currentVariable = "";
    plus += Normalize(Factors.get(0));
    Factors.get(0).buildFastCPT(Factors.get(0));
    for (String name : Root.keySet()) {
      currentVariable = name;
    }
    DecimalFormat df = new DecimalFormat("#0.00000");
    String finalans = Factors.get(0).FastCPT.get(Root.get(currentVariable));
    Double FinalAnsToDouble = Double.parseDouble(finalans);
    String writeToFile = df.format(FinalAnsToDouble)+","+ Integer.toString(plus)+","+Integer.toString(multipal);
    try (FileWriter fw = new FileWriter("output.txt", true);
      BufferedWriter bw = new BufferedWriter(fw)) {
      bw.write(writeToFile);
      bw.newLine();
      } catch (IOException e) {
          e.printStackTrace();
      }
    }
}

 /**
  * The FindHiddenFather finds all the hidden variables that need to be in the querry
  *
  * @param Root the variable we asked about
  * @param Given the evidence
  * @return ArrayList of the hidden variables
  */

 public static ArrayList<String> FindHiddenFather(HashMap Root, HashMap Given) {
  Hashtable<String, node> Temp = new Hashtable<>();
  Temp.putAll(BayesianNet.AllNodes);
  ArrayList<String> Hidden = new ArrayList<String>();
  for (Object key : Root.keySet()) {
   if (Temp.containsKey(key)) {
    Temp.remove(key);
   }
  }
  for (Object key : Given.keySet()) {
   if (Temp.containsKey(key)) {
    Temp.remove(key);
   }
  }
  HashMap<String, node> NodeInQuerry = new HashMap<>();
  NodeInQuerry.putAll(Root);
  NodeInQuerry.putAll(Given);
  HashMap<String, node> Parents = new HashMap<>();
  Parents = FindAllParents(NodeInQuerry);

  for (Object key : Temp.keySet()) {
    if(Parents.containsKey(key)){
      Hidden.add(BayesianNet.GetNode(key).name);
    }
  }
  // System.out.println(Temp);
  return Hidden;
 }

 /**
  * Help function to find all the parents of the nodes in the querry in order to find the hidden variables
  *
  * @param NodeInQuerry HashMap of all the nodes in the querry
  * @return all the Parents of them
  */

 public static HashMap<String,node> FindAllParents(HashMap NodeInQuerry){
  HashMap<String, node> Parents = new HashMap<>();
  for (Object Node : NodeInQuerry.keySet()) {
    if (!BayesianNet.AllNodes.get(Node).Parents.isEmpty()) {
      for (Object Parent : BayesianNet.AllNodes.get(Node).Parents) {
        Parents.put(Parent.toString(), BayesianNet.AllNodes.get(Parent));
      }
      Parents.putAll(FindAllParents(Parents));
    }
  }
  return Parents;
 }

 /**
  * The Join Function Join the CPT jables of 2 Factors
  *
  * @param f1 Factor 1
  * @param f2 Factor 2
  * @return HashMap that contain the New Factor and the number of time we use multiply (*)
  */

 public static HashMap<Factor,Integer> Join(Factor f1, Factor f2) {
  int multiply = 0;
  HashMap<Factor, Integer> answer = new HashMap<Factor,Integer>();
  List<node> allnodes = new ArrayList<node>();
  for (int i = 0; i < f1.variables.size(); i++) {
    allnodes.add(f1.variables.get(i));
  }
  for (int i = 0; i < f2.variables.size(); i++) {
    if(!allnodes.contains(f2.variables.get(i))){
      allnodes.add(f2.variables.get(i));
    }
  }

  int NumOfSubQuest = 1;
  int temp = 1;
  for (int i = 0; i < allnodes.size(); i++) {
   NumOfSubQuest *= BayesianNet.AllNodes.get(allnodes.get(i).name).Outcome.size();
  }

  String Mat[][] = new String[NumOfSubQuest+1][allnodes.size()+1];

  for (int i = 0; i < allnodes.size(); i++) {
    ArrayList<String> Outcomes = new ArrayList<String>();
    for (Object string : BayesianNet.AllNodes.get(allnodes.get(i).name).Outcome) {
     Outcomes.add(string.toString());
    }
    Mat[0][i] = allnodes.get(i).name;
    Mat = builtFullOptions(Mat, Outcomes, i, temp, NumOfSubQuest);
    temp *= Outcomes.size();
   }
   HashMap<String[][], Integer> solvedJoin = SolveJoin(Mat,f1,f2,NumOfSubQuest);
   for (String[][] mat : solvedJoin.keySet()) {
    Mat = mat;
    multiply += solvedJoin.get(mat);
   }
   
   answer.put(new Factor(f1.name+"+"+f2.name , allnodes, Mat), multiply);
   return answer;
}

 /**
  * The builtFullOptions Function built all the possible prmotation of the variables outcome
  * @param Mat CPT table
  * @param outcome The Outcome to put in the table
  * @param col the coloum we are working on
  * @param temp the number of time for each outcome
  * @param NumOfSubQuest the number of querrys
  * @return the new table
  */

 public static String[][] builtFullOptions(String Mat[][], ArrayList<String> outcome, int col, int temp,
int NumOfSubQuest) {
// initalize the row number to 1
int row = 1;
// while the row number is smaller then the probabilty options
while (row < NumOfSubQuest+1) {
// for each outcome of the parent
for (String string : outcome) {
 // loop of how mant time to put the specific outcome
 for (int i = 0; i < temp; i++) {
  Mat[row][col] = string;
  row++;
 }
}
}
return Mat;
}

 /**
  * The SolveJoin Function solve the multiply of the two CPT tables of 2 Factors 
  * @param mat the Matrix
  * @param f1 the Factor
  * @param f2 the Factor
  * @param numOfSubQuest the number of querrys
  * @return HashMap of the new Matrix and the number of time we used multiply (*)
  */

 private static HashMap<String[][],Integer> SolveJoin(String[][] mat, Factor f1, Factor f2, int numOfSubQuest) {
  int multiply = 0;
  HashMap<String[][], Integer> answer = new HashMap<String[][],Integer>();
  HashMap<String, Integer> map1 = new HashMap<String,Integer>();
  HashMap<String, Integer> map2 = new HashMap<String,Integer>();
  for (int k = 0; k < f2.variables.size(); k++) {
    for (int index = 0; index < mat[0].length-1; index++) {
      if (mat[0][index] == f2.variables.get(k).name) {
        map2.put(f2.variables.get(k).name, index);
      }
    }
  }
  for (int k = 0; k < f1.variables.size(); k++) {
    for (int index = 0; index < mat[0].length-1; index++) {
      if (mat[0][index] == f1.variables.get(k).name) {
        map1.put(f1.variables.get(k).name, index);
      }
    }
  }

  for (int i = 1; i < mat.length; i++) {
    String querry1 = "";
    String querry2 = "";
    for (int j = 0; j < f1.FactorCPT[0].length-1; j++) {
      querry1 += mat[i][map1.get(f1.FactorCPT[0][j])];
    }

    for (int j2 = 0; j2 < f2.FactorCPT[0].length-1; j2++) {
      querry2 += mat[i][map2.get(f2.FactorCPT[0][j2])];
    }
    multiply++;
    double querryAns = Double.parseDouble(f1.FastCPT.get(querry1)) * Double.parseDouble(f2.FastCPT.get(querry2));
    mat[i][mat[0].length-1] = String.valueOf(querryAns);

  }
  answer.put(mat, multiply);
  return answer;
}

 /**
  * The BuildFactorList build a List of Factors
  * @param Root the variable we asked about
  * @param Given the evidence
  * @param Hidden the hidden variables
  * @return The List of Factors
  */ 

 public static List<Factor> BuildFactorList(HashMap Root, HashMap Given, ArrayList<String> Hidden){
  HashMap<String, String> AllNodelist = new HashMap<String, String>();
  List<Factor> Factors = new ArrayList<Factor>();
  for (Object root : Root.keySet()) {
    AllNodelist.put(root.toString(), Root.get(root).toString());
  }
  for (Object given : Given.keySet()) {
    AllNodelist.put(given.toString(), Given.get(given).toString());
  }
  for (int i = 0; i < Hidden.size(); i++) {
    AllNodelist.put(Hidden.get(i).toString(), Hidden.get(i).toString());
  } 
  
  for (String node : AllNodelist.keySet()) {
    node currentnode = BayesianNet.GetNode(node);
    List<node> nodes = new ArrayList<node>();
    for (int i = 0; i < currentnode.CPT[0].length-1 ; i++) {
      nodes.add(BayesianNet.GetNode(currentnode.CPT[0][i]));
    }
    Factor newFactor = new Factor(currentnode.name , nodes, currentnode.CPT);
    newFactor.buildFastCPT(newFactor);
    Factors.add(newFactor);
  }

  return Factors;
 }

 /**
  * The EliminateGiven Function eliminate from the Factor CPT the evidence variable
  * @param f1 the Factor
  * @param ToEliminate the variable to eliminate (GIVEN)(EVIDENCE)
  * @param string the outcome of the Evidence 
  * @return the Factor f1 after elimination
  */

 public static Factor EliminateGiven(Factor f1, String ToEliminate, String string){
  int col = 0;
  for (int i = 0; i < f1.FactorCPT[0].length-1; i++) {
    if(f1.FactorCPT[0][i].contains(ToEliminate)){
      col = i;
    }
  }
  List<Integer> RowToEliminate = new ArrayList<Integer>();
  for (int i = 1; i < f1.FactorCPT.length; i++) {
    if(!f1.FactorCPT[i][col].contains(string)){
      RowToEliminate.add(i);
    }
  }
  String[][] newCPT = new String[f1.FactorCPT.length-RowToEliminate.size()][f1.FactorCPT[0].length-1];
  int rowNewCPT = 0;
  for (int i = 0; i < f1.FactorCPT.length; i++) {
    if (!RowToEliminate.contains(i)) {
      int colNewCPT = 0;
      for (int j = 0; j < f1.FactorCPT[0].length; j++) {
        if(j != col){
          newCPT[rowNewCPT][colNewCPT] = f1.FactorCPT[i][j];
          colNewCPT++;
        }
      }
      rowNewCPT++;
    }
  }
  List<node> variables = new ArrayList<>();
  for (int i = 0; i < newCPT[0].length-1; i++) {
    variables.add(BayesianNet.GetNode(newCPT[0][i]));
  }
  f1.setVariable(variables);
  f1.setCPT(newCPT);
  f1.buildFastCPT(f1);

  return f1;
 }

 /**
  * The EliminateSmallGiven Function eliminate from the small Factor CPT the evidence variable
  * @param f1 the snall Factor
  * @param ToEliminate the variable to eliminate (GIVEN)(EVIDENCE)
  * @param string the outcome of the Evidence
  * @return the Factor f1 after elimination
  */

 public static Factor EliminateSmallGiven(Factor f1, String ToEliminate, String string){
  int col = 0;
  List<Integer> RowToEliminate = new ArrayList<Integer>();
  for (int i = 1; i < f1.FactorCPT.length; i++) {
    if(!f1.FactorCPT[i][col].contains(string)){
      RowToEliminate.add(i);
    }
  }
  String[][] newCPT = new String[f1.FactorCPT.length-RowToEliminate.size()][f1.FactorCPT[0].length];
  int rowNewCPT = 0;
  for (int i = 0; i < f1.FactorCPT.length; i++) {
    if (!RowToEliminate.contains(i)) {
      int colNewCPT = 0;
      for (int j = 0; j < f1.FactorCPT[0].length; j++) {
          newCPT[rowNewCPT][colNewCPT] = f1.FactorCPT[i][j];
          colNewCPT++;
        
      }
      rowNewCPT++;
    }
  }
  List<node> variables = new ArrayList<>();
  for (int i = 0; i < newCPT[0].length-1; i++) {
    variables.add(BayesianNet.GetNode(newCPT[0][i]));
  }
  f1.setVariable(variables);
  f1.setCPT(newCPT);
  f1.buildFastCPT(f1);

  return f1;
 }

  /**
  * The Eliminate Function eliminate the Hidden from the Factor CPT
  * @param f1 the Factor
  * @param ToEliminate the variable to eliminate (HIDDEN)
  * @param string the outcome of the Evidence
  * @return the Factor f1 after elimination
  */

 public static HashMap<String[][],Integer> Eliminate(Factor f1, String ToEliminate){
  int indexToEliminate = 0;
  int plus = 0;
  for (int i = 0; i < f1.FactorCPT[0].length-1; i++) {
    if(f1.FactorCPT[0][i].contains(ToEliminate.toString())){
      indexToEliminate = i;
    }
  }
  int row = f1.FactorCPT.length;
  int col = f1.FactorCPT[0].length;

  String[][] newArray = new String[row][col-1]; //new Array will have one column less


  for(int i = 0; i < row; i++)
  {
      for(int j = 0,currColumn=0; j < col; j++)
      {
          if(j != indexToEliminate)
          {
              newArray[i][currColumn++] = f1.FactorCPT[i][j];
          }
      }
  }

  HashMap<String, String> AllNewQuerry = new HashMap<String, String>();

  for(int i = 1; i < newArray.length; i++)
  {
    String querry = "";
      for(int j = 0; j < newArray[0].length-1; j++)
      {
        querry += newArray[i][j];
      }
    AllNewQuerry.put(Integer.toString(i), querry);
  }
  

  for (String querry : AllNewQuerry.values()) {
    List<String> keys = getKeysForValue(AllNewQuerry, querry);
    for (int i = 0; i < keys.size(); i++) {
      if(i+1<keys.size()){
        if (Integer.parseInt(keys.get(i)) > Integer.parseInt(keys.get(i+1))){
          String temp = keys.get(i);
          keys.set(i, keys.get(i+1));
          keys.set(i+1, temp);
        }
      }
    }
    int firstKey = Integer.parseInt(keys.get(0));
    double newans = Double.parseDouble(newArray[firstKey][newArray[0].length-1]);
    for (int index = 1; index < keys.size(); index++) {
      plus++;
      newans += Double.parseDouble(newArray[Integer.parseInt(keys.get(index))][newArray[0].length-1]);
      newArray[firstKey][newArray[0].length-1] = Double.toString(newans);
    }
    int numofDeleted = 0;
    for (int index = 1; index < keys.size() ; index++) {
      newArray = deleteRow(newArray, Integer.parseInt(keys.get(index))-numofDeleted);
      numofDeleted++;
    }
    AllNewQuerry = FindAllIndex(newArray);

  }

  List<node> variables = new ArrayList<>();
  for (int i = 0; i < newArray[0].length-1; i++) {
    variables.add(BayesianNet.GetNode(newArray[0][i]));
  }

  f1.setVariable(variables);
  HashMap<String[][],Integer> answer = new HashMap<>();
  answer.put(newArray, plus);
  return answer;
 }

 /**
  * The deleteRow Function Delete row from matrix
  * @param mat matrix
  * @param row the row to delete
  * @return the new matrix
  */

 public static String[][] deleteRow(String[][] mat, int row){
  String[][] NewMat = new String[mat.length-1][mat[0].length];
  int rowNewMat = 0;
  for (int i = 0; i < mat.length; i++) {
    if(i != row){
      for (int j = 0; j < mat[0].length; j++) {
        NewMat[rowNewMat][j] = mat[i][j];
      }
      rowNewMat++;
    }
  }
  return NewMat;
 }

  /**
  * The FindAllIndex Function find all the indexes of querrys and put them in HashMap
  * @param newArray matrix
  * @return HashMap as a map to all the querry in the matrix
  */

 public static HashMap<String, String> FindAllIndex(String[][] newArray){
  HashMap<String, String> AllNewQuerry = new HashMap<String, String>();
  for(int i = 1; i < newArray.length; i++){
    String querry = "";
      for(int j = 0; j < newArray[0].length-1; j++)
      {
        querry += newArray[i][j];
      }
    AllNewQuerry.put(Integer.toString(i), querry);
  }
  return AllNewQuerry;
 }

 /**
  * The getKeysForValue Function finds all the keys with the same value
  * @param map the Map with all the querry
  * @param value the value we are loking for
  * @return List of keys
  */

 public static List<String> getKeysForValue(Map<String, String> map, String value) {
    List<String> keys = new ArrayList<>();
      for (Map.Entry<String, String> entry : map.entrySet()) {
        if (value.equals(entry.getValue())) {
          keys.add(entry.getKey());
        }
      }
    return keys;
  }

 /**
  * The Normalize Function normalize the last factor 
  * @param f1 The last Factor
  * @return Int of how many plus(+) we used
  */
 public static Integer Normalize(Factor f1){
  double sum = 0;
  int plus = 0;
  for (int i = 1; i < f1.FactorCPT.length; i++) {
    plus++;
    sum += Double.parseDouble(f1.FactorCPT[i][f1.FactorCPT[0].length-1]);
  }
  for (int i = 1; i < f1.FactorCPT.length; i++) {
    double newAns = Double.parseDouble(f1.FactorCPT[i][f1.FactorCPT[0].length-1]);
    newAns = newAns/sum;
    f1.FactorCPT[i][f1.FactorCPT[0].length-1] = Double.toString(newAns);
  }
  return plus;
 }

 public static boolean CheckCPT(HashMap Root, HashMap Given){
  String rootName = "";
  for (Object root : Root.keySet()) {
    rootName = root.toString();
  }
  node rootNode = BayesianNet.AllNodes.get(rootName);
  List<String> allVars = new ArrayList<>();
  for (int i = 0; i < rootNode.CPT[0].length-1; i++) {
    allVars.add(rootNode.CPT[0][i]);
  }
  for (Object given : Given.keySet()) {
    if(!allVars.contains(given.toString())){
      return false;
    }
  }
  return true;
 }
}
