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
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.ultreon.baseskript.event.Cancellable;

/**
 * @author Peter Güttinger
 */
@Name("Cancel Event")
@Description("Cancels the event (e.g. prevent blocks from being placed, or damage being taken).")
@Examples({"on damage:",
		"	victim is a player",
		"	victim has the permission \"skript.god\"",
		"	cancel the event"})
@Since("1.0")
public class EffCancelEvent extends Effect {
	static {
		Skript.registerEffect(EffCancelEvent.class, "cancel [the] event", "uncancel [the] event");
	}
	
	private boolean cancel;
	
	@SuppressWarnings("null")
	@Override
	public boolean init(final Expression<?> @NotNull [] vars, final int matchedPattern, final @NotNull Kleenean isDelayed, final @NotNull ParseResult parser) {
		if (isDelayed == Kleenean.TRUE) {
			Skript.error("Can't cancel an event anymore after it has already passed", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		cancel = matchedPattern == 0;
		final Class<? extends Object>[] es = getParser().getCurrentEvents();
		if (es == null)
			return false;
		for (final Class<? extends Object> e : es) {
			if (Cancellable.class.isAssignableFrom(e))
				return true; // TODO warning if some event(s) cannot be cancelled even though some can (needs a way to be suppressed)
		}
		Skript.error(Utils.A(getParser().getCurrentEventName()) + " event cannot be cancelled", ErrorQuality.SEMANTIC_ERROR);
		return false;
	}
	
	@Override
	public void execute(final @NotNull Object e) {
		if (e instanceof Cancellable)
			((Cancellable) e).setCancelled(cancel);
	}
	
	@Override
	public @NotNull String toString(final @Nullable Object e, final boolean debug) {
		return (cancel ? "" : "un") + "cancel event";
	}
	
}
