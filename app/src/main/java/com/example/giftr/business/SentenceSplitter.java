package com.example.giftr.business;

public class SentenceSplitter {
    public static String[] splitSentence(String sentence, int maxLength) {
        String[] words = sentence.split(" ");
        StringBuilder sb = new StringBuilder();
        String currentLine = "";
        int currentLength = 0;
        int wordIndex = 0;
        int sentenceIndex = 0;

        while (wordIndex < words.length) {
            String word = words[wordIndex];

            if (currentLength + word.length() <= maxLength) {
                sb.append(word).append(" ");
                currentLength += word.length() + 1;
                wordIndex++;
            } else {
                currentLine = sb.toString().trim();
                sb.setLength(0);
                currentLength = 0;
                sentenceIndex++;
                System.out.println("Sentence " + sentenceIndex + ": " + currentLine);
            }
        }

        if (sb.length() > 0) {
            currentLine = sb.toString().trim();
            sentenceIndex++;
            System.out.println("Sentence " + sentenceIndex + ": " + currentLine);
        }

        String[] splitSentences = new String[sentenceIndex];
        sentenceIndex = 0;
        sb.setLength(0);

        for (int i = 0; i < wordIndex; i++) {
            String word = words[i];

            if (sb.length() + word.length() <= maxLength) {
                sb.append(word).append(" ");
            } else {
                splitSentences[sentenceIndex] = sb.toString().trim();
                sentenceIndex++;
                sb.setLength(0);
                sb.append(word).append(" ");
            }
        }

        if (sb.length() > 0) {
            splitSentences[sentenceIndex] = sb.toString().trim();
        }

        return splitSentences;
    }
}
