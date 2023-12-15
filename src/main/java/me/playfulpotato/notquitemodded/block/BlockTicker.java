package me.playfulpotato.notquitemodded.block;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockTicker extends BukkitRunnable {

    public static List<BlockTicker> AllBlockTickers = new ArrayList<>();
    private final NQMBlock blockType;
    private List<Location> blockLocations = new ArrayList<>();

    public BlockTicker(@NotNull NQMBlock nqmBlock) {
        this.blockType = nqmBlock;
        runTaskTimer(NotQuiteModded.GetPlugin(), 1, nqmBlock.tickRate);

        AllBlockTickers.add(this);
    }

    @Override
    public void run() {
        for (Location blockLocation : blockLocations) {
            blockType.Tick(blockLocation);
        }
    }

    public boolean RemoveLocation(@NotNull Location locationToRemove) {
        return blockLocations.remove(locationToRemove);
    }
    public void AddLocation(@NotNull Location locationToAdd) {
        blockLocations.add(locationToAdd);
    }
    public NQMBlock getBlockType() {
        return blockType;
    }

}
