package dev.ultreon.baseskript;

import java.util.Objects;

public final class AdminBroadcastEvent {
	private final String message;

	public AdminBroadcastEvent(String message) {
		this.message = message;
	}

	public String message() {
		return message;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		AdminBroadcastEvent that = (AdminBroadcastEvent) obj;
		return Objects.equals(this.message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message);
	}

	@Override
	public String toString() {
		return "AdminBroadcastEvent[" +
			"message=" + message + ']';
	}

}
