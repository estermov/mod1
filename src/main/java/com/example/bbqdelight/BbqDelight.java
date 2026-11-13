package com.example.bbqdelight;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

public class BbqDelight implements ModInitializer {
    public static final String MOD_ID = "bbqdelight";

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        registerTrades();
    }

    /** O acougueiro vende os temperos (nivel 1 = novato, nivel 2 = aprendiz). */
    private static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.BUTCHER, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 1), new ItemStack(ModItems.CUMIN, 4), 12, 5, 0.05f));
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 1), new ItemStack(ModItems.PEPPER, 4), 12, 5, 0.05f));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.BUTCHER, 2, factories ->
                factories.add((entity, random) -> new TradeOffer(
                        new TradedItem(Items.EMERALD, 2), new ItemStack(ModItems.CHILI, 4), 12, 10, 0.05f)));
    }
}
