package org.broseidon.buildin.adapter;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface iChecker{

     //Interface allows checking methods to be grouped together
     boolean isValidPlacement(Location l, Player p);

}
