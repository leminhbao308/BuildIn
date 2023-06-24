package org.broseidon.buildin.main;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.DataException;
import org.broseidon.buildin.commands.PackageCommand;
import org.broseidon.buildin.commands.tabcompleter.PackageTabCompleter;
import org.broseidon.buildin.events.BlockListener;
import org.broseidon.buildin.events.PlayerActionsListener;
import org.broseidon.buildin.objects.BuildManager;
import org.broseidon.buildin.objects.Lang;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BuildIn extends JavaPlugin {
    private SchematicManager manager;
    private FileConfiguration config;
    private RegionManager rgManager;
    public Set<String> dependencies;
    private File lang_file;
    private FileConfiguration lang;
    private BuildManager buildManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginManager pm = Bukkit.getPluginManager();

        if (!getDataFolder().exists())
            createPluginFolders();

        lang_file = new File(getDataFolder() + File.separator + "lang.yml");

        if (lang_file == null || !lang_file.exists()) {
            createLangFile();
            loadLangFileDefaults();
        }

        lang = YamlConfiguration.loadConfiguration(lang_file);
        Lang.setFile(lang);

        if (!new File(getDataFolder() + File.separator + "config.yml").exists())
            saveDefaultConfig();


        buildManager = new BuildManager(this);
        initDependencies();
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "config.yml"));
        manager = new SchematicManager(this);
        getCommand("package").setExecutor(new PackageCommand(this));
        getCommand("package").setTabCompleter(new PackageTabCompleter(this));

        try {
            buildManager.initTasks();
        } catch (IOException e) {
            Bukkit.getLogger().severe("There are invalid build tasks!");
            //clear tasks
        } catch (DataException e) {
            e.printStackTrace();
        }

        rgManager = new RegionManager(this);
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new PlayerActionsListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        buildManager.saveAllTasks();
        getLogger().info("Saving all current build tasks!");
    }

    private void createPluginFolders() {
        getDataFolder().mkdirs();
        new File(getDataFolder() + File.separator + "schematics").mkdirs();
        try {
            new File(getDataFolder() + File.separator + "schematics.yml").createNewFile();
            saveDefaultConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createLangFile() {
        try {
            lang_file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lang = YamlConfiguration.loadConfiguration(lang_file);
    }

    public void loadLangFileDefaults() {
        for (Lang value : Lang.values()) {
            lang.set(value.getPath(), value.getDefault());
        }
        try {
            lang.save(lang_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDependencies() {
        List<String> depends = Collections.singletonList("WorldGuard");
        dependencies = new HashSet<>();

        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            if (depends.contains(p.getName())) {
                dependencies.add(p.getName());
            }
        }
    }

    public WorldEditPlugin getWorldEdit() {

        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        if (plugin == null || !(plugin instanceof WorldEditPlugin))
            return null;

        return (WorldEditPlugin) plugin;
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public SchematicManager getSchematicManager() {
        return manager;
    }

    public RegionManager getrManager() {
        return rgManager;
    }

    public BuildManager getBuildManager() {
        return buildManager;
    }

}
