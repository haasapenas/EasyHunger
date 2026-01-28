package com.haas.easyhunger.interactions;

import com.haas.easyhunger.EasyHunger;
import com.haas.easyhunger.components.ThirstComponent;
import com.haas.easyhunger.ui.EasyWaterHud;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class DrinkWaterInteraction extends SimpleInstantInteraction {
    
    private float thirstRestoreAmount = 20.0f;
    
    public static final BuilderCodec<DrinkWaterInteraction> CODEC = 
        ((BuilderCodec.Builder) ((BuilderCodec.Builder) BuilderCodec.builder(
            DrinkWaterInteraction.class, 
            DrinkWaterInteraction::new,
            SimpleInstantInteraction.CODEC)
            .append(new KeyedCodec<>("ThirstRestoreAmount", Codec.FLOAT), 
                (interaction, value) -> interaction.thirstRestoreAmount = value, 
                interaction -> interaction.thirstRestoreAmount)
            .add())
        ).build();
    
    public DrinkWaterInteraction() {
        super();
    }
    
    @Override
    protected void firstRun(@Nonnull InteractionType type, 
                           @Nonnull InteractionContext context, 
                           @Nonnull CooldownHandler cooldownHandler) {
        try {
            Ref<EntityStore> entityRef = context.getEntity();
            if (entityRef == null || !entityRef.isValid()) {
                context.getState().state = InteractionState.Failed;
                return;
            }
            
            Store<EntityStore> store = entityRef.getStore();
            PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
            
            if (playerRef == null) {
                // Try fallback search? No, Component Access is safer.
                context.getState().state = InteractionState.Failed;
                return;
            }
            
            ThirstComponent thirst = store.getComponent(entityRef, ThirstComponent.getComponentType());
            
            if (thirst != null) {
                float max = EasyHunger.get().getConfig().getMaxThirst();
                if (thirst.getThirstLevel() < max) {
                    // Try to get drink value from config based on item ID
                    float restoreAmount = thirstRestoreAmount; // Default from JSON
                    
                    // Get item ID from held item if available
                    if (context.getHeldItem() != null) {
                        String itemId = context.getHeldItem().getItemId();
                        // Remove leading asterisk if present (Hytale adds this for state variants)
                        if (itemId != null && itemId.startsWith("*")) {
                            itemId = itemId.substring(1);
                        }
                        Float configValue = EasyHunger.get().getConfig().getDrinkValue(itemId);
                        if (configValue > 0) {
                            restoreAmount = configValue;
                        }
                    } else {
                        // HeldItem is null, use default
                    }
                    
                    thirst.drink(restoreAmount);
                    
                    // Update HUD
                    EasyWaterHud.updatePlayerThirstLevel(playerRef, thirst.getThirstLevel());
                } else {
                     // Debug: EasyHunger.logInfo("[DrinkInteraction] Thirst Full.");
                }
            }
            
            context.getState().state = InteractionState.Finished;
            
        } catch (Exception e) {
            EasyHunger.logInfo("DRINK WATER ERROR: " + e.toString());
            context.getState().state = InteractionState.Failed;
        }
    }
}
