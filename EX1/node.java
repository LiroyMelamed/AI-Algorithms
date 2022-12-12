import java.util.HashMap;
import java.util.LinkedList;

public class node<T> {
    public String name;
    public LinkedList<String> Parents = new LinkedList<>();
    public LinkedList<String> Children = new LinkedList<>();
    public LinkedList<Double> Probability = new LinkedList<>();
    public LinkedList<String> Outcome = new LinkedList<>();
    HashMap<String, String> FastCPT = new HashMap<String, String>();
    public String[][] CPT;

    public node(String name) {
        this.name = name;
        Parents = new LinkedList<>();
        Children = new LinkedList<>();
        Probability = new LinkedList<>();
        Outcome = new LinkedList<>();
    }

    public node() {
        Parents = new LinkedList<>();
        Children = new LinkedList<>();
        Probability = new LinkedList<>();
        Outcome = new LinkedList<>();
    }

    public void ToString() {
        System.out.println(this.name);
        System.out.println(this.Parents);
        System.out.println(this.Children);
        System.out.println(this.Probability);
        System.out.println(this.Outcome);
    }

}