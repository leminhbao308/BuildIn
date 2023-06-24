package org.broseidon.buildin.adapter;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardChecker implements iChecker {

    public WorldGuardChecker() {

    }

    @Override
    public boolean isValidPlacement(Location loc, Player player) {
        LocalPlayer p = WorldGuardPlugin.inst().wrapPlayer(player);
        com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(p.getWorld(), Vector3.at(loc.getX(), loc.getY(), loc.getZ()));
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(p, p.getWorld());
        if (canBypass) {
            return true;
        }

        RegionQuery query = container.createQuery();
        return query.testState(location, p, Flags.BUILD);
    }
}
