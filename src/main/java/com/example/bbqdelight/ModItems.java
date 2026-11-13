package com.example.bbqdelight;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ModItems {
    public static final Map<SkewerType, Item> RAW = new EnumMap<>(SkewerType.class);
    public static final Map<SkewerType, Map<Seasoning, Item>> COOKED = new EnumMap<>(SkewerType.class);
    public static Item BURNT_SKEWER;
    public static Item CUMIN;
    public static Item PEPPER;
    public static Item CHILI;
    public static Item GRILL;
    public static Item BASIN;

    private static final List<Item> ALL_ITEMS = new ArrayList<>();

    private ModItems() {
    }

    /** Nome de registro do espeto cozido (com ou sem tempero). */
    public static String cookedName(SkewerType type, Seasoning seasoning) {
        String base = "cooked_" + type.id + "_skewer";
        return seasoning == Seasoning.NONE ? base : base + "_" + seasoning.id;
    }

    private static Item reg(String name, Item item) {
        Item registered = Registry.register(Registries.ITEM, BbqDelight.id(name), item);
        ALL_ITEMS.add(registered);
        return registered;
    }

    private static FoodComponent foodFor(SkewerType type, Seasoning seasoning) {
        FoodComponent.Builder builder = new FoodComponent.Builder()
                .nutrition(type.nutrition + (seasoning == Seasoning.NONE ? 0 : 1))
                .saturationModifier(type.saturation);
        if (seasoning == Seasoning.PEPPER) {
            builder.snack(); // pimenta: come na metade do tempo
        }
        if (seasoning == Seasoning.CHILI) {
            builder.alwaysEdible(); // pimenta-malagueta: pode comer com a fome cheia
        }
        return builder.build();
    }

    public static void register() {
        for (SkewerType type : SkewerType.values()) {
            RAW.put(type, reg("raw_" + type.id + "_skewer", new Item(new Item.Settings().maxCount(16))));
            Map<Seasoning, Item> cooked = new EnumMap<>(Seasoning.class);
            for (Seasoning seasoning : Seasoning.values()) {
                cooked.put(seasoning, reg(cookedName(type, seasoning),
                        new SkewerFoodItem(new Item.Settings().maxCount(16).food(foodFor(type, seasoning)), seasoning)));
            }
            COOKED.put(type, cooked);
        }

        FoodComponent burntFood = new FoodComponent.Builder()
                .nutrition(2)
                .saturationModifier(0.1f)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 200), 0.8f)
                .build();
        BURNT_SKEWER = reg("burnt_skewer",
                new SkewerFoodItem(new Item.Settings().maxCount(16).food(burntFood), Seasoning.NONE));

        CUMIN = reg("cumin", new Item(new Item.Settings()));
        PEPPER = reg("pepper", new Item(new Item.Settings()));
        CHILI = reg("chili", new Item(new Item.Settings()));

        GRILL = reg("grill", new BlockItem(ModBlocks.GRILL, new Item.Settings()));
        BASIN = reg("ingredient_basin", new BlockItem(ModBlocks.BASIN, new Item.Settings()));

        ItemGroup group = FabricItemGroup.builder()
                .icon(() -> new ItemStack(COOKED.get(SkewerType.BEEF).get(Seasoning.NONE)))
                .displayName(Text.translatable("itemGroup.bbqdelight"))
                .entries((context, entries) -> ALL_ITEMS.forEach(entries::add))
                .build();
        Registry.register(Registries.ITEM_GROUP, BbqDelight.id("main"), group);
    }

    // ---- consultas usadas pela grelha e pela bacia ----

    public static SkewerType rawTypeOf(ItemStack stack) {
        for (Map.Entry<SkewerType, Item> entry : RAW.entrySet()) {
            if (stack.isOf(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isRaw(ItemStack stack) {
        return rawTypeOf(stack) != null;
    }

    public static SkewerType cookedTypeOf(ItemStack stack) {
        for (Map.Entry<SkewerType, Map<Seasoning, Item>> entry : COOKED.entrySet()) {
            if (stack.isOf(entry.getValue().get(Seasoning.NONE))) {
                return entry.getKey();
            }
        }
        return null;
    }

    /** Espeto cozido sem tempero (o unico estagio que pode queimar na grelha). */
    public static boolean isPlainCooked(ItemStack stack) {
        return cookedTypeOf(stack) != null;
    }

    public static Item cookedOf(SkewerType type) {
        return COOKED.get(type).get(Seasoning.NONE);
    }
}
