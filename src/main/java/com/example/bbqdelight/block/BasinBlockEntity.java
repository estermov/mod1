package com.example.bbqdelight.block;

import com.example.bbqdelight.ModBlockEntities;
import com.example.bbqdelight.ModItems;
import com.example.bbqdelight.SkewerType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/** Guarda ate 4 ingredientes. Com um graveto na mao, transforma o conteudo em espeto cru. */
public class BasinBlockEntity extends BlockEntity {
    public static final int SIZE = 4;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);

    public BasinBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIN, pos, state);
    }

    public boolean addOne(ItemStack source) {
        for (int i = 0; i < SIZE; i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, source.copyWithCount(1));
                markDirty();
                return true;
            }
        }
        return false;
    }

    public ItemStack removeLast() {
        for (int i = SIZE - 1; i >= 0; i--) {
            if (!items.get(i).isEmpty()) {
                ItemStack out = items.get(i);
                items.set(i, ItemStack.EMPTY);
                markDirty();
                return out;
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    public List<ItemStack> contents() {
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                out.add(stack);
            }
        }
        return out;
    }

    /** Devolve o tipo de espeto cujos ingredientes batem exatamente com o conteudo, ou null. */
    public SkewerType matchRecipe() {
        for (SkewerType type : SkewerType.values()) {
            List<Item> needed = new ArrayList<>(List.of(type.ingredients));
            boolean ok = true;
            for (ItemStack stack : contents()) {
                if (!needed.remove(stack.getItem())) {
                    ok = false;
                    break;
                }
            }
            if (ok && needed.isEmpty()) {
                return type;
            }
        }
        return null;
    }

    public ItemStack craft(SkewerType type) {
        for (int i = 0; i < SIZE; i++) {
            items.set(i, ItemStack.EMPTY);
        }
        markDirty();
        return new ItemStack(ModItems.RAW.get(type));
    }

    public void dropAll(World world, BlockPos pos) {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            }
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        for (int i = 0; i < SIZE; i++) {
            items.set(i, ItemStack.EMPTY);
        }
        Inventories.readNbt(nbt, items, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, items, registryLookup);
    }
}
