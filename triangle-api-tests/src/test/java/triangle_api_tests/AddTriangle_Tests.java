package triangle_api_tests;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import triangle_api.SetUp;
import triangle_api.Triangle;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static triangle_api.Helpers.*;
import static triangle_api.Helpers.Strategy.*;
import static triangle_api.Utilities.getNewTrianglePayloadMap;

public class AddTriangle_Tests extends SetUp {

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
        Strategy strategy = switch (test.getName()) {
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


    @Test(description = "Code 200 verification for an attempt to add a new triangle with valid sides",
            dataProvider = "getTriangleObjects")
    @Description("This test tries to add a new triangle with sides values received from the 'validSides' data provider" +
            " and verify that the response has the Code 200, same sides values as were specified, and the id.")
    public void addTriangle_validSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        Response response =
                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .contentType(ContentType.JSON)
                        .body(getNewTrianglePayloadMap(triangle))
                .when()
                       .post("/")
                .then()
                       .log()
                       .ifValidationFails(LogDetail.ALL)
                .assertThat()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body("firstSide", equalTo(triangle.getSideA()),
                                "secondSide", equalTo(triangle.getSideB()),
                                "thirdSide", equalTo(triangle.getSideC()) )
                        .extract()
                        .response();

        String id = response.path("id");

        Reporter.log("A new triangle ID: " + id + " with sides: " + triangle.getSideA() + ", " + triangle.getSideB() + ", and " + triangle.getSideC() +
                " was successfully added", true);
    }


