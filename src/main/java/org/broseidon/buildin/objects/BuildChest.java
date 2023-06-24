package org.broseidon.buildin.objects;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class BuildChest {
    private Chest chestBlock;
    private int chestID;

    public BuildChest(Chest chest, int buildTaskID) {
        this.chestBlock = chest;
        this.chestID = buildTaskID;
    }

    public void update(){
        ItemStack[] currentContents = getCurrentChestInventoryContents();
        chestBlock.update();

        for(ItemStack stack : currentContents)  {
             if(stack != null) {
                 chestBlock.getInventory().addItem(stack);
             }
        }


    }

    public ItemStack[] getCurrentChestInventoryContents (){
        return chestBlock.getBlockInventory().getContents().clone();
    }

    public boolean containsRequirement(ItemStack requiredItem) {
        return chestBlock.getInventory().containsAtLeast(requiredItem, 1);
    }

    public void setName(String newName) {
        chestBlock.setCustomName(newName);
    }

    public String getName(){
        return chestBlock.getCustomName();
    }

    public Location getChestLocation(){
        return chestBlock.getLocation();
    }

    public void removeItemStack(ItemStack targetItem) {
        for(ItemStack itemStack: chestBlock.getBlockInventory().getContents()) {
            if(itemStack == null)
                continue;
            if(itemStack.getType().equals(targetItem.getType())) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                return;
            }
        }
    }
}
