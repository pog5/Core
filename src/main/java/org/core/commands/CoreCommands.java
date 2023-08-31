package org.core.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.Greedy;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CoreCommands {

    public static Map<UUID, UUID> msg = new WeakHashMap<>();
    BukkitTask task;

    // Alert
    @ProxiedBy("broadcast")
    @CommandMethod("alert <message>")
    @CommandDescription("Send an alert to all players")
    @CommandPermission("core.alert")
    public void alert(final @NotNull CommandSender sender, final @NotNull @Greedy @Argument("message") String message) {
        String alert = org.core.Core.getConfigValue("messages.alert");
        sender.getServer().broadcast(MiniMessage.miniMessage().deserialize(alert + message));
    }

    // Day

    @CommandMethod("day [world]")
    @CommandDescription("Set the time to day")
    @CommandPermission("core.time")
    public void day(final @NotNull CommandSender sender, @Argument("world") World world) {
        if (world == null) {
            if (sender instanceof Player) {
                world = Objects.requireNonNull(sender.getServer().getPlayer(sender.getName())).getWorld();
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
                return;
            }
        }
        Objects.requireNonNull(sender.getServer().getWorld(world.getUID())).setTime(6000);
        String day = org.core.Core.getConfigValue("messages.day");
        sender.sendMessage(MiniMessage.miniMessage().deserialize(day));
    }

    // Night

    @CommandMethod("night [world]")
    @CommandDescription("Set the time to night")
    @CommandPermission("core.time")
    public void night(final @NotNull CommandSender sender, @Argument("world") World world) {
        if (world == null) {
            if (sender instanceof Player) {
                world = Objects.requireNonNull(sender.getServer().getPlayer(sender.getName())).getWorld();
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
                return;
            }
        }
        Objects.requireNonNull(sender.getServer().getWorld(world.getUID())).setTime(18000);
        String night = org.core.Core.getConfigValue("messages.night");
        sender.sendMessage(MiniMessage.miniMessage().deserialize(night));
    }

    // Discord

    @CommandMethod("discord")
    @CommandDescription("Get the discord link")
    @CommandPermission("core.discord")
    public void discord(final @NotNull CommandSender sender) {
        String discord = org.core.Core.getConfigValue("messages.discord");
        String discordLink = org.core.Core.getConfigValue("discord");
        sender.sendMessage(MiniMessage.miniMessage().deserialize(discord + "<click:open_url:" + discordLink + ">discordLink"));
    }

    // Feed

    @CommandMethod("feed [player]")
    @CommandDescription("Feed yourself or another player")
    @CommandPermission("core.feed")
    public void feed(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        String feed = org.core.Core.getConfigValue("messages.feed");
        if (player != null && sender.hasPermission("core.feed.others")) {
            player.setFoodLevel(20);
            player.sendMessage(MiniMessage.miniMessage().deserialize(feed));
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setFoodLevel(20);
                p.sendMessage(MiniMessage.miniMessage().deserialize(feed));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }


    // Fly

    @CommandMethod("fly [player]")
    @CommandDescription("Toggle fly for yourself or another player")
    @CommandPermission("core.fly")
    public void fly(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        String fly = org.core.Core.getConfigValue("messages.fly");
        if (player != null && sender.hasPermission("core.fly.others")) {
            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                player.sendMessage(MiniMessage.miniMessage().deserialize(fly + "disabled!"));
            } else {
                player.setAllowFlight(true);
                player.sendMessage(MiniMessage.miniMessage().deserialize(fly + "enabled!"));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.getAllowFlight()) {
                    p.setAllowFlight(false);
                    p.sendMessage(MiniMessage.miniMessage().deserialize(fly + "disabled!"));
                } else {
                    p.setAllowFlight(true);
                    p.sendMessage(MiniMessage.miniMessage().deserialize(fly + "enabled!"));
                }
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    // Freeze
    @ProxiedBy("unfreeze")
    @CommandMethod("freeze|ss <player>")
    @CommandDescription("Freeze a player")
    @CommandPermission("core.freeze")
    public void freeze(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player) {
        String playerstr = org.core.Core.getConfigValue("messages.player");
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
            String unfrozen = org.core.Core.getConfigValue("messages.freeze.unfrozen");
            player.sendMessage(MiniMessage.miniMessage().deserialize(unfrozen));
            sender.sendMessage(MiniMessage.miniMessage().deserialize(playerstr + "unfrozen!"));
        } else {
            player.setInvulnerable(true);
            String frozen = org.core.Core.getConfigValue("messages.freeze.frozen");
            player.sendMessage(MiniMessage.miniMessage().deserialize(frozen));
            sender.sendMessage(MiniMessage.miniMessage().deserialize(playerstr + "frozen!"));
        }
    }

    // Gamemode

    @ProxiedBy("gm")
    @CommandMethod("gamemode <gamemode> [player]")
    @CommandDescription("Change someone's gamemode")
    @CommandPermission("core.gamemode")
    public void gamemode(final @NotNull CommandSender sender, final @NotNull @Argument("gamemode") GameMode gamemode, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.gamemode.others")) {
                player.setGameMode(gamemode);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                player.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + player.getName() + "'s gamemode was set to " + gamemode.name().toUpperCase()));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setGameMode(gamemode);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                p.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + "Your gamemode was set to " + gamemode.name().toUpperCase()));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    @ProxiedBy("gm1")
    @CommandMethod("gmc [player]")
    @CommandDescription("Change someone's gamemode to creative")
    @CommandPermission("core.gamemode")
    public void gmc(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.gamemode.others")) {
                player.setGameMode(GameMode.CREATIVE);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                player.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + player.getName() + "'s gamemode was set to CREATIVE"));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setGameMode(GameMode.CREATIVE);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                p.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + "Your gamemode was set to CREATIVE"));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    @ProxiedBy("gm0")
    @CommandMethod("gms [player]")
    @CommandDescription("Change someone's gamemode to survival")
    @CommandPermission("core.gamemode")
    public void gms(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.gamemode.others")) {
                player.setGameMode(GameMode.SURVIVAL);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                player.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + player.getName() + "'s gamemode was set to SURVIVAL"));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setGameMode(GameMode.SURVIVAL);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                p.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + "Your gamemode was set to SURVIVAL"));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    @ProxiedBy("gm3")
    @CommandMethod("gmsp [player]")
    @CommandDescription("Change someone's gamemode to spectator")
    @CommandPermission("core.gamemode")
    public void gmsp(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.gamemode.others")) {
                player.setGameMode(GameMode.SPECTATOR);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                player.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + player.getName() + "'s gamemode was set to SPECTATOR"));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setGameMode(GameMode.SPECTATOR);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                p.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + "Your gamemode was set to SPECTATOR"));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    @ProxiedBy("gm2")
    @CommandMethod("gma [player]")
    @CommandDescription("Change someone's gamemode to adventure")
    @CommandPermission("core.gamemode")
    public void gma(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.gamemode.others")) {
                player.setGameMode(GameMode.ADVENTURE);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                player.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + player.getName() + "'s gamemode was set to ADVENTURE"));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setGameMode(GameMode.ADVENTURE);
                String gamemodestr = org.core.Core.getConfigValue("messages.gamemode");
                p.sendMessage(MiniMessage.miniMessage().deserialize(gamemodestr + "Your gamemode was set to ADVENTURE"));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    // Heal

    @CommandMethod("heal [player]")
    @CommandDescription("Heal yourself or another player")
    @CommandPermission("core.heal")
    public void heal(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        String heal = org.core.Core.getConfigValue("messages.heal");
        if (player != null) {
            if (sender.hasPermission("core.heal.others")) {
                player.setHealth(20);
                player.sendMessage(MiniMessage.miniMessage().deserialize(heal));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setHealth(20);
                p.sendMessage(MiniMessage.miniMessage().deserialize(heal));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    // Invsee

    @CommandMethod("invsee <player>")
    @CommandDescription("View another player's inventory")
    @CommandPermission("core.invsee")
    public void invsee(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.openInventory(player.getInventory());
        } else {
            String invalid = org.core.Core.getConfigValue("messages.invalid");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
        }
    }

    // Item
    @ProxiedBy("i")
    @CommandMethod("item <item> [amount]")
    @CommandDescription("Give yourself some items")
    @CommandPermission("core.item")
    public void item(final @NotNull CommandSender sender, final @NotNull @Argument("item") Material item, final @Argument("amount") int amount) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            ItemStack itemStack = new ItemStack(item);
            if (amount > 0 && amount < 65) {
                itemStack.setAmount(amount);
            }
            p.getInventory().addItem(itemStack);
            String gave = org.core.Core.getConfigValue("messages.gave");
            p.sendMessage(MiniMessage.miniMessage().deserialize(gave + " you " + itemStack.getAmount() + " of " + itemStack.getType().name()));
        } else {
            String invalid = org.core.Core.getConfigValue("messages.invalid");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
        }
    }

    // Give

    @CommandMethod("give <player> <item> [amount]")
    @CommandDescription("Give a player or yourself some items")
    @CommandPermission("core.give")
    public void give(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player, final @NotNull @Argument("item") Material item, final @Argument("amount") int amount) {
        if (sender.hasPermission("core.give.others")) {
            ItemStack itemStack = new ItemStack(item);
            if (amount > 0 && amount < 65) {
                itemStack.setAmount(amount);
            }
            player.getInventory().addItem(itemStack);
            String gave = org.core.Core.getConfigValue("messages.gave");
            player.sendMessage(MiniMessage.miniMessage().deserialize(gave + " you " + itemStack.getAmount() + " of " + itemStack.getType().name()));
            sender.sendMessage(MiniMessage.miniMessage().deserialize(gave + " " + player.getName() + " " + itemStack.getAmount() + " of " + itemStack.getType().name()));
        } else {
            String noperms = org.core.Core.getConfigValue("messages.noperms");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms + "Use /item instead"));
        }
    }

    // Teleport and Teleport Here

    @ProxiedBy("tp")
    @CommandMethod("teleport <player> [target]")
    @CommandDescription("Teleport yourself or another player to another player")
    @CommandPermission("core.teleport")
    public void teleport(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player, final @Argument("target") Player target) {
        if (target != null) {
            if (sender.hasPermission("core.teleport.others")) {
                player.teleport(target);
                String teleported = org.core.Core.getConfigValue("messages.teleported");
                player.sendMessage(MiniMessage.miniMessage().deserialize(teleported + player.getName() + " to " + target.getName()));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.teleport(player);
                String teleported = org.core.Core.getConfigValue("messages.teleported");
                p.sendMessage(MiniMessage.miniMessage().deserialize(teleported + " to " + player.getName()));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    @ProxiedBy("tph")
    @CommandMethod("tphere <player>")
    @CommandDescription("Teleport another player to yourself")
    @CommandPermission("core.teleporthere")
    public void teleporthere(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            player.teleport(p);
            String teleported = org.core.Core.getConfigValue("messages.teleported");
            player.sendMessage(MiniMessage.miniMessage().deserialize(teleported + player.getName() + " to you."));
        } else {
            String invalid = org.core.Core.getConfigValue("messages.invalid");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
        }
    }

    @ProxiedBy("tpcoords")
    @CommandMethod("teleportcoords <x> <y> <z> [world]")
    @CommandDescription("Teleport yourself to coordinates")
    @CommandPermission("core.teleportcoords")
    public void teleportcoords(final @NotNull CommandSender sender, final @Argument("x") int x, final @Argument("y") int y, final @Argument("z") int z, final @Argument("world") World world) {
        if (!(sender instanceof Player)) {
            String invalid = org.core.Core.getConfigValue("messages.invalid");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            return;
        }
        Player p = (Player) sender;
        if (world == null) {
            p.teleport(new Location(p.getWorld(), x, y, z));
            String teleported = org.core.Core.getConfigValue("messages.teleported");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(teleported + " to " + x + ", " + y + ", " + z));
        } else {
            p.teleport(new Location(world, x, y, z));
            String teleported = org.core.Core.getConfigValue("messages.teleported");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(teleported + " to " + x + ", " + y + ", " + z + " in " + world.getName().toUpperCase()));
        }
    }

    // Lag

    @CommandMethod("lag")
    @CommandDescription("Get the server's performance status")
    @CommandPermission("core.lag")
    public void lag(final @NotNull CommandSender sender) {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        String lag = org.core.Core.getConfigValue("messages.lag");
        sender.sendMessage(MiniMessage.miniMessage().deserialize(lag + "TPS: " + Double.parseDouble(numberFormat.format(Bukkit.getServer().getTPS()[0])) + " | Ping: " + ((Player) sender).getPing() + "ms" + " | MSPT: " + Double.parseDouble(numberFormat.format(Bukkit.getServer().getAverageTickTime())) + "ms" + " | RAM: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L + "MB"));
    }

    // Tell

    @ProxiedBy("msg")
    @CommandMethod("tell|whisper <player> <message>")
    @CommandDescription("Send a private message to a player")
    @CommandPermission("core.tell")
    public void tell(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player, final @NotNull @Greedy @Argument("message") String message) {
        String to = org.core.Core.getConfigValue("messages.msg.to");
        String from = org.core.Core.getConfigValue("messages.msg.from");
        sender.sendMessage(MiniMessage.miniMessage().deserialize(to + player.getName() + ": <reset>" + message));
        if (sender instanceof Player) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(from + sender.getName() + ": <reset>" + message));
            Player p = (Player) sender;
            msg.put(player.getUniqueId(), p.getUniqueId());
            msg.put(p.getUniqueId(), player.getUniqueId());
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize(from + "CONSOLE: <reset>" + message));
        }
    }

    // Reply

    @ProxiedBy("r")
    @CommandMethod("reply <message>")
    @CommandDescription("Reply to a private message")
    @CommandPermission("core.reply")
    public void reply(final @NotNull CommandSender sender, final @NotNull @Greedy @Argument("message") String message) {
        if (!(sender instanceof Player)) {
            String invalid = org.core.Core.getConfigValue("messages.invalid");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            return;
        }
        String to = org.core.Core.getConfigValue("messages.msg.to");
        String from = org.core.Core.getConfigValue("messages.msg.from");
        Player p = (Player) sender;
        if (msg.containsKey(p.getUniqueId())) {
            Player player = Bukkit.getPlayer(msg.get(p.getUniqueId()));
            if (player != null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(to + player.getName() + ": <reset>" + message));
                player.sendMessage(MiniMessage.miniMessage().deserialize(from + sender.getName() + ": <reset>" + message));
                msg.put(player.getUniqueId(), p.getUniqueId());
                msg.put(p.getUniqueId(), player.getUniqueId());
            } else {
                String noreply = org.core.Core.getConfigValue("messages.noreply");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noreply));
            }
        } else {
            String noreply = org.core.Core.getConfigValue("messages.msg.noreply");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(noreply));
        }
    }

    // Staff Chat

    @ProxiedBy("sc")
    @CommandMethod("staffchat <message>")
    @CommandDescription("Talk to other staff")
    @CommandPermission("core.staffchat")
    public void staffchat(final @NotNull CommandSender sender, final @NotNull @Greedy @Argument("message") String message) {
        String staffchat = org.core.Core.getConfigValue("messages.staffchat");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("core.staffchat")) {
                p.sendMessage(MiniMessage.miniMessage().deserialize(staffchat + sender.getName() + ": <reset>" + message));
            }
        }
    }

    // Ping

    @CommandMethod("ping [player]")
    @CommandDescription("Get your ping or another player's ping")
    @CommandPermission("core.ping")
    public void ping(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.ping.others")) {
                String ping = org.core.Core.getConfigValue("messages.ping");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(ping + " of " + player.getName() + ": " + player.getPing() + "ms"));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                String ping = org.core.Core.getConfigValue("messages.ping");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(ping + ": " + p.getPing() + "ms"));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    // Speed, Walkspeed and Flyspeed

    @CommandMethod("speed <speed> [player]")
    @CommandDescription("Change yours or another player's flight/movement speed depending on if they are walking or flying")
    @CommandPermission("core.speed")
    public void speed(final @NotNull CommandSender sender, final @NotNull @Argument("speed") Float speed, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.speed.others")) {
                if (player.isFlying()) {
                    player.setFlySpeed(speed);
                } else {
                    player.setWalkSpeed(speed);
                }
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.isFlying()) {
                    p.setFlySpeed(speed);
                } else {
                    p.setWalkSpeed(speed);
                }
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    @ProxiedBy("wspeed")
    @CommandMethod("walkspeed <speed> [player]")
    @CommandDescription("Change yours or another player's walking speed")
    @CommandPermission("core.speed")
    public void walkspeed(final @NotNull CommandSender sender, final @NotNull @Argument("speed") Float speed, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.speed.others")) {
                player.setWalkSpeed(speed);
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setWalkSpeed(speed);
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    @ProxiedBy("fspeed")
    @CommandMethod("flyspeed <speed> [player]")
    @CommandDescription("Change yours or another player's flying speed")
    @CommandPermission("core.speed")
    public void flyspeed(final @NotNull CommandSender sender, final @NotNull @Argument("speed") Float speed, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.speed.others")) {
                player.setFlySpeed(speed);
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setFlySpeed(speed);
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    // Vanish

    @ProxiedBy("unvanish")
    @CommandMethod("vanish")
    @CommandDescription("Un/Vanish yourself")
    @CommandPermission("core.vanish")
    public void vanish(final @NotNull CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.isInvisible()) {
                p.setInvisible(false);
                String unvanished = org.core.Core.getConfigValue("messages.vanish.unvanished");
                p.sendMessage(MiniMessage.miniMessage().deserialize(unvanished));
            } else {
                p.setInvisible(true);
                String vanished = org.core.Core.getConfigValue("messages.vanish.vanished");
                p.sendMessage(MiniMessage.miniMessage().deserialize(vanished));
            }
        } else {
            String invalid = org.core.Core.getConfigValue("messages.invalid");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
        }
    }

    // Reboot

    @ProxiedBy("restart")
    @CommandMethod("reboot [cancel]")
    @CommandDescription("Reboot the server")
    @CommandPermission("core.reboot")
    public void reboot(final @NotNull CommandSender sender, final @Argument("cancel") String cancel) {
        if (cancel.equalsIgnoreCase("cancel")) {
            this.task.cancel();
            this.task = null;
        } else {
            AtomicInteger countdown = new AtomicInteger(60);
            this.task = new BukkitRunnable(){
                public void run() {
                    String restaring = org.core.Core.getConfigValue("messages.restarting");
                    int localInt = countdown.getAndDecrement();
                    if (localInt <= 0) {
                        this.cancel();
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendTitle((""), String.valueOf((MiniMessage.miniMessage().deserialize(restaring))), 5, 60, 0);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        }
                        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(restaring));
                        CoreCommands.this.task = null;
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
                        return;
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle((""), String.valueOf((MiniMessage.miniMessage().deserialize(restaring + " in " + localInt))), 5, 30, 5);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    }
                    Bukkit.broadcast(MiniMessage.miniMessage().deserialize(restaring + " in " + localInt));
                }
            }.runTaskTimer(org.core.Core.getPlugin(), 0L, 20L);
        }
    }

    // MuteChat

    @ProxiedBy("unmutechat")
    @CommandMethod("mutechat")
    @CommandDescription("Un/Mute the chat")
    @CommandPermission("core.mutechat")
    public void mutechat(final @NotNull CommandSender sender) {
        if (org.core.Core.muteChat) {
            org.core.Core.muteChat = false;
            String unmuted = org.core.Core.getConfigValue("messages.mutechat.unmuted");
            sender.getServer().broadcast(MiniMessage.miniMessage().deserialize(unmuted));
        } else {
            org.core.Core.muteChat = true;
            String muted = org.core.Core.getConfigValue("messages.mutechat.muted");
            sender.getServer().broadcast(MiniMessage.miniMessage().deserialize(muted));
        }
    }
}
