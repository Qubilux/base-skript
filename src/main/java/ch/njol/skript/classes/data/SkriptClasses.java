/**
 * This file is part of Skript.
 * <p>
 * Skript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Skript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.classes.data;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.*;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.localization.Noun;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.*;
import ch.njol.yggdrasil.Fields;
import org.eclipse.jdt.annotation.Nullable;

import java.io.StreamCorruptedException;
import java.util.Iterator;
import java.util.Locale;

/**
 * @author Peter Güttinger
 */
@SuppressWarnings("rawtypes")
public class SkriptClasses {
	public SkriptClasses() {
	}

	static {
		//noinspection unchecked
		Classes.registerClass(new ClassInfo<ClassInfo>(ClassInfo.class, "classinfo")
			.user("types?")
			.name("Type")
			.description("Represents a type, e.g. number, object, item type, location, block, world, entity type, etc.",
				"This is mostly used for expressions like 'event-&lt;type&gt;', '&lt;type&gt;-argument', 'loop-&lt;type&gt;', etc., e.g. event-world, number-argument and loop-player.")
			.usage("See the type name patterns of all types - including this one")
			.examples("{variable} is a number # check whether the variable contains a number, e.g. -1 or 5.5",
				"{variable} is a type # check whether the variable contains a type, e.g. number or player",
				"{variable} is an object # will always succeed if the variable is set as everything is an object, even types.",
				"disable PvP in the event-world",
				"kill the loop-entity")
			.since("2.0")
			.after("entitydata", "entitytype", "itemtype")
			.supplier(() -> (Iterator) Classes.getClassInfos().iterator())
			.parser(new Parser<ClassInfo>() {
				@Override
				@Nullable
				public ClassInfo parse(final String s, final ParseContext context) {
					return Classes.getClassInfoFromUserInput(Noun.stripIndefiniteArticle(s));
				}

				@Override
				public String toString(final ClassInfo c, final int flags) {
					return c.toString(flags);
				}

				@Override
				public String toVariableNameString(final ClassInfo c) {
					return c.getCodeName();
				}

				@Override
				public String getDebugMessage(final ClassInfo c) {
					return c.getCodeName();
				}

			})
			.serializer(new Serializer<ClassInfo>() {
				@Override
				public Fields serialize(final ClassInfo c) {
					final Fields f = new Fields();
					f.putObject("codeName", c.getCodeName());
					return f;
				}

				@Override
				public boolean canBeInstantiated() {
					return false;
				}

				@Override
				public void deserialize(final ClassInfo o, final Fields f) throws StreamCorruptedException {
					assert false;
				}

				@Override
				protected ClassInfo deserialize(final Fields fields) throws StreamCorruptedException {
					final String codeName = fields.getObject("codeName", String.class);
					if (codeName == null)
						throw new StreamCorruptedException();
					final ClassInfo<?> ci = Classes.getClassInfoNoError(codeName);
					if (ci == null)
						throw new StreamCorruptedException("Invalid ClassInfo " + codeName);
					return ci;
				}

				//					return c.getCodeName();
				@Override
				@Nullable
				public ClassInfo deserialize(final String s) {
					return Classes.getClassInfoNoError(s);
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}
			}));

		Classes.registerClass(new ClassInfo<Time>(Time.class, "time")
			.user("times?")
			.name("Time")
			.description("A time is a point in a minecraft day's time (i.e. ranges from 0:00 to 23:59), which can vary per world.",
				"See <a href='#date'>date</a> and <a href='#timespan'>timespan</a> for the other time types of Skript.")
			.usage("##:##",
				"##[:##][ ]am/pm")
			.examples("at 20:00:",
				"	time is 8 pm",
				"	broadcast \"It's %time%\"")
			.since("1.0")
			.defaultExpression(new EventValueExpression<Time>(Time.class))
			.parser(new Parser<Time>() {
				@Override
				@Nullable
				public Time parse(final String s, final ParseContext context) {
					return Time.parse(s);
				}

				@Override
				public String toString(final Time t, final int flags) {
					return t.toString();
				}

				@Override
				public String toVariableNameString(final Time o) {
					return "time:" + o.getTicks();
				}
			}).serializer(new YggdrasilSerializer<Time>()));

		Classes.registerClass(new ClassInfo<Timespan>(Timespan.class, "timespan")
			.user("time ?spans?")
			.name("Timespan")
			.description("A timespan is a difference of two different dates or times, " +
					"e.g '10 minutes'. Timespans are always displayed as real life time, but can be defined as minecraft time, " +
					"e.g. '5 minecraft days and 12 hours'.",
				"NOTE: Months always have the value of 30 days, and years of 365 days.",
				"See <a href='#date'>date</a> and <a href='#time'>time</a> for the other time types of Skript.")
			.usage("&lt;number&gt; [minecraft/mc/real/rl/irl] ticks/seconds/minutes/hours/days/weeks/months/years [[,/and] &lt;more...&gt;]",
				"[###:]##:##[.####] ([hours:]minutes:seconds[.milliseconds])")
			.examples("every 5 minecraft days:",
				"	wait a minecraft second and 5 ticks",
				"every 10 mc days and 12 hours:",
				"	halt for 12.7 irl minutes, 12 hours and 120.5 seconds")
			.since("1.0, 2.6.1 (weeks, months, years)")
			.parser(new Parser<Timespan>() {
				@Override
				@Nullable
				public Timespan parse(final String s, final ParseContext context) {
					try {
						return Timespan.parse(s);
					} catch (IllegalArgumentException e) {
						Skript.error("'" + s + "' is not a valid timespan");
						return null;
					}
				}

				@Override
				public String toString(final Timespan t, final int flags) {
					return t.toString(flags);
				}

				@Override
				public String toVariableNameString(final Timespan o) {
					return "timespan:" + o.getMilliSeconds();
				}
			}).serializer(new YggdrasilSerializer<Timespan>()));

		// TODO remove
		Classes.registerClass(new ClassInfo<Timeperiod>(Timeperiod.class, "timeperiod")
			.user("time ?periods?", "durations?")
			.name("Timeperiod")
			.description("A period of time between two <a href='#time'>times</a>. Mostly useful since you can use this to test for whether it's day, night, dusk or dawn in a specific world.",
				"This type might be removed in the future as you can use 'time of world is between x and y' as a replacement.")
			.usage("##:## - ##:##",
				"dusk/day/dawn/night")
			.examples("time in world is night")
			.since("1.0")
			.before("timespan") // otherwise "day" gets parsed as '1 day'
			.defaultExpression(new SimpleLiteral<Timeperiod>(new Timeperiod(0, 23999), true))
			.parser(new Parser<Timeperiod>() {
				@Override
				@Nullable
				public Timeperiod parse(final String s, final ParseContext context) {
					if (s.equalsIgnoreCase("day")) {
						return new Timeperiod(0, 11999);
					} else if (s.equalsIgnoreCase("dusk")) {
						return new Timeperiod(12000, 13799);
					} else if (s.equalsIgnoreCase("night")) {
						return new Timeperiod(13800, 22199);
					} else if (s.equalsIgnoreCase("dawn")) {
						return new Timeperiod(22200, 23999);
					}
					final int c = s.indexOf('-');
					if (c == -1) {
						final Time t = Time.parse(s);
						if (t == null)
							return null;
						return new Timeperiod(t.getTicks());
					}
					final Time t1 = Time.parse("" + s.substring(0, c).trim());
					final Time t2 = Time.parse("" + s.substring(c + 1).trim());
					if (t1 == null || t2 == null)
						return null;
					return new Timeperiod(t1.getTicks(), t2.getTicks());
				}

				@Override
				public String toString(final Timeperiod o, final int flags) {
					return o.toString();
				}

				@Override
				public String toVariableNameString(final Timeperiod o) {
					return "timeperiod:" + o.start + "-" + o.end;
				}
			}).serializer(new YggdrasilSerializer<Timeperiod>()));

		Classes.registerClass(new ClassInfo<Date>(Date.class, "date")
			.user("dates?")
			.name("Date")
			.description("A date is a certain point in the real world's time which can be obtained with <a href='./expressions.html#ExprNow'>now expression</a>, <a href='./expressions.html#ExprUnixDate'>unix date expression</a> and <a href='./functions.html#date'>date function</a>.",
				"See <a href='#time'>time</a> and <a href='#timespan'>timespan</a> for the other time types of Skript.")
			.usage("")
			.examples("set {_yesterday} to now",
				"subtract a day from {_yesterday}",
				"# now {_yesterday} represents the date 24 hours before now")
			.since("1.4")
			.serializer(new YggdrasilSerializer<Date>()));

		Classes.registerClass(new ClassInfo<Color>(Color.class, "color")
			.user("colou?rs?")
			.name("Color")
			.description("Wool, dye and chat colors.")
			.usage("black, dark grey/dark gray, grey/light grey/gray/light gray/silver, white, blue/dark blue, cyan/aqua/dark cyan/dark aqua, light blue/light cyan/light aqua, green/dark green, light green/lime/lime green, yellow/light yellow, orange/gold/dark yellow, red/dark red, pink/light red, purple/dark purple, magenta/light purple, brown/indigo")
			.examples("color of the sheep is red or black",
				"set the color of the block to green",
				"message \"You're holding a <%color of tool%>%color of tool%<reset> wool block\"")
			.since("")
			.supplier(SkriptColor.values())
			.parser(new Parser<Color>() {
				@Override
				@Nullable
				public Color parse(String input, ParseContext context) {
					Color rgbColor = ColorRGB.fromString(input);
					if (rgbColor != null)
						return rgbColor;
					return SkriptColor.fromName(input);
				}

				@Override
				public String toString(Color c, int flags) {
					return c.getName();
				}

				@Override
				public String toVariableNameString(Color color) {
					return "" + color.getName().toLowerCase(Locale.ENGLISH).replace('_', ' ');
				}
			}));
	}

}
