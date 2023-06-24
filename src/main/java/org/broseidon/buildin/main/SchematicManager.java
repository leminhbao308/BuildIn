package org.broseidon.buildin.main;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.SchematicCommands;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldguard.LocalPlayer;
import org.broseidon.buildin.objects.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchematicManager {


    private BuildIn main = null;
    File cFile = null;
    File dir = null;
    FileConfiguration config = null;
    FileConfiguration schematicsFile = null;
    List<String> permSchematics = null;

    protected SchematicManager(BuildIn instance) {
        main = instance;
        cFile = new File(main.getDataFolder() + File.separator + "schematics.yml");
        permSchematics = new ArrayList<>();
        dir = new File(main.getDataFolder() + File.separator + "schematics");
        schematicsFile = YamlConfiguration.loadConfiguration(cFile);
        config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + File.separator + "config.yml"));

        String perm = config.getString("Options.permissions");
        String mPerm = perm.replace("]", "").replace("[", "");

        //Multiple schematics check
        if (mPerm.contains(",")) {
            String[] strings = mPerm.split(",");
            for (String s : strings) {
                permSchematics.add(s.trim());
            }
            return;
        }

        permSchematics.add(mPerm);

    }


    public void createSchematic(Actor actor, String sName, String direction, ClipboardHolder holder)  {
        File out = new File(main.getDataFolder() + File.separator + "schematics" + File.separator + sName + ".schematic");
        try {
            out.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Clipboard board = holder.getClipboard();


        schematicsFile.set("Schematics." + sName, direction.toUpperCase());

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(out))) {
            writer.write(board);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            schematicsFile.save(cFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void deleteSchematic(String sName) throws IOException {
        if (dir.listFiles().length == 0) return;
        for (File f : dir.listFiles()) {
            if (f.getName().replace(".schematic", "").equals(sName)) {
                f.delete();
                schematicsFile.set("Schematics." + sName, null);
                schematicsFile.save(cFile);
                break;
            }
        }
    }


    @SuppressWarnings(value = "all")
    public boolean doesExist(String sName) {

        for (File f : dir.listFiles()) {

            if (dir.listFiles().length == 0) return false;

            if (sName.equals(f.getName().replace(".schematic", ""))) return true;
        }
        return false;
    }

    public BuildSchematic getSchematic(String sName) {
        BuildSchematic sch = null;
        try {
            sch = new BuildSchematic(sName, main);
            return sch;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean hasPermission(String sName) {
        if (permSchematics.contains(sName)) return true;
        return false;
    }

    public YamlConfiguration getSchematicsFile(){
        return (YamlConfiguration) schematicsFile;
    }

}