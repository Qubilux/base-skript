package ultreon.baseskript;

import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.skript.util.chat.MessageComponent;
import org.apache.logging.log4j.Level;

public class ConsoleCommandSender implements CommandSender {
    @Override
    public void sendMessage(String message) {
		SkriptLogger.LOGGER.log(Level.INFO, ChatMessages.parse(message).stream().map(MessageComponent::toString).reduce((a, b) -> a + "\n" + b).orElseThrow());
    }

	@Override
	public String getName() {
		return "console";
	}
}
