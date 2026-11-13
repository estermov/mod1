package com.example.bbqdelight;

import com.example.bbqdelight.block.BasinBlock;
import com.example.bbqdelight.block.GrillBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public final class ModBlocks {
    public static Block GRILL;
    public static Block BASIN;

    private ModBlocks() {
    }

    public static void register() {
        GRILL = Registry.register(Registries.BLOCK, BbqDelight.id("grill"),
                new GrillBlock(AbstractBlock.Settings.create()
                        .mapColor(MapColor.IRON_GRAY)
                        .strength(2.0f)
                        .sounds(BlockSoundGroup.METAL)
                        .nonOpaque()));
        BASIN = Registry.register(Registries.BLOCK, BbqDelight.id("ingredient_basin"),
                new BasinBlock(AbstractBlock.Settings.create()
                        .mapColor(MapColor.TERRACOTTA_ORANGE)
                        .strength(1.5f)
                        .sounds(BlockSoundGroup.STONE)
                        .nonOpaque()));
    }
}
