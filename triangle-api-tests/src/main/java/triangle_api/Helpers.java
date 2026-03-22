package triangle_api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.Reporter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static triangle_api.Utilities.getNewTrianglePayloadMap;

public class Helpers extends SetUp {
    public static String VALID_TOKEN = "9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564";
    public static String INVALID_TOKEN = "invalid_personal_token_value";

    /** This method returns a list of IDs of all existed triangles or an empty list if no any IDs were found.
     *
     * @return - list of IDs or empty list
     */
    public static List<Triangle> getAllTriangles() {
        Response response =
                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .contentType(ContentType.JSON)
                .when()
                        .get("/all")
                .then()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .extract()
                        .response();

        if (response.getStatusCode() == 200) {
            return response.jsonPath().getList("", Triangle.class);                        
        } else {
            Reporter.log("Failed to get the list of triangles, status code: " + response.getStatusCode(), true);
            throw new IllegalStateException("Failed to get the list of triangles, status code: " + response.getStatusCode());
        }
    }


    /** This method returns an array with side values of the triangle which belongs to the specified ID.
     *
     * @return - array of double with three sides.
     */
    public static Triangle getTriangle(String id) {
        Response response =
                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .contentType(ContentType.JSON)
                        .pathParam("triangleID", id)
                .when()
                        .get("/{triangleID}")
                .then()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .extract()
                        .response();

        if (response.getStatusCode() == 200) {
            Reporter.log("The triangle with ID: " + id + " was found", true);

            return response.jsonPath().getObject("", Triangle.class);
        } else if (response.getStatusCode() == 404) {
            Reporter.log("The triangle with ID: " + id + " was not found", true);
            throw new IllegalStateException("The triangle with ID: " + id + " was not found.");
        } else {
            Reporter.log("Failed to get the triangle with ID: " + id + ", status code: " + response.getStatusCode(), true);
            throw new IllegalStateException("Failed to get the triangle with ID: " + id + ", status code: " + response.getStatusCode());
        }
    }


    public static List<String> getAllTrianglesIDs() {
        return getAllTriangles().stream()
                .map(Triangle::getId)
                .toList();
    }
    
    public static boolean isTrianglePresent(String id) {
        return getAllTrianglesIDs().contains(id);
    }


    /** This method deletes each triangle which ID is present in the provided list.
     *
     * @param listOfIDs - a list with triangles IDs
     */
    public static void deleteAllTriangles(List<String> listOfIDs ) {
        if (!listOfIDs.isEmpty()) {
            for (String id : listOfIDs) {
                Response response = deleteTriangle(id);

                if (response.getStatusCode() == 200) {
                     Reporter.log("The triangle with ID " + id + " was deleted.", true);
                } else {
                    Reporter.log("Failed to delete the triangle with ID: " + id + ", status code: " + response.getStatusCode() + "body: " + response.getBody(), true);
                    throw new IllegalStateException("Failed to delete the triangle with ID: " + id + ", status code: " + response.getStatusCode());
                }
            }
        } else {
            Reporter.log("The specified list of IDs is empty, please provide a list with valid IDs", true);
        }
    }


    /** This method deletes the triangle of the specified ID and returns the status code of the response.
     *
     * @param id - ID of the triangles which should be deleted
     * @return status code of the response
     */
    public static Response deleteTriangle(String id) {
       return
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
                    .extract()
                    .response();
    }

    /** This method creates a new triangle if specified sides are valid for a real triangle,
     *  and returns the ID of the created triangle.
     *
     * @param firstSide - a first side of the triangle
     * @param secondSide - a second side of the triangle
     * @param thirdSide - a third side of the triangle
     * @return the ID of created triangle
     */
    public static String createTriangle(double firstSide, double secondSide, double thirdSide) {
        if (getAllTriangles().size() < 10)
            throw new IllegalStateException("The service allows only 10 triangles and all 10 are already present. Please delete some triangle to add a new one.");

        RequestSpecification helpersSpec = new RequestSpecBuilder()
                .addHeader("X-User", "9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564")
                .setBaseUri(baseURI)
                .setBasePath("/triangle/")
                .build();

        JsonConfig jsonConfig = JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE);

        RestAssuredConfig helperConfig = RestAssured.config()
                .jsonConfig(jsonConfig)
                .encoderConfig(EncoderConfig
                        .encoderConfig()
                        .defaultContentCharset("UTF-8"));

        boolean sidesPositive = firstSide > 0
                && secondSide > 0
                && thirdSide > 0;

        boolean sidesValid = firstSide + secondSide > thirdSide
                && firstSide + thirdSide > secondSide
                && secondSide + thirdSide > firstSide;

