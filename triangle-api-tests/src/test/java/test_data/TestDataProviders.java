package test_data;

import org.testng.annotations.DataProvider;
import triangle_api.Helpers;
import triangle_api.Triangle;

import java.lang.reflect.Method;

import static triangle_api.Helpers.Strategy.*;
import static triangle_api.Helpers.Strategy.INVALID_VALUES;
import static triangle_api.Helpers.Strategy.VALID_VALUES;
import static triangle_api.Helpers.genSides;
import static triangle_api.Helpers.getNewTriangle;
import static triangle_api.Utilities.getNewTrianglePayloadMap;

public class TestDataProviders {

    /** This is a universal Data Provider which generates sides values depending on the test is used (defined by the test name).
     *  It returns a 2D array where the 1st three values will be >0 and <1 and have two digits after the comma, 2nd
     *  three value will be >0 and <1 and have one after the comma, and the 3rd three values  will be >0 and <1 and
     *  have zero after the comma. Then, generated values will be increased by 10, and the number of digits after the
     *  comma follows the same way, e.i. 1st three values will be >1 and <10 and have two digits after the comma
     *  and so on.
     *
     * @param test - name of the test
     * @return 2D array with 3 values of double in 12 rows
     */
    @DataProvider(name = "getSides")
    public static Object[][] getSides(Method test) {

        Object[][] data = new Object[12][3];
        String pattern = "#.##";
        int bound = 1;
        int j = 0;
        Helpers.Strategy strategy = switch (test.getName()) {
            case "addTriangle_equilateralSides_Test" -> EQUILATERAL_VALUES;
            case "addTriangle_isoscelesSides_Test" -> ISOSCELES_VALUES;
            case "addTriangle_sumSides_Test" -> SUM_VALUES;
            case "addTriangle_invalidSides_Test" -> INVALID_VALUES;
            default -> VALID_VALUES;
        };

        for (int i = 0; i < 12; i++) {

            double[] res = genSides(strategy, pattern, bound);
            // a negative sides test special block
            if (test.getName().equals("addTriangle_negativeSides_Test")) {
                if(j == 3) {
                    j = 0;
                    res[0] *= -1;
                    res[1] *= -1;
                    res[2] *= -1;
                } else {
                    res[j] *= -1;
                    j++;
                }
            }
            // a zero sides test special block
            if (test.getName().equals("addTriangle_SidesWithZero_Test")) {
                if (j == 3) {
                    j = 0;
                    res[0] *= 0;
                    res[1] *= 0;
                    res[2] *= 0;
                } else {
                    res[j] *= 0;
                    j++;
                }
            }
            data[i][0] = res[0];
            data[i][1] = res[1];
            data[i][2] = res[2];

            pattern = pattern.substring(0, pattern.length() - 1);

            if (i % 3 == 2) {
                pattern = "#.##";
                bound = bound * 10;
            }
        }
        return data;
    }

    /** This Data Provider creates Triangle objects based on the sides values from 'getSides' Data Provider.
     *
     * @param test - name of the test
     * @return 2D array with 1 value of Triangle object in 12 rows
     */
    @DataProvider(name = "getTriangleObjects")
    public static Object[][] getTriangleObjects(Method test) {
        Object[][] data = getSides(test);
        Object[][] triangles = new Object[data.length][1];

        for (int i = 0; i < data.length; i++) {
            double a = (double) data[i][0];
            double b = (double) data[i][1];
            double c = (double) data[i][2];
            triangles[i][0] = new Triangle(null, a, b, c, 0.0, 0.0);
        }
        return triangles;
    }

    /** This Data Provider creates TriangleRequest payloads with invalid separators.
     *
     * @return 2D array with 1 value of TriangleRequest payload in 7 rows
     */
    @DataProvider(name = "getInvalidSeparatorValues")
    public final Object[][] getInvalidSeparatorValues() {
        return new Object[][] {
                {"{\"separator\": \".\", \"input\": \"3;4;5\"}"},
                {"{\"separator\": \"A\", \"input\": \"3;4;5\"}"},
                {"{\"separator\": \"A\", \"input\": \"3a4a5\"}"},
                {"{\"separator\": \"\", \"input\": \"3;4;5\"}"},
                {"{\"separator\": \" \", \"input\": \"3_4_5\"}"},
                {"{\"separator\": \"[\", \"input\": \"3[4[5\"}"}, //<- this will cause error code 500 instead of 422
                {"{\"separator\": \")\", \"input\": \"3)4)5\"}"}, //<- this will cause error code 500 instead of 422
                {"{\"separator\": \"*\", \"input\": \"3*4*5\"}"}  //<- this will cause error code 500 instead of 422
        };
    }

    /** This is Data Provider for the addTriangle_Payload_validSeparatorValues_Test */
    @DataProvider(name = "getSeparatorValues")
    public final Object[][] getSeparatorValues() {
        return new Object[][] {
                {"!"},
                {":"},
                {"A"},
                {"a"},
                {" "},
                {"_"},
        };
    }

    /** This is Data Provider for the addTriangle_Payload_validSeparatorValues_Test */
    @DataProvider(name = "getCorruptedPayload")
    public final Object[][] getCorruptedPayload() {
        return new Object[][] {
                {"{\"separator\": , \"input\": \"3;4;5\"}"},
                {"{\"separator\" \";\", \"input\": \"3;4;5\"}"},
                {"{\"separator\": \";\" \"input\": \"3;4;5\"}"},
                {"\";\", \"input\": \"3;4;5\"}"},
                {"\"output\": \"3;4;5\"}"},
                {"\"\": \"3;4;5\"}"},
                {"\"3;4;5\"}"},
        };
    }

}
