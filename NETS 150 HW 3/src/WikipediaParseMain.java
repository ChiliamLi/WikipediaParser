import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikipediaParseMain {
    
    public static void main (String[] args) {
        WikipediaParser bparse = new WikipediaParser(); 
        System.out.println("Question1"); 
        System.out.println();
        bparse.Question1();
        System.out.println();
        System.out.println("Question2"); 
        System.out.println();
        bparse.Question2(3);
        System.out.println();
        System.out.println("Question3"); 
        System.out.println();
        bparse.Question3("mammals");
        System.out.println();
        System.out.println("Question4"); 
        System.out.println();
        bparse.Question4("White Rhinoceros", "Class");
        System.out.println();
        System.out.println("Question5"); 
        System.out.println();
        bparse.Question5("Endangered birds", "Parrots");
        System.out.println();
        System.out.println("Question6"); 
        System.out.println();
        bparse.Question6("Australia");
        System.out.println();
        System.out.println("Question7"); 
        System.out.println();
        bparse.Question7("Endangered Amphibians", "Lungless salamanders", "United States"); 
        System.out.println();
        System.out.println("Question8"); 
        System.out.println();
        bparse.Question8("Amphibians");
        System.out.println();
        System.out.println("Question9"); 
        System.out.println();
        //Takes a little bit of time
        bparse.EC1(); 
//        bparse.EC2("Reptiles", "Critically_endangered"); 
    }
}
