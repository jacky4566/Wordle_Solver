import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import textio.TextIO;

public class Wordle {
    static final int wordLength = 5;
    
    public static void main(String[] args) throws FileNotFoundException, IOException { //Main program
        ArrayList<String> wordList = new ArrayList<String>();
        
        //Read Dictionary
        try (BufferedReader br = new BufferedReader(new FileReader("dictionary.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == wordLength){
                    wordList.add(line);
                }
            }
        }

        int charCount[] = letterCounter(wordList);

        int attemptCounter = 0;
        while(attemptCounter < 6){
            System.out.println("***Attempt: " + attemptCounter++);
            wordList = filterFixed(wordList);
            filterUnFixed(wordList);
            filterNotIncluded(wordList);
            sortList(wordList, charCount);
            printer(wordList, charCount);
        }
    }

    public static int[] letterCounter(ArrayList<String> countThis){
        int charCount[] = new int[40]; 
        for (String s:countThis) {
            for (char ch:s.toCharArray()) {
                charCount[Character.getNumericValue(ch)]++; 
            }
        }
        return charCount;
    }

    public static void sortList(ArrayList<String> sortThis, int[] scoreTable){
        Collections.sort(sortThis, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return countScore(o2, scoreTable) - countScore(o1, scoreTable);
            }
        });
    }

    public static int countScore(String aWord, int[] scoreTable){
        int score = 0;
        for (char ch:aWord.toCharArray()) {
            score = score + scoreTable[Character.getNumericValue(ch)];
        }
        return score;
    }

    public static void printer(ArrayList<String> printThis, int[] charCount){
        System.out.println(printThis.size() + " options to pick:");
        for ( int i = 0; i < printThis.size(); ) {
            System.out.print(printThis.get(i) + "\t");
            if ((++i % 10) == 0){
                System.out.print("\n");
            }
        }
        System.out.println("\n");        
    }

    public static ArrayList<String> filterFixed(ArrayList<String> inputList){
        //output array
        ArrayList<String> output = new ArrayList<String>();
        //get input
        System.out.println("Input fixed positions");
        String filter = TextIO.getln();
        if (filter.length() != wordLength){
            System.out.println("Must be of length: " + wordLength);
            return inputList;
        }
        //replace spaces with wildcards in filter
        String regex = filter.replace(' ','.');
        //generate regex
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        for (String s:inputList) {
            if (pattern.matcher(s).matches()) {
                output.add(s);
            }
          }
        return output;
    }

    public static void filterUnFixed(ArrayList<String> inputList){
        System.out.println("Input unfixed knowns");
        String filter = TextIO.getln();
        for (char ch:filter.toCharArray()) {
            for( int i = 0; i < inputList.size(); i++ ){
                if (!inputList.get(i).contains(String.valueOf(ch))){
                    inputList.remove(i);
                    i--;
                }
            }
        }
    }

    public static void filterNotIncluded(ArrayList<String> inputList){
        System.out.println("Input not included letters");
        String filter = TextIO.getln();
        for (char ch:filter.toCharArray()) {
            for( int i = 0; i < inputList.size(); i++ ){
                if (inputList.get(i).contains(String.valueOf(ch))){
                    inputList.remove(i);
                    i--;
                }
            }
        }
    }
}