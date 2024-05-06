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
package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionList;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
@Name("Exists/Is Set")
@Description("Checks whether a given expression or variable is set.")
@Examples({"{teams::%player's uuid%::preferred-team} is not set",
		"on damage:",
		"	projectile exists",
		"	broadcast \"%attacker% used a %projectile% to attack %victim%!\""})
@Since("1.2")
public class CondIsSet extends Condition {
	static {
		Skript.registerCondition(CondIsSet.class,
				"%~objects% (exist[s]|(is|are) set)",
				"%~objects% (do[es](n't| not) exist|(is|are)(n't| not) set)");
	}
	
	@SuppressWarnings("null")
	private Expression<?> expr;
	
	@SuppressWarnings("null")
	@Override
	public boolean init(final Expression<?> @NotNull [] exprs, final int matchedPattern, final @NotNull Kleenean isDelayed, final @NotNull ParseResult parseResult) {
		expr = exprs[0];
		setNegated(matchedPattern == 1);
		return true;
	}
	
	private boolean check(final Expression<?> expr, final Object e) {
		if (expr instanceof ExpressionList) {
			for (final Expression<?> ex : ((ExpressionList<?>) expr).getExpressions()) {
				assert ex != null;
				final boolean b = check(ex, e);
				if (expr.getAnd() ^ b)
					return !expr.getAnd();
			}
			return expr.getAnd();
		}
		assert expr.getAnd();
		final Object[] all = expr.getAll(e);
		return isNegated() ^ (all.length != 0);
	}
	
	@Override
	public boolean check(final @NotNull Object e) {
		return check(expr, e);
	}
	
	@Override
	public @NotNull String toString(final @Nullable Object e, final boolean debug) {
		return expr.toString(e, debug) + (isNegated() ? " isn't" : " is") + " set";
	}
	
}
