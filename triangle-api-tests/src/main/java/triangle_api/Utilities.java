package triangle_api;

import java.util.Map;

public class Utilities {

    /**
     * Prepares payload map for creating a new triangle via API
     * @param triangle Triangle object containing side lengths
     * @return Map representing the payload for the API request
     */
    public static Map<String, Object> getNewTrianglePayloadMap(Triangle triangle) {
        return Map.of(
                "separator", ";",
                "input", triangle.getSideA() + ";" + triangle.getSideB() + ";" + triangle.getSideC()
        );
    }

}
