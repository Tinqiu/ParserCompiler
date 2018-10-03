import CharProcessing.C1;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Program {
    static List<String> compiledMatchingPatterns = new ArrayList<>();
    static List<String> compiledTransformPatterns = new ArrayList<>();
    public static void main(String args[]){
        String[] inPatterns = {"a1n1a2 n2a3n3,a1n1a2n2a3n3",
                                "<code>   ,  <code>  "};
        String[] toMatch = {"G1Q 1Q9",
                            "Hi, this is my postal code   tw"};

        for(String var : toMatch){
            for(String inPattern : inPatterns) {
                C1 c1 = new C1();

                int patternCount = Arrays.asList(inPattern.split("((?<=\\s)|(?=\\s))")).size();

                compiledMatchingPatterns.add(c1.createMatchignPattern(inPattern.split(",")[0]));
                generateTransformPatterns(inPattern.split(",")[1]);

                Pattern pattern = Pattern.compile(compiledMatchingPatterns.get(compiledMatchingPatterns.size() - 1));
                process(pattern, var, patternCount);

                //System.out.println(compiledPatterns.get(compiledPatterns.size()-1));
            }
        }

    }

    private static void generateTransformPatterns(String prePattern){
        C1 c1 = new C1();
        compiledTransformPatterns.add(c1.createTransformPattern(prePattern));
    }



    private static void process(Pattern compiledPattern, String toMatch, int patternCount){
        List<String> reverseToMatch = new ArrayList();
        Stream.of(toMatch.split("((?<=\\W)|(?=\\W))"))
                .collect(Collectors.toCollection(LinkedList::new))
                .descendingIterator().forEachRemaining(reverseToMatch::add);



        if(patternCount > 1) {
            for(int i = 0; i<reverseToMatch.size()-patternCount;i++){
                StringBuilder sb = new StringBuilder();
                for(int j = patternCount; j>=0; j--) {
                    //crawl the range through the list to cover all ordered-word possibilities
                    sb.append(reverseToMatch.get(j+i));
                }

                toMatch = sb.toString();
                Matcher matcher = compiledPattern.matcher(toMatch);
                if(matcher.find()) {
                    System.out.println("found match: " + matcher.group());
                    transform(matcher.group(), compiledTransformPatterns);
                    break;
                }
                    //else System.out.println("no match found");
            }

        }else{
            for(int i = 0; i<=reverseToMatch.size()-1;i++){
                toMatch = reverseToMatch.get(i);

                Matcher matcher = compiledPattern.matcher(toMatch);
                if(matcher.find()){
                    System.out.println("found match: " + matcher.group());
                    break;
                }
                //else System.out.println("no match found");
            }
        }
    }

    private static void transform(String toTranform, List<String> transformPattern){
        StringBuilder sb = new StringBuilder();
        String[] mappingTable = transformPattern.get(0).split(",");
        String[] toMap = toTranform.split("");

        for(int i = 0; i <=mappingTable.length-1;i++){
            Pattern pattern = Pattern.compile(mappingTable[i]);
            for(int j = 0; j<=toMap.length-1;j++){
                Matcher matcher = pattern.matcher(toMap[j]);
                if(matcher.find()){
                    sb.append(toMap[j]);
                    toMap[j] = "%&%";
                    break;
                }
            }
        }
        System.out.println(sb.toString());

    }
}

