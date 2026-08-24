package me.videogamesm12.w2k.blackbox.window.menu.w2k;

import lombok.Getter;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.util.JComponents;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.module.WModule;
import net.fabricmc.loader.api.ModContainer;

import javax.swing.*;
import java.util.Map;

public class ModulesMenu extends JMenu
{
    public ModulesMenu()
    {
        super("Modules");
        //--
        final Map<ModContainer, Map<String, WModule>> moduleRegistry = W2K.getInstance().getModuleManager().getRegistry();
        //--
        // Show (none) if no modules were registered
        if (moduleRegistry.isEmpty())
        {
            final JMenuItem item = JComponents.createMenuItem("(none)",
                    null,
                    () -> JOptionPane.showMessageDialog(Blackbox.getInstance().getMainWindow(),
                            "Every breath you take\n" +
                            "And every move you make\n" +
                            "Every bond you break\n" +
                            "Every step you take\n" +
                            "I'll be raiding you",
                            "Important",
                            JOptionPane.INFORMATION_MESSAGE));
            item.setEnabled(false);
            add(item);
        }
        // Show all the modules if there's only one module
        else if (moduleRegistry.size() == 1)
        {
            moduleRegistry.values().forEach(registry -> registry.values().forEach(module -> add(new ModuleMenu<>(module))));
        }
        // Otherwise, split them by the mods which provided them
        else
        {
            W2K.getInstance().getModuleManager().getRegistry().forEach((mod, registry) ->
            {
                final JMenu providerMenu = new JMenu(mod.getMetadata().getName());
                registry.values().forEach(module -> providerMenu.add(new ModuleMenu<>(module)));
                add(providerMenu);
            });
        }
    }

    public static class ModuleMenu<T extends WModule> extends JMenu
    {
        @Getter
        private final T module;
        private final JCheckBoxMenuItem enabledItem;

        public ModuleMenu(final T module)
        {
            super(module.getName());
            setToolTipText(module.getDescription());
            //--
            this.module = module;
            this.enabledItem = JComponents.createCheckboxMenuItem("Enabled",
                    null,
                    () -> this.module.isEnabled(),
                    this.module::setEnabled);
            add(enabledItem);
            W2K.getEventBus().register(this);
        }

        /*@Subscribe
        public void onModuleToggled(ModuleToggledEvent event)
        {
            if (event.getModuleClass().equals(module.getClass()))
                enabledItem.setSelected(event.isEnabledNow());
        }*/
    }

    /*private final List<Class<? extends Module>> inList = new ArrayList<>();

    public ModulesMenu()
    {
        super("Modules");
        //--
        W2K.getModuleManager().getModules().values().forEach(module -> add(new ModuleMenu<>(module)));
        W2K.getEventBus().register(this);
    }

    @Subscribe
    public <T extends Module> void onModuleRegistered(ModuleRegisteredEvent<T> event)
    {
        if (!inList.contains(event.getModuleClass()))
        {
            add(new ModuleMenu<>(W2K.getModuleManager().getModule(event.getModuleClass())));
            inList.add(event.getModuleClass());
        }
    }

    @Subscribe
    public void onModuleUnregistered(ModuleUnregisteredEvent<?> event)
    {
        if (inList.contains(event.getModuleClass()))
        {
            Arrays.stream(getMenuComponents()).filter(menu -> menu instanceof ModuleMenu<?> mmenu &&
                    mmenu.getModuleClass().equals(event.getModuleClass())).forEach(this::remove);
            inList.remove(event.getModuleClass());
        }
    }

    public static class ModuleMenu<T extends Module> extends JMenu
    {
        @Getter
        private final Class<T> moduleClass;
        @Getter
        private final T module;
        private final JCheckBoxMenuItem enabledItem;

        public ModuleMenu(T module)
        {
            super(module.getMeta().name());
            setToolTipText(module.getMeta().description());
            //--
            this.moduleClass = (Class<T>) module.getClass();
            this.module = module;
            this.enabledItem = new JCheckBoxMenuItem("Enabled", module.isEnabled());
            this.enabledItem.addActionListener(action ->
            {
                if (module.isEnabled())
                {
                    module.disable();
                }
                else
                {
                    module.enable();
                }
            });
            //--
            add(enabledItem);
            //--
            W2K.getEventBus().register(this);
        }

        @Subscribe
        public void onModuleToggled(ModuleToggledEvent event)
        {
            if (event.getModuleClass().equals(module.getClass()))
                enabledItem.setSelected(event.isEnabledNow());
        }
    }*/
}
