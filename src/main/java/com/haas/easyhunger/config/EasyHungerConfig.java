package com.haas.easyhunger.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class EasyHungerConfig {
    
    // Original fields codecs
    private static final KeyedCodec<Integer> MAX_HUNGER = new KeyedCodec<>("MaxHunger", Codec.INTEGER);
    private static final KeyedCodec<Float> STARVATION_TICK_RATE = new KeyedCodec<>("StarvationTickRate", Codec.FLOAT);
    private static final KeyedCodec<Float> STARVATION_PER_TICK = new KeyedCodec<>("StarvationPerTick", Codec.FLOAT);
    private static final KeyedCodec<Float> STARVATION_STAMINA_MODIFIER = new KeyedCodec<>("StarvationStaminaModifier", Codec.FLOAT);
    private static final KeyedCodec<Float> HUNGRY_THRESHOLD = new KeyedCodec<>("HungryThreshold", Codec.FLOAT);
    private static final KeyedCodec<Float> STARVATION_DAMAGE = new KeyedCodec<>("StarvationDamage", Codec.FLOAT);


    private static final KeyedCodec<String> HUD_POSITION = new KeyedCodec<>("HudPosition", Codec.STRING);
    private static final KeyedCodec<Float> BLOCK_BREAK_HUNGER_COST = new KeyedCodec<>("BlockBreakHungerCost", Codec.FLOAT);
    private static final KeyedCodec<Float> JUMP_HUNGER_COST = new KeyedCodec<>("JumpHungerCost", Codec.FLOAT);
    
    // Thirst Configuration
    private static final KeyedCodec<Boolean> THIRST_ENABLED = new KeyedCodec<>("ThirstEnabled", Codec.BOOLEAN);
    private static final KeyedCodec<Integer> MAX_THIRST = new KeyedCodec<>("MaxThirst", Codec.INTEGER);
    private static final KeyedCodec<Float> THIRST_DECAY_RATE = new KeyedCodec<>("ThirstDecayRate", Codec.FLOAT);
    private static final KeyedCodec<Float> SPRINT_THIRST_MULTIPLIER = new KeyedCodec<>("SprintThirstMultiplier", Codec.FLOAT);
    private static final KeyedCodec<Float> THIRSTY_THRESHOLD = new KeyedCodec<>("ThirstyThreshold", Codec.FLOAT);

    // Food values (alphabetical order) for individual override support
    // Dynamic Food Values Map
    private static final KeyedCodec<Map<String, Float>> FOOD_VALUES = new KeyedCodec<>("FoodValues", new MapCodec<>(Codec.FLOAT, HashMap::new));
    private static final KeyedCodec<Map<String, Float>> DRINK_VALUES = new KeyedCodec<>("DrinkValues", new MapCodec<>(Codec.FLOAT, HashMap::new));
    private static final KeyedCodec<Float> THIRST_DAMAGE = new KeyedCodec<>("ThirstDamage", Codec.FLOAT);


    public static final BuilderCodec<EasyHungerConfig> CODEC = BuilderCodec.builder(EasyHungerConfig.class, EasyHungerConfig::new)
            // === HUNGER / FOOD CONFIGS ===
            .addField(MAX_HUNGER, (c, v) -> c.maxHunger = v, EasyHungerConfig::getMaxHunger)
            .addField(STARVATION_TICK_RATE, (c, v) -> c.starvationTickRate = v, EasyHungerConfig::getStarvationTickRate)
            .addField(STARVATION_PER_TICK, (c, v) -> c.starvationPerTick = v, EasyHungerConfig::getStarvationPerTick)
            .addField(STARVATION_STAMINA_MODIFIER, (c, v) -> c.starvationStaminaModifier = v, EasyHungerConfig::getStarvationStaminaModifier)
            .addField(HUNGRY_THRESHOLD, (c, v) -> c.hungryThreshold = v, EasyHungerConfig::getHungryThreshold)
            .addField(STARVATION_DAMAGE, (c, v) -> c.starvationDamage = v, EasyHungerConfig::getStarvationDamage)
            .addField(BLOCK_BREAK_HUNGER_COST, (c, v) -> c.blockBreakHungerCost = v, EasyHungerConfig::getBlockBreakHungerCost)
            .addField(JUMP_HUNGER_COST, (c, v) -> c.jumpHungerCost = v, EasyHungerConfig::getJumpHungerCost)
            
            // === THIRST / WATER CONFIGS ===
            .addField(THIRST_ENABLED, (c, v) -> c.thirstEnabled = v, EasyHungerConfig::isThirstEnabled)
            .addField(MAX_THIRST, (c, v) -> c.maxThirst = v, EasyHungerConfig::getMaxThirst)
            .addField(THIRST_DECAY_RATE, (c, v) -> c.thirstDecayRate = v, EasyHungerConfig::getThirstDecayRate)
            .addField(SPRINT_THIRST_MULTIPLIER, (c, v) -> c.sprintThirstMultiplier = v, EasyHungerConfig::getSprintThirstMultiplier)
            .addField(THIRST_DAMAGE, (c, v) -> c.thirstDamage = v, EasyHungerConfig::getThirstDamage)
            .addField(THIRSTY_THRESHOLD, (c, v) -> c.thirstyThreshold = v, EasyHungerConfig::getThirstyThreshold)
            
            // === HUD POSITION ===
            .addField(HUD_POSITION, (c, v) -> c.hudPosition = HudPosition.valueOf(v), c -> c.getHudPosition().name())
            
            // === FOOD VALUES ===
            .addField(FOOD_VALUES, (c, v) -> c.foodValues = v, EasyHungerConfig::getFoodValues)
            
            // === DRINK VALUES ===
            .addField(DRINK_VALUES, (c, v) -> c.drinkValues = v, EasyHungerConfig::getDrinkValues)
            .build();

    private Integer maxHunger = 50;
    private float starvationTickRate = 2f;
    private float starvationPerTick = 0.04f;

    public Integer getMaxHunger() { return maxHunger; }
    private float starvationStaminaModifier = 0.175f;
    private float hungryThreshold = 20.0f;
    private float starvationDamage = 5.0f;
    private float thirstDamage = 5.0f;

    private HudPosition hudPosition = HudPosition.BOTTOM;
    private float blockBreakHungerCost = 0.005f;
    private float jumpHungerCost = 0.01f;

    // Thirst settings
    private boolean thirstEnabled = true; // Enable/disable thirst system
    private Integer maxThirst = 50; // Match default hunger for symmetry
    private float thirstDecayRate = 0.05f; // Slower than hunger? Or same.
    private float sprintThirstMultiplier = 1.5f;
    private float thirstyThreshold = 20.0f; // Same as hungryThreshold
    
    // Internal map for lookups
    private Map<String, Float> foodValues;
    private Map<String, Float> drinkValues;

    public EasyHungerConfig() {
        foodValues = new HashMap<>();
        // Default values
        foodValues.put("Food_Beef_Raw", 3.75f);
        foodValues.put("Food_Bread", 5.5f);
        foodValues.put("Food_Candy_Cane", 2.25f);
        foodValues.put("Food_Cheese", 4.75f);
        foodValues.put("Food_Chicken_Raw", 2.75f);
        foodValues.put("Food_Egg", 1.75f);
        foodValues.put("Food_Fish_Grilled", 9.5f);
        foodValues.put("Food_Fish_Raw", 2.5f);
        foodValues.put("Food_Fish_Raw_Epic", 7.5f);
        foodValues.put("Food_Fish_Raw_Legendary", 10.25f);
        foodValues.put("Food_Fish_Raw_Rare", 5.25f);
        foodValues.put("Food_Fish_Raw_Uncommon", 3.75f);
        foodValues.put("Food_Kebab_Fruit", 7.5f);
        foodValues.put("Food_Kebab_Meat", 14.5f);
        foodValues.put("Food_Kebab_Mushroom", 9.75f);
        foodValues.put("Food_Kebab_Vegetable", 9.25f);
        foodValues.put("Food_Pie_Apple", 13.75f);
        foodValues.put("Food_Pie_Meat", 18.5f);
        foodValues.put("Food_Pie_Pumpkin", 15.25f);
        foodValues.put("Food_Popcorn", 1.5f);
        foodValues.put("Food_Pork_Raw", 3.5f);
        foodValues.put("Food_Salad_Berry", 5.75f);
        foodValues.put("Food_Salad_Caesar", 7.75f);
        foodValues.put("Food_Salad_Mushroom", 6.5f);
        foodValues.put("Food_Vegetable_Cooked", 7.25f);
        foodValues.put("Food_Wildmeat_Cooked", 11.5f);
        foodValues.put("Food_Wildmeat_Raw", 3.25f);
        foodValues.put("Halloween_Basket_Pumpkin", 14.75f);
        foodValues.put("Halloween_Basket_Straw", 11.5f);
        foodValues.put("Ingredient_Dough", 0.75f);
        foodValues.put("Ingredient_Flour", 0.5f);
        foodValues.put("Ingredient_Salt", 0.0f);
        foodValues.put("Ingredient_Spices", 0.0f);
        foodValues.put("Plant_Crop_Aubergine_Item", 2.0f);
        foodValues.put("Plant_Crop_Carrot_Item", 1.75f);
        foodValues.put("Plant_Crop_Cauliflower_Item", 2.25f);
        foodValues.put("Plant_Crop_Chilli_Item", 1.75f);
        foodValues.put("Plant_Crop_Corn_Item", 2.75f);
        foodValues.put("Plant_Crop_Lettuce_Item", 1.25f);
        foodValues.put("Plant_Crop_Onion_Item", 0.75f);
        foodValues.put("Plant_Crop_Potato_Item", 2.25f);
        foodValues.put("Plant_Crop_Pumpkin_Item", 3.5f);
        foodValues.put("Plant_Crop_Rice_Item", 1.5f);
        foodValues.put("Plant_Crop_Tomato_Item", 1.5f);
        foodValues.put("Plant_Crop_Turnip_Item", 1.75f);
        foodValues.put("Plant_Fruit_Apple", 3.5f);
        foodValues.put("Plant_Fruit_Azure", 2.75f);
        foodValues.put("Plant_Fruit_Berries_Red", 1.75f);
        foodValues.put("Plant_Fruit_Coconut", 3.75f);
        foodValues.put("Plant_Fruit_Mango", 3.75f);
        foodValues.put("Plant_Fruit_Pinkberry", 2.5f);
        foodValues.put("Plant_Fruit_Poison", 2.25f);
        foodValues.put("Plant_Fruit_Spiral", 2.75f);
        foodValues.put("Plant_Fruit_Windwillow", 2.5f);
        
        // Default drink values (thirst restoration)
        // Short prefixes work - getDrinkValue uses startsWith matching
        drinkValues = new HashMap<>();
        drinkValues.put("EasyHunger_Odre", 15.0f);
        drinkValues.put("EasyHunger_WaterBowl", 5.0f);
        drinkValues.put("Container_Bucket", 10.0f);
    }

    public float getStarvationTickRate() {
        return starvationTickRate;
    }
    public float getStarvationPerTick() {
        return starvationPerTick;
    }
    public float getStarvationStaminaModifier() {
        return starvationStaminaModifier;
    }
    public float getHungryThreshold() {
        return hungryThreshold;
    }
    public float getStarvationDamage() {
        return starvationDamage;
    }
    public float getThirstDamage() { return thirstDamage; }

    public HudPosition getHudPosition() {
        return hudPosition;
    }
    public float getBlockBreakHungerCost() {
        return blockBreakHungerCost;
    }
    public float getJumpHungerCost() {
        return jumpHungerCost;
    }
    
    public boolean isThirstEnabled() { return thirstEnabled; }
    public Integer getMaxThirst() { return maxThirst; }
    public float getThirstDecayRate() { return thirstDecayRate; }
    public float getSprintThirstMultiplier() { return sprintThirstMultiplier; }
    public float getThirstyThreshold() { return thirstyThreshold; }
    
    // Food getters/setters/logic
    public Map<String, Float> getFoodValues() { return foodValues; }
    
    public Float getFoodValue(String foodId) {
        Float value = foodValues.get(foodId);
        return value != null ? value : 0.0f;
    }
    
    public void setFoodValue(String foodId, float value) {
        foodValues.put(foodId, value);
    }
    
    // Drink getters/setters/logic
    public Map<String, Float> getDrinkValues() { return drinkValues; }
    
    public Float getDrinkValue(String drinkId) {
        // First try exact match
        Float value = drinkValues.get(drinkId);
        if (value != null && value > 0) {
            return value;
        }
        
        // Then try partial match (config key is prefix of actual ID)
        for (java.util.Map.Entry<String, Float> entry : drinkValues.entrySet()) {
            if (drinkId != null && drinkId.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return 0.0f;
    }
    
    public void setDrinkValue(String drinkId, float value) {
        drinkValues.put(drinkId, value);
    }
}



