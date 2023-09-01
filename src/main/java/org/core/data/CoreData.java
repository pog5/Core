package org.core.data;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.core.Core;

import java.util.*;

public class CoreData {
    public static List<UUID> chatCooldowns = new ArrayList<>();
    public static Map<UUID, UUID> dms = new WeakHashMap<>();
    public static Map<UUID, UUID> getDms() {
        return dms;
    }

    private static final Plugin Core = org.core.Core.getPlugin();

    // Vanish and Unvanish

    public static Set<UUID> vanished = new HashSet<>();
    public static Set<UUID> getVanishedPlayers() {
        return vanished;
    }
    public static boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }
    public static void vanish(Player player) {
        Core.getServer().getOnlinePlayers().stream().filter(p -> p != player && !p.hasPermission("core.vanish.bypass")).forEach(p -> p.hidePlayer(Core, player));
        player.setMetadata("vanished", new FixedMetadataValue(Core, true));
        vanished.add(player.getUniqueId());
        String vanishedmsg = org.core.Core.getConfigValue("messages.vanish.vanished");
        player.sendMessage(MiniMessage.miniMessage().deserialize(vanishedmsg));
        String left = org.core.Core.getConfigValue("messages.quit");
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(left + player.getName()));
        String staffchat = org.core.Core.getConfigValue("messages.staff.prefix");
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (!p2.hasPermission("core.staffchat")) continue;
            p2.sendMessage(MiniMessage.miniMessage().deserialize(staffchat + player.getName() + " vanished!"));
        }
    }

    public static void unvanish(Player player) {
        Core.getServer().getOnlinePlayers().forEach(p -> p.showPlayer(Core, player));
        player.setMetadata("vanished", new FixedMetadataValue(Core, false));
        vanished.remove(player.getUniqueId());
        String unvanishedmsg = org.core.Core.getConfigValue("messages.vanish.unvanished");
        player.sendMessage(MiniMessage.miniMessage().deserialize(unvanishedmsg));
        String join = org.core.Core.getConfigValue("messages.join");
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(join + player.getName()));
        String staffchat = org.core.Core.getConfigValue("messages.staff.prefix");
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (!p2.hasPermission("core.staffchat")) continue;
            p2.sendMessage(MiniMessage.miniMessage().deserialize(staffchat + player.getName() + " unvanished!"));
        }
    }

    // Freeze and Unfreeze

    public static Set<UUID> frozen = new HashSet<>();
    public static boolean isFrozen(Player player) {
        return frozen.contains(player.getUniqueId());
    }

    public static void freeze(Player player, Player staff) {
        frozen.add(player.getUniqueId());
        if (!player.getAllowFlight()) {
            player.setAllowFlight(true);
        }
        player.setFlying(true);
        player.setInvulnerable(true);
        String frozenmsg = org.core.Core.getConfigValue("messages.freeze.frozen");
        player.sendMessage(MiniMessage.miniMessage().deserialize(frozenmsg));
        String staffchat = org.core.Core.getConfigValue("messages.staff.prefix");
        String frozen = org.core.Core.getConfigValue("messages.staff.frozen");
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (!p2.hasPermission("core.staffchat")) continue;
            p2.sendMessage(MiniMessage.miniMessage().deserialize(staffchat + frozen, Placeholder.unparsed("target", player.getName()), Placeholder.unparsed("un", "") , Placeholder.unparsed("player", staff.getName())));
        }
    }

    public static void unfreeze(Player player, Player staff) {
        frozen.remove(player.getUniqueId());
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE || player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
            player.setAllowFlight(false);
        }
        player.setFlying(false);
        player.setInvulnerable(false);
        String unfrozenmsg = org.core.Core.getConfigValue("messages.freeze.unfrozen");
        player.sendMessage(MiniMessage.miniMessage().deserialize(unfrozenmsg));
        String staffchat = org.core.Core.getConfigValue("messages.staff.prefix");
        String frozen = org.core.Core.getConfigValue("messages.staff.frozen");
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (!p2.hasPermission("core.staffchat")) continue;
            if (!(staff == null)) {
                p2.sendMessage(MiniMessage.miniMessage().deserialize(staffchat + frozen, Placeholder.unparsed("target", player.getName()), Placeholder.unparsed("un", "un"), Placeholder.unparsed("player", staff.getName())));
            }
        }
    }

    // Staffchat

    public static Set<UUID> staffchatters = new HashSet<>();

    public static boolean isStaffchatting(Player player) {
        return staffchatters.contains(player.getUniqueId());
    }

    public static void staffchat(Player player) {
        staffchatters.add(player.getUniqueId());
        String staffchatmsg = org.core.Core.getConfigValue("messages.staff.chatenabled");
        player.sendMessage(MiniMessage.miniMessage().deserialize(staffchatmsg));
    }

    public static void unstaffchat(Player player) {
        staffchatters.remove(player.getUniqueId());
        String staffchatmsg = org.core.Core.getConfigValue("messages.staff.chatdisabled");
        player.sendMessage(MiniMessage.miniMessage().deserialize(staffchatmsg));
    }

    // Ignore

    public static boolean isIgnored(Player player, Player target) {
        return org.core.Core.getData().getBoolean("player." + player.getUniqueId() + ".ignored." + target.getUniqueId());
    }

    public static void ignore(Player player, Player target) {
        if (player == target) {
            String ignoremsg = org.core.Core.getConfigValue("messages.invalid");
            player.sendMessage(MiniMessage.miniMessage().deserialize(ignoremsg));
            return;
        }
        org.core.Core.getData().set("player." + player.getUniqueId() + ".ignored." + target.getUniqueId(), true);
        org.core.Core.saveData();
        String ignoremsg = org.core.Core.getConfigValue("messages.ignore.ignored");
        player.sendMessage(MiniMessage.miniMessage().deserialize(ignoremsg));
    }

    public static void unignore(Player player, Player target) {
        if (player == target) {
            String unignoremsg = org.core.Core.getConfigValue("messages.invalid");
            player.sendMessage(MiniMessage.miniMessage().deserialize(unignoremsg));
            return;
        }
        org.core.Core.getData().set("player." + player.getUniqueId() + ".ignored." + target.getUniqueId(), false);
        org.core.Core.saveData();
        String unignoremsg = org.core.Core.getConfigValue("messages.ignore.unignored");
        player.sendMessage(MiniMessage.miniMessage().deserialize(unignoremsg));
    }
}