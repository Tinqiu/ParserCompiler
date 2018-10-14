package PN005_C1;


import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static PN005_C1.TokenType.*;

/** Types of tokens that can make up a pattern */
/*
 *  ALPHA: single alpha character
 *  NUM: single numeric character
 *  ALPHAX: one or more alpha character
 *  NUMX: one or more numeric character
 *  SPACE: one space
 *  PSTART: start of a perfect match
 *  PEND: end of a perfect match
 */
enum TokenType {
    ALPHA, NUM, ALPHAX, NUMX, SPACE, PSTART, PEND
}

/***
 * PN005 - Move Patterns Between Variables
 * Feature: SRI.603.1.2
 * https://icos-doc.statcan.ca/pages/viewpage.action?pageId=622985634
 */
public class PN005_MovePatternsBetweenVariables  {
    //TODO: Clean up all the unused stuff once I confirm that it's not needed anywhere anymore
    /** This PN's identifier */
    public static final String NAME = "PN005";

     /** PN requires a two-column conversion table: var1pattern and var2pattern */
    private String[] table;

    /** The column in {@code table} containing the patterns to match {@code variable1} field against */
    private static final int VAR_1_PATTERN = 0;

    /** The column in {@code table} containing the patterns to convert {@code variable1} field to */
    private static final int VAR_2_PATTERN = 1;

    /** Storage for the value of {@code variable1} field */
    private String var1_field;

    /** Storage for the value of {@code subPhrase} once transformed by {@code var2_pattern} */
    private String var2_field;

    /** The normalized value of the sub phrase currently being matched against {@code var1_pattern} */
    public String subPhrase;

    /**  A constant to replace the regex to improve the readability of the code */
    private static final String SPLIT_ON_SPACE_LOOKBEHIND = "((?<=[\\s])|(?=[\\s]))";

    private static final Pattern VALID_PATTERN = Pattern.compile("^(a\\d|A\\d|n\\d|N\\d|<.+>| )*$");

    private String[] results = {"",""};



    public PN005_MovePatternsBetweenVariables(String variable1, String variable2, String[] table) {
        this.var1_field = variable1;
        this.var2_field = variable2;
        this.table = table;
        process();
    }

    /**
     * For each word in {@code variable1} field, starting with the last and
     * accumulating words into "phrases", compare each "sub phrase" to all
     * patterns in {@code table[VAR_1_PATTERN]} until the first match is found.
     * If a match is found, the "sub phrase" is transformed to the pattern
     * described by {@code table[VAR_2_PATTERN]}, and both {@code variable1}
     * and {@code variable2} are updated.
     */

    public String[] getResults(){return results;}
    public void process(){
        try {
            match(table, var1_field,var2_field);
        }
        //TODO: do something with these exceptions
        catch (InvalidPatternException | AmbiguousPatternException ex){
            ex.printStackTrace();
        }
    }


