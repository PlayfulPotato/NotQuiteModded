package me.playfulpotato.notquitemodded;

import me.playfulpotato.notquitemodded.block.BlockHandler;
import me.playfulpotato.notquitemodded.block.inventory.PlayerClickCustomInventory;
import me.playfulpotato.notquitemodded.block.inventory.PlayerDragCustomInventory;
import me.playfulpotato.notquitemodded.block.listeners.*;
import me.playfulpotato.notquitemodded.item.ItemHandler;
import me.playfulpotato.notquitemodded.item.listeners.*;
import me.playfulpotato.notquitemodded.particle.ParticleHandler;
import me.playfulpotato.notquitemodded.projectile.ProjectileHandler;
import me.playfulpotato.notquitemodded.recipe.RecipeHandler;
import me.playfulpotato.notquitemodded.recipe.listeners.CheckForSpecialCraftClick;
import me.playfulpotato.notquitemodded.recipe.listeners.PrepareCraftCheckRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NotQuiteModded extends JavaPlugin {

    public static BlockHandler blockHandler = null;
    public static ParticleHandler particleHandler = null;
    static NotQuiteModded plugin = null;
    public static ItemHandler itemHandler = null;
    public static RecipeHandler recipeHandler = null;
    public static ProjectileHandler projectileHandler = null;
    @Override
    public void onEnable() {
        plugin = this;
        blockHandler = new BlockHandler();
        particleHandler = new ParticleHandler();
        itemHandler = new ItemHandler();
        recipeHandler = new RecipeHandler();
        projectileHandler = new ProjectileHandler();

        PluginManager pm = this.getServer().getPluginManager();

        // Block Related Events
        pm.registerEvents(new BlockBreak(), this);
        pm.registerEvents(new BlockBurn(), this);
        pm.registerEvents(new BlockExplode(), this);
        pm.registerEvents(new BlockFade(), this);
        pm.registerEvents(new ChunkLoad(), this);
        pm.registerEvents(new ChunkUnload(), this);
        pm.registerEvents(new PistonExtend(), this);
        pm.registerEvents(new PistonRetract(), this);
        pm.registerEvents(new EntityExplode(), this);
        pm.registerEvents(new EntityChangeBlock(), this);
        pm.registerEvents(new PlayerInteract(), this);
        // Inventory
        pm.registerEvents(new PlayerClickCustomInventory(), this);
        pm.registerEvents(new PlayerDragCustomInventory(), this);

        // Item Related Events
        pm.registerEvents(new PlayerInteractItemCheck(), this);
        pm.registerEvents(new PlayerItemConsumeItemCheck(), this);
        pm.registerEvents(new PlayerSwapHandsItemCheck(), this);
        pm.registerEvents(new PlayerPlaceBlockItemCheck(), this);
        pm.registerEvents(new PlayerPrepareCraftItemCheck(), this);

        // Recipe Related Events
        pm.registerEvents(new PrepareCraftCheckRecipe(), this);
        pm.registerEvents(new CheckForSpecialCraftClick(), this);
    }

    @Override
    public void onDisable() {
        particleHandler.DestroyAllParticles();
        projectileHandler.DestroyAllProjectiles();
    }

    public static BlockHandler GetBlockHandler() {
        return blockHandler;
    }
    public static ParticleHandler GetParticleHandler() {return particleHandler;}
    public static NotQuiteModded GetPlugin() {
        return plugin;
    }
    public static ItemHandler GetItemHandler() {return itemHandler;}
    public static RecipeHandler GetRecipeHandler() {return recipeHandler;}
}
