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
package ch.njol.skript;

import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.timings.SkriptTimings;
import ch.njol.skript.util.Task;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.ultreon.libs.events.v0.EventPriority;
import com.ultreon.libs.events.v0.SubscribeEvent;
import ultreon.baseskript.BaseSkript;
import ultreon.baseskript.event.Cancellable;
import ultreon.baseskript.event.EventExecutor;
import ultreon.baseskript.event.Listener;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public final class SkriptEventHandler {

	private SkriptEventHandler() { }

	/**
	 * An event listener for one priority.
	 * Also stores the registered events for this listener, and
	 * the {@link EventBus} to be used with this listener.
	 */
	public static class PriorityListener extends Listener {

		public final EventPriority priority;

		public final EventExecutor executor = (listener, event) -> check(event, ((PriorityListener) listener).priority);

		public PriorityListener(EventPriority priority) {
			this.priority = priority;
		}

		@SubscribeEvent
		public void onEvent(Object event) {
			check(event, priority);
		}
	}

	/**
	 * This method is used for validating that the provided Event may be handled by Skript.
	 * If validation is successful, all Triggers associated with the provided Event are executed.
	 * A Trigger will only be executed if its priority matches the provided EventPriority.
	 * @param event The Event to check.
	 * @param priority The priority of the Event.
	 */
	private static void check(Object event, EventPriority priority) {
		List<Trigger> triggers = getTriggers(event.getClass());
		if (triggers.isEmpty())
			return;

		if (Skript.logVeryHigh()) {
			boolean hasTrigger = false;
			for (Trigger trigger : triggers) {
				SkriptEvent triggerEvent = trigger.getEvent();
				if (
					triggerEvent.getEventPriority() == priority
						&& triggerEvent.canExecuteAsynchronously() ? triggerEvent.check(event) : Boolean.TRUE.equals(Task.callSync(() -> triggerEvent.check(event)))
				) {
					hasTrigger = true;
					break;
				}
			}
			if (!hasTrigger)
				return;
		}

		boolean isCancelled = event instanceof Cancellable && ((Cancellable) event).isCancelled();

		if (isCancelled) {
			if (Skript.logVeryHigh())
				Skript.info(" -x- was cancelled");
			return;
		}

		for (Trigger trigger : triggers) {
			SkriptEvent triggerEvent = trigger.getEvent();
			if (triggerEvent.getEventPriority() != priority)
				continue;

			// these methods need to be run on whatever thread the trigger is
			Runnable execute = () -> {
				Object timing = SkriptTimings.start(trigger.getDebugLabel());
				trigger.execute(event);
				SkriptTimings.stop(timing);
			};

			if (trigger.getEvent().canExecuteAsynchronously()) {
				if (triggerEvent.check(event))
					execute.run();
			} else { // Ensure main thread
				Task.callSync(() -> {
					if (triggerEvent.check(event))
						execute.run();
					return null; // we don't care about a return value
				});
			}
		}
	}

	/**
	 * A utility method to get all Triggers registered under the provided Event class.
	 * @param event The event to find pairs from.
	 * @return A List containing all Triggers registered under the provided Event class.
	 */
	private static List<Trigger> getTriggers(Class<? extends Object> event) {
		return triggers.asMap().entrySet().stream()
			.filter(entry -> entry.getKey().isAssignableFrom(event))
			.flatMap(entry -> entry.getValue().stream())
			.collect(Collectors.toList()); // forces evaluation now and prevents us from having to call getTriggers again if very high logging is enabled
	}

	/**
	 * Stores one {@link PriorityListener} per {@link EventPriority}.
	 */
	private static final PriorityListener[] listeners;

	static {
		EventPriority[] priorities = EventPriority.values();
		listeners = new PriorityListener[priorities.length];
		for (int i = 0; i < priorities.length; i++) {
			listeners[i] = new PriorityListener(priorities[i]);
		}

		for (PriorityListener listener : listeners)
			BaseSkript.getEventBus().subscribe(listener);
	}

	/**
	 * A Multimap tracking what Triggers are paired with what Events.
	 * Each Event effectively maps to an ArrayList of Triggers.
	 */
	private static final Multimap<Class<? extends Object>, Trigger> triggers = ArrayListMultimap.create();

	/**
	 * A utility method that calls {@link #registerBukkitEvent(Trigger, Class)} for each Event class provided.
	 * For specific details of the process, see the javadoc of that method.
	 * @param trigger The Trigger to run when the Event occurs.
	 * @param events The Event to listen for.
	 * @see #registerBukkitEvent(Trigger, Class)
	 * @see #unregisterBukkitEvents(Trigger)
	 */
	public static void registerBukkitEvents(Trigger trigger, Class<? extends Object>[] events) {
		for (Class<? extends Object> event : events)
			registerBukkitEvent(trigger, event);
	}

	/**
	 * Registers a {@link PriorityListener} with Bukkit for the provided Event.
	 * Marks that the provided Trigger should be executed when the provided Event occurs.
	 * @param trigger The Trigger to run when the Event occurs.
	 * @param event The Event to listen for.
	 * @see #registerBukkitEvents(Trigger, Class[])
	 * @see #unregisterBukkitEvents(Trigger)
	 */
	public static void registerBukkitEvent(Trigger trigger, Class<? extends Object> event) {

		EventPriority priority = trigger.getEvent().getEventPriority();

		if (!isEventRegistered(event, trigger)) { // Check if event is registered
			triggers.put(event, trigger);
			BaseSkript.getEventBus().subscribe(priority, false, event, o -> check(o, priority));
		}
	}

	private static boolean isEventRegistered(Class<? extends Object> event, Trigger trigger) {
		for (Trigger eventTrigger : triggers.get(event))
			if (eventTrigger == trigger)
				return true;
		return false;
	}

	/**
	 * Unregisters all events tied to the provided Trigger.
	 * @param trigger The Trigger to unregister events for.
	 */
	public static void unregisterBukkitEvents(Trigger trigger) {
		Iterator<Entry<Class<? extends Object>, Trigger>> entryIterator = triggers.entries().iterator();
		entryLoop: while (entryIterator.hasNext()) {
			Entry<Class<? extends Object>, Trigger> entry = entryIterator.next();
			if (entry.getValue() != trigger)
				continue;
			Class<? extends Object> event = entry.getKey();

			// Remove the trigger from the map
			entryIterator.remove();

			// check if we can unregister the listener
			EventPriority priority = trigger.getEvent().getEventPriority();
			for (Trigger eventTrigger : triggers.get(event)) {
				if (eventTrigger.getEvent().getEventPriority() == priority)
					continue entryLoop;
			}

			// We can attempt to unregister this listener
			Skript skript = Skript.getInstance();
			for (PriorityListener listener : listeners) {
				if (listener.priority != priority)
					continue;
				BaseSkript.getEventBus().unsubscribe(listener);
			}
		}
	}
}
