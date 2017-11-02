package llama.com.serpentcharts.view;

import android.graphics.Color;
import android.support.annotation.NonNull;

/**
 * @author theWhiteLlama
 */
class Editor {

    @NonNull
    private static int[] colors = new int[]{
            Color.rgb(55, 255, 170),
            Color.rgb(255, 57, 120),
            Color.rgb(255, 255, 0),
            Color.rgb(255, 100, 77),
            Color.rgb(180, 40, 230),
    };

    @NonNull
    public static int[] getColors(int amount) {
        int[] c = new int[amount];
        System.arraycopy(colors, 0, c, 0, amount);
        return c;
    }

}
