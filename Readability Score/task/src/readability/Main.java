package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;


public class Main {
    public static void main(String[] args) {
        String fileName = args[0];
        Scanner scanner = new Scanner(System.in);

        long words;
        long sentences;
        long characters;
        long syllables;
        long pollysullables;
        float scoreARI;
        float scoreFK;
        double scoreSMOG;
        double scoreCL;

        String text = getTextFromFile(fileName);
        System.out.println("The text is:");
        System.out.println(text);

        Processor processor = new Processor();

        words = processor.countOfWord(text);
        sentences = processor.countOfStrings(text);
        characters = processor.countOfChar(text);
        syllables = processor.countSyllablesInString(text);
        pollysullables = processor.countPollyllablesInString(text);

        scoreARI = 4.71f * characters / words + 0.5f * words / sentences - 21.43f;
        scoreFK = 0.39f * words / sentences + 11.8f * syllables / words - 15.59f;
        scoreSMOG = 1.043f * Math.sqrt(pollysullables * 30 / (double) sentences) + 3.1291f;
        scoreCL = 0.0588f * (characters / (float) words * 100) - 0.296f * (sentences / (float) words * 100) - 15.8;

        System.out.println("\nWords: " + words);
        System.out.println("Sentences: " + sentences);
        System.out.println("Characters: " + characters);
        System.out.println("Syllables: " + syllables);
        System.out.println("Polysyllables: " + pollysullables);
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String command = scanner.next();

        switch (command.toUpperCase()) {
            case "ARI":
                System.out.println("\nAutomated Readability Index: " + formatScore(scoreARI) + understood(scoreARI));
                break;
            case "FK":
                System.out.println("\nFlesch–Kincaid readability tests: " + formatScore(scoreFK) + understood(scoreFK));
                break;
            case "SMOG":
                System.out.println("\nSimple Measure of Gobbledygook: " + formatScore((float) scoreSMOG) + understood((float) scoreSMOG));
                break;
            case "CL":
                System.out.println("\nColeman–Liau index: " + formatScore((float) scoreCL) + understood((float) scoreCL));
                break;
            case "ALL":
                System.out.println();
                System.out.println("Automated Readability Index: " + formatScore(scoreARI) + understood(scoreARI));
                System.out.println("Flesch–Kincaid readability tests: " + formatScore(scoreFK) + understood(scoreFK));
                System.out.println("Simple Measure of Gobbledygook: " + formatScore((float) scoreSMOG) + understood((float) scoreSMOG));
                System.out.println("Coleman–Liau index: " + formatScore((float) scoreCL) + understood((float) scoreCL));
                System.out.println("\nThis text should be understood in average by 14.25 year olds.");
                break;
        }
    }

    private static String understood(float score) {
        if (score > 0 && score <= 1) {
            return " (about 5 year olds).";
        } else if (score > 1 && score <= 2) {
            return " (about 6 year olds).";
        } else if (score > 2 && score <= 3) {
            return " (about 7 year olds).";
        } else if (score > 3 && score <= 4) {
            return " (about 9 year olds).";
        } else if (score > 4 && score <= 5) {
            return " (about 10 year olds).";
        } else if (score > 5 && score <= 6) {
            return " (about 11 year olds).";
        } else if (score > 6 && score <= 7) {
            return " (about 12 year olds).";
        } else if (score > 7 && score <= 8) {
            return " (about 13 year olds).";
        } else if (score > 8 && score <= 9) {
            return " (about 14 year olds).";
        } else if (score > 9 && score <= 10) {
            return " (about 15 year olds).";
        } else if (score > 10 && score <= 11) {
            return " (about 16 year olds).";
        } else if (score > 11 && score <= 12) {
            return " (about 17 year olds).";
        } else if (score > 12 && score <= 13) {
            return " (about 18 year olds).";
        } else if (score > 13) {
            return " (about 24 year olds).";
        }
        return "WHAT??";
    }


    private static String formatScore(float score) {
        String scr = String.valueOf(score);
        return scr.substring(0, score / 10 > 1 ? 5 : 4);
    }

    private static String getTextFromFile(String fileName) {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}

class Processor {

    public long countOfStrings(String text) {
        return extractStrings(text).size();
    }

    public long countOfWord(String text) {
        return extractStrings(text).stream()
                .map(this::extractWords)
                .mapToLong(List::size)
                .sum();
    }

    public long countOfChar(String text) {
        return Stream.of(text.split("\\s+"))
                .map(this::extractWords)
                .mapToLong(x -> {
                    int count = 0;
                    for (String each : x) {
                        count += each.length();
                    }
                    return count;
                }).sum();
    }


    public long countSyllablesInString(String text) {
        return Stream.of(text.split("\\s"))
                .map(this::extractWordsWithoutNum)
                .mapToLong(this::countSyllablesInWord)
                .sum();
    }

    public long countPollyllablesInString(String text) {
        return Stream.of(text.split("\\s"))
                .map(this::extractWordsWithoutNum)
                .mapToLong(this::countPolysyllablesInWord)
                .sum();
    }

    private long countPolysyllablesInWord(List<String> word) {
        long count = 0;
        for (String each : word) {
            if (findSyllablesInWord(each) > 2) {
                count++;
            }
        }
        return count;
    }

    private long countSyllablesInWord(List<String> word) {
        long count = 0;
        for (String each : word) {
            count += findSyllablesInWord(each);
        }
        return count;
    }

    public int findSyllablesInWord(String word) {
        char[] array = word.toLowerCase().toCharArray();
        List<Character> vowels = List.of('a', 'e', 'i', 'o', 'u', 'y');
        int count = 0;
        int index = 0;

        while (index < array.length) {
            if (vowels.contains(array[index]) &&
                    (index + 1 < array.length && !vowels.contains(array[index + 1]))) {
                count++;
            }
            if (index == array.length - 1 && array[index] == 'e') {
                count--;
            }
            index++;
        }
        if (array.length > 0 && count > 0) {
            return count;
        } else if (array.length == 0) {
            return 0;
        } else return 1;
    }

    private List<String> extractStrings(String text) {
        return Arrays.asList(text.split("!+\\s*|\\?+\\s*|\\.+\\s*"));
    }

    private List<String> extractWords(String strings) {
        return Arrays.asList(strings.split("\\s+"));
    }

    private List<String> extractWordsWithoutNum(String strings) {
        return Arrays.asList(strings.replaceAll("[0-9]+,*[0-9]+", "").split("\\s+"));
    }

}