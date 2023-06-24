package org.broseidon.buildin.objects;

import org.bukkit.Material;

public enum IgnoredMaterial {


    AIR(Material.AIR), GRASS (Material.GRASS), TALL_GRASS(Material.TALL_GRASS), LAVA(Material.LAVA), GRASS_BLOCK(Material.GRASS_BLOCK), ;



    private Material mat;


    IgnoredMaterial(Material material){
        this.mat = material;
    }

    public String toString(){
        return mat.toString();
    }


    public Material getMaterial(){
        return mat;
    }

    public static boolean isIgnoredMaterial(Material material){
        for(IgnoredMaterial ignoredMaterials: values()) {
            if(material.equals(ignoredMaterials.getMaterial())) {
                return true;
            }
        }
        return false;
    }
}
