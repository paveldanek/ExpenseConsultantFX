package gui_v1.mainWindows.summaryWElements;

import java.awt.*;

public class ColorSet {
    private static int[][] colors = {
            {165,42,42}, {255,215,0}, {107,142,35}, {0,139,139}, {0,0,139}, {255,0,255}, {139,69,19}, {119,136,153},
            {255,0,0}, {184,134,11}, {0,128,0}, {0,255,255}, {138,43,226}, {255,105,180}, {210,105,30}, {176,196,222},
            {255,127,80}, {218,165,32}, {0,255,0}, {64,224,208}, {123,104,238}, {255,182,193}, {244,164,96}, {240,255,255},
            {250,128,114}, {238,232,170}, {0,250,154}, {175,238,238}, {139,0,139}, {255,228,196}, {222,184,135}, {105,105,105},
            {255,165,0}, {189,183,107}, {102,205,170}, {0,191,255}, {186,85,211}, {255,250,205}, {255,218,185}, {192,192,192}
    };

    private static int colorPick = colors.length;

    public static Color get() {
        if (colorPick<0 || colorPick>=colors.length) colorPick=0;
        float[] temp = Color.RGBtoHSB(colors[colorPick][0], colors[colorPick][1],
                colors[colorPick][2], null);
        return Color.getHSBColor(temp[0], temp[1], temp[2]);
    }

    public static void setFirst() {
        colorPick=0;
    }

    public static void setNext() {
        colorPick++;
        if (colorPick<0 || colorPick>=colors.length) colorPick=0;
    }

    public static void setPrevious() {
        colorPick--;
        if (colorPick<0 || colorPick>=colors.length) colorPick=colors.length-1;
    }

    public static void setLast() {
        colorPick=colors.length-1;
    }

    public static void set(int colorPosition) {
        if (colorPosition<0 || colorPosition>=colors.length) return;
        colorPick = colorPosition;
    }

    public static int getPosition() {
        return colorPick;
    }

    public static Color getFirst() { setFirst(); return get(); }

    public static Color getNext() { setNext(); return get(); }

    public static Color getPrevious() { setPrevious(); return get(); }

    public static Color getLast() { setLast(); return get(); }
}
