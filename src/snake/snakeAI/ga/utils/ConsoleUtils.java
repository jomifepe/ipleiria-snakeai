package snake.snakeAI.ga.utils;

public final class ConsoleUtils {

    private static String buildColoredString(ConsoleColor color, String... msg) {
        StringBuilder sb = new StringBuilder().append(color.getCode());
        for (String str : msg)
            sb.append(str + " ");
        sb.setLength(sb.length() - 1);
        sb.append(ConsoleColor.RESET.getCode());
        return sb.toString();
    }

    public static void println(ConsoleColor color, String... msg) {
        System.out.println(buildColoredString(color, msg));
    }

    public static void print(ConsoleColor color, String... msg) {
        System.out.print(buildColoredString(color, msg));
    }
}
