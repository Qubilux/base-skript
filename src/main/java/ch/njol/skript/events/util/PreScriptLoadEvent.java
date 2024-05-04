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
package ch.njol.skript.events.util;

import java.util.List;

import ch.njol.skript.config.Config;
import ch.njol.util.Validate;
import ultreon.baseskript.event.Event;

/**
 * This event has no guarantee of being on the main thread.
 * Please do not use bukkit api before checking {@link Bukkit#isPrimaryThread()}
 */

public class PreScriptLoadEvent extends Event {

    private final List<Config> scripts;

    public PreScriptLoadEvent(List<Config> scripts) {
        Validate.notNull(scripts);
        this.scripts = scripts;
    }

    public List<Config> getScripts() {
    	return scripts;
	}

}
