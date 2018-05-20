package nadav.tasher.handasaim.tools;

import nadav.tasher.handasaim.architecture.app.Framable;
import nadav.tasher.lightool.parts.Tower;

public class TowerHub {
    public static Tower<Integer> textColorChangeTunnle = new Tower<>();
    public static Tower<Integer> colorAChangeTunnle = new Tower<>();
    public static Tower<Integer> colorBChangeTunnle = new Tower<>();
    public static Tower<Integer> fontSizeChangeTunnle = new Tower<>();
    public static Tower<Boolean> breakTimeTunnle = new Tower<>();
    public static Tower<String> scriptEventTunnel = new Tower<>();
    public static Framable current;

}
