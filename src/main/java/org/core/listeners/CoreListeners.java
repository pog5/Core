package org.core.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.core.data.CoreData;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.core.Core.getPlugin;
import static org.core.data.CoreData.getVanishedPlayers;

public class CoreListeners implements Listener {

    public CoreListeners() {
        VanishListener();
    }

    public void VanishListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(getPlugin(), ListenerPriority.HIGH, PacketType.Status.Server.SERVER_INFO){
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                WrappedServerPing ping = packet.getServerPings().read(0);
                ImmutableList<WrappedGameProfile> players = ping.getPlayers();
                ping.setPlayers(players.stream().filter((player) -> !getVanishedPlayers().contains(((Player)player).getUniqueId())).collect(Collectors.toList()));
                packet.getServerPings().write(0, ping);
                event.setPacket(packet);
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Set<UUID> vanished = getVanishedPlayers();
        if (vanished.isEmpty()) {
            return;
        }
        vanished.stream().filter(uuid -> !uuid.equals(event.getPlayer().getUniqueId())).filter(uuid -> Bukkit.getServer().getPlayer(uuid) != null).forEach(uuid -> event.getPlayer().hidePlayer(getPlugin(), Objects.requireNonNull(Bukkit.getServer().getPlayer(uuid))));
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        // Vanish
        if (CoreData.isVanished(event.getPlayer())) {
            CoreData.unvanish(event.getPlayer());
            event.quitMessage(null);
        }
        // Freeze
        if (CoreData.isFrozen(p)) {
            CoreData.unfreeze(p);
            p.setFlying(false);
            p.setInvulnerable(false);
            String staffprefix = org.core.Core.getConfigValue("messages.staff.prefix");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("core.staffchat")) continue;
                player.sendMessage(MiniMessage.miniMessage().deserialize(staffprefix + p.getName() + " <red>left while frozen!"));
            }
        }
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

}
