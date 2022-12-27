import java.util.HashMap;
import java.util.LinkedList;


/**
 * My node class represents a random variable.
 * Each node stores it`s name, parents, probability, outcome and 2 diffrent type of the same CPT tables.
 * 
 */

public class node<T> {
    public String name;
    public LinkedList<String> Parents = new LinkedList<>();
    public LinkedList<String> Children = new LinkedList<>();
    public LinkedList<Double> Probability = new LinkedList<>();
    public LinkedList<String> Outcome = new LinkedList<>();
    HashMap<String, String> FastCPT = new HashMap<String, String>();
    public String[][] CPT;

    /**
     * Constractor of the node
     * 
     * @param name it`s name
     */

    public node(String name) {
        this.name = name;
        Parents = new LinkedList<>();
        Children = new LinkedList<>();
        Probability = new LinkedList<>();
        Outcome = new LinkedList<>();
    }

    /**
     * Empty constructor
     */
    
    public node() {
        Parents = new LinkedList<>();
        Children = new LinkedList<>();
        Probability = new LinkedList<>();
        Outcome = new LinkedList<>();
    }

    /**
     * To string function that print the node
     * 
     */

    public void ToString() {
        System.out.println(this.name);
        System.out.println(this.Parents);
        System.out.println(this.Children);
        System.out.println(this.Probability);
        System.out.println(this.Outcome);
    }

    public int getNumValues() {
        return Outcome.size();
    }

}