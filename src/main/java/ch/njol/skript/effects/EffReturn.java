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
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.ScriptFunction;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Return")
@Description("Makes a function return a value")
@Examples({
	"function double(i: number) :: number:",
		"\treturn 2 * {_i}",
	"",
	"function divide(i: number) returns number:",
		"\treturn {_i} / 2"
})
@Since("2.2, 2.8.0 (returns aliases)")
public class EffReturn extends Effect {
	
	static {
		Skript.registerEffect(EffReturn.class, "return %objects%");
	}
	
	private ScriptFunction<?> function;
	
	private Expression<?> value;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		ScriptFunction<?> f = Functions.currentFunction;
		if (f == null) {
			Skript.error("The return statement can only be used in a function");
			return false;
		}
		
		if (!isDelayed.isFalse()) {
			Skript.error("A return statement after a delay is useless, as the calling trigger will resume when the delay starts (and won't get any returned value)");
			return false;
		}
		
		function = f;
		ClassInfo<?> returnType = function.getReturnType();
		if (returnType == null) {
			Skript.error("This function doesn't return any value. Please use 'stop' or 'exit' if you want to stop the function.");
			return false;
		}
		
		RetainingLogHandler log = SkriptLogger.startRetainingLog();
		Expression<?> convertedExpr;
		try {
			convertedExpr = exprs[0].getConvertedExpression(returnType.getC());
			if (convertedExpr == null) {
				log.printErrors("This function is declared to return " + returnType.getName().withIndefiniteArticle() + ", but " + exprs[0].toString(null, false) + " is not of that type.");
				return false;
			}
			log.printLog();
		} finally {
			log.stop();
		}
		
		if (f.isSingle() && !convertedExpr.isSingle()) {
			Skript.error("This function is defined to only return a single " + returnType + ", but this return statement can return multiple values.");
			return false;
		}
		value = convertedExpr;
		
		return true;
	}
	
	@Override
	@Nullable
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected TriggerItem walk(@NotNull Object event) {
		debug(event, false);
		if (event instanceof FunctionEvent) {
			((ScriptFunction) function).setReturnValue(value.getArray(event));
		} else {
			assert false : event;
		}

		TriggerSection parent = getParent();
		while (parent != null) {
			if (parent instanceof LoopSection)
				((LoopSection) parent).exit(event);

			parent = parent.getParent();
		}

		return null;
	}
	
	@Override
	protected void execute(@NotNull Object event) {
		assert false;
	}
	
	@Override
	public @NotNull String toString(@Nullable Object event, boolean debug) {
		return "return " + value.toString(event, debug);
	}
	
}
