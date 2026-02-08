package triangle_api;

import java.util.Map;

public class Utilities {

    /**
     * Prepares payload map for creating a new triangle via API
     * @param triangle Triangle object containing side lengths
     * @return Map representing the payload for the API request
     */
    public static Map<String, Object> getNewTrianglePayloadMap(Triangle triangle, String separator) {
        return Map.of(
                "separator", separator,
                "input", triangle.getSideA() + separator + triangle.getSideB() + separator + triangle.getSideC()
        );
    }

}
