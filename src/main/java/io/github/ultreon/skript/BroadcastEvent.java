package io.github.ultreon.skript;

import java.util.Objects;

public final class BroadcastEvent {
	private final String message;
	private final String permission;

	public BroadcastEvent(String message, String permission) {
		this.message = message;
		this.permission = permission;
	}

	public String message() {
		return message;
	}

	public String permission() {
		return permission;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		BroadcastEvent that = (BroadcastEvent) obj;
		return Objects.equals(this.message, that.message) &&
			Objects.equals(this.permission, that.permission);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message, permission);
	}

	@Override
	public String toString() {
		return "BroadcastEvent[" +
			"message=" + message + ", " +
			"permission=" + permission + ']';
	}

}
