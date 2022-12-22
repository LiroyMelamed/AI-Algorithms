import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class Algorithms {
 public static void SimpleDeduction(String[] querry) {
  HashMap<String, String> Given = new HashMap<String, String>();
  HashMap<String, String> Root = new HashMap<String, String>();
  ArrayList<String> Hidden = new ArrayList<String>();
  String[] Quests;
  double Numerator = 0;
  double Denominator = 0;
  String[] RootSplit = querry[0].trim().split("=");
  Root.put(RootSplit[0], RootSplit[1]);
  String[] array = querry[1].trim().split(",");
  for (String string : array) {
   String[] Splited = string.trim().split("=");
   Given.put(Splited[0], Splited[1]);
  }
  // System.out.println(Root);
  // System.out.println(Given);
  Hidden = FindHidden(Root, Given);
  Quests = MakeQuests(Root, Given, Hidden);
  for (int i = 0; i < Quests.length; i++) {
   Numerator += SolveQuerry(Quests[i]);
  }
  Hidden = FindHidden(Root, Given);
  Quests = MakeQuestsDenominator(Root, Given, Hidden);
  for (int i = 0; i < Quests.length; i++) {
   Denominator += SolveQuerry(Quests[i]);
  }
  System.out.println("Numerator = " + Numerator);
  System.out.println("Denominator = " + Denominator);
  double FinalSoulotion = Numerator / (Numerator + Denominator);
  System.out.println(FinalSoulotion);

 }

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

 public static String[] MakeQuests(HashMap Root, HashMap Given, ArrayList Hidden) {
  int NumOfSubQuest = 1;
  int temp = 1;
  for (int i = 0; i < Hidden.size(); i++) {
   // for (Object string : BayesianNet.AllNodes.get(Hidden.get(i)).Outcome) {
   // Outcomes.add(string.toString());
   // }
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
  // print the mat
  // for (int i = 0; i < Mat.length; i++) {
  // for (int j = 0; j < Mat[0].length; j++) {
  // System.out.print(Mat[i][j] + " ");
  // }
  // System.out.println();
  // }
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
   // for (Object string : BayesianNet.AllNodes.get(Hidden.get(i)).Outcome) {
   // Outcomes.add(string.toString());
   // }
   Quests[i] = quest;
  }
  return Quests;
 }

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
   temp *= Outcomes.size();
   Mat = builtOptions(Mat, Outcomes, Hidden.size(), temp, NumOfSubQuest);
  }

  // print the mat
  // for (int i = 0; i < Mat.length; i++) {
  // for (int j = 0; j < Mat[0].length; j++) {
  // System.out.print(Mat[i][j] + " ");
  // }
  // System.out.println();
  // }
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
   // for (Object string : BayesianNet.AllNodes.get(Hidden.get(i)).Outcome) {
   // Outcomes.add(string.toString());
   // }
   Quests[i] = quest;
  }
  return Quests;
 }

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

 public static double SolveQuerry(String querry) {
  double Prob = 1;
  HashMap<String, String> Variable = new HashMap<String, String>();
  String[] Splitted = querry.trim().split(",");
  for (int i = 0; i < Splitted.length; i++) {
   String[] SplitEqual = Splitted[i].trim().split("=");
   Variable.put(SplitEqual[0], SplitEqual[1]);
  }
  for (String string : Variable.keySet()) {
   if (BayesianNet.GetNode(string).Parents.isEmpty()) {
    Prob *= Double.parseDouble(BayesianNet.GetNode(string).FastCPT.get(Variable.get(string)).toString());
    // System.out.println("Current node with no parent " + string + " the quest is "
    // + Variable.get(string) + " prob = "
    // +
    // Double.parseDouble(BayesianNet.GetNode(string).FastCPT.get(Variable.get(string)).toString()));
   } else {
    String QuestWithParents = "";
    for (Object parent : BayesianNet.GetNode(string).Parents) {
     QuestWithParents += Variable.get(parent.toString()).toString();
    }
    QuestWithParents += Variable.get(string);
    // System.out.println("Current node " + string + " the quest is " +
    // QuestWithParents + " his prob is " + Double
    // .parseDouble(BayesianNet.GetNode(string).FastCPT.get(QuestWithParents).toString()));
    Prob *= Double.parseDouble(BayesianNet.GetNode(string).FastCPT.get(QuestWithParents).toString());
   }
  }
  return Prob;
 }
}