    /***
     * Will attempt to find a match using the provided matching patterns applied to the provided string.
     * If a match is found then it will attempt to transform the matched section using the provided transform pattern
     * @param matchingPatterns an {@code ArrayList} containing String arrays that hold the matching and transform patterns
     * @param toMatch the string in which to attempt to find a match
     * @throws InvalidPatternException
     * @throws AmbiguousPatternException
     */
    private void match(String[] matchingPatterns, String toMatch, String variable1)
            throws InvalidPatternException, AmbiguousPatternException {
        Stack<String> reverseStack = new Stack<>();

        Normalizer.Form originalForm = getNormalizationForm(variable1);
        Normalizer.Form targetForm = getNormalizationForm(var2_field);
        //if var2_field has no Normalizer.Form, then make it NFC
        targetForm = targetForm != null ? targetForm : originalForm;


        String normalizedToMatch = toMatch;
        if (!originalForm.equals(Normalizer.Form.NFD)) {
            normalizedToMatch = Normalizer.normalize(toMatch, Normalizer.Form.NFD);
        }
        //split the string to match on spaces but keep them in the list using the lookbehind pattern
        //collect the string array into a linked list then reverse the order of the "sentence"
        Stream.of(normalizedToMatch.split(SPLIT_ON_SPACE_LOOKBEHIND))
                .forEach(c -> reverseStack.push(c));

        int patternLength;

        Matcher matcher = VALID_PATTERN.matcher(matchingPatterns[0]);
        if (matcher.find()) {
            C1 c1 = new C1();
            c1.setOriginalForm(originalForm);
            c1.setTargetForm(targetForm);
            Pattern compiledMatchPattern = Pattern.compile(c1.createPatternForMatching(matchingPatterns[0]));

            //setup the pattern
            String matchAttempt = "";

            while (!reverseStack.isEmpty()) {
                matchAttempt = reverseStack.pop() + matchAttempt;
                c1.setOriginalMatch(matchAttempt.toString());
                matcher = compiledMatchPattern.matcher(matchAttempt.replaceAll("[^\\P{M}]", "").toUpperCase());
                if (matcher.find()) {
                    c1.setOriginalMatcher(matcher);
                    transform(normalizedToMatch, c1, matchingPatterns[1]);
                }
            }
        }
    }


    /***
     * Builds the transformed string based on the match found and the transform pattern provided then updates the register
     * with the new values for var1 and var2
     * @param matched the substring that was matched from the original string
     * @param c1 the current instance of the C1 class containing the current matching and transformation metadata
     * @param baseTransformPattern the string to be used to create the transform pattern tokens to transform the matched string
     */
    private void transform(String matched, C1 c1, String baseTransformPattern)
            throws InvalidPatternException{
        List<String> transformPattern = c1.createPatternForTransforming(baseTransformPattern);
        if(c1.transformAvailable()) {
            StringBuilder sb = new StringBuilder();
            transformPattern
                    .forEach(c -> sb.append(c.startsWith("~") ? c.split("~")[1] : c1.getReconstructString(c)));

            String var1 = matched.replace(c1.getOriginalMatch(), "");
            String var2 = sb.toString();
            updateRegisters(var1,var2);
        }else
            throw new InvalidPatternException(c1.getTransformPatternTooLongExceptionMessage());
    }

    /***
     * Updates the registers with the matched and transformed values
     * @param var1 the original string that contained the match, minus the matched section
     * @param var2 the transformed matched section of the original string
     */
    private void updateRegisters(String var1, String var2) {

        results[0] = var1;
        results[1] = var2;
    }

    private Normalizer.Form getNormalizationForm(String s){
        if(Normalizer.isNormalized(s,Normalizer.Form.NFC))
            return Normalizer.Form.NFC;
        else if(Normalizer.isNormalized(s,Normalizer.Form.NFD))
            return Normalizer.Form.NFD;
        else if(Normalizer.isNormalized(s,Normalizer.Form.NFKC))
            return Normalizer.Form.NFKC;
        else if(Normalizer.isNormalized(s,Normalizer.Form.NFKD))
            return Normalizer.Form.NFKD;

        return null;
    }

}

/* The following are valid constructs that may be contained in {@code var1_pattern):
 *
 * 1. 'a', to declare the presence of a single alphabetic character
 * 2. 'n', to declare the presence of a single numeric character
 * 3. 'A', to declare the presence of one or more alphabetic characters
 * 4. 'N', to declare the presence of a one or more numeric characters
 * 5. '<', to declare the beginning of an exact match
 * 6. '>', to declare the end of an exact match
 * 7. ' ', a single space character to separate constructs
 *
 * Items 1 through 4 must be followed by an occurrence number.
 */
class C1 {
    /**
     * Internals for metrics and proper assigning
     */
    private int aIndex = 0;
    private int nIndex = 0;
    private int pIndex = 0;
    private int bigNIndex = 0;
    private int bigAIndex = 0;
    private int sIndex = 0;
    private String tempPerfectMatch = "";

