import CharProcessing.C1;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Program {
    public static void main(String args[]){
        String[] inPatterns = {"a1n1a2<->n2a3n3", "a1a2a3n1n2n3", "a1a2a3 n1n2n3"};
        String[] toMatch = {"é1è-1m1 Truly this is a day of é1ë-2n2",
                            "aaa111 bbb222",
                            "çôê 777 This is the end of the kkhk 222"};

        int index = 0;
        List<String> compiledPatterns = new ArrayList<>();
        for(String inPattern : inPatterns){
            C1 c1 = new C1();
            int patternCount = Arrays.asList(inPattern.split(" ")).size();

            compiledPatterns.add(c1.createPattern(inPattern));
            Pattern pattern = Pattern.compile(compiledPatterns.get(compiledPatterns.size()-1));
            process(pattern, toMatch[index], patternCount);
            index++;
           System.out.println(compiledPatterns.get(compiledPatterns.size()-1));
        }
    }



    private static void process(Pattern compiledPattern, String toMatch, int patternCount){
        List<String> reverseToMatch = new ArrayList();
        Stream.of(toMatch.split("((?<=\\s)|(?=\\s))"))
                .collect(Collectors.toCollection(LinkedList::new))
                .descendingIterator().forEachRemaining(reverseToMatch::add);



        if(patternCount > 1) {
            for(int i = 0; i<=reverseToMatch.size()/patternCount;i++){
                StringBuilder sb = new StringBuilder();
                for(int j = patternCount; j>=0; j--) {
                    sb.append(reverseToMatch.get(j+(i*patternCount)));
                }

                toMatch = sb.toString();
                Matcher matcher = compiledPattern.matcher(toMatch);
                if(matcher.find()) {
                    System.out.println("found match: " + matcher.group());
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
}

