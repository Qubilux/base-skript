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

import ch.njol.skript.util.Date;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Utils;
import ch.njol.util.Math2;
import org.skriptlang.skript.lang.arithmetic.Arithmetics;
import org.skriptlang.skript.lang.arithmetic.Operator;

public class DefaultOperations {

	static {
		// Number - Number
		Arithmetics.registerOperation(Operator.ADDITION, Number.class, (left, right) -> {
			if (Utils.isInteger(left, right))
				return left.longValue() + right.longValue();
			return left.doubleValue() + right.doubleValue();
		});
		Arithmetics.registerOperation(Operator.SUBTRACTION, Number.class, (left, right) -> {
			if (Utils.isInteger(left, right))
				return left.longValue() - right.longValue();
			return left.doubleValue() - right.doubleValue();
		});
		Arithmetics.registerOperation(Operator.MULTIPLICATION, Number.class, (left, right) -> {
			if (Utils.isInteger(left, right))
				return left.longValue() * right.longValue();
			return left.doubleValue() * right.doubleValue();
		});
		Arithmetics.registerOperation(Operator.DIVISION, Number.class, (left, right) -> left.doubleValue() / right.doubleValue());
		Arithmetics.registerOperation(Operator.EXPONENTIATION, Number.class, (left, right) -> {
			if (Utils.isInteger(left, right) && right.longValue() >= 0)
				return (long) Math.pow(left.longValue(), right.longValue());
			return Math.pow(left.doubleValue(), right.doubleValue());
		});
		Arithmetics.registerDifference(Number.class, (left, right) -> {
			if (Utils.isInteger(left, right))
				return Math.abs(left.longValue() - right.longValue());
			return Math.abs(left.doubleValue() - right.doubleValue());
		});
		Arithmetics.registerDefaultValue(Number.class, () -> 0L);

		// Timespan - Timespan
		Arithmetics.registerOperation(Operator.ADDITION, Timespan.class, (left, right) -> new Timespan(Math2.addClamped(left.getMilliSeconds(), right.getMilliSeconds())));
		Arithmetics.registerOperation(Operator.SUBTRACTION, Timespan.class, (left, right) -> new Timespan(Math.max(0, left.getMilliSeconds() - right.getMilliSeconds())));
		Arithmetics.registerDifference(Timespan.class, (left, right) -> new Timespan(Math.abs(left.getMilliSeconds() - right.getMilliSeconds())));
		Arithmetics.registerDefaultValue(Timespan.class, Timespan::new);

		// Timespan - Number
		// Number - Timespan
		Arithmetics.registerOperation(Operator.MULTIPLICATION, Timespan.class, Number.class, (left, right) -> {
			long scalar = right.longValue();
			if (scalar < 0)
				return null;
			return new Timespan(Math2.multiplyClamped(left.getMilliSeconds(), scalar));
		}, (left, right) -> {
			long scalar = left.longValue();
			if (scalar < 0)
				return null;
			return new Timespan(scalar * right.getMilliSeconds());
		});
		Arithmetics.registerOperation(Operator.DIVISION, Timespan.class, Number.class, (left, right) -> {
			long scalar = right.longValue();
			if (scalar <= 0)
				return null;
			return new Timespan(left.getMilliSeconds() / scalar);
		});

		// Date - Timespan
		Arithmetics.registerOperation(Operator.ADDITION, Date.class, Timespan.class, Date::plus);
		Arithmetics.registerOperation(Operator.SUBTRACTION, Date.class, Timespan.class, Date::minus);
		Arithmetics.registerDifference(Date.class, Timespan.class, Date::difference);

	}

}
