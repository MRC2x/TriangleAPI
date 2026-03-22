package triangle_api_tests;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Reporter;
import org.testng.annotations.Test;
import test_data.TestDataProviders;
import triangle_api.SetUp;
import triangle_api.Triangle;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static triangle_api.Helpers.*;
import static triangle_api.Helpers.Strategy.*;
import static triangle_api.Utilities.getNewTrianglePayloadMap;

public class AddTriangle_Tests extends SetUp {

    @Test(description = "Code 200 verification for an attempt to add a new triangle with valid sides",
            dataProvider = "getTriangleObjects", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle with sides values received from the 'validSides' data provider" +
            " and verify that the response has the Code 200, same sides values as were specified, and the id.")
    public void addTriangle_validSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        given()
            .log().ifValidationFails(LogDetail.ALL)
            .contentType(ContentType.JSON)
            .body(getNewTrianglePayloadMap(triangle, ";"))
        .when()
            .post("/")
        .then()
            .log().ifValidationFails(LogDetail.ALL)
        .assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("firstSide", equalTo(triangle.getSideA()),
                  "secondSide", equalTo(triangle.getSideB()),
                  "thirdSide", equalTo(triangle.getSideC()),
                  "id", notNullValue());

        Reporter.log("A new triangle with sides: " + triangle.getSideA() + ", " + triangle.getSideB() + ", and " + triangle.getSideC() +
                " was successfully added.", true);
    }

    @Test(description = "Code 422 verification for an attempt to add a new triangle with negative values of the valid sides",
            dataProvider = "getTriangleObjects", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle with negative values of sides and verify that the response has " +
            "the Code 422.")
    public void addTriangle_negativeSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle, ";"))
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

    @Test(description = "Code 422 verification for an attempt to add a new triangle with sum of two sides equals " +
            "to the third one",
            dataProvider = "getTriangleObjects", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle which sum of some two sides equals to the third one and verify " +
            "that the response has the Code 422.")
    public void addTriangle_sumSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle, ";"))
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
            dataProvider = "getTriangleObjects", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle with the sides where the sum of any two is less than the " +
            "third one and verify that the response has the Code 422.")
    public void addTriangle_invalidSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle, ";"))
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
            dataProvider = "getTriangleObjects", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle with the sides where one of them or all of them have a zero " +
            "value and verify that the response has the Code 422.")
    public void addTriangle_SidesWithZero_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle, ";"))
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
            dataProvider = "getTriangleObjects", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle with equilateral sides and verify that the response " +
            "has the Code 200, same sides values as were specified, and the id.")
    public void addTriangle_equilateralSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        Response response =
                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .contentType(ContentType.JSON)
                        .body(getNewTrianglePayloadMap(triangle, ";"))
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
                                "thirdSide", equalTo(triangle.getSideC()),
                                "id", notNullValue() )
                        .extract()
                        .response();

        Reporter.log("A new equilateral triangle ID: " + response.path("id") + " with sides: " + triangle.getSideA() + ", " + triangle.getSideB() + ", and " + triangle.getSideC() +
                " was successfully added.", true);
    }

    @Test(description = "Code 200 verification for an attempt to add a new triangle with isosceles sides",
            dataProvider = "getTriangleObjects", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle with isosceles sides and verify that the response " +
            "has the Code 200.")
    public void addTriangle_isoscelesSides_Test(Triangle triangle) {
        cleanUpTringlesIfNeeded(10);

        Response response =
                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .contentType(ContentType.JSON)
                        .body(getNewTrianglePayloadMap(triangle, ";"))
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
                                "thirdSide", equalTo(triangle.getSideC()),
                                "id", notNullValue() )
                        .extract()
                        .response();
        Reporter.log("A new isosceles triangle ID: " + response.path("id") + " with sides: " + triangle.getSideA() + ", " + triangle.getSideB() + ", and " + triangle.getSideC() +
                " was successfully added.", true);
    }

    @Test(description = "Code 422 verification for an attempt to add a new triangle with no separator in the payload")
    @Description("This test tries to add a new triangle without 'separator' key in the payload and verify that the " +
            "response has the Code 422.")
    public void addTriangle_Payload_noSeparator_Test() {
        deleteOneTringleIfAboveLimit(10);

        Triangle triangle = getNewTriangle(VALID_VALUES, "#", 10);

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle, ""))
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

    @Issue("It seems there's a lack of validation for the separator values in the payload, separator values like ']', '(', or '*', will cause code 500 instead of 422 -> BUG")
    @Test(description = "Code 422 verification for an attempt to add a new triangle with invalid separator values " +
            "in the payload", dataProvider = "getInvalidSeparatorValues", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle with different invalid separator values in the payload and " +
            "verify that the response has the Code 422.")
    public void addTriangle_Payload_invalidSeparatorValues_Test(String payload) {
        deleteOneTringleIfAboveLimit(10);

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

    @Test(description = "Code 200 verification for an attempt to add a new triangle with a custom separator values " +
            "in the payload", dataProvider = "getSeparatorValues", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle with different valid separator values in the payload and " +
            "verify that the response has the Code 200, same sides values as were specified, and the id.")
    public void addTriangle_Payload_validSeparatorValues_Test(String separator) {
        cleanUpTringlesIfNeeded(10);

        Triangle triangle = getNewTriangle(VALID_VALUES, "#", 10);

        Response response =
            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON)
                    .body(getNewTrianglePayloadMap(triangle, separator))
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
        Reporter.log("The triangle with '"+separator+"' in the payload was successfully created. " +
                "\nIt has sides " + triangle.getSideA() + ", " + triangle.getSideB() + ", and " + triangle.getSideC() + " and ID: " +id, true);
    }

    @Test(description = "Code 400 verification for an attempt to add a new triangle with a custom separator values " +
            "in the payload", dataProvider = "getCorruptedPayload", dataProviderClass = TestDataProviders.class)
    @Description("This test tries to add a new triangle with different valid separator values in the payload and " +
            "verify that the response has the Code 400, same sides values as were specified, and the id.")
    public void addTriangle_Payload_corruptedValues_Test(String payload) {
        deleteOneTringleIfAboveLimit(10);

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
        deleteAllTriangles(getAllTrianglesIDs());

        int responseCode = 200;
        // let's try to add a new 11 triangles in a row
        for (int i = 0; i < 11; i++) {
            // for 11th triangle set the expected response code to '422'
            if (i == 10)
                responseCode = 422;

            Triangle triangle = getNewTriangle(VALID_VALUES, "#", 10);

            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON)
                    .body(getNewTrianglePayloadMap(triangle, ";"))
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
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body("")
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

    @Test(description = "Code 400 verification for an attempt to add a new triangle with without valid payload")
    @Description("This test tries to add a new triangle with none empty but invalid payload provided in the request and verify " +
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

        Triangle triangle = getNewTriangle(VALID_VALUES, "#", 10);

        List<String> httpMethods = Arrays.asList("GET", "PUT", "DELETE");

        for (String method : httpMethods) {
            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON).body(getNewTrianglePayloadMap(triangle, ";"))
            .when()
                    .request(method, "/")
            .then()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
            .assertThat()
                    .statusCode(405)
                    .body("error", equalTo("Method Not Allowed"),
                            "message", equalTo("Request method not supported"));
        }
    }

}
