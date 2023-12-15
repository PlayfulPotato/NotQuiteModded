package me.playfulpotato.notquitemodded.recipe;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeHandler {

    public List<NQMShapedRecipe> shapedRecipes = new ArrayList<>();

    public void AddShapedRecipe(@NotNull NQMShapedRecipe recipe) {
        shapedRecipes.add(recipe);
        ShapedRecipe recipeBookRecipe = new ShapedRecipe(new NamespacedKey(NotQuiteModded.GetPlugin(), "ShapedRecipe_" + shapedRecipes.size()), recipe.resultItem);
        recipeBookRecipe.shape("012", "345", "678");
        for (int i = 0; i < 8; i++) {
            if (recipe.specialIDs[i] != null) {
                recipeBookRecipe.setIngredient(Integer.toString(i).charAt(0), NotQuiteModded.GetItemHandler().ItemTypeFromStorageKey(recipe.specialIDs[i]).baseItemStack);
            } else if (recipe.gridSetup[i] != null) {
                recipeBookRecipe.setIngredient(Integer.toString(i).charAt(0), recipe.gridSetup[i]);
            }
        }
        NotQuiteModded.GetPlugin().getServer().addRecipe(recipeBookRecipe);
    }

}
