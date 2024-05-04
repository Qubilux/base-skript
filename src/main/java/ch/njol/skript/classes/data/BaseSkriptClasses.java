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

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import io.github.ultreon.skript.CommandSender;
import org.eclipse.jdt.annotation.Nullable;

import java.util.regex.Pattern;

/**
 * @author Peter Güttinger
 */
public class BaseSkriptClasses {

	public BaseSkriptClasses() {
	}

	public static final Pattern UUID_PATTERN = Pattern.compile("(?i)[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");

	static {
		Classes.registerClass(new ClassInfo<CommandSender>(CommandSender.class, "commandsender")
			.user("((commands?)? ?)?(sender|executor)s?")
			.name("Command Sender")
			.description("A player or the console.")
			.usage("use <a href='expressions.html#LitConsole'>the console</a> for the console",
				"see <a href='#player'>player</a> for players.")
			.examples("command /push [&lt;player&gt;]:",
				"\ttrigger:",
				"\t\tif arg-1 is not set:",
				"\t\t\tif command sender is console:",
				"\t\t\t\tsend \"You can't push yourself as a console :\\\" to sender",
				"\t\t\t\tstop",
				"\t\t\tpush sender upwards with force 2",
				"\t\t\tsend \"Yay!\"",
				"\t\telse:",
				"\t\t\tpush arg-1 upwards with force 2",
				"\t\t\tsend \"Yay!\" to sender and arg-1")
			.since("1.0")
			.defaultExpression(new EventValueExpression<CommandSender>(CommandSender.class))
			.parser(new Parser<CommandSender>() {
				@Override
				@Nullable
				public CommandSender parse(final String s, final ParseContext context) {
					return null;
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return false;
				}

				@Override
				public String toString(final CommandSender s, final int flags) {
					return s.getName();
				}

				@Override
				public String toVariableNameString(final CommandSender s) {
					return s.getName();
				}
			}));
	}
}
