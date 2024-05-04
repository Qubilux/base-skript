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
package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.util.ExceptionUtils;
import ch.njol.util.Kleenean;
import io.github.ultreon.skript.event.Event;
import org.apache.logging.log4j.Level;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.script.Script;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author Peter Güttinger
 */
@Name("LogToConsole")
@Description({"Writes text to the console. Skript will write these files to /.skript/logs."})
@Examples({"on place of TNT:",
		"	log \"%player% placed TNT in %world% at %location of block%\" to the console"})
@Since("2.0")
public class EffLogToConsole extends Effect {
	static {
		Skript.registerEffect(EffLogToConsole.class, "log %strings% [to the (console|terminal|output|log)]");
	}
	
	private final static File logsFolder = new File(Skript.getInstance().getDataFolder(), "logs");
	
	final static HashMap<String, PrintWriter> writers = new HashMap<>();
	static {
		Skript.closeOnDisable(() -> {
			for (final PrintWriter pw : writers.values())
				pw.close();
		});
	}
	
	@SuppressWarnings("null")
	private Expression<String> messages;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final @NotNull Kleenean isDelayed, final @NotNull ParseResult parser) {
		messages = (Expression<String>) exprs[0];
		return true;
	}
	
	@Override
	protected void execute(final @NotNull Event e) {
		for (final String message : messages.getArray(e)) {
			Trigger t = getTrigger();
			String scriptName = "---";
			if (t != null) {
				Script script = t.getScript();
				if (script != null)
					scriptName = script.getConfig().getFileName();
			}
			Skript.info("[" + scriptName + "] " + message);
		}
	}
	
	@Override
	public @NotNull String toString(final @Nullable Event e, final boolean debug) {
		return "log " + messages.toString(e, debug) + " to the console";
	}
}
