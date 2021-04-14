import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class WikipediaParser {
    private String baseURL; 
    private Document currentDoc;
    Map<String, String> articleMap;   
    /*
     * Constructor that initializes the base URL and loads 
     * the document produced from that URL
     * 
     */
    public WikipediaParser() {
        this.baseURL = "https://en.wikipedia.org/wiki/Endangered_species";
        try {
            this.currentDoc = Jsoup.connect(this.baseURL).get();
            //System.out.println(this.currentDoc);
        } catch (IOException e) {
            //System.out.println("Could not get the corgis :(");
        }
    }
    
    /*
     * Constructor gets document from URL
     * Append wiki to wikipedia.org
     * IF TIME NULL FILE, but also can assume everything is valid 
     */
    public Document getDocument (String url) {
        url = "https://en.wikipedia.org"+url; 
        Document a = null;
        try {
            a = Jsoup.connect(url).get();
            //System.out.println(this.currentDoc);
        } catch (IOException e) {
            //System.out.println("Could not get the corgis :(");
        }
        return a; 
    }
    
    /*
     * Question 1: List all current conservation statuses that are used to categorize different species according to IUCN.
     * Assumption that all the things are in ()  --> 
     * 
     */
    public void Question1 () {
        Element link = currentDoc.selectFirst("a[href=/wiki/Conservation_status]" ); 
        Document a = getDocument(link.attr("href"));
        Element UL = a.selectFirst("div[id=toc] ~ ul"); 
        Elements possibilities = UL.select("li"); 
        for (Element element: possibilities) {
            if (element.text().contains("(") &&  element.text().contains(")")) {
                String text = element.text(); 
                text = text.substring(0, text.indexOf(")")+1); 
                System.out.println(text);
            } 
        }
    }
    /* Question 2: What is the __third__ criteria for determining Critically Endangered species? (NOTE that __input__ implies
     * changing criteria
    * Assumption that the index is within bounds
    * 
    */
    public void Question2(int index) {
        Element link = currentDoc.selectFirst("a[href=/wiki/Critically_endangered]"); 
        Document a = getDocument(link.attr("href"));
        Elements DD = a.select("dd");
        ArrayList<String> strings = new ArrayList<String>(); 
        for (Element dd : DD) {
            String original = dd.text(); 
            String StringLong = dd.selectFirst("ol, li").text(); 
            String b = dd.select("b").text();
            original = original.replace(StringLong, "");
//          Makes sure it only takes out the first letter and nothing later
            original = original.replaceFirst(b, "");
            original = original.replace(": " ,"");
            strings.add(original); 
        }
        System.out.println(strings.get(index-1));
    }
    
    /* Question 3: How many __mammals__ are extinct or extinct in the wild?
    * Assumption that the string is a valid species
    */
    public void Question3(String classification) {
        String targetlink = "a[href=/wiki/List_of_endangered_" + classification + "]"; 
        Element link = currentDoc.selectFirst(targetlink); 
        Document a = getDocument(link.attr("href"));
        Element caption = a.selectFirst("div[class=thumbcaption]"); 
        Elements possibilities = caption.select("li"); 
        Element text = possibilities.get(4); 
        Element delete = text.selectFirst("ul"); 
        String answer = text.text(); 
        answer = answer.replace(delete.text(), ""); 
        answer = answer.replace(":", ""); 
        System.out.println(answer); 
    }
    /* Question 4: What is the IUCN conservation status of the __White Rhinoceros__? What is its __Class__ according to the Scientific Classification?
    * Assumption that the string is a valid species listed on wikipedia
    * Assumption that the species is in the correct formatting of plurality
    * 
    */
    public void Question4(String species, String category) {
        if (species.contains(" ")) {
            species = species.replace(" ", "_"); 
        }
        String targetlink = "a[href=/wiki/" + species + "]"; 
        Element link = currentDoc.selectFirst(targetlink); 
        if (link == null) {
            System.out.println("no link"); 
        }
        else {
        Document a = getDocument(link.attr("href"));
            Element table = a.selectFirst("table[class = infobox biota]"); 
            Element br = table.selectFirst("br + a"); 
            String answer = br.text(); 
            answer = answer.replace(" (IUCN 3.1)", ""); 
            answer = answer.replace("[1]", "");
            answer = answer.replace("[2]", ""); 
            System.out.println(answer); 
            
            //Find the Class
            Elements TD = table.select("td");
    //      target index of our category
            int index = 0; 
            for (Element td : TD) {
                if (td.text().contains(category)) {
                    index = TD.indexOf(td); 
                }
            } 
            String answer2 = TD.get(index+1).text(); 
            System.out.println(answer2); 
        }
    }
    
    /* Question 5: Some of the __Endangered Birds__ are __Parrots__. List all species of __Parrots__ that are endangered.
    * Assumption that the string is a valid species listed on wikipedia
    * Assumption that the species is in the correct formatting 
    * (lower case for the classification, and upper case for species)
    */
    public void Question5(String classification, String species) {
        if (classification.contains("Endangered")) {
            classification = classification.replace("Endangered ", ""); 
        }
        if (classification.equals("molluscs") || classification.equals("fishes") 
                || classification.equals("invertebrates")) {
            String targetlink = "a[href=/wiki/List_of_endangered_" + classification + "]"; 
            Element link = currentDoc.selectFirst(targetlink); 
            Document a = getDocument(link.attr("href"));
            Elements H3 = a.select("h3");
            Elements h3_li = a.select("h3, li"); 
            int startindex = 0; 
            int endindex = 0; 
            for (Element h3 : h3_li) {
                if (h3.text().contains(species)) {
                    startindex = h3_li.indexOf(h3); 
                }
            } 
            int nextindex = H3.indexOf(h3_li.get(startindex))+1; 
            String next = H3.get(nextindex).text(); 
            for (Element h3 : h3_li) {
                if (h3.text().contains(next)) {
                    endindex = h3_li.indexOf(h3); 
                }
            } 
            for (int i = startindex+1; i < endindex; i++) {
                System.out.println(h3_li.get(i).text()); 
            } 
        }
        else {
        String targetlink = "a[href=/wiki/List_of_endangered_" + classification + "]"; 
        Element link = currentDoc.selectFirst(targetlink); 
        Document a = getDocument(link.attr("href"));
        Elements H2 = a.select("h2");
        Elements h2_li = a.select("h2, li"); 
        int startindex = 0; 
        int endindex = 0; 
        for (Element h2 : h2_li) {
            if (h2.text().contains(species)) {
                startindex = h2_li.indexOf(h2); 
            }
        } 
        int nextindex = H2.indexOf(h2_li.get(startindex))+1; 
        String next = H2.get(nextindex).text(); 
        for (Element h2 : h2_li) {
            if (h2.text().contains(next)) {
                endindex = h2_li.indexOf(h2); 
            }
        } 
        for (int i = startindex+1; i < endindex; i++) {
            System.out.println(h2_li.get(i).text()); 
        } 
        }
    }

    /* Question 6: List the common names of the Recently Extinct Mammals that went extinct in __Australia__.
    * Assumption that the string is a valid country in our wiki page
    * Assumption that we will ignore the images as countries, and assume that
    * the recent endangered mammals appears in a row the the country shown in text
    * Assume no duplicate years (there aren't multiple answerS)
    * Does not include possibly extinct or extinct in the wild
    * Early and Late prefixes will be truncated (so will s's
    */
    public void Question6(String country) {
        String targetlink = "a[href=/wiki/List_of_endangered_" + "mammals" + "]"; 
        Element link = currentDoc.selectFirst(targetlink); 
        Document a = getDocument(link.attr("href"));
        Element link2 = a.selectFirst("a[href=/wiki/List_of_recently_extinct_mammals]");
        Document b = getDocument(link2.attr("href"));
        
        //Inside link
        
        Element tbody = b.selectFirst("tbody"); 
        Elements TR = tbody.select("tr"); 
        ArrayList<Integer> possibleIndex = new ArrayList<Integer>(); 
        for (Element tr : TR) {
            Elements TD = tr.select("td");
            for (Element td : TD) {
                if (td.text().contains(country)) {
                    possibleIndex.add(TR.indexOf(tr)); 
                }
            }
        }
        int recent_year = 0; 
        Elements TD1 =TR.get(12).select("td"); 
        String commonName = ""; 
        for (int i: possibleIndex) {
           Elements TD =TR.get(i).select("td"); 
           String text_int = TD.get(3).text(); 
           text_int = text_int.replace("s", ""); 
           text_int = text_int.replace(" ", ""); 
           text_int = text_int.replace("IUCN", ""); 
           text_int = text_int.replace("early", ""); 
           text_int = text_int.replace("late", ""); 
           text_int = text_int.substring(0,4);
           int year = Integer.parseInt(text_int); 
           String possibleName = TD.get(0).text(); 
           if (year > recent_year) {
               recent_year = year; 
               commonName = possibleName;
           }
        }
        System.out.println(commonName); 
    }
    
    /* Question 7: Some of the __Endangered Amphibians__ are __Lungless Salamanders__. List the ones that are found in the __United States__.
     *     // Assumptions: lower case second word for species
     */
    public void Question7(String classification, String species, String country) {
        if (classification.contains("Endangered")) {
            classification = classification.replace("Endangered ", ""); 
        }
            String targetlink = "a[href=/wiki/List_of_endangered_" + classification + "]"; 
            Element link = currentDoc.selectFirst(targetlink); 
            Document a = getDocument(link.attr("href"));
            Elements H3 = a.select("h3");
            Elements h3_li = a.select("h3, li");
            int startindex = 0; 
            int endindex = 0; 
            for (Element h3 : h3_li) {
                if (h3.text().contains(species)) {
                    startindex = h3_li.indexOf(h3); 
                }
            } 
            int nextindex = H3.indexOf(h3_li.get(startindex))+1; 
            String next = H3.get(nextindex).text(); 
            for (Element h3 : h3_li) {
                if (h3.text().contains(next)) {
                    endindex = h3_li.indexOf(h3); 
                }
            } 
            for (int i = startindex+1; i < endindex; i++) {
                String text = h3_li.get(i).text();
                if (text.contains("(")) {
                    int start_of_name = text.indexOf("("); 
                    text = text.substring(start_of_name,text.length()-1); 
                    text = text.replace("(", ""); 
                    text = text.replace(")", "");
                    text = text.replace(" ", "_"); 
                }
                text = text.replace(" ", "_"); 
                String newTargetLink = "a[href=/wiki/" + text+"]";
                Element link2 = a.selectFirst(newTargetLink);
                Document c = getDocument(link2.attr("href"));
                Elements paragraphs = c.select("p"); 
                for (Element p: paragraphs) {
                    if (p.text().contains(country)) {
                        System.out.println(text); 
                    }

                }
            } 
        }
    // Question 8: What percentage of *amphibians* studied are endangered
    //Assumptions: Similar to the previous questions

    public void Question8(String classification) {
        String targetlink = "a[href=/wiki/List_of_endangered_" + classification + "]"; 
        Element link = currentDoc.selectFirst(targetlink); 
        Document a = getDocument(link.attr("href"));
        Element thumbImage = a.selectFirst("div[class=thumbimage noresize]"); 
        Elements possibilities = thumbImage.select("li"); 
        Element endangeredNumber = possibilities.get(2); 
        String answer = endangeredNumber.text(); 
//        answer = answer.replace(delete.text(), ""); 
        answer = answer.replace("Endangered (EN): ", ""); 
        answer = answer.replace(" species", "");
        int numEndangeredSpecies = Integer.parseInt(answer); 
        Element caption = a.selectFirst("div[class=thumbcaption]"); 
        Elements possibilities1 = caption.select("li"); 
        Element text = possibilities1.get(0); 
        String answer1 = text.text(); 
        answer1 = answer1.replace("extant species have been evaluated", ""); 
        answer1 = answer1.replace(" ", ""); 
        double numSpecies = Integer.parseInt(answer1); 
        double percentage = ((numEndangeredSpecies*1.0)/numSpecies)*100;
        System.out.println(percentage+ "% of " + classification + " studied are endangered"); 

    }
    //Extra Credit 1: Find the country or continent with the most distinct Stingray species?
    //Assumption: The countries listed below are the main countries to be looking for, and it will either be
    // in one of these countries (found through sampling) or a large ocean body.
    public void EC1() {
        HashMap<String, Integer> map = new HashMap<>();
        
        // Add elements to the map
        map.put("United States", helper("Rays and skates", "United States"));
        map.put("Australia", helper("Rays and skates", "Australia"));
        map.put("Angola", helper("Rays and skates", "Angola"));
        map.put("Namibia", helper("Rays and skates", "Namibia"));
        map.put("Africa", helper("Rays and skates", "Africa"));
        map.put("Mozambique", helper("Rays and skates", "Mozambique"));
        map.put("Atlantic Ocean", helper("Rays and skates", "Atlantic Ocean"));
        map.put("Pacific Ocean", helper("Rays and skates", "Pacific Ocean"));
        map.put("Atlantic Ocean", helper("Rays and skates", "Atlantic Ocean"));
        map.put("Hawaiian Islands", helper("Rays and skates", "Hawaiian Islands"));
        map.put("Indonesia", helper("Rays and skates", "Indonesia"));
        map.put("Papua New Guinea", helper("Rays and skates", "Papua New Guinea"));
        
        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : map.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
        System.out.println(maxEntry.getKey() + " has " + maxEntry.getValue() + " distinct fishes"); 
    }
    //returns the number of sting ray in a specific country
    public int helper(String species, String country) {
        int answer = 0; 
            String targetlink = "a[href=/wiki/List_of_endangered_fishes]"; 
            Element link = currentDoc.selectFirst(targetlink); 
            Document a = getDocument(link.attr("href"));
            Element link2 = a.selectFirst("a[href=/wiki/List_of_least_concern_fishes]"); 
            Document b = getDocument(link2.attr("href"));
            Elements H3 = b.select("h3");
            Elements h3_li = b.select("h3, li");
            int startindex = 0; 
            int endindex = 0; 
            for (Element h3 : h3_li) {
                if (h3.text().contains(species)) {
                    startindex = h3_li.indexOf(h3); 
                }
            } 
            int nextindex = H3.indexOf(h3_li.get(startindex))+1; 
            String next = H3.get(nextindex).text(); 
            for (Element h3 : h3_li) {
                if (h3.text().contains(next)) {
                    endindex = h3_li.indexOf(h3); 
                }
            } 
            for (int i = startindex+1; i < endindex; i++) {
                String text = h3_li.get(i).text();
                if (text.contains("(")) {
                    int start_of_name = text.indexOf("("); 
                    text = text.substring(start_of_name,text.length()-1); 
                    text = text.replace("(", ""); 
                    text = text.replace(")", "");
                    text = text.replace(" ", "_"); 
                }
                text = text.replace(" ", "_"); 
                    String newTargetLink = "a[href=/wiki/" + text+"]";
                    Element link3 = b.selectFirst(newTargetLink);
                if (link3 != null) {
                    Document c = getDocument(link3.attr("href"));
                    Elements paragraphs = c.select("p"); 
                    for (Element p: paragraphs) {
                        if (p.text().contains(country)) {
                            answer++;  
                        }
                    
                    }
                } 
            }
                return answer; 
        }
//    public void EC2(String classification, String status) {
//        status.replace(" ", "_"); 
//        System.out.println(status); 
//        String targetlink = "a[href=/wiki/List_of_endangered_" + classification + "]"; 
//        Element link = currentDoc.selectFirst(targetlink); 
//        Document a = getDocument(link.attr("href"));
//        Element link2 = a.selectFirst("a[href=/wiki/List_of_"+ status + "_" + classification + "]"); 
//        System.out.println("a[href=/wiki/List_of_"+ status + "_" + classification + "]"); 
//        Document b = getDocument(link2.attr("href"));
//        Elements li = b.select("li");
//            for (Element element: li) {
//                String text = element.text();
//                if (text.contains("(")) {
//                    int start_of_name = text.indexOf("("); 
//                    text = text.substring(start_of_name,text.length()-1); 
//                    text = text.replace("(", ""); 
//                    text = text.replace(")", "");
//                    text = text.replace(" ", "_"); 
//                }
//                text = text.replace(" ", "_"); 
//                Question4(text, "Genus"); 
//                    
//                } 
//        }
}
