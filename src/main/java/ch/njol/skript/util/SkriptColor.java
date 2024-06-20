/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.util;

import ch.njol.skript.localization.Adjective;
import ch.njol.skript.localization.Language;
import ch.njol.skript.variables.Variables;
import ch.njol.yggdrasil.Fields;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.ultreon.baseskript.ChatColor;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.*;

@SuppressWarnings("null")
public enum SkriptColor implements Color {

	BLACK(ChatColor.BLACK),
	DARK_GREY(ChatColor.DARK_GRAY),
	// DyeColor.LIGHT_GRAY on 1.13, DyeColor.SILVER on earlier (dye colors were changed in 1.12)
	LIGHT_GREY(ChatColor.GRAY),
	WHITE(ChatColor.WHITE),
	
	DARK_BLUE(ChatColor.DARK_BLUE),
	BROWN(ChatColor.BLUE),
	DARK_CYAN(ChatColor.DARK_AQUA),
	LIGHT_CYAN(ChatColor.AQUA),
	
	DARK_GREEN(ChatColor.DARK_GREEN),
	LIGHT_GREEN(ChatColor.GREEN),
	
	YELLOW(ChatColor.YELLOW),
	ORANGE(ChatColor.GOLD),
	
	DARK_RED(ChatColor.DARK_RED),
	LIGHT_RED(ChatColor.RED),
	
	DARK_PURPLE(ChatColor.DARK_PURPLE),
	LIGHT_PURPLE(ChatColor.LIGHT_PURPLE);

	private final static Map<String, SkriptColor> names = new HashMap<String, SkriptColor>();
	private final static Set<SkriptColor> colors = new HashSet<SkriptColor>();
	private final static String LANGUAGE_NODE = "colors";
	
	static {
		colors.addAll(Arrays.asList(values()));
		Language.addListener(() -> {
			names.clear();
			for (SkriptColor color : values()) {
				String node = LANGUAGE_NODE + "." + color.name();
				color.setAdjective(new Adjective(node + ".adjective"));
				for (String name : Language.getList(node + ".names"))
					names.put(name.toLowerCase(Locale.ENGLISH), color);
			}
		});
	}
	
	private ChatColor chat;
	@Nullable
	private Adjective adjective;
	
	SkriptColor(ChatColor chat) {
		this.chat = chat;
	}

	public static Color fromRGB(String input) {
		try {
			int rgb = Integer.parseInt(input, 16);
			return fromRGB(rgb);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Color fromRGB(int rgb) {
		return new ColorRGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}

	@Override
	public java.awt.Color asBukkitColor() {
		return chat.toBukkitColor();
	}

	@Override
	public String getName() {
		assert adjective != null;
		return adjective.toString();
	}

	public String getFormattedChat() {
		return "" + chat;
	}
	
	@Nullable
	public Adjective getAdjective() {
		return adjective;
	}

	public ChatColor asChatColor() {
		return chat;
	}

	private void setAdjective(@Nullable Adjective adjective) {
		this.adjective = adjective;
	}
	
	
	/**
	 * @param name The String name of the color defined by Skript's .lang files.
	 * @return Skript Color if matched up with the defined name
	 */
	@Nullable
	public static SkriptColor fromName(String name) {
		return names.get(name);
	}

	public static SkriptColor fromBukkitColor(java.awt.Color color) {
		for (SkriptColor c : colors) {
			if (c.asBukkitColor().equals(color))
				return c;
		}
		assert false;
		return null;
	}

	/**
	 * Replace chat color character '§' with '&'
	 * This is an alternative method to {@link ChatColor#stripColor(String)}
	 * But does not strip the color code.
	 * @param s string to replace chat color character of.
	 * @return String with replaced chat color character
	 */
	public static String replaceColorChar(String s) {
		return s.replace('\u00A7', '&');
	}

	@Override
	public String toString() {
		return adjective == null ? name() : adjective.toString(-1, 0);
	}

	@Override
	public @NotNull Fields serialize() throws NotSerializableException {
		return new Fields(this, Variables.yggdrasil);
	}

	@Override
	public void deserialize(@NotNull Fields fields) throws StreamCorruptedException {
		chat = fields.getObject("chat", ChatColor.class);
		try {
			adjective = fields.getObject("adjective", Adjective.class);
		} catch (StreamCorruptedException ignored) {}
	}
}
