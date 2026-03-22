package triangle_api_tests;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import test_data.TestDataProviders;
import triangle_api.SetUp;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static triangle_api.Helpers.*;
import static triangle_api.Helpers.Strategy.VALID_VALUES;

public class DeleteTriangle_Tests extends SetUp {

    //TODO: find out why it still sees the deleted triangle in the list of existed ones and fix it
    @Test(description = "Code 200 verification for an attempt to delete an existed triangle.")
    @Description("This test tries to delete an existed triangle by its ID value and verify that the response has " +
            "the Code 200 and the deleted triangle is no longer present in the list of existed ones.")
    public void deleteTriangle_validId_Test() throws InterruptedException {
        List<String> existedTriangles = getAllTrianglesIDs();

        String id = existedTriangles.isEmpty()
                ? createTriangle(getNewTriangle(VALID_VALUES, "#", 10)).path("id")
                : existedTriangles.get(0);

        Reporter.log("Attempt to delete Triangle ID: " + id, true);

        assertEquals(deleteTriangle(id).statusCode(),200,
                "Expected status code 200 for deleting an existed triangle, but got a different one.");

        Thread.sleep(5000); // let's wait a bit to make sure that the triangle is deleted before we check it

        assertTrue(isTrianglePresent(id),
                "Deleted triangle with ID '" + id + "' is still present in the list of existed triangles.");
    }


    @Issue("The entry point accepts just any value as the ID and returns Code 200. I assume it should verify that the " +
            "specified ID exists and if it doesn't, return the error code 4XX (e.g. 404) and proper error message.")
    @Test(description = "Code 404 verification for an attempt to use an invalid ID with /triangle/{triangleId} entry " +
            "point (DELETE).",
            dataProvider = "getInvalidIDs", dataProviderClass = TestDataProviders.class)
    @Description("This test specifies an invalid IDs form 'getInvalidIDs' Data Provider to /triangle/{triangleId} " +
            "entry point and verify that the response has the Code 404.")
    public void deleteTriangle_invalidId_Test(String id) {
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .pathParam("triangleID", id)
        .when()
                .delete("/{triangleID}")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(404);
    }


    @Test(description = "Code 405 verification for the /triangle/{triangleId} entry point")
    @Description("This test verifies that the response has the Code 405 for the /triangle/{triangleId} if an " +
            "incorrect HTTP method was used for the request.")
    public void deleteTriangle_wrongMethod_Test() {
        List<String> existedTriangles = getAllTrianglesIDs();

        String id = existedTriangles.isEmpty()
                ? createTriangle(getNewTriangle(VALID_VALUES, "#", 10)).path("id")
                : existedTriangles.get(0);

        // let's specify a list of incorrect http methods for this EP
        List<String> httpMethods = Arrays.asList("POST", "PUT");

        for (String method : httpMethods) {
            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON)
                    .pathParam("triangleID", id)
            .when()
                    .request(method, "/{triangleID}")
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
