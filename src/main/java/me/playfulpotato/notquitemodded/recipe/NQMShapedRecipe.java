package me.playfulpotato.notquitemodded.recipe;

import me.playfulpotato.notquitemodded.item.NQMItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class NQMShapedRecipe {

    public ItemStack resultItem;
    public final int itemCount;
    public int customItemCount = 0;
    public Material[] gridSetup = new Material[9];
    public String[] specialIDs = new String[9];
    public NQMShapedRecipe(@NotNull ItemStack resultItem, int itemCount) {
        this.resultItem = resultItem;
        this.itemCount = itemCount;
    }
    protected void SetGridItem(@NotNull Material materialType, int gridSlot) {
        this.gridSetup[gridSlot - 1] = materialType;
    }
    protected void SetGridItem(@NotNull NQMItem itemType, int gridSlot) {
        this.gridSetup[gridSlot - 1] = itemType.baseItemStack.getType();
        this.specialIDs[gridSlot - 1] = itemType.fullStorageKey;
        customItemCount++;
    }

}
