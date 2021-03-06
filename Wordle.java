import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import textio.TextIO;

//TODO:
// - Handle double characters 
// - Remove known characters in wrong place

public class Wordle {
    static final int wordLength = 5; //All words are 5 characters
    
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
		
		//Build our character freqency table
        int charCount[] = characterFreqCounter(wordList);

		// Loop for 5 attempts
        int attemptCounter = 0;
        while(attemptCounter < 6){
            System.out.println("***Attempt: " + attemptCounter++);
            filterFixed(wordList);
            filterUnFixed(wordList);
            filterNotIncluded(wordList);
            sortList(wordList, charCount);
            printer(wordList, charCount);
        }
    }

    public static int[] characterFreqCounter(ArrayList<String> countThis){
		//Sums the freqency of every unique character
        int charCount[] = new int[40]; 
        for (String s:countThis) {
            for (char ch:s.toCharArray()) {
                charCount[Character.getNumericValue(ch)]++; 
            }
        }
        return charCount;
    }

    public static void sortList(ArrayList<String> sortThis, int[] scoreTable){
		//Sort the option list by the freqency of the characters compared to our freqency table
        Collections.sort(sortThis, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return countScore(o2, scoreTable) - countScore(o1, scoreTable);
            }
        });
    }

    public static int countScore(String aWord, int[] scoreTable){
		//create a score based on the sum of our characters freqency
        int score = 0;
        for (char ch:aWord.toCharArray()) {
            score = score + scoreTable[Character.getNumericValue(ch)];
        }
        return score;
    }

    public static void printer(ArrayList<String> printThis, int[] charCount){
		//print out the potential list
        System.out.println(printThis.size() + " options to pick:");
        for ( int i = 0; i < printThis.size(); ) {
            System.out.print(printThis.get(i) + "\t");
            if ((++i % 10) == 0){
                System.out.print("\n");
            }
        }
        System.out.println("\n");        
    }

    public static void filterFixed(ArrayList<String> inputList){
        //get input
        System.out.println("Input fixed positions");
        String filter = TextIO.getln();
        if (filter.length() != wordLength){
            System.out.println(filter + " must be of length: " + wordLength);
            return;
        }
        //replace spaces with wildcards in filter
        String regex = filter.replace(' ','.');
        //generate regex
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        for( int i = 0; i < inputList.size(); i++ ){
            if (!pattern.matcher(inputList.get(i)).matches()) {
                inputList.remove(inputList.get(i));
                i--;
            }
          }
        return;
    }

    public static void filterUnFixed(ArrayList<String> inputList){
        System.out.println("Input unfixed knowns");
        String filter = TextIO.getln();
        //Check input
        if (filter.length() != wordLength){
            System.out.println(filter + " must be of length: " + wordLength);
            return;
        }
        //remove words that dont contain target chars
        for( int i = 0; i < filter.length(); i++ ){
            char filterChar = filter.charAt(i);
            if (filterChar != ' '){
                for( int j = 0; j < inputList.size(); j++ ){
                    //Remove words that dont have our target char
                    if (!inputList.get(j).contains(String.valueOf(filterChar))){
                        inputList.remove(j);
                        j--;
                    }
                }
                for( int j = 0; j < inputList.size(); j++ ){
                    //Remove words that have our target char in the same position
                    if (inputList.get(j).charAt(i) == filterChar){
                        inputList.remove(j);
                        j--;
                    }
                }
            }
        }
    }

    public static void filterNotIncluded(ArrayList<String> inputList){
        System.out.println("Input not included letters, do not enter double letters");
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