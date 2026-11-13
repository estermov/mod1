package com.example.bbqdelight.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import com.example.bbqdelight.SkewerType;

public class BasinBlock extends BlockWithEntity {
    public static final MapCodec<BasinBlock> CODEC = createCodec(BasinBlock::new);
    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 8, 14);

    public BasinBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BasinBlockEntity(pos, state);
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
        if (!(world.getBlockEntity(pos) instanceof BasinBlockEntity basin)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Graveto na mao: monta o espeto
        if (stack.isOf(Items.STICK)) {
            SkewerType type = basin.matchRecipe();
            if (type == null) {
                player.sendMessage(Text.translatable("message.bbqdelight.no_recipe"), true);
                return ItemActionResult.SUCCESS;
            }
            ItemStack skewer = basin.craft(type);
            stack.decrementUnlessCreative(1, player);
            player.getInventory().offerOrDrop(skewer);
            return ItemActionResult.SUCCESS;
        }

        // Qualquer outro item: coloca um na bacia
        if (!stack.isEmpty()) {
            if (basin.addOne(stack)) {
                stack.decrementUnlessCreative(1, player);
            } else {
                player.sendMessage(Text.translatable("message.bbqdelight.basin_full"), true);
            }
            return ItemActionResult.SUCCESS;
        }

        // Mao vazia + agachado: tira o ultimo ingrediente; so mao vazia: mostra o conteudo
        if (player.isSneaking()) {
            ItemStack out = basin.removeLast();
            if (!out.isEmpty()) {
                player.getInventory().offerOrDrop(out);
            }
        } else if (basin.isEmpty()) {
            player.sendMessage(Text.translatable("message.bbqdelight.basin_empty"), true);
        } else {
            Text list = Text.empty();
            boolean first = true;
            for (ItemStack content : basin.contents()) {
                if (!first) {
                    list = Text.empty().append(list).append(Text.literal(", "));
                }
                list = Text.empty().append(list).append(content.getName());
                first = false;
            }
            player.sendMessage(Text.translatable("message.bbqdelight.basin_contents", list), true);
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof BasinBlockEntity basin) {
            basin.dropAll(world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
