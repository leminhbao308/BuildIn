package org.broseidon.buildin.events;

import org.broseidon.buildin.main.BuildSchematic;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class iNPlaceEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String name;
    private Block block;
    private boolean cancelled;
    private Player placer;
    private Location loc;
    private BuildSchematic schematic;

    public iNPlaceEvent(String name, Player p, Location playerLocation, Block block, BuildSchematic buildSchematic) {
        this.name = name;
        placer = p;
        loc = playerLocation;
        cancelled = false;
        this.block = block;
        schematic = buildSchematic;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Block getBlock() {
        return block;
    }

    public String getName() {
        return name;
    }

    public BuildSchematic getSchematic() {
        return schematic;
    }

    public Player getPlacer() {
        return placer;
    }

    public Location getLocation() {
        return loc;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
