package dev.ultreon.baseskript;

import java.util.Objects;

public final class MessageEvent {
	private final CommandSender sender;
	private final String info;

	public MessageEvent(CommandSender sender, String info) {
		this.sender = sender;
		this.info = info;
	}

	public CommandSender sender() {
		return sender;
	}

	public String info() {
		return info;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		MessageEvent that = (MessageEvent) obj;
		return Objects.equals(this.sender, that.sender) &&
			Objects.equals(this.info, that.info);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sender, info);
	}

	@Override
	public String toString() {
		return "MessageEvent[" +
			"sender=" + sender + ", " +
			"info=" + info + ']';
	}

}
