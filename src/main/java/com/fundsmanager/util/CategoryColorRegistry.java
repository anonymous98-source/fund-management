package fundsmanager.util;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CategoryColorRegistry {

    private static final Color[] PALETTE = {
            new Color(0x4CAF50),
            new Color(0x2196F3),
            new Color(0xFFC107),
            new Color(0xFF5722),
            new Color(0x9C27B0),
            new Color(0x009688),
            new Color(0x795548),
            new Color(0x607D8B)
    };

    private static final Map<String, Color> colorMap = new HashMap<>();
    private static int index = 0;

    public static Color getColor(String categoryName) {
        return colorMap.computeIfAbsent(
                categoryName,
                k -> PALETTE[index++ % PALETTE.length]
        );
    }

    private CategoryColorRegistry() {}
}

