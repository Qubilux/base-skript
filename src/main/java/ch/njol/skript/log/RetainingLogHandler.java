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
package ch.njol.skript.log;

import ch.njol.skript.Skript;
import ultreon.baseskript.CommandSender;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Peter Güttinger
 */
public class RetainingLogHandler extends LogHandler {
	
	private final Deque<LogEntry> log = new LinkedList<LogEntry>();
	private int numErrors = 0;
	
	boolean printedErrorOrLog = false;
	
	@Override
	public LogResult log(LogEntry entry) {
		log.add(entry);
		if (entry.getLevel().intLevel() >= Level.FATAL.intLevel())
			numErrors++;
		printedErrorOrLog = false;
		return LogResult.CACHED;
	}
	
	@Override
	public void onStop() {
		if (!printedErrorOrLog && Skript.testing())
			SkriptLogger.LOGGER.warn("Retaining log wasn't instructed to print anything at {}", SkriptLogger.getCaller());
	}
	
	@Override
	public RetainingLogHandler start() {
		SkriptLogger.startLogHandler(this);
		return this;
	}
	
	public final boolean printErrors() {
		return printErrors(null);
	}
	
	/**
	 * Prints all retained errors or the given one if no errors were retained.
	 * <p>
	 * This handler is stopped if not already done.
	 * 
	 * @param def Error to print if no errors were logged, can be null to not print any error if there are none
	 * @return Whether there were any errors
	 */
	public final boolean printErrors(@Nullable String def) {
		return printErrors(def, ErrorQuality.SEMANTIC_ERROR);
	}
	
	public final boolean printErrors(@Nullable String def, ErrorQuality quality) {
		assert !printedErrorOrLog;
		printedErrorOrLog = true;
		stop();
		
		boolean hasError = false;
		for (LogEntry e : log) {
			if (e.getLevel().intLevel() >= Level.FATAL.intLevel()) {
				SkriptLogger.log(e);
				hasError = true;
			} else {
				e.discarded("not printed");
			}
		}
		
		if (!hasError && def != null)
			SkriptLogger.log(SkriptLogger.FATAL, def);
		
		return hasError;
	}
	
	/**
	 * Sends all retained error messages to the given recipient.
	 * <p>
	 * This handler is stopped if not already done.
	 * 
	 * @param recipient
	 * @param def Error to send if no errors were logged, can be null to not print any error if there are none
	 * @return Whether there were any errors to send
	 */
	public final boolean printErrors(CommandSender recipient, @Nullable String def) {
		assert !printedErrorOrLog;
		printedErrorOrLog = true;
		stop();

		boolean hasError = false;
		for (LogEntry e : log) {
			if (e.getLevel().intLevel() >= Level.FATAL.intLevel()) {
				SkriptLogger.sendFormatted(recipient, e.toFormattedString());
				e.logged();
				hasError = true;
			} else {
				e.discarded("not printed");
			}
		}
		
		if (!hasError && def != null) {
			SkriptLogger.sendFormatted(recipient, def);
		}
		return hasError;
	}
	
	/**
	 * Prints all retained log messages.
	 * <p>
	 * This handler is stopped if not already done.
	 */
	public final void printLog() {
		assert !printedErrorOrLog;
		printedErrorOrLog = true;
		stop();
		SkriptLogger.logAll(log);
	}
	
	public boolean hasErrors() {
		return numErrors != 0;
	}
	
	@Nullable
	public LogEntry getFirstError() {
		for (LogEntry e : log) {
			if (e.getLevel().intLevel() >= Level.FATAL.intLevel())
				return e;
		}
		return null;
	}
	
	public LogEntry getFirstError(String def) {
		for (LogEntry e : log) {
			if (e.getLevel().intLevel() >= Level.FATAL.intLevel())
				return e;
		}
		return new LogEntry(SkriptLogger.FATAL, def);
	}
	
	/**
	 * Clears the list of retained log messages.
	 */
	public void clear() {
		for (LogEntry e : log)
			e.discarded("cleared");
		log.clear();
		numErrors = 0;
	}
	
	public int size() {
		return log.size();
	}
	
	@SuppressWarnings("null")
	public Collection<LogEntry> getLog() {
		// if something is grabbing the log entries, they're probably handling them manually
		printedErrorOrLog = true;
		return Collections.unmodifiableCollection(log);
	}
	
	public Collection<LogEntry> getErrors() {
		Collection<LogEntry> r = new ArrayList<LogEntry>();
		for (LogEntry e : log) {
			if (e.getLevel().intLevel() >= Level.FATAL.intLevel())
				r.add(e);
		}
		return r;
	}
	
	public int getNumErrors() {
		return numErrors;
	}
	
}
