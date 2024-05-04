package ultreon.baseskript.event;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean cancel);
}
