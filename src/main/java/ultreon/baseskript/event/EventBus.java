package ultreon.baseskript.event;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.libs.events.v0.EventPriority;
import com.ultreon.libs.events.v0.ICancellable;
import com.ultreon.libs.events.v0.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import ultreon.baseskript.BaseSkript;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class EventBus {
	protected static final Predicate<Method> classPredicate;

	protected static final Predicate<Method> instancePredicate;
	private static int id;

	static {
		Predicate<Method> isSubscriber = EventBus::isSubscriber;
		Predicate<Method> isSubscribing = EventBus::isSubscribing;
		classPredicate = isSubscriber.and(isSubscribing).and((method) -> Modifier.isStatic(method.getModifiers()));
		instancePredicate = isSubscriber.and(isSubscribing).and((method) -> !Modifier.isStatic(method.getModifiers()));
	}

	private final List<Subscription> subscriptions = new ArrayList<>();
	private final Map<Consumer<?>, Subscription> consumer2subscription = new HashMap<>();
	private final Map<Object, CopyOnWriteArraySet<Subscription>> object2subscriptions = new ConcurrentHashMap<>();
	private final Map<Class<?>, CopyOnWriteArraySet<Subscription>> class2subscriptions = new ConcurrentHashMap<>();

	private final Map<Class<?>, CopyOnWriteArraySet<Subscriber<?>>> event2subscribers = new ConcurrentHashMap<>();
	private final Map<Subscriber<?>, CopyOnWriteArraySet<Class<?>>> subscriber2events = new ConcurrentHashMap<>();

	private static boolean isSubscribing(Method method) {
//        LogManager.getLogger("Subscribe-Check").info(method.getDeclaringClass().getName() + "." + method.getName());

		return method.isAnnotationPresent(SubscribeEvent.class);
	}

	private static boolean isSubscriber(Method method) {
		Class<?>[] parameterObjectypes = method.getParameterTypes();
		if (parameterObjectypes.length == 1) {
			Class<?> clazz1 = parameterObjectypes[0];
			return Object.class.isAssignableFrom(clazz1);
		}
		return false;
	}

	@SuppressWarnings("UnusedReturnValue")
	public <E> boolean publish(E event) {
		if (!this.event2subscribers.containsKey(event.getClass())) {
			return false;
		}

		CopyOnWriteArraySet<Subscriber<?>> methods = this.event2subscribers.get(event.getClass());
		for (Subscriber<?> subscriber : methods) {
			if (subscriber == null) {
				continue;
			}

			try {
				subscriber.handle0(event);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}

		return event instanceof ICancellable && ((ICancellable) event).isCancelled();
	}

	public void subscribe(Class<?> clazz) {
		this.loopDeclaredMethods(clazz, (method) -> {
			// Get types and values.
			Class<?> event = method.getParameterTypes()[0];
			this.addHandlers(event, null, method);
		});
	}

	public void subscribe(Object o) {
		this.loopMethods(o, (method) -> {
			// Get types and values.
			Class<?> event = method.getParameterTypes()[0];
			this.addHandlers(event, o, method);
		});
	}

	private void loopDeclaredMethods(Class<?> clazz, Consumer<Method> consumer) {
		// Loop declared methods.
		this.loopMethods0(clazz.getDeclaredMethods(), classPredicate, consumer);
	}

	private void loopMethods(Object o, Consumer<Method> consumer) {
		// Loop methods.
		this.loopMethods0(o.getClass().getMethods(), instancePredicate, consumer);
	}

	private void loopMethods0(Method[] methods, Predicate<Method> predicate, Consumer<Method> consumer) {
		// Check all methods for event subscribers.
		for (Method method : methods) {
			// Check is instance method.
			if (predicate.test(method)) {
				// Set accessible.
				method.setAccessible(true);
				consumer.accept(method);
			}
		}
	}

	@CanIgnoreReturnValue
	public <T> Subscription subscribe(Class<T> eventClass, Consumer<T> subscriber) {
		return subscribe(EventPriority.NORMAL, eventClass, subscriber);
	}

	@CanIgnoreReturnValue
	public <T> Subscription subscribe(EventPriority priority, Class<T> eventClass, Consumer<T> subscriber) {
		return subscribe(priority, true, eventClass, subscriber);
	}

	@CanIgnoreReturnValue
	public <T> Subscription subscribe(Consumer<T> subscriber, T... typeGetter) {
		return subscribe(EventPriority.NORMAL, subscriber, typeGetter);
	}

	@CanIgnoreReturnValue
	public <T> Subscription subscribe(EventPriority priority, Consumer<T> subscriber, T... typeGetter) {
		return subscribe(priority, true, subscriber, typeGetter);
	}

	@CanIgnoreReturnValue
	public <T> Subscription subscribe(EventPriority priority, boolean ignoreCancelled, Consumer<T> subscriber, T... typeGetter) {
		return subscribe(priority, ignoreCancelled, (Class<T>) typeGetter.getClass().getComponentType(), subscriber);
	}

	@CanIgnoreReturnValue
	public <T> Subscription subscribe(EventPriority priority, boolean ignoreCancelled, Class<T> eventClass, Consumer<T> subscriber) {
		Subscriber<T> e = new Subscriber<T>() {
			@Override
			public void handle(T e) {
				subscriber.accept(e);
			}

			@Override
			public EventPriority getPriority() {
				return priority;
			}

			@Override
			public Class<T> getType() {
				return eventClass;
			}
		};
		this.event2subscribers.computeIfAbsent(eventClass, k -> new CopyOnWriteArraySet<>()).add(e);

		return new Subscription() {
			@Override
			protected void onRemove() {
				event2subscribers.get(eventClass).remove(e);
				subscriber2events.get(e).remove(eventClass);
			}

			@Override
			public Subscriber<?> getSubscriber(Class<?> clazz) {
				return e;
			}
		};
	}

	public void unsubscribe(Subscription subscription) {
		boolean remove = this.subscriptions.remove(subscription);
		if (remove) {
			subscription.unsubscribe();
		}
	}

	public void unsubscribe(Object o) {
		CopyOnWriteArraySet<Subscription> remove = this.object2subscriptions.remove(o);
		if (remove != null) {
			for (Subscription subscription : remove) {
				subscription.unsubscribe();
			}
		}
	}

	public void unsubscribe(Class<?> clazz) {
		CopyOnWriteArraySet<Subscription> remove = this.class2subscriptions.remove(clazz);
		if (remove != null) {
			for (Subscription subscription : remove) {
				subscription.unsubscribe();
			}
		}
	}

	public void unsubscribe(Consumer<?> consumer) {
		Subscription remove = this.consumer2subscription.remove(consumer);
		if (remove != null) {
			remove.unsubscribe();
		}
	}

	protected void addHandlers(Class<?> event, @Nullable Object obj, Method method) {
		Consumer<Object> subscriberFunc = o -> {
			try {
				method.invoke(obj, o);
			} catch (Throwable t) {
				BaseSkript.LOGGER.error("Failed to invoke event subscriber", t);
			}
		};

		Subscriber<?> subscriber = new Subscriber() {
			@Override
			public void handle(Object e) {
				subscriberFunc.accept(e);
			}

			@Override
			public EventPriority getPriority() {
				return EventPriority.NORMAL;
			}

			@Override
			public Class<?> getType() {
				return event;
			}
		};

		if (!this.event2subscribers.containsKey(event))
			this.event2subscribers.put(event, new CopyOnWriteArraySet<>());

		if (!this.subscriber2events.containsKey(subscriber))
			this.subscriber2events.put(subscriber, new CopyOnWriteArraySet<>());

		this.event2subscribers.get(event).add(subscriber);
		this.subscriber2events.get(subscriber).add(event);

		if (obj == null) {
			this.class2subscriptions.computeIfAbsent(event, k -> new CopyOnWriteArraySet<>()).add(new Subscription() {
				@Override
				protected void onRemove() {
					event2subscribers.get(event).remove(subscriber);
					subscriber2events.get(subscriber).remove(event);
				}

				@Override
				public Subscriber<?> getSubscriber(Class<?> clazz) {
					return subscriber;
				}
			});
		} else {
			this.object2subscriptions.computeIfAbsent(obj, k -> new CopyOnWriteArraySet<>()).add(new Subscription() {
				@Override
				protected void onRemove() {
					event2subscribers.get(event).remove(subscriber);
					subscriber2events.get(subscriber).remove(event);
				}

				@Override
				public Subscriber<?> getSubscriber(Class<?> clazz) {
					return subscriber;
				}
			});
		}
	}

	public static abstract class Subscription {
		protected abstract void onRemove();

		public abstract Subscriber<?> getSubscriber(Class<?> clazz);

		public void unsubscribe() {
			this.onRemove();
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		void onPublish(Object event) {

		}
	}
}
