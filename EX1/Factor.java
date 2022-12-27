import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * My Factor class is built for Algorithm number 2 (VE).
 * Every Factor has a name, list of variavles(nodes).
 * The Factor CPT table and a Fast access CPT table. 
 */

public class Factor implements Comparable<Factor>{
    public String name;              // List of variables in the factor
    public List<node> variables;     // List Of nodes in CPT table / Factor
    public String[][] FactorCPT;     // Factor values
    HashMap<String, String> FastCPT = new HashMap<String, String>();

    /**
     * Empty Constructor
     */
    public Factor(){
      this.variables = new ArrayList<node>();
      this.FactorCPT = new String[0][0];
      this.name = "";
      this.FastCPT = new HashMap<>();      
    }


    /**
     * Constructor of a new Factor 
     * 
     * @param name the new name of the factor
     * @param vars the list of nodes the Factor has
     * @param FactorCPT the CPT table of the Factor
     */

    public Factor(String name, List<node> vars, String[][] FactorCPT) {
      this.variables = new ArrayList<node>();
      for (node var : vars) {
        this.variables.add(var);
      }
      this.FactorCPT = FactorCPT;
      this.name = name;
      this.FastCPT = new HashMap<>();
    }
  
    /**
     * My containsVariable function checks if the factor contains the given node.
     * 
     * @param var node to check if the Factor contains it
     * @return True Or False if the Factor contains it or not
     */

    public boolean containsVariable(node var) {
      return variables.contains(var);
    }


    /**
     * My buildFastCPT build the original CPT as an HashMap for fast access
     * 
     * @param NCPT the Factor to build the fast CPT for
     */

    public void buildFastCPT(Factor NCPT) {
      NCPT.FastCPT.clear();
      for (int i = 1; i < NCPT.FactorCPT.length; i++) {
       String querry = "";
       for (int j = 0; j < NCPT.FactorCPT[0].length; j++) {
        if (j != NCPT.FactorCPT[0].length - 1) {
         querry = querry + NCPT.FactorCPT[i][j];
        }
        if (j == NCPT.FactorCPT[0].length - 1) {
         NCPT.FastCPT.put(querry, NCPT.FactorCPT[i][j]);
        }
       }
      }
    }

    /**
     * SETTER of the CPT table
     * @param newCPT the new CPT to set.
     */

    public void setCPT(String[][] newCPT){
      this.FactorCPT = newCPT;
    }

    /**
     * SETTER of the variables
     * 
     * @param variables the new list of variables
     */
    public void setVariable(List<node> variables){
      this.variables = variables;
    }


    @Override
    public int compareTo(Factor o) {
      if(this.FactorCPT.length == o.FactorCPT.length)  
        return 0;  
      else if(this.FactorCPT.length > o.FactorCPT.length)  
        return 1;  
      else
        return -1;  
    }

  }
  