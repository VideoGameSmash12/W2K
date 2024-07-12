package me.videogamesm12.w2k.kernel.driver.base;

/**
 * <h1>WEventPassThruDriver</h1>
 * <p>Interface that is used as a pass-through for Fabric API events required by W2K to function correctly.</p>
 * <p>Despite being functionally identical, the Legacy Fabric API event system is not compatible with the regular Fabric
 * API event system. To maintain compatibility between Fabric and Legacy Fabric, we use neither APIs and instead pass
 * their events through our own event system from Google's EventBus.</p>
 */
public interface WEventPassThruDriver extends WDriver
{
    default void setupEvents()
    {
        setupStartedEvent();
        setupStoppedEvent();
    }

    void setupStartedEvent();

    void setupStoppedEvent();
}
