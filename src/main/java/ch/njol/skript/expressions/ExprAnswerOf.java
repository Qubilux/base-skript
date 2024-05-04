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
package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * @author Peter Güttinger
 */
@Name("Question")
@Description("Asks a question and returns the answer.")
@Examples("set {answer} to answer of \"What is your name?\"")
@Since("3.0")
public class ExprAnswerOf extends SimplePropertyExpression<String, String> {
	static {
		register(ExprAnswerOf.class, String.class, "answer", "strings");
	}
	
	private static final Scanner SCANNER = new Scanner(System.in);
	
	@SuppressWarnings("null")
	@Override
	public @Nullable String convert(final String s) {
		System.out.println(s);
		return SCANNER.nextLine();
	}
	
	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}
	
	@Override
	protected @NotNull String getPropertyName() {
		return "answer";
	}
	
}
