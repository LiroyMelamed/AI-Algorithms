import java.util.LinkedList;

public class node<T> {

    public String name;
    public LinkedList<String> Parents = new LinkedList<>();
    public LinkedList<String> Children = new LinkedList<>();
    public LinkedList<Double> Probability = new LinkedList<>();
    public LinkedList<String> Outcome = new LinkedList<>();

    public node(String name){
        this.name = name;
        Parents = new LinkedList<>();
        Children = new LinkedList<>();
        Probability = new LinkedList<>();
        Outcome = new LinkedList<>();
    }

    


}