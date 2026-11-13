package com.example.bbqdelight.block;

import com.example.bbqdelight.ModBlockEntities;
import com.example.bbqdelight.ModItems;
import com.example.bbqdelight.SkewerType;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Cozinha um espeto por vez. Fluxo: coloca cru -> assa um lado -> vira (agachar + clique)
 * -> assa o outro lado -> pronto. Se esquecer de virar ou de tirar, queima.
 */
public class GrillBlockEntity extends BlockEntity {
    /** Ticks ate a fumaca engrossar e o espeto precisar ser virado (10 s). */
    public static final int FLIP_AFTER = 200;
    /** Ticks extras (depois de precisar virar) ate queimar por falta de virada (10 s). */
    public static final int FLIP_GRACE = 200;
    /** Ticks para assar o segundo lado (10 s). */
    public static final int SECOND_SIDE = 200;
    /** Ticks depois de pronto ate queimar (15 s). */
    public static final int BURN_AFTER_COOKED = 300;

    private ItemStack skewer = ItemStack.EMPTY;
    private int ticks = 0;
    private boolean flipped = false;

    public GrillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRILL, pos, state);
    }

    public boolean isEmpty() {
        return skewer.isEmpty();
    }

    public void place(ItemStack stack) {
        this.skewer = stack;
        this.ticks = 0;
        this.flipped = false;
        markDirty();
    }

    public ItemStack take() {
        ItemStack out = skewer;
        skewer = ItemStack.EMPTY;
        ticks = 0;
        flipped = false;
        markDirty();
        return out;
    }

    /** O espeto cru ja pode ser virado (fumaca grossa)? */
    public boolean needsFlip() {
        return ModItems.isRaw(skewer) && !flipped && ticks >= FLIP_AFTER;
    }

    /** Tenta virar. Devolve false se ainda nao e hora (ou nao ha o que virar). */
    public boolean tryFlip() {
        if (!needsFlip()) {
            return false;
        }
        flipped = true;
        ticks = 0;
        markDirty();
        return true;
    }

    private static boolean hasHeat(World world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.down());
        if (below.isIn(BlockTags.CAMPFIRES)) {
            return below.contains(CampfireBlock.LIT) && below.get(CampfireBlock.LIT);
        }
        return below.isOf(Blocks.FIRE) || below.isOf(Blocks.SOUL_FIRE)
                || below.isOf(Blocks.LAVA) || below.isOf(Blocks.MAGMA_BLOCK);
    }

    public static void tick(World world, BlockPos pos, BlockState state, GrillBlockEntity grill) {
        if (world.isClient || grill.skewer.isEmpty() || !hasHeat(world, pos)) {
            return;
        }
        boolean raw = ModItems.isRaw(grill.skewer);
        boolean plainCooked = ModItems.isPlainCooked(grill.skewer);
        if (!raw && !plainCooked) {
            return; // queimado ou temperado: nada a fazer
        }

        grill.ticks++;

        if (world instanceof ServerWorld serverWorld && grill.ticks % 4 == 0) {
            int count = grill.needsFlip() ? 3 : 1; // fumaca mais grossa = hora de virar
            serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5,
                    count, 0.15, 0.05, 0.15, 0.01);
        }

        if (raw) {
            if (!grill.flipped && grill.ticks > FLIP_AFTER + FLIP_GRACE) {
                grill.burn(world, pos);
            } else if (grill.flipped && grill.ticks >= SECOND_SIDE) {
                SkewerType type = ModItems.rawTypeOf(grill.skewer);
                grill.skewer = new ItemStack(ModItems.cookedOf(type));
                grill.ticks = 0;
                grill.markDirty();
                world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.6f, 1.4f);
            }
        } else if (grill.ticks > BURN_AFTER_COOKED) {
            grill.burn(world, pos);
        }
    }

    private void burn(World world, BlockPos pos) {
        this.skewer = new ItemStack(ModItems.BURNT_SKEWER);
        this.ticks = 0;
        markDirty();
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.6f, 1.0f);
    }

    public void dropContents(World world, BlockPos pos) {
        if (!skewer.isEmpty()) {
            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, skewer);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        skewer = nbt.contains("Skewer")
                ? ItemStack.fromNbt(registryLookup, nbt.get("Skewer")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        ticks = nbt.getInt("Ticks");
        flipped = nbt.getBoolean("Flipped");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!skewer.isEmpty()) {
            nbt.put("Skewer", skewer.encode(registryLookup));
        }
        nbt.putInt("Ticks", ticks);
        nbt.putBoolean("Flipped", flipped);
    }
}
