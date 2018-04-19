package nadav.tasher.handasaim.tools;

import java.io.File;

import nadav.tasher.lightool.communication.Tunnel;

public class TunnelHub {
    public static Tunnel<File> scheduleFileTunnel;
    public static Tunnel<Integer> textColorChangeTunnle = new Tunnel<>();
    public static Tunnel<Integer> colorAChangeTunnle = new Tunnel<>();
    public static Tunnel<Integer> colorBChangeTunnle = new Tunnel<>();
    public static Tunnel<Integer> fontSizeChangeTunnle = new Tunnel<>();
    public static Tunnel<Boolean> breakTimeTunnle = new Tunnel<>();
    public static Tunnel<String> scriptEventTunnel = new Tunnel<>();
}