    private List<String> patternForm = new ArrayList<>();
    private List<String> transformOrder = new ArrayList<>();

    private String baseMatchingPattern;
    private String baseTransformPattern;

    private String originalMatch;
    private Matcher originalMatcher;
    private Normalizer.Form originalForm;
    private Normalizer.Form targetForm;
    private List<String> matchingOrder = new ArrayList<>();

    /**
     * Maps of tokens used to check if a transform is available based on the inputed patterns
     */
    private EnumMap<TokenType, Integer> matchingTokens = new EnumMap<>(TokenType.class);
    private EnumMap<TokenType, Integer> transformTokens = new EnumMap<>(TokenType.class);

    /**
     * Flag used for perfect string imputing in matching or transform patterns
     */
    private boolean perfectFlag = false;

    private String[] ambiguousPatterns = {"A1a2"};

    /***
     * Default constructor; initializes the internal maps to hold the token counts for matching and transform patterns
     */
    C1() {
        for (TokenType t : TokenType.values()) {
            matchingTokens.put(t, 0);
            transformTokens.put(t, 0);
        }
    }

    void setOriginalMatch(String s) {
        this.originalMatch = s;
    }

    void setOriginalMatcher(Matcher matcher) {
        this.originalMatcher = matcher;
        buildReconstructTable();
    }

    void setOriginalForm(Normalizer.Form form) {
        this.originalForm = form;
    }

    void setTargetForm(Normalizer.Form form) {
        this.targetForm = form;
    }

    String getOriginalMatch() {
        return this.originalMatch;
    }

    /***
     * Transforms a string containing legal constructs into a string ready to be compiled into a {@code Pattern} for matching
     * @param s the string pattern to be used for finding a match
     * @return a string ready to be compiled into a {@code Pattern}
     */
    String createPatternForMatching(String s) throws AmbiguousPatternException, InvalidPatternException {
        resetInternals();
        if (Arrays.stream(ambiguousPatterns).parallel().anyMatch(s::contains))
            throw new AmbiguousPatternException("Pattern " + s + " is ambiguous and could lead to non-valid matches");

        //save the original pattern for accurate error message
        baseMatchingPattern = s;
        s.chars()
                .mapToObj(c -> (char) c)
                .forEach(c -> tokenizePattern(c, false));
        String returnString = prepareForCompilation(patternForm, false);

        if (perfectFlag)
            throw new InvalidPatternException("The pattern is missing a perfect match sequence end ('>' character)");
        matchingOrder = transformOrder;
        return returnString;
    }

    /***
     * Transforms a string containing legal constructs into a string ready to be used for transforming a match
     * @param s the string pattern to be used for transforming a match
     * @return a {@code List} of strings to be used to transform a match
     */
    List<String> createPatternForTransforming(String s) {
        resetInternals();
        //save the original pattern for accurate error message
        baseTransformPattern = s;
        s.chars()
                .mapToObj(c -> (char) c)
                .forEach(c -> tokenizePattern(c, true));
        patternForm.forEach(c -> convertTokenToPattern(c, true));

        return transformOrder;
    }

    /***
     * Support method to indicate if a transform is a valid operation based on the previously generated patterns
     * @return Returns true if the transform pattern contains fewer or an equal number of tokens of each type than the matching pattern
     */
    Boolean transformAvailable() {
        List<TokenType> tokenList = new ArrayList<>();
        tokenList.add(ALPHA);
        tokenList.add(ALPHAX);
        tokenList.add(NUM);
        tokenList.add(NUMX);

        for (TokenType t : tokenList) {
            if (transformTokens.get(t) > matchingTokens.get(t)) {
                return false;
            }
        }
        return true;
    }

    String getReconstructString(String groupName) {
        String originalSubstring = reconstructTable.get(groupName);
        if (Normalizer.isNormalized(originalSubstring,targetForm))
            return originalSubstring;
        else
            return Normalizer.normalize(originalSubstring, targetForm);

    }

