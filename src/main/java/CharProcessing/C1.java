package CharProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static CharProcessing.Type.*;


enum Type{
    ALPHA, NUM, SPACE, PSTART, PEND, ALPHAX, NUMX
}

public class C1 {
    private List<String> patternForm = new ArrayList<>();
    private List<String> transformOrder = new ArrayList<>();
    private int patternLength = 1;
    private HashMap<Type, Integer> matchingTokens  = new HashMap<>();
    private HashMap<Type, Integer> transformTokens  = new HashMap<>();

    public C1(){
        for(Type t : Type.values()){
            matchingTokens.put(t,0);
            transformTokens.put(t,0);
        }
    }

    public int getPatternWordCount(){return trailingSpace ? patternLength -1 : patternLength;}

    public String createPatternForMatching(String s) {
        resetInternals();
        s.chars()
                .mapToObj(c -> (char) c)
                .forEach(c -> tokenizePattern(c,false));
        return compressAndConvert(patternForm, false);
    }

    public List<String> createPatternForTransforming(String s){
        resetInternals();
        s.chars()
                .mapToObj(c -> (char) c)
                .forEach(c -> tokenizePattern(c,true));
        patternForm.forEach(this::getMatchType);
        return transformOrder;
    }

    public Boolean transformAvailable(){
        for(Type t : Type.values()){
            if(transformTokens.get(t) > matchingTokens.get(t)){
                return false;
            }
        }
        return true;
    }

    private boolean perfectFlag = false;
    private void tokenizePattern(Character c, Boolean transform){
        if(perfectFlag){
            if(c.compareTo('>')!=0){
                patternForm.add(c.toString());
            }
            else {
                patternForm.add(PEND.name());
                perfectFlag = false;
            }
        }
        else {
            if (c.compareTo('a') == 0) {
                patternForm.add(ALPHA.name());
                updateMap(ALPHA,transform);
            } else if(c.compareTo('A') == 0){
                patternForm.add(ALPHAX.name());
                updateMap(ALPHAX,transform);
            } else if (c.compareTo('n') == 0) {
                patternForm.add(NUM.name());
                updateMap(NUM,transform);
            } else if (c.compareTo('N') == 0) {
                patternForm.add(NUMX.name());
                updateMap(NUMX,transform);
            } else if (c.compareTo(' ') == 0) {
                patternForm.add(SPACE.name());
                updateMap(SPACE,transform);
            } else if (c.compareTo('<') == 0) {
                patternForm.add(PSTART.name());
                perfectFlag = true;
            }
        }
    }

    private void updateMap(Type type, Boolean transform){
        HashMap<Type, Integer> tempMap = transform ? transformTokens : matchingTokens;
        tempMap.put(type,tempMap.get(type)+1);
    }


    private String compressAndConvert(List<String> toCompress, boolean toTransform){
        StringBuilder converted = new StringBuilder(toTransform ? "" : "^");
        toCompress
                .forEach(c -> converted.append(toTransform ? getMatchType(c) + "," : getMatchType(c)));

        String returnString = converted.toString();
        //remove the trailing comma before returning if it was a transform pattern that was generated
        return toTransform ? returnString.substring(1, returnString.length()-1) : returnString + "$";
    }

    private int aIndex = 0;
    private int nIndex = 0;
    private int pIndex = 0;
    private int bigNIndex = 0;
    private int bigAIndex = 0;
    private int sIndex = 0;
    private boolean trailingSpace = false;
    private String tempPerfectMatch = "";

    private String getMatchType(String s){
        String toAdd = "";

        if(s.equals(ALPHA.name())) {
            trailingSpace = false;
            aIndex ++;
            toAdd = "a" + aIndex;
            transformOrder.add(toAdd);
            return "(?<" + toAdd + ">\\p{L})";
        }
        else if(s.equals(ALPHAX.name())) {
            trailingSpace = false;
            bigAIndex ++;
            toAdd = "A" + bigAIndex;
            transformOrder.add(toAdd);
            return "(?<" + toAdd + ">\\p{L}*)";
        }
        else if(s.equals(NUM.name())){
            trailingSpace = false;
            nIndex++;
            toAdd = "n" + nIndex;
            transformOrder.add(toAdd);
            return "(?<" + toAdd + ">\\p{N})";
        }
        else if(s.equals(NUMX.name())) {
            trailingSpace = false;
            bigNIndex ++;
            toAdd = "N" + bigNIndex;
            transformOrder.add(toAdd);
            return "(?<" + toAdd + ">\\p{N}*)";
        }
        else if(s.equals(SPACE.name())) {
            sIndex++;
            patternLength = trailingSpace ? patternLength + 1 : patternLength + 2;
            trailingSpace = true;
            toAdd = "s" + sIndex;
            transformOrder.add(toAdd);
            return "(?<" + toAdd + "> )";
        }
        else if(s.equals(PSTART.name())){
            trailingSpace = false;
            pIndex++;
            toAdd = "p" + pIndex;
            tempPerfectMatch = "~";
            return "(?<" + toAdd + ">";
        }
        else if(s.equals(PEND.name())){
            transformOrder.add(tempPerfectMatch);
            tempPerfectMatch = "";
            return ")";
        }
        else{
            tempPerfectMatch = tempPerfectMatch + s;
            return s;
        }
    }

    private void resetInternals(){
        aIndex = 0;
        nIndex = 0;
        pIndex = 0;
        bigNIndex = 0;
        bigAIndex = 0;
        sIndex = 0;
        patternForm = new ArrayList<>();
        transformOrder = new ArrayList<>();
    }
}