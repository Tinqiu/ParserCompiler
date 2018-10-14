package PN005_C1;



import junit.framework.AssertionFailedError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Stream;

/***
 * PN005 - Move Patterns Between Variables
 * Feature: SRI.603.1.2
 * https://icos-doc.statcan.ca/pages/viewpage.action?pageId=622985634
 */
public class PN005_MovePatternsBetweenVariables_Tests {
    private static String variable1 = "";
    private static String variable2 = "";
    private static ArrayList<String[]> axNxPatterns = new ArrayList<>();
    private static ArrayList<String[]> axNxTextPatterns;
    private static ArrayList<String[]> axNxReOrderPatterns;
    private static ArrayList<String[]> invalidPatterns;

    @BeforeAll
    static void setup() {
        // Initialize conversion table pattern data
//        String[] abc = {"a1 A2 a3","a1 a3 A2"};
//        axNxPatterns.add(abc);

        //axNxPatterns = initTable("TestData/PN005_Example1Scenario1.csv");
//        axNxTextPatterns = initTable("TestData/PN005_Example2Scenario2.csv");
//        axNxReOrderPatterns = initTable("TestData/PN005_Example3.csv");
//        invalidPatterns = initTable("TestData/PN005_InvalidPatterns.csv");
    }

    @ParameterizedTest()
    @MethodSource("pn005_args")
    @DisplayName("PN005 - Move Patterns Between Variables")
    void PN005Tests(String[] input, String[] output, String[] table) {

        variable1 = input[0];
        variable2 = input[1];
        PN005_MovePatternsBetweenVariables pn005 = new PN005_MovePatternsBetweenVariables(variable1, variable2, table);

        pn005.process();
        String[] results = pn005.getResults();
        Assertions.assertAll("PN005",
                () -> Assertions.assertEquals(output[0], results[0]),
                () -> Assertions.assertEquals(output[1], results[1])
        );
    }

