package com.example.bbqdelight.block;

import com.example.bbqdelight.ModBlockEntities;
import com.example.bbqdelight.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GrillBlock extends BlockWithEntity {
    public static final MapCodec<GrillBlock> CODEC = createCodec(GrillBlock::new);
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 5, 16);

    public GrillBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrillBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, ModBlockEntities.GRILL, GrillBlockEntity::tick);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ItemActionResult.SUCCESS;
        }
        if (!(world.getBlockEntity(pos) instanceof GrillBlockEntity grill)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (grill.isEmpty()) {
            if (ModItems.isRaw(stack)) {
                grill.place(stack.copyWithCount(1));
                stack.decrementUnlessCreative(1, player);
                world.playSound(null, pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.8f, 1.0f);
                return ItemActionResult.SUCCESS;
            }
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (player.isSneaking()) {
            // agachado + mao vazia: vira o espeto
            if (grill.tryFlip()) {
                world.playSound(null, pos, SoundEvents.BLOCK_WOOL_HIT, SoundCategory.BLOCKS, 0.8f, 1.2f);
            } else {
                player.sendMessage(Text.translatable("message.bbqdelight.not_ready"), true);
            }
        } else {
            // clique normal: tira o espeto (cru, pronto ou queimado)
            player.getInventory().offerOrDrop(grill.take());
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.2f);
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof GrillBlockEntity grill) {
            grill.dropContents(world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
