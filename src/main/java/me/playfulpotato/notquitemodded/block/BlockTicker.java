package me.playfulpotato.notquitemodded.block;

import me.playfulpotato.notquitemodded.NotQuiteModded;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockTicker extends BukkitRunnable {

    public static List<BlockTicker> AllBlockTickers = new ArrayList<>();
    private final NQMBlockFactory blockFactory;
    private HashMap<Long, NQMBlock> blockHashMap;

    public BlockTicker(@NotNull NQMBlockFactory nqmBlockFactory) {
        this.blockFactory = nqmBlockFactory;

        HashMap<Long, NQMBlock> tempHash = NotQuiteModded.GetBlockHandler().idBlockMap.get(blockFactory.storageKey);
        if (tempHash != null) {
            blockHashMap = tempHash;
        }
        AllBlockTickers.add(this);

        runTaskTimer(NotQuiteModded.GetPlugin(), 1, nqmBlockFactory.tickRate);
    }

    @Override
    public void run() {
        for (NQMBlock nqmBlock : blockHashMap.values()) {
            nqmBlock.Tick();
        }
    }

}
