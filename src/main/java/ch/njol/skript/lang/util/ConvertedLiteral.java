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
package ch.njol.skript.lang.util;

import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Checker;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.util.coll.iterator.ArrayIterator;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.ConverterInfo;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.Iterator;

/**
 * @see SimpleLiteral
 */
public class ConvertedLiteral<F, T> extends ConvertedExpression<F, T> implements Literal<T> {

	protected transient T[] data;

	@SuppressWarnings("unchecked")
	public ConvertedLiteral(Literal<F> source, T[] data, Class<T> to) {
		super(source, to, new ConverterInfo<F, T>((Class<F>) source.getReturnType(), to, from -> Converters.convert(from, to), 0));
		this.data = data;
		assert data.length > 0;
	}

	@Override
	@Nullable
	@SuppressWarnings("unchecked")
	public <R> Literal<? extends R> getConvertedExpression(Class<R>... to) {
		if (CollectionUtils.containsSuperclass(to, this.to))
			return (Literal<? extends R>) this;
		return ((Literal<F>) source).getConvertedExpression(to);
	}

	@Override
	public String toString(@Nullable Object event, boolean debug) {
		return Classes.toString(data, getAnd());
	}

	@Override
	public T[] getArray() {
		return data;
	}

	@Override
	public T[] getAll() {
		return data;
	}

	@Override
	public T[] getArray(Object event) {
		return getArray();
	}

	@Override
	public T getSingle() {
		if (getAnd() && data.length > 1)
			throw new SkriptAPIException("Call to getSingle on a non-single expression");
		return CollectionUtils.getRandom(data);
	}

	@Override
	public T getSingle(Object event) {
		return getSingle();
	}

	@Override
	@Nullable
	public Iterator<T> iterator(Object event) {
		return new ArrayIterator<T>(data);
	}

	@Override
	public boolean check(Object event, Checker<? super T> checker) {
		return SimpleExpression.check(data, checker, false, getAnd());
	}

	@Override
	public boolean check(Object event, Checker<? super T> checker, boolean negated) {
		return SimpleExpression.check(data, checker, negated, getAnd());
	}

}
