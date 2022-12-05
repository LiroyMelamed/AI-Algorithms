import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Functions {
    public static String[] FileToList(String File) throws IOException {
        FileReader fr = new FileReader(File);
        BufferedReader br = new BufferedReader(fr);
        String str = "", l = "";
        while ((l = br.readLine()) != null) {
            str += " " + l;
        }
        br.close();
        String[] array = str.trim().split(" ");
        return array;
    }

    public static void BuildTree(BayesianNet myNet) {
        for (Object NodeName : myNet.AllNodes.keySet()) {
            myNet.BuildCPT(myNet.GetNode(NodeName));
        }
    }

    public static String[] QuestSplitFirst(String File) throws IOException {
        String[] array = File.trim().split("P\\(");
        return array;
    }

    public static String[] QuestSplitSecond(String File) throws IOException {
        String[] array = File.trim().split("\\|");
        return array;
    }

    public static String[] QuestSplitThird(String File) throws IOException {
        String[] array = File.trim().split("\\)");
        return array;
    }

    public static String[] QuestSplitFourth(String File) throws IOException {
        String[] array = File.trim().split(",");
        return array;
    }

    public static String[] QuestSplitProbability(String File) throws IOException {
        String[] array = File.trim().split(" ");
        return array;
    }

    public static void main(String[] args) throws IOException, XMLStreamException {
        String[] Instruction = FileToList("input.txt");
        String Tree = Instruction[0];
        // for (int i = 1; i < Instruction.length; i++) {
        // String[] Splitted = QuestSplitFirst(Instruction[i]);
        // String[] Splittedtwo = QuestSplitSecond(Splitted[1]);
        // String[] Splittedthird = QuestSplitThird(Splittedtwo[1]);
        // String[] SplittedForth = QuestSplitFourth(Splittedthird[1]);
        // System.out.println(Splittedtwo[0]);
        // System.out.println(Splittedthird[0]);
        // System.out.println(SplittedForth[1]);
        // }
        File file = new File(Tree);
        parser(file);

    }

    public static void parser(File file) throws XMLStreamException, IOException {
        node Curr = new node<>();
        BayesianNet MyNet = new BayesianNet();
        boolean variable;
        boolean name;
        boolean outcome;
        boolean definition;
        boolean forQ;
        boolean given;
        boolean table;
        // Variables to make sure whether a element
        // in the xml is being accessed or not
        // if false that means elements is
        // not been used currently , if true the element or the
        // tag is being used currently
        name = variable = definition = outcome = forQ = given = table = false;

        // Instance of the class which helps on reading tags
        XMLInputFactory factory = XMLInputFactory.newInstance();

        // Initializing the handler to access the tags in the XML file
        XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(file));

        // Checking the availability of the next tag
        while (eventReader.hasNext()) {
            // Event is actually the tag . It is of 3 types
            // <name> = StartEvent
            // </name> = EndEvent
            // data between the StartEvent and the EndEvent
            // which is Characters Event
            XMLEvent event = eventReader.nextEvent();

            // This will trigger when the tag is of type <...>
            if (event.isStartElement()) {
                StartElement element = (StartElement) event;

                // Iterator for accessing the metadeta related
                // the tag started.
                // Here, it would name of the company

                // Checking which tag needs to be opened for reading.
                // If the tag matches then the boolean of that tag
                // is set to be true.
                if (element.getName().toString().equalsIgnoreCase("variable")) {
                    variable = true;
                }
                if (element.getName().toString().equalsIgnoreCase("name")) {
                    name = true;
                }
                if (element.getName().toString().equalsIgnoreCase("outcome")) {
                    outcome = true;
                }
                if (element.getName().toString().equalsIgnoreCase("definition")) {
                    definition = true;
                }
                if (element.getName().toString().equalsIgnoreCase("for")) {
                    forQ = true;
                }
                if (element.getName().toString().equalsIgnoreCase("given")) {
                    given = true;
                }
                if (element.getName().toString().equalsIgnoreCase("table")) {
                    table = true;
                }
            }

            // This will be triggered when the tag is of type </...>
            if (event.isEndElement()) {
                EndElement element = (EndElement) event;
                if (element.getName().toString().equalsIgnoreCase("variable")) {
                    variable = false;
                    Curr = new node<>();
                }
                if (element.getName().toString().equalsIgnoreCase("name")) {
                    name = false;
                }
                if (element.getName().toString().equalsIgnoreCase("outcome")) {
                    outcome = false;
                }
                if (element.getName().toString().equalsIgnoreCase("definition")) {
                    Curr.CPT = new String[Curr.Probability.size() + 1][Curr.Parents.size() + 2];
                    definition = false;
                }
                if (element.getName().toString().equalsIgnoreCase("for")) {
                    forQ = false;
                }
                if (element.getName().toString().equalsIgnoreCase("given")) {
                    given = false;
                }
                if (element.getName().toString().equalsIgnoreCase("table")) {
                    table = false;
                }
            }

            // Triggered when there is data after the tag which is
            // currently opened.
            if (event.isCharacters()) {
                // Depending upon the tag opened the data is retrieved .
                Characters element = (Characters) event;
                if (variable) {
                }
                if (name) {
                    Curr.name = element.getData();
                    MyNet.AllNodes.put(element.getData(), Curr);
                }
                if (outcome) {
                    Curr.Outcome.add(element.getData());
                }
                if (definition) {
                }
                if (forQ) {
                    Curr = MyNet.GetNode(element.getData());
                }
                if (given) {
                    Curr.Parents.add(element.getData());
                    node Parent = MyNet.GetNode(element.getData());
                    if (!Parent.Children.contains(Curr.name)) {
                        Parent.Children.add(Curr.name);
                    }
                }
                if (table) {
                    String[] SplitedProb = QuestSplitProbability(element.getData());
                    for (int index = 0; index < SplitedProb.length; index++) {
                        Curr.Probability.add(SplitedProb[index]);
                    }
                }
            }
        }
        BuildTree(MyNet);
    }
}
