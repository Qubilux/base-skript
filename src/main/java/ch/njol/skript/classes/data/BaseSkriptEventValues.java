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
package ch.njol.skript.classes.data;

import ch.njol.skript.events.util.ScriptEvent;
import ch.njol.skript.events.util.SkriptStartEvent;
import ch.njol.skript.events.util.SkriptStopEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.jetbrains.annotations.Nullable;
import ultreon.baseskript.BaseSkript;
import ultreon.baseskript.CommandSender;

/**
 * @author Peter Güttinger
 */
@SuppressWarnings("deprecation")
public final class BaseSkriptEventValues {

	public BaseSkriptEventValues() {
	}

	static {
		// === ServerEvents ===
		// Script load/unload event
		EventValues.registerEventValue(ScriptEvent.class, CommandSender.class, new Getter<CommandSender, ScriptEvent>() {
			@Nullable
			@Override
			public CommandSender get(ScriptEvent e) {
				return BaseSkript.getConsoleSender();
			}
		}, 0);
		// Server load event
		EventValues.registerEventValue(SkriptStartEvent.class, CommandSender.class, new Getter<CommandSender, SkriptStartEvent>() {
			@Nullable
			@Override
			public CommandSender get(SkriptStartEvent e) {
				return BaseSkript.getConsoleSender();
			}
		}, 0);
		// Server stop event
		EventValues.registerEventValue(SkriptStopEvent.class, CommandSender.class, new Getter<CommandSender, SkriptStopEvent>() {
			@Nullable
			@Override
			public CommandSender get(SkriptStopEvent e) {
				return BaseSkript.getConsoleSender();
			}
		}, 0);}

}
