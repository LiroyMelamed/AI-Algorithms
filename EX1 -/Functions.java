import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Functions {

    public static String[] FileToList(String File) throws IOException{
        FileReader fr = new FileReader(File);
        BufferedReader br = new BufferedReader(fr);
        String str="", l="";
        while((l=br.readLine())!=null) {
            str += " " + l;
        }
        br.close();
        String[] array = str.trim().split(" ");
        return array;
    }

    public static void BuildTree(String File){
    }

    public static String[] QuestSplitFirst(String File) throws IOException{
        String[] array = File.trim().split("P\\(");
        return array;
    }

    public static String[] QuestSplitSecond(String File) throws IOException{
        String[] array = File.trim().split("\\|");
        return array;
    }

    public static String[] QuestSplitThird(String File) throws IOException{
        String[] array = File.trim().split("\\)");
        return array;
    }

    public static String[] QuestSplitFourth(String File) throws IOException{
        String[] array = File.trim().split(",");
        return array;
    }

    public static void main(String[] args) throws IOException{
        String[] Instruction = FileToList("input.txt");
        String Tree = Instruction[0];
        BuildTree(Tree);
        for (int i = 1; i < Instruction.length; i++) {
            String[] Splitted = QuestSplitFirst(Instruction[i]);
            String[] Splittedtwo = QuestSplitSecond(Splitted[1]);
            String[] Splittedthird = QuestSplitThird(Splittedtwo[1]);
            String[] SplittedForth = QuestSplitFourth(Splittedthird[1]);
            System.out.println(Splittedtwo[0]);
            System.out.println(Splittedthird[0]);
            System.out.println(SplittedForth[1]);
        }
    }

    public static void parser(File file) throws FileNotFoundException,
                                                XMLStreamException
    {
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
        XMLEventReader eventReader =
                factory.createXMLEventReader(new FileReader(file));
  
        // Checking the availability of the next tag
        while (eventReader.hasNext())
        {
            // Event is actually the tag . It is of 3 types
            // <name> = StartEvent
            // </name> = EndEvent
            // data between the StartEvent and the EndEvent
            // which is Characters Event
            XMLEvent event = eventReader.nextEvent();
  
            // This will trigger when the tag is of type <...>
            if (event.isStartElement())
            {
                StartElement element = (StartElement)event;
  
                // Iterator for accessing the metadeta related
                // the tag started.
                // Here, it would name of the company
                Iterator<Attribute> iterator = element.getAttributes();
                while (iterator.hasNext())
                {
                    Attribute attribute = iterator.next();
                    QName qname = attribute.getName();
                    QName qvalue = attribute.getName();
                    System.out.println(qname+" = " + qvalue);
                }
  
                // Checking which tag needs to be opened for reading.
                // If the tag matches then the boolean of that tag
                // is set to be true.
                if (element.getName().toString().equalsIgnoreCase("variable"))
                {
                    variable = true;
                }
                if (element.getName().toString().equalsIgnoreCase("name"))
                {
                    name = true;
                }
                if (element.getName().toString().equalsIgnoreCase("outcome"))
                {
                    outcome = true;
                }
                if (element.getName().toString().equalsIgnoreCase("definition"))
                {
                    definition = true;
                }
                if (element.getName().toString().equalsIgnoreCase("for"))
                {
                    forQ = true;
                }
                if (element.getName().toString().equalsIgnoreCase("given"))
                {
                    given = true;
                }
                if (element.getName().toString().equalsIgnoreCase("table"))
                {
                    table = true;
                }
            }
  
            // This will be triggered when the tag is of type </...>
            if (event.isEndElement())
            {
                EndElement element = (EndElement) event;
                if (element.getName().toString().equalsIgnoreCase("variable"))
                {
                    variable = false;
                }
                if (element.getName().toString().equalsIgnoreCase("name"))
                {
                    name = false;
                }
                if (element.getName().toString().equalsIgnoreCase("outcome"))
                {
                    outcome = false;
                }
                if (element.getName().toString().equalsIgnoreCase("definition"))
                {
                    definition = false;
                }
                if (element.getName().toString().equalsIgnoreCase("for"))
                {
                    forQ = false;
                }
                if (element.getName().toString().equalsIgnoreCase("given"))
                {
                    given = false;
                }
                if (element.getName().toString().equalsIgnoreCase("table"))
                {
                    table = false;
                }
            }
  
            // Triggered when there is data after the tag which is
            // currently opened.
            if (event.isCharacters())
            {
                // Depending upon the tag opened the data is retrieved .
                Characters element = (Characters) event;
                if (variable)
                {
                    System.out.println(element.getData());
                }
                if (name)
                {
                    System.out.println(element.getData());
                }
                if (outcome)
                {
                    System.out.println(element.getData());
                }
                if (definition)
                {
                    System.out.println(element.getData());
                }
                if (forQ)
                {
                    System.out.println(element.getData());
                }
                if (given)
                {
                    System.out.println(element.getData());
                }
                if (table)
                {
                    System.out.println(element.getData());
                }
            }
        }
    }
}

