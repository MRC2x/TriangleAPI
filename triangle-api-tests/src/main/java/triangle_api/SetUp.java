package triangle_api;

import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.testng.annotations.BeforeClass;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;

import static triangle_api.Helpers.VALID_TOKEN;

public class SetUp {

    @BeforeClass
    public final void setup() {
        RestAssured.baseURI = "http://localhost:8080";

        RestAssured.basePath = "/triangle/";

        JsonConfig jsonConfig = JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE);

        RestAssured.config = RestAssured.config()
                .jsonConfig(jsonConfig)
                .encoderConfig(EncoderConfig
                        .encoderConfig()
                        .defaultContentCharset("UTF-8"));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("X-User", VALID_TOKEN)
                .build()
                .filter(new AllureRestAssured()
                .setRequestTemplate("http-request.ftl")
                .setResponseTemplate("http-response.ftl"));
    }

}
