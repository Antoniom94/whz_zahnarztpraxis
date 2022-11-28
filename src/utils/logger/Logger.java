package utils.logger;

public class Logger {
    public void info(String message) {
        System.out.println(String.format("[Info]    %s", message));
    }

    public void warning(String message) {
        System.out.println(String.format("\u001B[33m[Warning] %s\u001B[0m", message));
    }

    public void error(String message) {
        System.out.println(String.format("\u001B[31m[Error]   %s\u001B[0m", message));
    }
}