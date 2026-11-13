package com.example.bbqdelight;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

/** Tipos de espeto. Os ingredientes sao os itens que precisam estar na bacia (alem do graveto). */
public enum SkewerType {
    BEEF("beef", 7, 0.8f, Items.BEEF, Items.BEEF),
    CHICKEN("chicken", 6, 0.7f, Items.CHICKEN, Items.CHICKEN),
    VEGETABLE("vegetable", 5, 0.6f, Items.CARROT, Items.POTATO);

    public final String id;
    public final int nutrition;
    public final float saturation;
    public final Item[] ingredients;

    SkewerType(String id, int nutrition, float saturation, Item... ingredients) {
        this.id = id;
        this.nutrition = nutrition;
        this.saturation = saturation;
        this.ingredients = ingredients;
    }
}
