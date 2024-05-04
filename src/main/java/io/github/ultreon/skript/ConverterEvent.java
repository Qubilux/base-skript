package io.github.ultreon.skript;

public class ConverterEvent<T> {
	private final Class<?> hint;
	private final Class<? extends T> type;

	public ConverterEvent(Class<?> hint, Class<? extends T> type) {
		this.hint = hint;
		this.type = type;
	}

	public Class<?> getHint() {
		return hint;
	}

	public Class<? extends T> getType() {
		return type;
	}
}
