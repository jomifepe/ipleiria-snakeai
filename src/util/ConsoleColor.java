package util;

public enum ConsoleColor {
    BLACK   ("\u001B[30m"), BRIGHT_BLACK  ("\033[0;90m"),
    RED     ("\u001B[31m"), BRIGHT_RED    ("\033[0;91m"),
    GREEN   ("\u001B[32m"), BRIGHT_GREEN  ("\033[0;92m"),
    YELLOW  ("\u001B[33m"), BRIGHT_YELLOW ("\033[0;93m"),
    BLUE    ("\u001B[34m"), BRIGHT_BLUE   ("\033[0;94m"),
    PURPLE  ("\u001B[35m"), BRIGHT_PURPLE ("\033[0;95m"),
    CYAN    ("\u001B[36m"), BRIGHT_CYAN   ("\033[0;96m"),
    WHITE   ("\u001B[37m"), BRIGHT_WHITE  ("\033[0;97m"),
    RESET   ("\u001B[0m");

    private String code;

    ConsoleColor(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