    private static Stream<Arguments> pn005_args() {
        return Stream.of(
                // ***** Invalid (malformed) patterns ***** //
//                Arguments.of(
//                        // [<H<ELLO>> N1] => nested <>
//                        new String[]{"hello 123", ""},
//                        new String[]{"hello 123", ""},
//                        invalidPatterns
//                ),
//                Arguments.of(
//                        // [<ISLAND> BAY N1] => text not surround by <>
//                        new String[]{"ISLAND BAY 123", ""},
//                        new String[]{"ISLAND BAY 123", ""},
//                        invalidPatterns
//                ),
//                Arguments.of(
//                        // [A1a2] => ambiguous pattern, one or more alpha followed by one alpha
//                        new String[]{"abcdefg", ""},
//                        new String[]{"abcdefg", ""},
//                        invalidPatterns
//                ),
//                Arguments.of(
//                        // [N1 N2] => invalid VAR2PATTERN, contains construct not in VAR1PATTERN
//                        new String[]{"555 444", ""},
//                        new String[]{"555 444", ""},
//                        invalidPatterns
//                ),

                //  ***** Accent tests ***** //
                Arguments.of(
                        // One or more alpha, with accents and var2 empty
                        new String[]{"x ábÇ z", ""},
                        new String[]{"", "x z ábÇ"},
                        new String[]{"a1 A2 a3","a1 a3 A2"}
                ),

                Arguments.of(
                        // One or more alpha, with accents and var2 != var1
                        new String[]{"x ábÇ z", "ÇĎ"},
                        new String[]{"", "ÇĎ x z ábÇ"},
                        new String[]{"a1 A2 a3","a1 a2 A3"}
                ),
                Arguments.of(
                        // One or more alpha, with decomposed accents and var2 empty
                        new String[]{"x \u0061\u0301b\u0043\u0327 z", ""},
                        new String[]{"", "x z ábÇ"},
                        new String[]{"a1 A2 a3","a1 a2 A3"}
                ),
                Arguments.of(
                        // One or more alpha, with decomposed accents and var2 != var1
                        new String[]{"x \u0061\u0301b\u0043\u0327 z", "\u0061\u0301"},
                        new String[]{"", "\u0061\u0301 x z \u0061\u0301b\u0043\u0327"},
                        new String[]{"a1 A2 a3","a1 a2 A3"}
                ),
                Arguments.of(
                        // single alpha, with accents and var2 empty
                        new String[]{"á 1 b 2 Ç 3", ""},
                        new String[]{"", "á1b2Ç3"},
                        new String[]{"a1 n1 A2 n2 a3 n3","a1n2A2n2a3n3"}
                ),
                Arguments.of(
                        // single alpha, with accents and var2 != var1
                        new String[]{"á1b 2Ç3", "Ö"},
                        new String[]{"", "Ö á1b2Ç3"},
                        new String[]{"a1n1a2 n2a3n3","a1n2a2n2a3n3"}
                ),
                Arguments.of(
                        // single alpha, with decomposed accents and var2 empty
                        new String[]{"\u0061\u03011b 2\u0043\u03273", ""},
                        new String[]{"", "á1b2Ç3"},
                        new String[]{"a1n1a2 n2a3n3","a1n2a2n2a3n3"}
                ),
                Arguments.of(
                        // single alpha, with decomposed accents and var2 != var 1
                        new String[]{"\u0061\u03011b 2\u0043\u03273", "\u0061\u0301"},
                        new String[]{"", "\u0061\u0301 \u0061\u03011b2\u0043\u03273"},
                        new String[]{"a1n1a2 n2a3n3","a1n2a2n2a3n3"}
                ),
                Arguments.of(
                        // exact match, with accents
                        new String[]{"611 boul. Alexandre-Taché, P.ö. Böx 245 , Gatineau, QC", ""},
                        new String[]{"611 boul. Alexandre-Taché,  , Gatineau, QC", "PO Box 245"},
                        new String[]{"<P.O. BOX> N1","<PO Box> N1"}
                ),

                Arguments.of(
                        // var2 empty
                        new String[]{"P.O. Box 245 Ottawa ON K1K 3R4", ""},
                        new String[]{" Ottawa ON K1K 3R4", "PO Box 245"},
                        new String[]{"<P.O. BOX> N1","<PO Box> N1"}
                ),

                Arguments.of(
                        // var2 <> table(var2pattern) of word(s) in var1
                        new String[]{"CP 245 Ottawa ON K1K 3R4", "PO Box 623"},
                        new String[]{" Ottawa ON K1K 3R4", "PO Box 623 PO Box 245"},
                        new String[]{"<CP> N1","<PO Box> N1"}
                ),

                Arguments.of(
                        // var2 empty
                        new String[]{"G1q 1q9 76B", ""},
                        new String[]{" 76B", "G1q1q9"},
                        new String[]{"a1n1a2 n2a3n3", "a1n1a2n2a3n3"}
                )
//                Arguments.of(
//                        // exact match, with decomposed accents
//                        new String[]{"P.\u006f\u0308. B\u006f\u0308x 245", ""},
//                        new String[]{"", "PO Box 245"},
//                        new String[]{"<P.O. BOX> N1","<PO Box> N1"}
//                ),
//                Arguments.of(
//                        // exact match
//                        new String[]{"É ü D--Ö/G44!*'  5", ""},
//                        new String[]{"", "É ü dog  5"},
//                        new String[]{"a1 a2 <D--O/G44!*'>  n1", "a1 a2 <dog>  n1"}
//                ),
//
//                Arguments.of(
//                        // exact match with isolated diacritics
//                        new String[]{"É ü D--Ö/G44!*' \u0308\u0301 5", ""},
//                        new String[]{"", "É ü dog  5"},
//                        new String[]{"a1 a2 <D--O/G44!*'>  n1", "a1 a2 <dog>  n1"}
//                ),
//
//
//                // ***** Supplied Example #1, axnx pattern ***** //
//                Arguments.of(
//                        // var2 empty
//                        new String[]{"123 Main ST Ottawa ON K1K 3R4", ""},
//                        new String[]{"123 Main ST Ottawa ON ", "K1K3R4"},
//                        new String[]{"a1n1a2 n2a3n3", "a1n1a2n2a3n3"}
//                ),
////                Arguments.of(
////                        // var2 == table(var2pattern) of word(s) in var1
////                        new String[]{"123 Main ST Ottawa ON K1K3R4", "K1K3R4"},
////                        new String[]{"123 Main ST Ottawa ON ", "K1K3R4"},
////                        new String[]{"a1n1a2 n2a3n3", "a1n1a2n2a3n3"}
////                ),
//                Arguments.of(
//                        // var2 <> table(var2pattern) of word(s) in var1
//                        new String[]{"123 Main ST Ottawa ON K1K 3R4", "K1K3R5"},
//                        new String[]{"123 Main ST Ottawa ON ", "K1K3R5 K1K3R4"},
//                        new String[]{"a1n1a2 n2a3n3", "a1n1a2n2a3n3"}
//                ),

                // ***** Supplied Example #2, <text> Nx pattern ***** //

//                Arguments.of(
//                        // var2 == table(var2pattern) of word(s) in var1
//                        new String[]{"PO Box 245 Ottawa ON K1K3R4", "PO Box 245"},
//                        new String[]{" Ottawa ON K1K3R4", "PO Box 245"},
//                        axNxTextPatterns
//                ),


////                // ***** Supplied Example #3, axnx pattern ***** //
////                Arguments.of(
////                        // var2 empty
////                        new String[]{"1 A 2 B 3 C 4 D F", ""},
////                        new String[]{"1 A 2 B 3 C  F", "D-4"},
////                        axNxReOrderPatterns
////                ),
////                Arguments.of(
////                        // var2 <> table(var2pattern) of word(s) in var1
////                        new String[]{"1 A 2 B 3 C 4 D F", "D 4"},
////                        new String[]{"1 A 2 B 3 C  F", "D 4 D-4"},
////                        axNxReOrderPatterns
////                ),

                // ***** Supplied Scenario 1, axnx pattern ***** //

//
//                // ***** Supplied Scenario 2, <text>  N1 pattern ***** //
//                Arguments.of(
//                        // var2 <> table(var2pattern) of word(s) in var1
//                        new String[]{"Casier postal 123 Creek 23", "Sucker Creek 23"},
//                        new String[]{" Creek 23", "Sucker Creek 23 PO Box 123"},
//                        axNxTextPatterns
//                ),
//                Arguments.of(
//                        // var2 <> table(var2pattern) of word(s) in var1
//                        new String[]{"Factory Island 1", "Factory Island 1"},
//                        new String[]{"Factory ", "Factory Island 1 ISL 1"},
//                        axNxTextPatterns
//                ),
//
//                // ***** Supplied Scenario 3 ***** //
//                Arguments.of(
//                        // conditions not met
//                        new String[]{"Kettle 12 Point 44", "Kettle Point 44"},
//                        new String[]{"Kettle 12 ", "Kettle Point 44 Pt 44"},
//                        axNxTextPatterns
//                ),
//                Arguments.of(
//                        new String[]{"", ""},
//                        new String[]{"", ""},
//                        axNxPatterns
//                ),
//                Arguments.of(
//                        new String[]{"Tu ne fais pas 17 de sens",""},
//                        new String[]{"Tu ne fais pas ","de sens 17"},
//                        axNxTextPatterns
//                )

        );
    }

