package net;

public class PluginLoadingException extends Exception {
    public PluginLoadingException(String className, Throwable cause) {
        super(String.format("Couldn't load plugin %s: %s", className, cause.getMessage()), cause);
    }
}
