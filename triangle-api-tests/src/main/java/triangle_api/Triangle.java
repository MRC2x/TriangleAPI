package triangle_api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Triangle {
    String id;
    double sideA, sideB, sideC;
    double perimeter, area;
}
