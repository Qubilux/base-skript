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

import ch.njol.skript.Skript;
import ch.njol.skript.localization.Language;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Checker;
import ch.njol.util.NonNullPair;
import ch.njol.util.Pair;
import ch.njol.util.StringUtils;
import ch.njol.util.coll.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.ultreon.baseskript.ChatColor;
import dev.ultreon.baseskript.Plugin;
import dev.ultreon.baseskript.PluginClassesProvider;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class.
 * 
 * @author Peter Güttinger
 */
public abstract class Utils {

	public static final boolean COPY_SUPPORTED = true;

	private Utils() {}
	
	public final static Random random = new Random();

	public static String join(final Object[] objects) {
		assert objects != null;
		final StringBuilder b = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			if (i != 0)
				b.append(", ");
			b.append(Classes.toString(objects[i]));
		}
		return b.toString();
	}

	public static String join(final Iterable<?> objects) {
		assert objects != null;
		final StringBuilder b = new StringBuilder();
		boolean first = true;
		for (final Object o : objects) {
			if (!first)
				b.append(", ");
			else
				first = false;
			b.append(Classes.toString(o));
		}
		return b.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> boolean isEither(@Nullable T compared, @Nullable T... types) {
		return CollectionUtils.contains(types, compared);
	}

	public static Pair<String, Integer> getAmount(String s) {
		if (s.matches("\\d+ of .+")) {
			return new Pair<String, Integer>(s.split(" ", 3)[2], Utils.parseInt(s.split(" ", 2)[0]));
		} else if (s.matches("\\d+ .+")) {
			return new Pair<String, Integer>(s.split(" ", 2)[1], Utils.parseInt(s.split(" ", 2)[0]));
		} else if (s.matches("an? .+")) {
			return new Pair<String, Integer>(s.split(" ", 2)[1], 1);
		}
		return new Pair<String, Integer>(s, Integer.valueOf(-1));
	}
	
//	public final static class AmountResponse {
//		public final String s;
//		public final int amount;
//		public final boolean every;
//
//		public AmountResponse(final String s, final int amount, final boolean every) {
//			this.s = s;
//			this.amount = amount;
//			this.every = every;
//		}
//
//		public AmountResponse(final String s, final boolean every) {
//			this.s = s;
//			amount = -1;
//			this.every = every;
//		}
//
//		public AmountResponse(final String s, final int amount) {
//			this.s = s;
//			this.amount = amount;
//			every = false;
//		}
//
//		public AmountResponse(final String s) {
//			this.s = s;
//			amount = -1;
//			every = false;
//		}
//	}
//
//	public final static AmountResponse getAmountWithEvery(final String s) {
//		if (s.matches("\\d+ of (all|every) .+")) {
//			return new AmountResponse("" + s.split(" ", 4)[3], Utils.parseInt("" + s.split(" ", 2)[0]), true);
//		} else if (s.matches("\\d+ of .+")) {
//			return new AmountResponse("" + s.split(" ", 3)[2], Utils.parseInt("" + s.split(" ", 2)[0]));
//		} else if (s.matches("\\d+ .+")) {
//			return new AmountResponse("" + s.split(" ", 2)[1], Utils.parseInt("" + s.split(" ", 2)[0]));
//		} else if (s.matches("an? .+")) {
//			return new AmountResponse("" + s.split(" ", 2)[1], 1);
//		} else if (s.matches("(all|every) .+")) {
//			return new AmountResponse("" + s.split(" ", 2)[1], true);
//		}
//		return new AmountResponse(s);
//	}

	/**
	 * Loads classes of the plugin by package. Useful for registering many syntax elements like Skript does it.
	 *
	 * @param basePackage The base package to add to all sub packages, e.g. <tt>"ch.njol.skript"</tt>.
	 * @param subPackages Which subpackages of the base package should be loaded, e.g. <tt>"expressions", "conditions", "effects"</tt>. Subpackages of these packages will be loaded
	 *            as well. Use an empty array to load all subpackages of the base package.
	 * @throws IOException If some error occurred attempting to read the plugin's jar file.
	 * @return This SkriptAddon
	 */
	public static Class<?>[] getClasses(Plugin plugin, String basePackage, String... subPackages) throws IOException {
		PluginClassesProvider provider = PluginClassesProvider.get(plugin);
		if (provider != null) {
			return provider.getClasses(plugin, basePackage, subPackages);
		}

		assert subPackages != null;
		JarInputStream jar = new JarInputStream(getLocation(plugin).openStream());
		for (int i = 0; i < subPackages.length; i++)
			subPackages[i] = subPackages[i].replace('.', '/') + "/";
		basePackage = basePackage.replace('.', '/') + "/";
		List<Class<?>> classes = new ArrayList<>();
		try {
			List<String> classNames = checkClasses(basePackage, subPackages, jar);

			classNames.sort(String::compareToIgnoreCase);

			for (String c : classNames) {
				try {
					classes.add(Class.forName(c, true, plugin.getClass().getClassLoader()));
				} catch (ClassNotFoundException | NoClassDefFoundError ex) {
					Skript.exception(ex, "Cannot load class " + c);
				} catch (ExceptionInInitializerError err) {
					Skript.exception(err.getCause(), "class " + c + " generated an exception while loading");
				}
			}
		} finally {
			try {
				jar.close();
			} catch (IOException e) {}
		}
		return classes.toArray(new Class<?>[classes.size()]);
	}

	private static @NotNull List<String> checkClasses(String basePackage, String[] subPackages, JarInputStream jar) throws IOException {
		List<String> classNames = new ArrayList<>();

		JarEntry e;
		while ((e = jar.getNextJarEntry()) != null) {
			if (e.getName().startsWith(basePackage) && e.getName().endsWith(".class") && !e.getName().endsWith("package-info.class")) {
				boolean load = subPackages.length == 0;
				for (String sub : subPackages) {
					if (e.getName().startsWith(sub, basePackage.length())) {
						load = true;
						break;
					}
				}

				if (load)
					classNames.add(e.getName().replace('/', '.').substring(0, e.getName().length() - ".class".length()));
			}
		}
		return classNames;
	}

	/**
	 * The first invocation of this method uses reflection to invoke the protected method {@link JavaPlugin#getFile()} to get the plugin's jar file.
	 *
	 * @return The jar file of the plugin.
	 */
	@Nullable
	public static URL getLocation(Plugin plugin) {
		try {
			return plugin.getPluginLocation();
		} catch (IllegalArgumentException e) {
			Skript.outdatedError(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private final static String[][] plurals = {
			
			{"fe", "ves"},// most -f words' plurals can end in -fs as well as -ves
			
			{"axe", "axes"},
			{"x", "xes"},
			
			{"ay", "ays"},
			{"ey", "eys"},
			{"iy", "iys"},
			{"oy", "oys"},
			{"uy", "uys"},
			{"kie", "kies"},
			{"zombie", "zombies"},
			{"y", "ies"},
			
			{"h", "hes"},
			
			{"man", "men"},
			
			{"us", "i"},
			
			{"hoe", "hoes"},
			{"toe", "toes"},
			{"o", "oes"},
			
			{"alias", "aliases"},
			{"gas", "gases"},
			
			{"child", "children"},
			
			{"sheep", "sheep"},
			
			// general ending
			{"", "s"},
	};
	
	/**
	 * @param s trimmed string
	 * @return Pair of singular string + boolean whether it was plural
	 */
	@SuppressWarnings("null")
	public static NonNullPair<String, Boolean> getEnglishPlural(final String s) {
		assert s != null;
		if (s.isEmpty())
			return new NonNullPair<String, Boolean>("", Boolean.FALSE);
		for (final String[] p : plurals) {
			if (s.endsWith(p[1]))
				return new NonNullPair<String, Boolean>(s.substring(0, s.length() - p[1].length()) + p[0], Boolean.TRUE);
			if (s.endsWith(p[1].toUpperCase(Locale.ENGLISH)))
				return new NonNullPair<String, Boolean>(s.substring(0, s.length() - p[1].length()) + p[0].toUpperCase(Locale.ENGLISH), Boolean.TRUE);
		}
		return new NonNullPair<String, Boolean>(s, Boolean.FALSE);
	}
	
	/**
	 * Gets the english plural of a word.
	 * 
	 * @param s
	 * @return The english plural of the given word
	 */
	public static String toEnglishPlural(final String s) {
		assert s != null && s.length() != 0;
		for (final String[] p : plurals) {
			if (s.endsWith(p[0]))
				return s.substring(0, s.length() - p[0].length()) + p[1];
		}
		assert false;
		return s + "s";
	}
	
	/**
	 * Gets the plural of a word (or not if p is false)
	 * 
	 * @param s
	 * @param p
	 * @return The english plural of the given word, or the word itself if p is false.
	 */
	public static String toEnglishPlural(final String s, final boolean p) {
		if (p)
			return toEnglishPlural(s);
		return s;
	}
	
	/**
	 * Adds 'a' or 'an' to the given string, depending on the first character of the string.
	 * 
	 * @param s The string to add the article to
	 * @return The given string with an appended a/an and a space at the beginning
	 * @see #A(String)
	 * @see #a(String, boolean)
	 */
	public static String a(final String s) {
		return a(s, false);
	}
	
	/**
	 * Adds 'A' or 'An' to the given string, depending on the first character of the string.
	 * 
	 * @param s The string to add the article to
	 * @return The given string with an appended A/An and a space at the beginning
	 * @see #a(String)
	 * @see #a(String, boolean)
	 */
	public static String A(final String s) {
		return a(s, true);
	}
	
	/**
	 * Adds 'a' or 'an' to the given string, depending on the first character of the string.
	 * 
	 * @param s The string to add the article to
	 * @param capA Whether to use a capital a or not
	 * @return The given string with an appended a/an (or A/An if capA is true) and a space at the beginning
	 * @see #a(String)
	 */
	public static String a(final String s, final boolean capA) {
		assert s != null && s.length() != 0;
		if ("aeiouAEIOU".indexOf(s.charAt(0)) != -1) {
			if (capA)
				return "An " + s;
			return "an " + s;
		} else {
			if (capA)
				return "A " + s;
			return "a " + s;
		}
	}

	final static ChatColor[] styles = new ChatColor[]{ChatColor.BOLD, ChatColor.ITALIC, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.MAGIC, ChatColor.RESET};
	final static Map<String, String> chat = new HashMap<String, String>();
	final static Map<String, String> englishChat = new HashMap<String, String>();
	
	public final static boolean HEX_SUPPORTED = true;

	static {
		Language.addListener(() -> {
            final boolean english = englishChat.isEmpty();
            chat.clear();
            for (final ChatColor style : styles) {
                for (final String s : Language.getList("chat styles." + style.name())) {
                    chat.put(s.toLowerCase(Locale.ENGLISH), style.toString());
                    if (english)
                        englishChat.put(s.toLowerCase(Locale.ENGLISH), style.toString());
                }
            }
        });
	}

    static {
        Pattern.compile("<([^<>]+)>");
    }

	private final static Pattern stylePattern = Pattern.compile("<([^<>]+)>");

	/**
	 * Replaces &lt;chat styles&gt; in the message
	 *
	 * @param message
	 * @return message with localised chat styles converted to Minecraft's format
	 */
	public static String replaceChatStyles(final String message) {
		if (message.isEmpty())
			return message;
		String m = StringUtils.replaceAll(Matcher.quoteReplacement(message.replace("<<none>>", "")), stylePattern, m1 -> {
			SkriptColor color = SkriptColor.fromName(m1.group(1));
			if (color != null)
				return color.getFormattedChat();
			final String tag = m1.group(1).toLowerCase(Locale.ENGLISH);
			final String f = chat.get(tag);
			if (f != null)
				return f;
			if (HEX_SUPPORTED && tag.startsWith("#")) { // Check for parsing hex colors
				ChatColor chatColor = parseHexColor(tag);
				if (chatColor != null)
					return chatColor.toString();
			}
			return m1.group();
		});
		assert m != null;
		// Restore user input post-sanitization
		// Sometimes, the message has already been restored
		if (!message.equals(m)) {
			m = m.replace("\\$", "$").replace("\\\\", "\\");
		}
		m = ChatColor.translateAlternateColorCodes('&', m);
		return m;
	}

    /**
	 * Replaces english &lt;chat styles&gt; in the message. This is used for messages in the language file as the language of colour codes is not well defined while the language is
	 * changing, and for some hardcoded messages.
	 * 
	 * @param message
	 * @return message with english chat styles converted to Minecraft's format
	 */
	public static String replaceEnglishChatStyles(final String message) {
		// TODO port to non-Minecraft.
//		if (message.isEmpty())
//			return message;
//		String m = StringUtils.replaceAll(Matcher.quoteReplacement(message), stylePattern, new Callback<String, Matcher>() {
//			@Override
//			public String run(final Matcher m) {
//				SkriptColor color = SkriptColor.fromName("" + m.group(1));
//				if (color != null)
//					return color.getFormattedChat();
//				final String tag = m.group(1).toLowerCase(Locale.ENGLISH);
//				final String f = englishChat.get(tag);
//				if (f != null)
//					return f;
//				if (HEX_SUPPORTED && tag.startsWith("#")) { // Check for parsing hex colors
//					ChatColor chatColor = parseHexColor(tag);
//					if (chatColor != null)
//						return chatColor.toString();
//				}
//				return "" + m.group();
//			}
//		});
//		assert m != null;
//		// Restore user input post-sanitization
//		// Sometimes, the message has already been restored
//		if (!message.equals(m)) {
//			m = m.replace("\\$", "$").replace("\\\\", "\\");
//		}
//		m = ChatColor.translateAlternateColorCodes('&', "" + m);
//		return "" + m;
		return message;
	}

	/**
	 * Gets a random value between <tt>start</tt> (inclusive) and <tt>end</tt> (exclusive)
	 * 
	 * @param start
	 * @param end
	 * @return <tt>start + random.nextInt(end - start)</tt>
	 */
	public static int random(final int start, final int end) {
		if (end <= start)
			throw new IllegalArgumentException("end (" + end + ") must be > start (" + start + ")");
		return start + random.nextInt(end - start);
	}

	/**
	 * @see #highestDenominator(Class, Class[])
	 */
	public static Class<?> getSuperType(final Class<?>... classes) {
		return highestDenominator(Object.class, classes);
	}

	/**
	 * Searches for the highest common denominator of the given types;
	 * in other words, the first supertype they all share.
	 *
	 * <h3>Arbitrary Selection</h3>
	 * Classes may have <b>multiple</b> highest common denominators: interfaces that they share
	 * which do not extend each other.
	 * This method selects a <b>superclass</b> first (where possible)
	 * but its selection of interfaces is quite random.
	 * For this reason, it is advised to specify a "best guess" class as the first parameter, which will be selected if
	 * it's appropriate.
	 * Note that if the "best guess" is <i>not</i> a real supertype, it can never be selected.
	 *
	 * @param bestGuess The fallback class to guess
	 * @param classes The types to check
	 * @return The most appropriate common class of all provided
	 * @param <Found> The highest common denominator found
	 * @param <Type> The input type spread
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <Found, Type extends Found> Class<Found> highestDenominator(Class<? super Found> bestGuess, @NotNull Class<? extends Type> @NotNull ... classes) {
		assert classes.length > 0;
		Class<?> chosen = classes[0];
		outer:
		for (final Class<?> checking : classes) {
			assert checking != null && !checking.isArray() && !checking.isPrimitive() : checking;
			if (chosen.isAssignableFrom(checking))
				continue;
			Class<?> superType = checking;
			do if (superType != Object.class && superType.isAssignableFrom(chosen)) {
				chosen = superType;
				continue outer;
			}
			while ((superType = superType.getSuperclass()) != null);
			for (final Class<?> anInterface : checking.getInterfaces()) {
				superType = highestDenominator(Object.class, anInterface, chosen);
				if (superType != Object.class) {
					chosen = superType;
					continue outer;
				}
			}
			return (Class<Found>) bestGuess;
		}
		if (!bestGuess.isAssignableFrom(chosen)) // we struck out on a type we don't want
			return (Class<Found>) bestGuess;
		// Cloneable is about as useful as object as super type
		// However, it lacks special handling used for Object supertype
		// See #1747 to learn how it broke returning items from functions
		return (Class<Found>) (chosen == Cloneable.class ? bestGuess : chosen == Object.class ? bestGuess : chosen);
	}
	
	/**
	 * Parses a number that was validated to be an integer but might still result in a {@link NumberFormatException} when parsed with {@link Integer#parseInt(String)} due to
	 * overflow.
	 * This method will return {@link Integer#MIN_VALUE} or {@link Integer#MAX_VALUE} respectively if that happens.
	 * 
	 * @param s
	 * @return The parsed integer, {@link Integer#MIN_VALUE} or {@link Integer#MAX_VALUE} respectively
	 */
	public static int parseInt(final String s) {
		assert s.matches("-?\\d+");
		try {
			return Integer.parseInt(s);
		} catch (final NumberFormatException e) {
			return s.startsWith("-") ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		}
	}
	
	/**
	 * Parses a number that was validated to be an integer but might still result in a {@link NumberFormatException} when parsed with {@link Long#parseLong(String)} due to
	 * overflow.
	 * This method will return {@link Long#MIN_VALUE} or {@link Long#MAX_VALUE} respectively if that happens.
	 * 
	 * @param s
	 * @return The parsed long, {@link Long#MIN_VALUE} or {@link Long#MAX_VALUE} respectively
	 */
	public static long parseLong(final String s) {
		assert s.matches("-?\\d+");
		try {
			return Long.parseLong(s);
		} catch (final NumberFormatException e) {
			return s.startsWith("-") ? Long.MIN_VALUE : Long.MAX_VALUE;
		}
	}
	
	/**
	 * Gets class for name. Throws RuntimeException instead of checked one.
	 * Use this only when absolutely necessary.
	 * @param name Class name.
	 * @return The class.
	 */
	public static Class<?> classForName(String name) {
		Class<?> c;
		try {
			c = Class.forName(name);
			return c;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found!");
		}
	}
	
	/**
	 * Finds the index of the last in a {@link List} that matches the given {@link Checker}.
	 *
	 * @param list the {@link List} to search.
	 * @param checker the {@link Checker} to match elements against.
	 * @return the index of the element found, or -1 if no matching element was found.
	 */
	public static <T> int findLastIndex(List<T> list, Checker<T> checker) {
		int lastIndex = -1;
		for (int i = 0; i < list.size(); i++) {
			if (checker.check(list.get(i)))
				lastIndex = i;
		}
		return lastIndex;
	}

	public static boolean isInteger(Number... numbers) {
		for (Number number : numbers) {
			if (Double.class.isAssignableFrom(number.getClass()) || Float.class.isAssignableFrom(number.getClass()))
				return false;
		}
		return true;
	}

	public static ChatColor parseHexColor(String name) {
		try {
			return ChatColor.ofHex(name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
