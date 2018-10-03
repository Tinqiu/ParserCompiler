package CharProcessing;

import java.util.ArrayList;
import java.util.List;


enum Type{
    ALPHA, NUM, SPACE, START
}

public class C1 {
    private List<String> patternForm = new ArrayList<>();

    public String createMatchignPattern(String s) {
            s.chars()
                    .mapToObj(c -> (char) c)
                    .forEach(c -> preBuildPattern(c));
        return compressString(patternForm, false);
    }

    public String createTransformPattern(String s) {
        s.chars()
                .mapToObj(c -> (char) c)
                .forEach(c -> preBuildPattern(c));
        return compressString(patternForm, true);
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

    //convert and compress the string to a regex pattern with Run Length encoding
    private String compressString(List<String> toCompress, boolean toTransform){
        String compressed = "";

        String str = toTransform ? "" : "^";
        int count=1;
        for (String val : toCompress) {
            if(val.equals(str)){
                if(toTransform){
                    compressed = compressed + getMatchType(str) +",";
                }else count++;
            }
            else {
                if(toTransform){
                    compressed = compressed + getMatchType(str) +",";
                } else compressed = compressed + getMatchType(str);
                if(count != 1){
                    if(toTransform){
                        compressed = compressed + getMatchType(str) +",";
                    }else compressed = compressed + "{" + String.valueOf(count) +"}";
                }
                str = val;
                count = 1;
            }
        }
        if(toTransform){
            compressed = compressed + getMatchType(str) +",";
        } else compressed = compressed + getMatchType(str);
        if(count != 1){
            if(toTransform){
                compressed = compressed + getMatchType(str) +",";
            } else compressed = compressed + "{" + String.valueOf(count) +"}";
            return toTransform ? compressed.substring(1,compressed.length()-1) : compressed + "$";
        }
        return toTransform ? compressed.substring(1,compressed.length()-1) : compressed + "$";

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
