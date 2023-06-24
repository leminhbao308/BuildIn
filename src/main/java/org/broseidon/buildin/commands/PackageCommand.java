package org.broseidon.buildin.commands;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.broseidon.buildin.builders.BlockBuilder;
import org.broseidon.buildin.main.BuildIn;
import org.broseidon.buildin.main.BuildSchematic;
import org.broseidon.buildin.main.PUtils;
import org.broseidon.buildin.main.SchematicManager;
import org.broseidon.buildin.objects.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PackageCommand implements CommandExecutor {
    private BuildIn main;
    private SchematicManager schematicManager = null;
    private static List<String> cmdList = Arrays.asList("delete", "give", "create", "list", "reload");

    public PackageCommand(BuildIn buildIn) {
        this.main = buildIn;
        schematicManager = main.getSchematicManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + Lang.CONSOLE.toString());
            return false;
        }

        if (command.getName().equalsIgnoreCase("package")) {

            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + Lang.SYNTAX.toString());
                return false;
            }

            if (!cmdList.contains(args[0])) {
                sender.sendMessage(ChatColor.RED + Lang.COMMANDS.toString());
                return false;
            }

            if (args[0].equalsIgnoreCase("reload")) {

                if (!sender.hasPermission("package.reload")) {
                    sender.sendMessage(ChatColor.RED + Lang.NO_PERM.toString());
                    return false;
                }

                main.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Đã tải lại config thành công!");

                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {

                if (!sender.hasPermission("package.list")) {
                    sender.sendMessage(ChatColor.RED + Lang.NO_PERM.toString());
                    return false;
                }

                YamlConfiguration schematicFile = schematicManager.getSchematicsFile();

                if (schematicFile.getConfigurationSection("Schematics") == null) {
                    sender.sendMessage(ChatColor.RED + "Danh sách công trình trống!");
                    return true;
                }

                for (String schematics : schematicFile.getConfigurationSection("Schematics").getKeys(false)) {
                    sender.sendMessage(ChatColor.YELLOW + schematics);
                }
                return true;

            }


            if (args[0].equalsIgnoreCase("create")) {

                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + Lang.CONSOLE.toString());
                    return false;
                }

                Player player1 = (Player) sender;

                if (!player1.hasPermission("package.create")) {
                    player1.sendMessage(ChatColor.RED + Lang.NO_PERM.toString());
                    return false;
                }

                if (args.length != 2) {
                    player1.sendMessage(ChatColor.RED + Lang.SYNTAX.toString());
                    player1.sendMessage(ChatColor.AQUA + "Lỗi: /package create [Tên công trình]");
                    return false;
                }


                String schematicName = args[1];

                if (schematicManager.doesExist(schematicName)) {
                    player1.sendMessage(ChatColor.RED + Lang.ALREADY_EXISTS.toString());
                    return false;
                }
                Actor actor = BukkitAdapter.adapt(player1); // WorldEdit's native Player class extends Actor
                SessionManager manager = main.getWorldEdit().getWorldEdit().getInstance().getSessionManager();
                LocalSession localSession = manager.get(actor);
                World selectionWorld = localSession.getSelectionWorld();

                if (localSession == null) {
                    player1.sendMessage(ChatColor.RED + "Không tìm thấy công trình!");
                    return false;
                }
                try {
                    if (selectionWorld == null) throw new IncompleteRegionException();
                } catch (IncompleteRegionException ex) {
                    actor.printError(TextComponent.of("Please make a region selection first."));
                    return false;
                }

                ClipboardHolder clipboard; // declare variable
                try {
                    clipboard = localSession.getClipboard();
                } catch (EmptyClipboardException ex) {
                    Bukkit.getPlayer(actor.getName()).sendMessage(ChatColor.RED + Lang.EMPTY_CLIPBOARD.toString());
                    return false;
                }

                String direction = PUtils.getCardinalDirection(player1.getLocation().getYaw(), false).toString();
                schematicManager.createSchematic(actor, schematicName, direction, clipboard);

                player1.sendMessage(ChatColor.AQUA + Lang.CREATE.toString().replace("%s", schematicName).replace("%d", direction));
                return true;

            }


            CommandSender p = sender;
            if (args[0].equalsIgnoreCase("delete")) {
                if (!sender.hasPermission("package.remove")) {
                    p.sendMessage(ChatColor.RED + Lang.NO_PERM.toString());
                    return false;
                }

                if (args.length != 2) {
                    p.sendMessage(ChatColor.RED + Lang.SYNTAX.toString());
                    p.sendMessage(ChatColor.AQUA + "Lỗi: /package delete [Tên công trình]");
                    return false;
                }

                if (!schematicManager.doesExist(args[1])) {
                    p.sendMessage(ChatColor.RED + Lang.EXISTS.toString());
                    return false;
                }
                try {
                    schematicManager.deleteSchematic(args[1]);
                    p.sendMessage(ChatColor.AQUA + Lang.DELETE.toString().replace("%s", args[1]));
                } catch (Exception ignored) {
                }
            }


            if (args[0].equalsIgnoreCase("give")) {

                if (!sender.hasPermission("package.give")) {
                    p.sendMessage(ChatColor.RED + Lang.NO_PERM.toString());
                    return false;
                }
                //in give pName schematicName 30
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + Lang.SYNTAX.toString());
                    p.sendMessage(ChatColor.AQUA + "Lỗi: /package give [player] [Tên công trình] [số lượng]");
                    return false;
                }

                Player target = Bukkit.getPlayer(args[1]);

                String schematicName = args[2];

                if (!schematicManager.doesExist(schematicName)) {
                    p.sendMessage(ChatColor.RED + Lang.EXISTS.toString());
                    return false;
                }

                if (target == null) {
                    p.sendMessage(ChatColor.RED + "Không tìm thấy người chơi!");
                    return false;
                }


                BlockBuilder builder = new BlockBuilder(schematicName, main);

                if (args.length == 4) {
                    int amount = Integer.parseInt(args[3]);
                    builder.setAmount(amount);
                    target.getInventory().addItem(builder.build());
                    target.updateInventory();
                    return true;
                }

                builder.setAmount(1);
                target.getInventory().addItem(builder.build());
                target.updateInventory();

            }
        }

        return false;
    }
}
