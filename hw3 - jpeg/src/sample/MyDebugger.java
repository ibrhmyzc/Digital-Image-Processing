package sample;

public class MyDebugger {
    public static boolean isEnabled() {
        return false;
    }
    public static void log(Object o) {
        System.out.println(o.toString());
    }
}
