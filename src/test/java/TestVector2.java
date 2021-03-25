import static org.junit.jupiter.api.Assertions.assertEquals;

import core.Vector2;
import org.junit.jupiter.api.Test;

class TestVector2 {

    @Test
    void addition() {
        double x1 = 87;
        double y1 = 52;
        double x2 = 786;
        double y2 = 14;

        Vector2 v1 = new Vector2(x1, y1);
        Vector2 v2 = new Vector2(x2, y2);

        v1.add(v2);

        assertEquals(v1.x, x1 + x2);
        assertEquals(v1.y, y1 + y2);
    }

}