    /***
     * Builds an error message with the token counts of each input pattern.
     * @return an error message for the {@code TransformPatternTooLongException}
     */
    String getTransformPatternTooLongExceptionMessage() {
        StringBuilder sb = new StringBuilder();
        //Title + headers
        sb.append("The transform pattern contains too many tokens." +
                " The number of tokens in the transform pattern must be equal to" +
                " or less than the number of tokens in the matching pattern. \n");
        sb.append("Matching Pattern: ");
        sb.append(baseMatchingPattern);
        sb.append("\n");
        sb.append("Transform Pattern: ");
        sb.append(baseTransformPattern);
        sb.append("\n");
        sb.append("Token Counts\n");
        //Matching tokens
        sb.append("Match\n");
        sb.append("a" + ": ");
        sb.append(matchingTokens.get(TokenType.ALPHA));
        sb.append(" | ");
        sb.append("A" + ": ");
        sb.append(matchingTokens.get(TokenType.ALPHAX));
        sb.append(" | ");
        sb.append("n" + ": ");
        sb.append(matchingTokens.get(TokenType.NUM));
        sb.append(" | ");
        sb.append("N" + ": ");
        sb.append(matchingTokens.get(TokenType.NUMX));
        //Transformation tokens
        sb.append("\nTransform\n");
        sb.append("a" + ": ");
        sb.append(transformTokens.get(TokenType.ALPHA));
        sb.append(" | ");
        sb.append("A" + ": ");
        sb.append(transformTokens.get(TokenType.ALPHAX));
        sb.append(" | ");
        sb.append("n" + ": ");
        sb.append(transformTokens.get(TokenType.NUM));
        sb.append(" | ");
        sb.append("N" + ": ");
        sb.append(transformTokens.get(TokenType.NUMX));

        return sb.toString();
    }

    /***
     * Method to transform the match and transform pattern inputs into easy to parse list of tokens
     * @param c Character value used to determine the {@code TokenType} token value to add to the pattern
     * @param transform Boolean flag indicating if tokenizing the match or the transform pattern
     */
    private void tokenizePattern(Character c, Boolean transform) {
        if (perfectFlag) {
            if (c.compareTo('>') != 0) {
                patternForm.add(c.toString());
            } else {
                patternForm.add(PEND.name());
                perfectFlag = false;
            }
        } else {
            if (c.compareTo('a') == 0) {
                patternForm.add(ALPHA.name());
                updateMap(ALPHA, transform);
            } else if (c.compareTo('A') == 0) {
                patternForm.add(ALPHAX.name());
                updateMap(ALPHAX, transform);
            } else if (c.compareTo('n') == 0) {
                patternForm.add(NUM.name());
                updateMap(NUM, transform);
            } else if (c.compareTo('N') == 0) {
                patternForm.add(NUMX.name());
                updateMap(NUMX, transform);
            } else if (c.compareTo(' ') == 0) {
                patternForm.add(SPACE.name());
                updateMap(SPACE, transform);
            } else if (c.compareTo('<') == 0) {
                patternForm.add(PSTART.name());
                perfectFlag = true;
            }
        }
    }

    /***
     * Increments the count for a specific token type in either the matching or transforming token map
     * @param type The {@code TokenType} to have their count updated
     * @param transform Boolean flag to indicate if updating the matching or the transform token map
     */
    private void updateMap(TokenType type, Boolean transform) {
        EnumMap<TokenType, Integer> tempMap = transform ? transformTokens : matchingTokens;
        tempMap.put(type, tempMap.get(type) + 1);
    }


    /***
     * Converts a list of tokens into a string compilable into a regex {@code Pattern}
     * @param toCompile the list of string tokens to convert into regex form
     * @param toTransform boolean flag indicating if converting the tokens for a matching or a transform pattern
     * @return the full compilable string pattern
     */
    private String prepareForCompilation(List<String> toCompile, boolean toTransform) {
        StringBuilder converted = new StringBuilder(toTransform ? "" : "^");
        toCompile
                .forEach(c -> converted.append(toTransform ? convertTokenToPattern(c, true) + "," : convertTokenToPattern(c, false)));

        String returnString = converted.toString();
        //remove the trailing comma before returning if it was a transform pattern that was generated
        return toTransform ? returnString.substring(1, returnString.length() - 1) : returnString + "$";
    }