    /*
    @ParameterizedTest()
    @MethodSource("pn005_match_args")
    void testMatch(String subPhrase, String pattern) {
        PN005_MovePatternsBetweenVariables pn005 = new PN005_MovePatternsBetweenVariables(variable1, variable2, null);
        pn005.subPhrase = subPhrase;
        pn005.var1_pattern = pattern;

        boolean isMatch = pn005.matchToPattern();

        Assertions.assertAll("PN005",
                () -> Assertions.assertTrue(isMatch)
        );
    }


    private static Stream<Arguments> pn005_match_args() {
        return Stream.of(
                Arguments.of(
                        "K1K 3R4",
                        "a1n1a2 n2a3n3"
                ),
                Arguments.of(
                        "P.O. BOX",
                        "<P.O. BOX>"
                ),
                Arguments.of(
                        "P.O. BOX 245",
                        "<P.O. BOX> N1"
                ),
                Arguments.of(
                        "  8 245  123",
                        "  N1 N2  N3"
                ),
                Arguments.of(
                        "Hell0 doggy 123 N1 N2 N3",
                        "<Hell0 doggy 123 N1 N2 N3>"
                ),
                Arguments.of(
                        "Hell0 doggy 123",
                        "A1N1 A2 N2"
                ),
                Arguments.of(
                        "K1K 3R4",
                        "a1n1a2 n2a3n3"
                ),
                Arguments.of(
                        "j2b-6u4",
                        "A1N1A2<->N2A3N3"
                ),
                Arguments.of(
                        "1 b",
                        "n1 a1"
                ),
                Arguments.of(
                        "ISLAND 12345",
                        "<ISLAND> N1"
                ),
                Arguments.of(
                        "1 A 2 B 3 C 4 D F",
                        "n1 a1 N2 A2 n4 a4 N5 a5 a6"
                ),
                Arguments.of(
                        "    ",
                        "    "
                ),
                Arguments.of(
                        "",
                        ""
                )
        );
    }
    */

    private static ArrayList<String[]> initTable(String path) {
        ArrayList<String[]> table = new ArrayList<>();
        InputStream csv = ClassLoader.getSystemClassLoader().getResourceAsStream(path);

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(csv))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                table.add(new String[]{values[0], values[1]});
            }
        } catch (Exception e) {
            throw new AssertionFailedError("Error loading table from " + path);
        }

        return table;
    }

}

