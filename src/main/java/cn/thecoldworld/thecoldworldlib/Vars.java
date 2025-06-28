package cn.thecoldworld.thecoldworldlib;

public final class Vars {
    public static final String MOD_VERSION = "0.0.1";
    public static final String COMPATIBLE_VERSION = "0.0.1";
    public static final String MODID = "thecoldworldlib";

    private Vars() {
    }

    public static String createFailString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred:");
        sb.append(e.getClass().getCanonicalName());
        sb.append("\nStarkTrace:");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            sb.append(stackTraceElement.toString()).append("\n");
        }
        return sb.toString();
    }
}
