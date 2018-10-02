package CharProcessing;

import java.util.ArrayList;
import java.util.List;


enum Type{
    ALPHA, NUM, SPACE, START
}

public class C1 {
    private List<String> patternForm = new ArrayList<>();

    public String createPattern(String s) {
            s.chars()
                    .mapToObj(c -> (char) c)
                    .forEach(c -> preBuildPattern(c));
        return compressString(patternForm);
    }

    static boolean perfectFlag = false;
    private void preBuildPattern(Character c){
        if(perfectFlag){
            if(c.compareTo('>')!=0){
                patternForm.add(c.toString());
            }
            else perfectFlag = false;
        }
        else {
            if (c.compareTo('a') == 0) {
                patternForm.add("ALPHA");
            } else if (c.compareTo('n') == 0) {
                patternForm.add("NUM");
            } else if (c.compareTo(' ') == 0) {
                patternForm.add("SPACE");
            } else if (c.compareTo('<') == 0) {
                perfectFlag = true;
            }
        }
    }

    //PARSE
    //convert and compress the string to a regex pattern with Run Length encoding
    private String compressString(List<String> toCompress){
        String compressed = "";

        String str = "^";
        int count=1;
        for (String val:toCompress) {
            if(val.equals(str)){
                count++;
            }
            else {
                compressed = compressed + getMatchType(str);
                if(count != 1){
                    compressed = compressed + "{" + String.valueOf(count) +"}";
                }
                str = val;
                count = 1;
            }
        }
        compressed = compressed + getMatchType(str);
        if(count != 1){
            compressed = compressed + "{" + String.valueOf(count) +"}";
            return compressed + "$";
        }
        return compressed + "$";

    }

    private String getMatchType(String s){

        if(s.equals("ALPHA"))
            return "\\p{L}";
        else if(s.equals("NUM"))
            return "\\p{N}";
        else if(s.equals("SPACE"))
            return  " ";
        else return s;
    }

}
