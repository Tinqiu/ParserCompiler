import CharProcessing.C1;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Program {
    private static final String SPLIT_ON_SPACE_LOOKBEHIND = "((?<=[\\s])|(?=[\\s]))";

    public static void main(String args[]){
        ArrayList<String[]> inPatterns = new ArrayList<>();

        String toMatch = "G1Q  1Q9";

        String[] patterns = {"a1n1a2  n2a3n3","<test>a1n1a2 n2a3n3"};
        inPatterns.add(patterns);
        try {
            match(inPatterns, toMatch);
        }catch (TransformPatternTooLongException ex){
            ex.printStackTrace();
        }
    }

    private static void match(ArrayList<String[]> matchingPatterns, String toMatch)
            throws TransformPatternTooLongException{
        List<String> reverseToMatch = new ArrayList<>();
        //split the string to match on spaces and commas but keep them in the list using the lookbehind pattern
        //collect the string array into a linked list then reverse the order of the "sentence"
        Stream.of(toMatch.split(SPLIT_ON_SPACE_LOOKBEHIND))
                .collect(Collectors.toCollection(LinkedList::new))
                .descendingIterator().forEachRemaining(reverseToMatch::add);

        int patternLength;

        attemptMatch:
        for (String[] matchPattern : matchingPatterns) {
            C1 c1 = new C1();
            Pattern compiledMatchPattern = Pattern.compile(c1.createPatternForMatching(matchPattern[0]));
            patternLength = c1.getPatternWordCount();

            if (patternLength <= reverseToMatch.size()) {
                if (patternLength > 1) {
                    for (int i = 0; i <= reverseToMatch.size() - patternLength; i++) {
                        StringBuilder sb = new StringBuilder();
                        for (int j = patternLength - 1; j >= 0; j--) {
                            sb.append(reverseToMatch.get(i + j));
                        }

                        Matcher matcher = compiledMatchPattern.matcher(sb.toString());
                        if (matcher.find()) {
                            System.out.println("found match: " + matcher.group() + " using pattern: " + compiledMatchPattern.toString());

                            List<String> transformPattern = c1.createPatternForTransforming(matchPattern[1]);
                            if(c1.transformAvailable()) {
                                transform(toMatch, matcher, transformPattern);
                                break attemptMatch;
                            }else
                                throw new TransformPatternTooLongException("The transformation pattern must have a token count that is equal or less than the " +
                                        "matching pattern.");
                        }
                    }

                } else {
                    for (int i = 0; i <= reverseToMatch.size() - 1; i++) {
                        Matcher matcher = compiledMatchPattern.matcher(reverseToMatch.get(i));
                        if (matcher.find()) {
                            System.out.println("Original string: " + toMatch);
                            System.out.println("found match: " + matcher.group() + " using pattern: " + compiledMatchPattern.toString());
                            List<String> transformPattern = c1.createPatternForTransforming(matchPattern[1]);
                            if (c1.transformAvailable()) {
                                transform(toMatch, matcher, transformPattern);
                                break attemptMatch;
                            } else
                                throw new TransformPatternTooLongException("The transformation pattern must be equal or smaller than the matching pattern.");
                        }
                    }
                }
            }
        }
    }


    private static void transform(String toMatch, Matcher matcher, List<String> transformPattern){
        StringBuilder sb = new StringBuilder();

        System.out.println("Target Pattern " + transformPattern.toString());

        transformPattern
                .forEach(c -> sb.append(c.startsWith("~") ? c.split("~")[1] : matcher.group(c)));


        System.out.println("Original String post-match: " + toMatch.replace(matcher.group(), ""));
        System.out.println("Transformed Match: " + sb.toString());
        System.out.println();
    }

   static class TransformPatternTooLongException extends Exception{
        public TransformPatternTooLongException(String message){
            super(message);
        }
    }
}

