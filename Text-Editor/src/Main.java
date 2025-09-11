import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String input = PromptUserForInput("Please input a lengthy paragraph:");

        int characterCount = CountCharacters(input);
        int wordsCount = CountWords(input);
        char commonChar = FindMostCommonCharacter(input);
        System.out.println("\nCHARACTERS COUNT = " + characterCount);
        System.out.println("WORDS COUNT = " + wordsCount);
        System.out.println("MOST COMMON CHARACTER = " + commonChar);

        char characterInput = PromptUserForCharacter("\nPlease enter a single character:");
        int characterFrequency = ComputeCharacterFrequency(characterInput, input);
        System.out.println("CHARACTER FREQUENCY = " + characterFrequency);

        String wordInput = PromptUserForInput("\nPlease input a word:");
        int wordFrequency = ComputeWordFrequency(wordInput, input);
        System.out.println("WORD FREQUENCY = " + wordFrequency);

        int uniqueWordsCount = CountUniqueWords(input);
        System.out.println("UNIQUE WORDS COUNT = " + uniqueWordsCount);
    }

    private static int CountCharacters(String input) {
        int characterCount = 0;
        if(input != null && !input.isEmpty()) {
            characterCount = input.trim().length();
        }
        return characterCount;
    }
    private static int CountWords(String input) {
        int wordCount = 0;
        if(input != null && !input.isEmpty()) {
            String[] words = input.trim().split(" ");
            wordCount = words.length;
        }
        return wordCount;
    }
    private static int CountUniqueWords(String input) {
        int uniqueWordsCount = 0;
        if(input != null && !input.isEmpty()) {
            String[] words = input.toLowerCase().replaceAll("[^a-z0-9\\s]", "").split("\\s+");
            List<String> foundWords = new java.util.ArrayList<>();
            for(String word : words) {
                if(!word.isEmpty() && !foundWords.contains(word)) {
                    uniqueWordsCount++;
                    foundWords.add(word);
                }
            }
        }
        return uniqueWordsCount;
    }
    private static int ComputeCharacterFrequency(char character, String input) {
        int frequency = 0;
        if(input != null && !input.isEmpty()) {
            String[] words = input.toLowerCase().split(" ");
            for(String word : words) {
                if(word.contains(String.valueOf(character))) {
                    frequency++;
                }
            }
        }
        return frequency;
    }
    private static int ComputeWordFrequency(String word, String input) {
        int frequency = 0;
        if(input != null && !input.isEmpty()) {
            String[] words = input.toLowerCase().split(" ");
            for(String w : words) {
                if(w.equals(word)) {
                    frequency++;
                }
            }
        }
        return frequency;
    }
    private static String PromptUserForInput(String prompt) {
        String input = "";
        if(prompt != null && !prompt.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.print(prompt);
            input = scanner.nextLine() ;
        }
        return input; //this is the input from the user
    }
    private static char PromptUserForCharacter(String prompt) {
        char input = ' ';
        if(prompt != null && !prompt.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.print(prompt);
            String rawInput = scanner.nextLine();
            if(rawInput.trim().length() != 1) {
                System.out.println("Invalid input. Please enter a single character.");
            } else {
                input = rawInput.charAt(0);
            }
        }
        return input;
    }
    private static char FindMostCommonCharacter(String input) {
        char mostCommonChar = ' ';
        if(input != null && !input.isEmpty()) {
            char[] characters = input.trim().replace(" ", "").replaceAll(
                    "[^a-z0-9\\s]", "").toCharArray();
            int[] characterCount = new int[256];
            for(char character : characters) {
                characterCount[character]++;
            }
            int maxCount = 0;
            for(int i = 0; i < characterCount.length; i++) {
                if(characterCount[i] > maxCount) {
                    maxCount = characterCount[i];
                    mostCommonChar = (char)i;
                }
            }
        }
        return mostCommonChar;
    }
}