package nadav.tasher.handasaim.architecture.app;

public class Theme {
    public int textSize, textColor;
    public int colorTop, colorBottom, colorMix;
    public int menuColor;
    public boolean showBreaks, showMessages, markPrehours, showRemainingTime;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TextSize: ").append(textSize).append('\n');
        builder.append("TextColor: ").append(textColor).append('\n');
        builder.append("ColorTop: ").append(colorTop).append('\n');
        builder.append("ColorBottom: ").append(colorBottom).append('\n');
        builder.append("ColorMix: ").append(colorMix).append('\n');
        builder.append("ColorMenu: ").append(menuColor).append('\n');
        builder.append("ShowBreaks: ").append(showBreaks).append('\n');
        builder.append("ShowMessages: ").append(showMessages).append('\n');
        builder.append("MarkPrehours: ").append(markPrehours).append('\n');
        builder.append("ShowRemainigTime: ").append(showRemainingTime).append('\n');
        return builder.toString();
    }
}
