package ultreon.baseskript.events;

import ch.njol.skript.Skript;
import ultreon.baseskript.event.Cancellable;

import java.io.File;

public class LoadLanguageFilesEvent implements Cancellable {
	private final Skript skript;
	private final File lang;
	private boolean cancelled;

	public LoadLanguageFilesEvent(Skript skript, File lang) {
		this.skript = skript;
		this.lang = lang;
	}

	public Skript getSkript() {
		return skript;
	}

	public File getLang() {
		return lang;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