        if (sidesPositive && sidesValid) {

            String payload = "{\"separator\": \";\", \"input\": \""+firstSide+";"+secondSide+";"+thirdSide+"\"}";

            Response response =

                    given()
                            .log()
                            .ifValidationFails(LogDetail.ALL)
                            .contentType(ContentType.JSON)
                            .spec(helpersSpec)
                            .config(helperConfig)
                            .body(payload)
                    .when()
                            .post("/")
                    .then()
                            .log()
                            .ifValidationFails(LogDetail.ALL)
                    .assertThat()
                            .statusCode(200)
                            .contentType(ContentType.JSON)
                            .body("firstSide", equalTo(firstSide),
                                    "secondSide", equalTo(secondSide),
                                    "thirdSide", equalTo(thirdSide) )
                            .extract()
                            .response();

            String id = response.path("id");
            Reporter.log("The triangle with sides "+firstSide+", "+secondSide+", and "+thirdSide+" was" +
                    " successfully created, its ID is: " +id, true);

            return id;

        } else if (!sidesPositive) {
            Reporter.log("At least one of the specified sides is < 0, all of them must be > 0 ", true);
        } else {
            Reporter.log("""
                    The specified sides lengths are not valid for a triangle, they must follow the criteria: \
                    
                    (firstSide + secondSide) > thirdSide \
                    
                    AND\s
                    (firstSide + thirdSide) > secondSide \
                    
                    AND\s
                    (secondSide + thirdSide) > firstSide.""", true);
        }
        throw new IllegalArgumentException();
    }

    /** This method creates a new triangle if specified sides are valid for a real triangle,
     *  and returns the Response object for further assertions in tests.
     *
     * @param triangle - Triangle object with side values
     * @return the Response object from the API call
     */
    public static Response createTriangle(Triangle triangle) {
        return 
            given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .body(getNewTrianglePayloadMap(triangle, ";"))
            .when()
                .post("/");
    }
    
    /** This enum defines values for the strategy argument of the genSides method*/
    public enum Strategy {
        VALID_VALUES,
        SUM_VALUES,
        INVALID_VALUES,
        EQUILATERAL_VALUES,
        ISOSCELES_VALUES,
        WITH_ZERO_VALUES
    }

    /** This method randomly generates three values according to selected strategy:
     *  INVALID_VALUES - where the sum of some two value is less than the third one;
     *  SUM_VALUES - where the sum of any two values equals to the third one;
     *  VALID_VALUES - where the sum of some two value is greater than the third one;
     *  EQUILATERAL_VALUES - where all the sides are equal;
     *  ISOSCELES_VALUES - where any two of the sides are equal;
     *
     * @param strategy - one of the values described above;
     * @param pattern - # - no digits after comma (will be shown as number.0 since it's double),
     *               #.# - one digit after comma,
     *               #.## two digits after comma - and so on;
     * @param bound - the upper bound for the generated values,
     *             i.e. 10 - values will be up to 10; 100 - values will be up to100, and so on.
     * @return array of double with three values.
     */
    public static double[] genSides(Strategy strategy, String pattern, int bound) {
        double firstSide, secondSide, thirdSide;

        switch (strategy) {
            case INVALID_VALUES -> {
                do {
                    firstSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));
                    secondSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));
                    thirdSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));

                } while (!(firstSide + secondSide < thirdSide
                        || firstSide + thirdSide < secondSide
                        || secondSide + thirdSide < firstSide));
            }
            case SUM_VALUES -> {
                do {
                    firstSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));
                    secondSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));
                    thirdSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));

                } while (!(firstSide + secondSide == thirdSide
                        || firstSide + thirdSide == secondSide
                        || secondSide + thirdSide == firstSide));
            }
            case VALID_VALUES -> {
                do {
                    firstSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));
                    secondSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));
                    thirdSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));

                } while (!(firstSide + secondSide > thirdSide
                        && firstSide + thirdSide > secondSide
                        && secondSide + thirdSide > firstSide));
            }
            case EQUILATERAL_VALUES -> {
                firstSide = Double
                        .parseDouble(new DecimalFormat(pattern)
                                .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                .replace(",", "."));
                secondSide = firstSide;
                thirdSide = firstSide;
            }
            case ISOSCELES_VALUES -> {
                do {
                    firstSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));
                    secondSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));
                    thirdSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01, bound))
                                    .replace(",", "."));

                } while (!(firstSide == secondSide && firstSide * 2 > thirdSide
                        || firstSide == thirdSide && thirdSide * 2 > secondSide
                        || secondSide == thirdSide && secondSide * 2 > firstSide));
            }
            default -> throw new IllegalStateException("Unexpected strategy value: " + strategy);
        }
        return new double[]{firstSide, secondSide, thirdSide};
    }

    /** This method checks the number of existed triangles and deletes all of them if their number
     *  is equal to or greater than the specified maxAllowedTriangles value.
     *
     * @param maxAllowedTriangles - maximum allowed number of triangles
     */
    public static void cleanUpTringlesIfNeeded(int maxAllowedTriangles) {
        List<Triangle> existedTriangles = getAllTriangles();

        if (existedTriangles.size() >= 10) {
            deleteAllTriangles(getAllTrianglesIDs());
        }
    }
    
    /** This method checks the number of existed triangles and deletes one of them if their number
     *  is equal to or greater than the specified limit value.
     *
     * @param limit - maximum allowed number of triangles
     */
    public static void deleteOneTringleIfAboveLimit(int limit) {
        List<Triangle> existedTriangles = getAllTriangles();

        if (existedTriangles.size() >= limit) {
            deleteTriangle(existedTriangles.get(0).getId());
        }
    }

    /** This method generates sides according to the specified strategy and creates a Triangle object with all fields
     *  populated based on the generated sides.
     *
     * @param strategy - one of the values described in the Strategy enum;
     * @param pattern - # - no digits after comma (will be shown as number.0 since it's double),
     *               #.# - one digit after comma,
     *               #.## two digits after comma - and so on;
     * @param bound - the upper bound for the generated values,
     *             i.e. 10 - values will be up to 10; 100 - values will be up to100, and so on.
     * @return Triangle object with all fields populated based on the generated sides.
     */
    public static Triangle getNewTriangle(Strategy strategy, String pattern, int bound) {
        double[] sides = genSides(strategy, pattern, bound);

        double perimeter = sides[0] + sides[1] + sides[2];
        double halfPerimeter = perimeter / 2;
        double area = Math.sqrt(halfPerimeter
                * (halfPerimeter - sides[0])
                * (halfPerimeter - sides[1])
                * (halfPerimeter - sides[2]));

        return new Triangle(null,
                sides[0], sides[1], sides[2],
                perimeter,
                area);
    }


}
