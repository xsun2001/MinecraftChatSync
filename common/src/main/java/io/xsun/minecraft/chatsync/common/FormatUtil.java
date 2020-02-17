package io.xsun.minecraft.chatsync.common;

public final class FormatUtil {
    public static String format(String formatHint, Object... args) {
        StringBuilder builder = new StringBuilder();
        char[] chars = new char[formatHint.length()];
        formatHint.getChars(0, formatHint.length(), chars, 0);
        boolean good = true;
        int last = 0, argIndex = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '{') {
                if (chars[i + 1] == '}') {
                    if (argIndex >= args.length) {
                        good = false;
                        break;
                    }
                    builder.append(chars, last, i - last);
                    builder.append(args[argIndex++]);
                    last = i + 2;
                    ++i;
                }
            }
        }
        return good ? builder.toString() : "Format Error";
    }
}
