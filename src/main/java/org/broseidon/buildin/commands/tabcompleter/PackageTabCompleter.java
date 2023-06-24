package org.broseidon.buildin.commands.tabcompleter;

import org.broseidon.buildin.main.BuildIn;
import org.broseidon.buildin.main.SchematicManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PackageTabCompleter implements TabCompleter {

    private BuildIn main;
    private SchematicManager schematicManager;

    public PackageTabCompleter(BuildIn main) {
        this.main = main;
        schematicManager = main.getSchematicManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("create", "delete", "give", "list"), new ArrayList<>());
        } else if (args.length == 2) {
            if (args[0].equals("delete")) {
                YamlConfiguration schematicFile = schematicManager.getSchematicsFile();
                List<String> schemFile = new ArrayList<>();
                for (String schematics : schematicFile.getConfigurationSection("Schematics").getKeys(false)) {
                    schemFile.add(schematics);
                }
                return StringUtil.copyPartialMatches(args[1], schemFile, new ArrayList<>());
            } else if (args[0].equals("give")) {
                List<String> names = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    names.add(player.getName());
                }
                return StringUtil.copyPartialMatches(args[1], names, new ArrayList<>());
            }
        } else if (args.length == 3) {
            if (args[0].equals("give")) {
                YamlConfiguration schematicFile = schematicManager.getSchematicsFile();
                List<String> schemFile = new ArrayList<>();
                for (String schematics : schematicFile.getConfigurationSection("Schematics").getKeys(false)) {
                    schemFile.add(schematics);
                }
                return StringUtil.copyPartialMatches(args[2], schemFile, new ArrayList<>());
            }
        }
        return new ArrayList<>();
    }
}
