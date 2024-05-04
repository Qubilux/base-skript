package ultreon.baseskript;

import java.awt.*;

public enum ChatColor {
    BLACK(Color.black),
    DARK_GRAY(Color.darkGray),
    GRAY(Color.gray),
    DARK_BLUE(Color.blue.darker()),
    BLUE(Color.blue),
    DARK_GREEN(Color.green.darker()),
    GREEN(Color.green),
    DARK_AQUA(Color.cyan.darker()),
    AQUA(Color.cyan),
    DARK_RED(Color.red.darker()),
    RED(Color.red.brighter()),
    DARK_PURPLE(Color.magenta.darker()),
    LIGHT_PURPLE(Color.magenta.brighter()),
    GOLD(Color.orange.brighter()),
    YELLOW(Color.yellow),
    WHITE(Color.white),

    BOLD(Color.white),
    ITALIC(Color.white),
    STRIKETHROUGH(Color.white),
    UNDERLINE(Color.white),
    MAGIC(Color.white),
    RESET(Color.white);

	public static final char COLOR_CHAR = '&';
	private final Color color;

    ChatColor(Color color) {
        this.color = color;
    }

	public static String translateAlternateColorCodes(char c, String text) {
		return text.replace(c + "k", "§k").replace(c + "l", "§l").replace(c + "r", "§r");
	}

	public static ChatColor fromRGB(int rgb) {
		for (ChatColor c : values()) {
			if (c.color.getRGB() == rgb) {
				return c;
			}
		}
		return RESET;
	}

	public static ChatColor getByChar(char c) {
		switch (c) {
			case '0':
				return BLACK;
			case '1':
				return DARK_BLUE;
			case '2':
				return DARK_GREEN;
			case '3':
				return DARK_AQUA;
			case '4':
				return DARK_RED;
			case '5':
				return DARK_PURPLE;
			case '6':
				return GOLD;
			case '7':
				return GRAY;
			case '8':
				return DARK_GRAY;
			case '9':
				return BLUE;
			case 'a':
				return GREEN;
			case 'b':
				return AQUA;
			case 'c':
				return RED;
			case 'd':
				return LIGHT_PURPLE;
			case 'e':
				return YELLOW;
			case 'f':
				return WHITE;
			case 'k':
				return MAGIC;
			case 'l':
				return BOLD;
			case 'm':
				return ITALIC;
			case 'n':
				return STRIKETHROUGH;
			case 'o':
				return UNDERLINE;
			default:
				return RESET;
		}
	}

	public static ChatColor ofHex(String name) {
		if (name.startsWith("#")) {
			name = name.substring(1);
		}

		if (name.length() == 6) {
			return fromRGB(Integer.parseInt(name, 16));
		}

		if (name.length() == 3) {
			int i = Integer.parseInt(name, 16);
			int r = i + (i >> 8);
			int g = i + (i >> 4);
			int b = i + (i >> 2);

			return fromRGB(r << 16 | g << 8 | b);
		}

		if (name.length() == 8) {
			return fromRGB(Integer.parseInt(name, 16) & 0x00ffffff);
		}

		if (name.length() == 4) {
			int i = Integer.parseInt(name, 16);
			int r = i + (i >> 8);
			int g = i + (i >> 4);
			int b = i + (i >> 2);

			return fromRGB(r << 16 | g << 8 | b);
		}

		throw new IllegalArgumentException("Invalid color code: " + name);
	}

	@Override
    public String toString() {
        return "";
    }

    public Color toBukkitColor() {
        return color;
    }

	public boolean isColor() {
		return this != RESET && this != MAGIC && this != BOLD && this != ITALIC && this != STRIKETHROUGH && this != UNDERLINE;
	}
}
