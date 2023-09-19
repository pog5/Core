package org.prismcore.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.google.common.collect.ImmutableList;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.prismcore.Core;
import org.prismcore.data.CoreData;
import org.prismcore.messages.CoreMessages;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.prismcore.Core.getPlugin;
import static org.prismcore.data.CoreData.getVanishedPlayers;

public class CoreListeners implements Listener {

    public CoreListeners() {
        VanishListener();
    }

    public void VanishListener() {
        Core.getProtocolManager().addPacketListener(new PacketAdapter(getPlugin(), ListenerPriority.HIGH, PacketType.Status.Server.SERVER_INFO){
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                WrappedServerPing ping = packet.getServerPings().read(0);
                ImmutableList<WrappedGameProfile> players = ping.getPlayers();
                ImmutableList.Builder<WrappedGameProfile> newPlayers = ImmutableList.builder();
                for (WrappedGameProfile player : players) {
                    if (CoreData.isVanished((Player) player)) {
                        continue;
                    }
                    newPlayers.add(player);
                }
                ping.setPlayers(newPlayers.build());
                packet.getServerPings().write(0, ping);
                event.setPacket(packet);
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Join message
        event.joinMessage(MiniMessage.miniMessage().deserialize(CoreMessages.join, Placeholder.parsed("player", event.getPlayer().getName())));
        // Welcome Message
        Arrays.stream(CoreMessages.welcome).forEach(welcome -> event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(welcome)));
        // Vanish
        Set<UUID> vanished = getVanishedPlayers();
        if (vanished.isEmpty()) return;
        vanished.stream().filter(uuid -> !uuid.equals(event.getPlayer().getUniqueId())).filter(uuid -> Bukkit.getServer().getPlayer(uuid) != null).forEach(uuid -> event.getPlayer().hidePlayer(getPlugin(), Objects.requireNonNull(Bukkit.getServer().getPlayer(uuid))));
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        // Freeze
        if (CoreData.isFrozen(p)) {
            CoreData.unfreeze(p, null);
            p.setFlying(false);
            p.setInvulnerable(false);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + p.getName() + " left while frozen!"), "core.staffchat");
        }
        // Vanish
        if (CoreData.isVanished(event.getPlayer())) {
            CoreData.unvanish(event.getPlayer());
            event.quitMessage(null);
            return;
        }
        // Quit Message
        event.quitMessage(MiniMessage.miniMessage().deserialize(CoreMessages.leave, Placeholder.parsed("player", event.getPlayer().getName())));
    }

    // Freeze

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (CoreData.isFrozen(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onAnything(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (CoreData.isFrozen(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if (CoreData.isFrozen(p)) {
            e.setCancelled(true);
        }
    }

    // Chat

    @EventHandler(priority=EventPriority.MONITOR)
    public void onChat(AsyncChatEvent e) {
        if (!e.isCancelled()) {
            if (CoreData.isStaffchatting(e.getPlayer())) {
                e.setCancelled(true);
                new BukkitRunnable() {
                    public void run() {
                        Bukkit.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.chatformat,
                            Placeholder.unparsed("player", e.getPlayer().getName()),
                            Placeholder.unparsed("message", MiniMessage.miniMessage().serialize(e.message()))), "core.staffchat");
                    }
                }.runTaskAsynchronously(getPlugin());
                getPlugin().getServer().getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("<gold>[STAFFCHAT] " + e.getPlayer().getName() + ": " + MiniMessage.miniMessage().serialize(e.message())));
                return;
            }
            e.setCancelled(true);
            if (!e.getPlayer().hasPermission("core.chatfilter.bypass")) {
                for (String blocked : CoreMessages.chatfilter) {
                    if (MiniMessage.miniMessage().serialize(e.message()).contains(blocked)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.messagefiltered));
                        return;
                    }
                }
            }
            if (!e.getPlayer().hasPermission("core.chatcooldown.bypass")) {
                if (CoreData.chatCooldowns.contains(e.getPlayer().getUniqueId())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.cooldown));
                    return;
                }
            }
            if (Core.muteChat && !e.getPlayer().hasPermission("core.mutechat.bypass")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.chatstaffed,
                        Placeholder.unparsed("state", "muted")));
                return;
            }
            new BukkitRunnable() {
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (CoreData.isIgnored(p, e.getPlayer())) continue;
                        String message = MiniMessage.miniMessage().serialize(e.message());
                        if (message.contains(p.getName())) {
                            message = message.replaceFirst(p.getName(), CoreMessages.mentionformat);
                            String mentionmsg = MiniMessage.miniMessage().serialize(MiniMessage.miniMessage().deserialize(message,
                                    Placeholder.unparsed("mentioned", p.getName())));
                            Component pformat = MiniMessage.miniMessage().deserialize(CoreMessages.chatformat,
                                    Placeholder.unparsed("player", e.getPlayer().getName()),
                                    Placeholder.parsed("message", mentionmsg));
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                            p.sendMessage(pformat);
                            continue;
                        }
                        p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.chatformat,
                                Placeholder.unparsed("player", e.getPlayer().getName()),
                                Placeholder.unparsed("message", message)));
                    }
                }
            }.runTaskAsynchronously(getPlugin());
            getPlugin().getServer().getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("<aqua>[CHAT] " + e.getPlayer().getName() + ": " + MiniMessage.miniMessage().serialize(e.message())));
            CoreData.chatCooldowns.add(e.getPlayer().getUniqueId());
            new BukkitRunnable() {
                public void run() {
                    CoreData.chatCooldowns.remove(e.getPlayer().getUniqueId());
                }
            }.runTaskLater(getPlugin(), 10L);
        }
    }
}
