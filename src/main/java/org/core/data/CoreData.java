package org.core.data;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CoreData {
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

    public static void freeze(Player player) {
        frozen.add(player.getUniqueId());
        player.setFlying(true);
        player.setInvulnerable(true);
        String frozenmsg = org.core.Core.getConfigValue("messages.freeze.frozen");
        player.sendMessage(MiniMessage.miniMessage().deserialize(frozenmsg));
        String staffchat = org.core.Core.getConfigValue("messages.staff.prefix");
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (!p2.hasPermission("core.staffchat")) continue;
            p2.sendMessage(MiniMessage.miniMessage().deserialize(staffchat + player.getName() + " frozen!"));
        }
    }

    public static void unfreeze(Player player) {
        frozen.remove(player.getUniqueId());
        String unfrozenmsg = org.core.Core.getConfigValue("messages.freeze.unfrozen");
        player.sendMessage(MiniMessage.miniMessage().deserialize(unfrozenmsg));
        String staffchat = org.core.Core.getConfigValue("messages.staff.prefix");
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            if (!p2.hasPermission("core.staffchat")) continue;
            p2.sendMessage(MiniMessage.miniMessage().deserialize(staffchat + player.getName() + " unfrozen!"));
        }
    }

}
