package com.example.bbqdelight;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

/** Espeto comestivel: devolve o graveto e aplica o efeito do tempero. */
public class SkewerFoodItem extends Item {
    private final Seasoning seasoning;

    public SkewerFoodItem(Settings settings, Seasoning seasoning) {
        super(settings);
        this.seasoning = seasoning;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        if (!world.isClient) {
            if (seasoning == Seasoning.CUMIN) {
                user.heal(4.0f); // cominho: cura ao comer
            } else if (seasoning == Seasoning.CHILI) {
                user.damage(world.getDamageSources().magic(), 2.0f); // malagueta: machuca
            }
            if (user instanceof PlayerEntity player && !player.isCreative()) {
                player.getInventory().offerOrDrop(new ItemStack(Items.STICK));
            }
        }
        return result;
    }
}
