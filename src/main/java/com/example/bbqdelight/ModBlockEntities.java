package com.example.bbqdelight;

import com.example.bbqdelight.block.BasinBlockEntity;
import com.example.bbqdelight.block.GrillBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {
    public static BlockEntityType<GrillBlockEntity> GRILL;
    public static BlockEntityType<BasinBlockEntity> BASIN;

    private ModBlockEntities() {
    }

    public static void register() {
        GRILL = Registry.register(Registries.BLOCK_ENTITY_TYPE, BbqDelight.id("grill"),
                BlockEntityType.Builder.create(GrillBlockEntity::new, ModBlocks.GRILL).build());
        BASIN = Registry.register(Registries.BLOCK_ENTITY_TYPE, BbqDelight.id("ingredient_basin"),
                BlockEntityType.Builder.create(BasinBlockEntity::new, ModBlocks.BASIN).build());
    }
}
