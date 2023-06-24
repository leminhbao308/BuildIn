package org.broseidon.buildin.main;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.DataException;
import org.broseidon.buildin.adapter.WorldGuardChecker;
import org.broseidon.buildin.adapter.iChecker;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegionManager {
    private BuildIn main;
    private List<iChecker> checkers;
    private FileConfiguration config;

    public RegionManager(BuildIn buildIn) {
        main = buildIn;
        config = main.getConfig();
        checkers = new ArrayList<>();

        //This takes the instances provided and, depending on the plugin, adds the method reference
        //Prevents redundant checks
        registerProtectionDependencies();
    }

        public boolean canPlayerPlace(final Player p, BuildSchematic sch, Location blockL) throws DataException, IOException {

        if (p.isOp())
            return true;

        //Welp no dependencies, MY WORK IS DONE HERE
        if (main.dependencies.isEmpty())
            return true;



        List<Location> locs = generateLocs(blockL, sch.getRegion());
        List<Boolean> bools = new ArrayList<>();

        for (Location loc : locs) {
            for (iChecker check : checkers) {
                bools.add(check.isValidPlacement(loc, p));
            }
        }

        if (bools.contains(false))
            return false;

        return true;
    }

    private void registerProtectionDependencies(){
        for (String plugin : main.dependencies) {
            if (plugin.equals("WorldGuard")) {
                checkers.add(new WorldGuardChecker());
            }
        }
    }

    //TODO Get around to replacing this...
    @Deprecated
    public List<Location> generateLocs(Location l, CuboidRegion region) throws DataException, IOException {
        List<Location> locs = new ArrayList<>();
        int xWidth, zLength, yHeight;
        xWidth = region.getWidth();
        yHeight = region.getHeight();
        zLength = region.getLength();
        int cRadius = config.getInt("Options.check-radius");


        for (int x = 0; x <= xWidth + cRadius; x++) {
            for (int y = 0; y <=  yHeight + cRadius; y++) {
                for (int z = 0; z <=  zLength + cRadius; z++) {
                    locs.add(l.clone().add(x, y, z));
                }
            }
        }
        return locs;
    }
}
