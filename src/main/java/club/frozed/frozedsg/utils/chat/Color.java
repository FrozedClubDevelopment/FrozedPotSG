package club.frozed.frozedsg.utils.chat;

import org.bukkit.ChatColor;
import java.util.List;
import java.util.stream.Collectors;

public class Color {

    public static String translate(String text) {
        String output = text;
        return ChatColor.translateAlternateColorCodes('&', output);
    }

    public static List<String> translate(List<String> list) {
        return list.stream().map(Color::translate).collect(Collectors.toList());
    }

}
