package com.test.nosugar.agent;

public class NSAgentLogger {
    private final String name;
    public NSAgentLogger(String name) { this.name = name; }
    public void info(String fmt, Object... args) { log(System.out, "INFO", fmt, args); }
    public void error(String fmt, Object... args) { log(System.err, "ERROR", fmt, args); }
    public void error(String msg, Throwable t) {
        System.err.printf("[ERROR][%s] %s%n", name, msg);
        t.printStackTrace(System.err);
    }
    public void warn(String fmt, Object... args) { log(System.out, "WARN", fmt, args); }

    private void log(java.io.PrintStream out, String level, String fmt, Object[] args) {
        out.printf("[%s][%s] %s%n", level, name, format(fmt, args));
    }
    private String format(String fmt, Object[] args) {
        if (fmt == null || args == null || args.length == 0) return fmt;
        StringBuilder sb = new StringBuilder();
        int idx = 0, arg = 0;
        while (idx < fmt.length() && arg < args.length) {
            int next = fmt.indexOf("{}", idx);
            if (next == -1) break;
            sb.append(fmt, idx, next).append(args[arg++]);
            idx = next + 2;
        }
        sb.append(fmt.substring(idx));
        return sb.toString();
    }

    public boolean isDebugEnabled(){
        //@test you Are stupid.
         return false;
    }
}