package org.prismcore.data;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.prismcore.messages.CoreMessages;

import java.util.*;

public class CoreData {
    public static List<UUID> chatCooldowns = new ArrayList<>();
    public static Map<UUID, UUID> dms = new WeakHashMap<>();
    public static Map<UUID, UUID> getDms() {
        return dms;
    }

    private static final Plugin Core = org.prismcore.Core.getPlugin();

    // Vanish and Unvanish

    public static Set<UUID> vanished = new HashSet<>();
    public static Set<UUID> getVanishedPlayers() {
        return vanished;
    }
    public static boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }
    public static void vanish(Player player) {
        Core.getServer().getOnlinePlayers().stream().filter(p -> p != player && !p.hasPermission("prismcore.vanish.bypass")).forEach(p -> p.hidePlayer(Core, player));
        player.setMetadata("vanished", new FixedMetadataValue(Core, true));
        vanished.add(player.getUniqueId());
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.leave, Placeholder.parsed("player", player.getName())));
        Core.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.staffvanished,
                Placeholder.parsed("player", player.getName()),
                Placeholder.parsed("un", "")), "prismcore.staffchat");
    }

    public static void unvanish(Player player) {
        Core.getServer().getOnlinePlayers().forEach(p -> p.showPlayer(Core, player));
        player.setMetadata("vanished", new FixedMetadataValue(Core, false));
        vanished.remove(player.getUniqueId());
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.join, Placeholder.parsed("player", player.getName())));
        Core.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.staffvanished,
                Placeholder.parsed("player", player.getName()),
                Placeholder.parsed("un", "un")), "prismcore.staffchat");
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
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.frozen));
        if (staff == null) {
            Core.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.stafffrozen,
                    Placeholder.unparsed("target", player.getName()),
                    Placeholder.unparsed("un", ""),
                    Placeholder.unparsed("player", "Console")), "prismcore.staffchat");
        } else {
            Core.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.stafffrozen,
                    Placeholder.unparsed("target", player.getName()),
                    Placeholder.unparsed("un", ""),
                    Placeholder.unparsed("player", staff.getName())), "prismcore.staffchat");
        }
    }

    public static void unfreeze(Player player, Player staff) {
        frozen.remove(player.getUniqueId());
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE || player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
            player.setAllowFlight(false);
        }
        player.setFlying(false);
        player.setInvulnerable(false);
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.unfrozen));
        if (staff == null) {
            Core.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.stafffrozen,
                    Placeholder.unparsed("target", player.getName()),
                    Placeholder.unparsed("un", "un"),
                    Placeholder.unparsed("player", "Console")), "prismcore.staffchat");
        } else {
            Core.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.stafffrozen,
                    Placeholder.unparsed("target", player.getName()),
                    Placeholder.unparsed("un", "un"),
                    Placeholder.unparsed("player", staff.getName())), "prismcore.staffchat");
        }
    }

    // Staffchat

    public static Set<UUID> staffchatters = new HashSet<>();

    public static boolean isStaffchatting(Player player) {
        return staffchatters.contains(player.getUniqueId());
    }

    public static void staffchat(Player player) {
        staffchatters.add(player.getUniqueId());
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.staffchattoggled, Placeholder.unparsed("state", "enabled")));
    }

    public static void unstaffchat(Player player) {
        staffchatters.remove(player.getUniqueId());
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.staffchattoggled, Placeholder.unparsed("state", "disabled")));
    }

    // Ignore

    public static boolean isIgnored(Player player, Player target) {
        return org.prismcore.Core.getData().getBoolean("player." + player.getUniqueId() + ".ignored." + target.getUniqueId());
    }

    public static void ignore(Player player, Player target) {
        if (player == target) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
            return;
        }
        org.prismcore.Core.getData().set("player." + player.getUniqueId() + ".ignored." + target.getUniqueId(), true);
        org.prismcore.Core.saveData();
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.ignored,
                Placeholder.unparsed("state", "ignored"),
                Placeholder.unparsed("player", target.getName())));
    }

    public static void unignore(Player player, Player target) {
        if (player == target) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
            return;
        }
        org.prismcore.Core.getData().set("player." + player.getUniqueId() + ".ignored." + target.getUniqueId(), false);
        org.prismcore.Core.saveData();
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.ignored,
                Placeholder.unparsed("state", "unignored"),
                Placeholder.unparsed("player", target.getName())));
    }

    // Mutes

    public static boolean isMuted(Player player) {
        return !player.getMetadata("muteend").isEmpty();
    }

    public static Date getMuteTime(Player player) {
        return (Date) player.getMetadata("muteend").get(0).value();
    }

    public static String getMuteReason(Player player) {
        return (String) player.getMetadata("mutereason").get(0).value();
    }

    public static OfflinePlayer getMuter(Player player) {
        return Bukkit.getOfflinePlayer((String) player.getMetadata("muter").get(0).value());
    }

    public static void mute(Player player, String reason, Date date, CommandSender staff) {
        if (!player.getMetadata("muteend").isEmpty()) {
            staff.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#ff5555:#ffb86c>That player is already muted!"));
            return;
        }
        player.setMetadata("muteend", new FixedMetadataValue(Core, date));
        player.setMetadata("mutereason", new FixedMetadataValue(Core, reason));
        if (staff != null) {
            player.setMetadata("muter", new FixedMetadataValue(Core, staff.getName()));
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.muted,
                    Placeholder.unparsed("expiry", date.toString()),
                    Placeholder.unparsed("reason", reason),
                    Placeholder.unparsed("staff", staff.getName())));
        } else {
            player.setMetadata("muter", new FixedMetadataValue(Core, "Console"));
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.muted,
                    Placeholder.unparsed("expiry", date.toString()),
                    Placeholder.unparsed("reason", reason),
                    Placeholder.unparsed("staff", "Console")));
        }
    }

    public static void unmute(Player player, CommandSender staff) {
        if (player.getMetadata("muteend").isEmpty()) {
            staff.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.notmuted));
            return;
        }
        player.removeMetadata("muteend", Core);
        player.removeMetadata("mutereason", Core);
        player.removeMetadata("muter", Core);
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.unmuted));
    }
}