    @Test(description = "Code 422 verification for an attempt to add a new triangle with negative values of the valid sides",
            dataProvider = "getTriangleObjects")
    @Description("This test tries to add a new triangle with negative values of sides and verify that the response has " +
            "the Code 422.")
    public void addTriangle_negativeSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle))
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(422)
                .body("error", equalTo("Unprocessable Entity"),
                        "message", equalTo("Cannot process input") );
    }


    @Test(description = "Code 422 verification for an attempt to add a new triangle which sum of some two sides equals " +
            "to the third one",
            dataProvider = "getTriangleObjects")
    @Description("This test tries to add a new triangle which sum of some two sides equals to the third one and verify " +
            "that the response has the Code 422.")
    public void addTriangle_sumSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle))
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(422)
                .body("error", equalTo("Unprocessable Entity"),
                        "message", equalTo("Cannot process input") );
    }


    @Test(description = "Code 422 verification for an attempt to add a new triangle with the sides where the " +
            "sum of any two is less than the third one.",
            dataProvider = "getTriangleObjects")
    @Description("This test tries to add a new triangle with the sides where the sum of any two is less than the " +
            "third one and verify that the response has the Code 422.")
    public void addTriangle_invalidSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle))
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(422)
                .body("error", equalTo("Unprocessable Entity"),
                        "message", equalTo("Cannot process input") );
    }


    @Test(description = "Code 422 verification for an attempt to add a new triangle with the sides where one of them " +
            "or all of them are zero.",
            dataProvider = "getTriangleObjects")
    @Description("This test tries to add a new triangle with the sides where one of them or all of them have a zero " +
            "value and verify that the response has the Code 422.")
    public void addTriangle_SidesWithZero_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle))
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(422)
                .body("error", equalTo("Unprocessable Entity"),
                        "message", equalTo("Cannot process input") );
    }


    @Test(description = "Code 200 verification for an attempt to add a new triangle with equilateral sides",
            dataProvider = "getTriangleObjects")
    @Description("This test tries to add a new triangle with equilateral sides and verify that the response " +
            "has the Code 200, same sides values as were specified, and the id.")
    public void addTriangle_equilateralSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        Response response =
                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .contentType(ContentType.JSON)
                        .body(getNewTrianglePayloadMap(triangle))
                .when()
                        .post("/")
                .then()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                .assertThat()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body("firstSide", equalTo(triangle.getSideA()),
                                "secondSide", equalTo(triangle.getSideB()),
                                "thirdSide", equalTo(triangle.getSideC()) )
                        .extract()
                        .response();

        String id = response.path("id");

        Reporter.log("A new equilateral triangle ID: " + id + " with sides: " + triangle.getSideA() + ", " + triangle.getSideB() + ", and " + triangle.getSideC() +
                " was successfully added.", true);
    }


    @Test(description = "Code 200 verification for an attempt to add a new triangle with isosceles sides",
            dataProvider = "getTriangleObjects")
    @Description("This test tries to add a new triangle with isosceles sides and verify that the response " +
            "has the Code 200, same sides values as were specified, and the id.")
    public void addTriangle_isoscelesSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        Response response =
                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .contentType(ContentType.JSON)
                        .body(getNewTrianglePayloadMap(triangle))
                .when()
                        .post("/")
                .then()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                .assertThat()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body("firstSide", equalTo(triangle.getSideA()),
                                "secondSide", equalTo(triangle.getSideB()),
                                "thirdSide", equalTo(triangle.getSideC()) )
                        .extract()
                        .response();

        String id = response.path("id");

        Reporter.log("A new isosceles triangle ID: " + id + " with sides: " + triangle.getSideA() + ", " + triangle.getSideB() + ", and " + triangle.getSideC() +
                " was successfully added.", true);
    }


    @Test(description = "Code 200 verification for an attempt to add a new triangle with no separator in the payload")
    @Description("This test tries to add a new triangle without 'separator' key in the payload and verify that the " +
            "response has the Code 200, same sides values as were specified, and the id.")
    public void addTriangle_Payload_noSeparator_Test() {
        deleteOneTringleIfAboveLimit(0);

        // let's get a valid sides for a triangle
        double[] sides = genSides(VALID_VALUES, "#", 10);
        // specify the payload without 'separator' part
        String payload = "{\"input\": \""+sides[0]+";"+sides[1]+";"+sides[2]+"\"}";

        Response response =
                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post("/")
                .then()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                .assertThat()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body("firstSide", equalTo(sides[0]),
                                "secondSide", equalTo(sides[1]),
                                "thirdSide", equalTo(sides[2]) )
                        .extract()
                        .response();

        String id = response.path("id");

        Reporter.log("A new triangle without separator in the payload was successfully added. " +
                "\nIt has the following sides: "+sides[0]+", "+sides[1]+", and "+sides[2]+" " +
                "and its ID is: "+id, true);
    }


    /** This is Data Provider for the addTriangle_Payload_invalidSeparatorValues_Test */
     @DataProvider(name = "getInvalidSeparatorValues")
    public final Object[][] getInvalidSeparatorValues() {
        return new Object[][] {
                {"{\"separator\": \".\", \"input\": \"3;4;5\"}"},
                {"{\"separator\": \".\", \"input\": \"3.4.5\"}"},
                {"{\"separator\": \"$\", \"input\": \"3$4$5\"}"},
                {"{\"separator\": \"A\", \"input\": \"3;4;5\"}"},
                {"{\"separator\": \"A\", \"input\": \"3a4a5\"}"},
                {"{\"separator\": \"\", \"input\": \"3;4;5\"}"},
                {"{\"separator\": \" \", \"input\": \"3_4_5\"}"},
                {"{\"separator\": \"[\", \"input\": \"3[4[5\"}"}, //<- this will cause error 500 instead of 422
                {"{\"separator\": \")\", \"input\": \"3)4)5\"}"}, //<- this will cause error 500 instead of 422
                {"{\"separator\": \"*\", \"input\": \"3*4*5\"}"}, //<- this will cause error 500 instead of 422
                {"{\"terminator\": \";\", \"input\": \"3;4;5\"}"} //<- 'separator' key could be renamed to anything
                                                                  // and the payload will be accepted
        };
    }

    @Test(description = "Code 422 verification for an attempt to add a new triangle with invalid separator values " +
            "in the payload", dataProvider = "getInvalidSeparatorValues")
    @Description("This test tries to add a new triangle with different invalid separator values in the payload and " +
            "verify that the response has the Code 422.")
    public void addTriangle_Payload_invalidSeparatorValues_Test(String payload) {
        // let's check if there's a room for a  one new triangles and if not, delete one existed triangle
        List<String> existedTriangles = getAllTriangles();

        if (existedTriangles.size() >= 10) {
            deleteOneTriangle(existedTriangles.get(0));
        }

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(payload)
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(422)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Unprocessable Entity"),
                        "message", equalTo("Cannot process input") );
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

    @Test(description = "Code 200 verification for an attempt to add a new triangle with a custom separator values " +
            "in the payload", dataProvider = "getSeparatorValues")
    @Description("This test tries to add a new triangle with different valid separator values in the payload and " +
            "verify that the response has the Code 200, same sides values as were specified, and the id.")
    public void addTriangle_Payload_validSeparatorValues_Test(String separator) {
        cleanUpTringlesIfNeeded(10);

        // let's get a valid sides for a triangle
        double[] sides = genSides(VALID_VALUES, "#", 10);

        // specify the payload without 'separator' part
        String payload = "{\"separator\": \""+separator+"\", \"input\": " +
                "\""+sides[0]+separator+sides[1]+separator+sides[2]+"\"}";

        Response response =
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(payload)
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("firstSide", equalTo(sides[0]),
                        "secondSide", equalTo(sides[1]),
                        "thirdSide", equalTo(sides[2]) )
                .extract()
                .response();

        String id = response.path("id");
        Reporter.log("The triangle with '"+separator+"' in the payload was successfully created. " +
                "\nIt has sides "+sides[0]+", "+sides[1]+", and "+sides[2]+" and ID: " +id, true);
    }


    /** This is Data Provider for the addTriangle_Payload_validSeparatorValues_Test */
    @DataProvider(name = "getCorruptedPayload")
    public final Object[][] getCorruptedPayload() {
        return new Object[][] {
                {"{\"separator\": , \"input\": \"3;4;5\"}"},
                {"{\"separator\" \";\", \"input\": \"3;4;5\"}"},
                {"{\"separator\": \";\" \"input\": \"3;4;5\"}"},
                {"\";\", \"input\": \"3;4;5\"}"},
                {"\"output\": \"3;4;5\"}"}, //<- this will cause error 422 instead of 400
                {"\"\": \"3;4;5\"}"}, //<- this will cause error 422 instead of 400
                {"\"3;4;5\"}"}, // <- this will be accepted as a valid payload
        };
    }

    @Issue("#1: In some cases, we have 'Code 400 - bad Request' in other cases 'Code 422 - Unprocessable Entity' though " +
            "in all cases the payload about the same way inappropriate. Need criteria to distinguish these cases " +
            "or it's a bug to fix.")
    @Issue("#2: A payload without any keys is accepted e.i. '{5;6;8}' it's either intentional just not described " +
            "or it's a bug.")
    @Test(description = "Code 400 verification for an attempt to add a new triangle with a custom separator values " +
            "in the payload", dataProvider = "getCorruptedPayload")
    @Description("This test tries to add a new triangle with different valid separator values in the payload and " +
            "verify that the response has the Code 400, same sides values as were specified, and the id.")
    public void addTriangle_Payload_corruptedValues_Test(String payload) {
        // let's check if there's a room for one new triangles and if not, delete one existed triangle
        List<String> existedTriangles = getAllTriangles();

        if (existedTriangles.size() >= 10) {
            deleteOneTriangle(existedTriangles.get(0));
        }

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(payload)
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Bad Request") );
    }


    @Issue("Unlike it was specified, the service allows to add 11 triangles, not 10 -> BUG")
    @Test(description = "Code 422 verification for an attempt to add the 11th triangle in a row")
    @Description("This test tries to add 11 new triangles in a row and verify that the 11th will be rejected " +
            "and the response will show code 422.")
    public void addTriangles_aboveLimit_Test() {
        // let's delete all existed triangles to add 11 new ones
        deleteAllTriangles(getAllTriangles());

        int responseCode = 200;
        // let's try to add a new 11 triangles in a row
        for (int i = 0; i < 11; i++) {
            // for 11th triangle set the expected response code to '422'
            if (i == 10) {
                responseCode = 422;
            }
            double[] sides = genSides(VALID_VALUES,"#", 100);

            String payload = "{\"separator\": \";\", \"input\": \""+sides[0]+";"+sides[1]+";"+sides[2]+"\"}";

            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON)
                    .body(payload)
            .when()
                    .post("/")
            .then()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
            .assertThat()
                    .statusCode(responseCode);

            Reporter.log("Triangle #"+(i+1)+" was successfully added.", true);
        }
    }


    @Test(description = "Code 400 verification for an attempt to add a new triangle with with an empty payload")
    @Description("This test tries to add a new triangle with an empty payload and verify that the response " +
            "has the Code 400 and a proper error message.")
    public void addTriangle_emptyPayload_Test() {

        String payload = "";

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(payload)
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Bad Request"),
                        "message", containsString("Required request body is missing"));
    }


    @Test(description = "Code 400 verification for an attempt to add a new triangle with without payload")
    @Description("This test tries to add a new triangle with no empty payload provided in the request and verify " +
            "that the response has the Code 400 and a proper error message.")
    public void addTriangle_noPayload_Test() {

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Bad Request"),
                        "message", containsString("Required request body is missing"));
    }


    @Test(description = "Code 405 verification for the /triangle entry point")
    @Description("This test verifies that the response has the Code 405 for the /triangle if an incorrect HTTP method " +
            "was used for the request.")
    public void addTriangle_wrongMethod_Test() {
        cleanUpTringlesIfNeeded(10);

        // let's get a valid sides for a triangle
        double[] sides = genSides(VALID_VALUES, "#", 10);
        // specify the payload without 'separator' part
        String payload = "{\"input\": \""+sides[0]+";"+sides[1]+";"+sides[2]+"\"}";

        // let's specify a list of incorrect http methods for this EP
        List<String> httpMethods = Arrays.asList("GET", "PUT", "DELETE");

        for (String method : httpMethods) {
            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON).body(payload)
            .when()
                    .request(method, "/")
            .then()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
            .assertThat()
                    .statusCode(405)
                    .body("error", equalTo("Method Not Allowed"),
                            "message", equalTo("Request method '"+method+"' not supported"));
        }
    }


}
