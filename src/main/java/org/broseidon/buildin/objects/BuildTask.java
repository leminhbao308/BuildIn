package org.broseidon.buildin.objects;

import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import org.broseidon.buildin.main.BuildIn;
import org.broseidon.buildin.main.BuildSchematic;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BuildTask {
    private BuildSchematic schematic;
    private int place, sizeX, sizeY, sizeZ;
    private BlockType[][][] blockArray;
    private Player player;
    private FileConfiguration config;

    private BuildIn main;
    private int buildTaskID;

    private BuildChest buildChest;
    private Location placementLocation;
    private List<Block> originalBlocks;
    private HashMap<Block, Material> originalBlockMaterials;

    int size;
    HashMap<Block, BlockType> blocks;

    public BuildTask(BuildSchematic schematicName, Block chestBlock, String pName, BuildIn main) {
        this.schematic = schematicName;
        sizeX = schematic.sizeX;
        sizeY = schematic.sizeY;
        sizeZ = schematic.sizeZ;

        Chest chest = null;

        if (chestBlock.getType() != Material.CHEST)
            chestBlock.getLocation().getBlock().setType(Material.CHEST);


        chest = (Chest) chestBlock.getState();
        placementLocation = chest.getLocation().clone().add(1, 0, 1);

        blockArray = schematic.loadBlocks();

        player = Bukkit.getServer().getPlayer(pName);
        this.main = main;
        buildTaskID = 0;
        config = main.getConfig();

        buildChest = new BuildChest(chest, getBuildTaskID());

        buildChest.setName(ChatColor.GREEN + schematic.getName());
        buildChest.update();


        blocks = new HashMap<>();
        originalBlocks = new ArrayList<>();
        originalBlockMaterials = new HashMap<>();

        //Map real-world block equivalents to base blocks
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    blocks.put(placementLocation.clone().add(x, y, z).getBlock(), blockArray[x][y][z]);
                    originalBlocks.add(placementLocation.clone().add(x, y, z).getBlock());
                    originalBlockMaterials.put(placementLocation.clone().add(x, y, z).getBlock(), placementLocation.clone().add(x, y, z).getBlock().getType());
                }
            }
        }
        //Sort based on Y level for bottom-to-top placement
        originalBlocks.sort((o1, o2) -> Double.compare(o1.getY(), o2.getY()));
        place = 0;
        size = blocks.size();
    }

    public void run() {
        if (place < size) {

            if (player == null || !player.isOnline()) {
                main.getBuildManager().saveTask(this);
                cancel();
            }

            //For each BaseBlock get the vector of the player and place the corresponding block
            Block block = originalBlocks.get(place);
            BlockType base = blocks.get(block);


            //Disabled by default to make plugin backwards compatible
            if (config.getBoolean("Options.sound"))
                player.playSound(placementLocation, Sound.BLOCK_GLASS_STEP, 1, 0);


            if (config.getBoolean("Options.survival-mode")) {

                ItemStack stack = new ItemStack(block.getType(), 1);
                boolean isIgnoredMaterial = IgnoredMaterial.isIgnoredMaterial(stack.getType());

                if (!buildChest.containsRequirement(stack) && !isIgnoredMaterial) {
                    String newName = ChatColor.GREEN + schematic.getName() + ChatColor.RED + " Requires: " + stack.getType().toString();
                    if (!buildChest.getName().equals(newName)) {
                        buildChest.setName(newName);
                        buildChest.update();
                    }
                } else if (isIgnoredMaterial) {
                    buildChest.setName(ChatColor.GREEN + schematic.getName());
                    buildChest.update();
                    place++;
                    block.setType(block.getType(), false);
                    block.setBlockData(block.getBlockData(), false);
                } else {
                    buildChest.setName(ChatColor.GREEN + schematic.getName());
                    buildChest.removeItemStack(stack);
                    buildChest.update();
                    place++;
                    block.setType(block.getType(), false);
                    block.setBlockData(block.getBlockData(), false);
                }


            } else {
                place++;
                block.setType(block.getType(), false);
                block.setBlockData(block.getBlockData(), false);
            }
        } else {
            player.sendMessage(ChatColor.GREEN + Lang.COMPLETE.toString());
            main.getBuildManager().removeTask(this);

            this.cancel();
        }
    }

    public void cancel() {
    }

    public boolean isPlayerTaskOwner(String playerName){
        return playerName.equals(player.getName());
    }

    public void clearBuild(){
        for(Block b: originalBlocks){
            Material mat = originalBlockMaterials.get(b);
            b.getLocation().getBlock().setType(mat);
        }
    }


    public List<ItemStack> getCurrentBlocksInBuild(){
        //create a for loop ending at place and get all of the blocks in the region
        //use original blocks list and return all items on the floor.
        List<ItemStack> currentItemStacks = new ArrayList<>();
        for(Block block: originalBlocks){
            Block currentBlock = block.getLocation().getBlock();
            ItemStack stack = new ItemStack(currentBlock.getType(), 1);
            currentItemStacks.add(stack);
        }
        return currentItemStacks;
    }


    public void setPlace(int place) {
        this.place = place;
    }

    public int getPlace() {
        return place;
    }

    public BuildSchematic getSchematic() {
        return schematic;
    }

    public Location getLocation() {
        return placementLocation;
    }

    public String getOwnerName() {
        return player.getName();
    }

    public void setBuildTaskID(int buildTaskID){ this.buildTaskID = buildTaskID; }

    public int getBuildTaskID(){ return buildTaskID; }

    public BuildChest getBuildChest(){return buildChest; }

    public void runTaskTimer(BuildIn main, int i, int i1) {

    }
}
