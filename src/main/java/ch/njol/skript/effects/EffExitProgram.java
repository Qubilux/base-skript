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
package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.ultreon.baseskript.BaseSkript;

/**
 * @author Peter Güttinger
 */
@Name("Exit Program")
@Description({"Exit the program with or without a custom exit code."})
@Examples({
	"log \"Hello World\" to the console",
	"exit program"
})
@Since("3.0")
public class EffExitProgram extends Effect {
	static {
		Skript.registerEffect(EffExitProgram.class,
			"exit (program|app|application)",
			"exit (program|app|application) with code %number%"
		);
	}

	@Nullable
	private Expression<Number> numbers;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?> @NotNull [] exprs, final int matchedPattern, final @NotNull Kleenean isDelayed, final @NotNull ParseResult parser) {
		if (matchedPattern == 1) {
			numbers = (Expression<Number>) exprs[0];
		} else {
			numbers = null;
		}
		return true;
	}

	@Override
	protected void execute(final @NotNull Object e) {
		if (numbers != null) {
			int code = numbers.getSingle(e).intValue();
			BaseSkript.exit(code);
		} else {
			BaseSkript.exit(0);
		}
	}

	@Override
	public @NotNull String toString(final @Nullable Object e, final boolean debug) {
		Expression<Number> numbers1 = numbers;
		if (numbers1 == null) {
			return "exit program";
		}
		@Nullable Number single = numbers1.getSingle(e);
		return "exit program" + (single == null ? "" : " with code " + single);

	}
}
