package com.haas.easyhunger;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.haas.easyhunger.commands.SetHungerCommand;
import com.haas.easyhunger.components.HungerComponent;
import com.haas.easyhunger.config.EasyHungerConfig;
import com.haas.easyhunger.config.BiomeModifiersConfig;
import com.haas.easyhunger.events.GameModeChangeListener;
import com.haas.easyhunger.events.EasyHungerPlayerReady;
import com.haas.easyhunger.systems.OnDeathSystem;
import com.haas.easyhunger.systems.StarveSystem;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.logging.Level;

public class EasyHunger extends JavaPlugin {
    private static EasyHunger instance;
    private final Config<EasyHungerConfig> config;
    private final Config<BiomeModifiersConfig> biomeConfig;
    private ComponentType<EntityStore, HungerComponent> hungerComponentType;
    private ComponentType<EntityStore, com.haas.easyhunger.components.ThirstComponent> thirstComponentType;

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public EasyHunger(@NonNullDecl JavaPluginInit init) {
        super(init);
        instance = this;
        this.config = this.withConfig("HungerConfig", EasyHungerConfig.CODEC);
        this.biomeConfig = this.withConfig("BiomeModifiers", BiomeModifiersConfig.CODEC);
    }

    @Override
    protected void setup () {
        super.setup();

        this.config.save();
        this.biomeConfig.save();

        // register hunger component
        this.hungerComponentType = this.getEntityStoreRegistry()
                .registerComponent(HungerComponent.class, "HungerComponent", HungerComponent.CODEC);
        
        // register thirst component
        this.thirstComponentType = this.getEntityStoreRegistry()
                .registerComponent(com.haas.easyhunger.components.ThirstComponent.class, "ThirstComponent", com.haas.easyhunger.components.ThirstComponent.CODEC);

        // register starve system
        final var entityStoreRegistry = this.getEntityStoreRegistry();
        entityStoreRegistry.registerSystem(StarveSystem.create());
        entityStoreRegistry.registerSystem(new OnDeathSystem());
        entityStoreRegistry.registerSystem(new com.haas.easyhunger.systems.EasyHungerBlockBreakSystem());
        entityStoreRegistry.registerSystem(new com.haas.easyhunger.systems.EasyHungerJumpSystem());

        // DISABLED: Old Food Handler (replaced by ConsumeFoodInteraction)
        // final com.haas.easyhunger.systems.EasyHungerFoodHandler foodHandler = new com.haas.easyhunger.systems.EasyHungerFoodHandler(this.hungerComponentType);
        // this.getEventRegistry().registerGlobal(com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent.class, foodHandler::handleInventoryChange);
        
        // Thirst Handler
        final com.haas.easyhunger.systems.EasyThirstHandler thirstHandler = new com.haas.easyhunger.systems.EasyThirstHandler(this.thirstComponentType);
        this.getEventRegistry().registerGlobal(com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent.class, thirstHandler::handleInventoryChange);
        
        // register thirst system
        entityStoreRegistry.registerSystem(com.haas.easyhunger.systems.EasyThirstSystem.create());
        

        // Interactions
        final var interactionRegistry = this.getCodecRegistry(Interaction.CODEC);
        interactionRegistry.register("EasyHunger_DrinkWater", com.haas.easyhunger.interactions.DrinkWaterInteraction.class, com.haas.easyhunger.interactions.DrinkWaterInteraction.CODEC);
        interactionRegistry.register("EasyHunger_ConsumeFood", com.haas.easyhunger.interactions.ConsumeFoodInteraction.class, com.haas.easyhunger.interactions.ConsumeFoodInteraction.CODEC);
        interactionRegistry.register("EasyHunger_RefillWaterskin", com.haas.easyhunger.interactions.RefillWaterskinInteraction.class, com.haas.easyhunger.interactions.RefillWaterskinInteraction.CODEC);

        // setup hunger component and hud on player join
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, EasyHungerPlayerReady::handle);

        // listen to gamemode changes
        PacketAdapters.registerOutbound(new GameModeChangeListener());

        // register admin commands
        this.getCommandRegistry().registerCommand(new SetHungerCommand());
        this.getCommandRegistry().registerCommand(new com.haas.easyhunger.commands.SetThirstCommand());

        // register admin commands
        this.getCommandRegistry().registerCommand(new SetHungerCommand());
        this.getCommandRegistry().registerCommand(new com.haas.easyhunger.commands.SetThirstCommand());

        // Try to prune recipes immediately, but also plan for a delayed pruning if needed
        this.pruneRecipes();
    }

    public void pruneRecipes() {
        if (this.getConfig().isThirstEnabled()) {
            return;
        }

        try {
            java.lang.reflect.Field registriesField = com.hypixel.hytale.builtin.crafting.CraftingPlugin.class.getDeclaredField("registries");
            registriesField.setAccessible(true);
            java.util.Map<String, com.hypixel.hytale.builtin.crafting.BenchRecipeRegistry> registries = 
                (java.util.Map<String, com.hypixel.hytale.builtin.crafting.BenchRecipeRegistry>) registriesField.get(null);
            
            if (registries != null) {
                for (com.hypixel.hytale.builtin.crafting.BenchRecipeRegistry registry : registries.values()) {
                    boolean changed = false;
                    for (com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe recipe : registry.getAllRecipes()) {
                        String outputId = recipe.getPrimaryOutput() != null ? recipe.getPrimaryOutput().getItemId() : null;
                        if ("EasyHunger_Odre_Empty".equals(outputId) || "EasyHunger_WaterBowl_Empty".equals(outputId)) {
                            registry.removeRecipe(recipe.getId());
                            changed = true;
                        }
                    }
                    if (changed) {
                        registry.recompute();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.at(java.util.logging.Level.SEVERE).log("Failed to robustly prune Waterskin recipes", e);
        }
    }

    public void saveConfig() {
        this.config.save();
    }

    public ComponentType<EntityStore, HungerComponent> getHungerComponentType() {
        return this.hungerComponentType;
    }

    public ComponentType<EntityStore, com.haas.easyhunger.components.ThirstComponent> getThirstComponentType() {
        return this.thirstComponentType;
    }

    public EasyHungerConfig getConfig() {
        return this.config.get();
    }

    public BiomeModifiersConfig getBiomeConfig() {
        return this.biomeConfig.get();
    }

    public static EasyHunger get() {
        return instance;
    }

    public static void logInfo(String message) {
        LOGGER.at(Level.WARNING).log(message);
    }
}