    /***
     * Converts a single token into a {@code Pattern} compilable string
     * @param s a string token to be converted into a compilable regex string
     * @return the string token's equivalent compilable string
     */
    private String convertTokenToPattern(String s, boolean transform) {
        String toAdd = "";

        if (s.equals(ALPHA.name())) {
            aIndex++;
            toAdd = "a" + aIndex;
            transformOrder.add(toAdd);
            return "(?<" + toAdd + ">\\p{L})";
        } else if (s.equals(ALPHAX.name())) {
            bigAIndex++;
            toAdd = "A" + bigAIndex;
            transformOrder.add(toAdd);
            return "(?<" + toAdd + ">\\p{L}*)";
        } else if (s.equals(NUM.name())) {
            nIndex++;
            toAdd = "n" + nIndex;
            transformOrder.add(toAdd);
            return "(?<" + toAdd + ">\\p{N})";
        } else if (s.equals(NUMX.name())) {
            bigNIndex++;
            toAdd = "N" + bigNIndex;
            transformOrder.add(toAdd);
            return "(?<" + toAdd + ">\\p{N}*)";
        } else if (s.equals(SPACE.name())) {
            sIndex++;
            toAdd = "s" + sIndex;
            transformOrder.add(transform ? "~ " : toAdd);
            return "(?<" + toAdd + "> )";
        } else if (s.equals(PSTART.name())) {
            pIndex++;
            toAdd = "p" + pIndex;
            tempPerfectMatch = "~";
            return "(?<" + toAdd + ">";
        } else if (s.equals(PEND.name())) {
            transformOrder.add(transform ? tempPerfectMatch : tempPerfectMatch.toUpperCase());
            tempPerfectMatch = "";
            return ")";
        } else {
            tempPerfectMatch = tempPerfectMatch + escape(s);
            return escape(s);
        }
    }

    private String escape(String s) {
        String[] toEscape = {"/", "\\", "+", "*"};
        boolean needsEscaping = Arrays.stream(toEscape).parallel().anyMatch(s::contains);

        return needsEscaping ? "\\" + s : s;
    }

    /***
     * resets the internal counters and temporary arrays
     */
    private void resetInternals() {
        aIndex = 0;
        nIndex = 0;
        pIndex = 0;
        bigNIndex = 0;
        bigAIndex = 0;
        sIndex = 0;
        patternForm = new ArrayList<>();
        transformOrder = new ArrayList<>();
    }

    private HashMap<String, String> reconstructTable = new HashMap<>();

    private void buildReconstructTable() {
        String[] originalSplit = originalMatch.split(" ");
        for (String match : matchingOrder) {
            //only proceed of there's an actual group to be matched (a, A, n or N tokens)
            if (!match.startsWith("s") && !match.startsWith("p") && !match.startsWith("~")) {
                for(int i=0; i<=originalSplit.length-1;i++){
                    if(originalMatcher.group(match).equals(originalSplit[i].toUpperCase().replaceAll("[^\\P{M}]",""))){
                        reconstructTable.put(match,originalSplit[i]);
                        originalSplit[i] = "";
                        break;
                    }
                }
            }
        }
    }
}



/***
 * This exception is thrown when an ambiguous matching or transform pattern is detected. An ambiguous
 * pattern is one where there is a high risk of a false positive match.
 */
class AmbiguousPatternException extends Exception{
    public AmbiguousPatternException(String message){
        super(message);
    }
}

/***
 * This exception is thrown when:
 * there is an illegal character that is found in a matching or transform pattern;
 * the pattern is misshapen;
 */
class InvalidPatternException extends Exception{
    public InvalidPatternException(String message){super (message);}
}
