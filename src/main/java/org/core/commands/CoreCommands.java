package org.core.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.Greedy;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.core.data.CoreData;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class CoreCommands {



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

    // Rules

    @CommandMethod("rules")
    @CommandDescription("Get the rules")
    @CommandPermission("core.rules")
    public void rules(final @NotNull CommandSender sender) {
        for (Object rules : org.core.Core.getConfigListValue("rules")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize((String) rules));
        }
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
        sender.sendMessage(MiniMessage.miniMessage().deserialize(discord + "<click:open_url:" + discordLink + ">" + discordLink));
    }

    // Store

    @ProxiedBy("buy")
    @CommandMethod("store")
    @CommandDescription("Get the store link")
    @CommandPermission("core.store")
    public void store(final @NotNull CommandSender sender) {
        String store = org.core.Core.getConfigValue("messages.store");
        String storeLink = org.core.Core.getConfigValue("store");
        sender.sendMessage(MiniMessage.miniMessage().deserialize(store + "<click:open_url:" + storeLink + ">" + storeLink));
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

    // God

    @CommandMethod("god [player]")
    @CommandDescription("Toggle invulnerability for yourself or another player")
    @CommandPermission("core.god")
    public void god(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        String god = org.core.Core.getConfigValue("messages.god");
        if (player != null) {
            if (sender.hasPermission("core.god.others")) {
                if (player.isInvulnerable()) {
                    player.setInvulnerable(false);
                    player.sendMessage(MiniMessage.miniMessage().deserialize(god + "disabled!"));
                } else {
                    player.setInvulnerable(true);
                    player.sendMessage(MiniMessage.miniMessage().deserialize(god + "enabled!"));
                }
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.isInvulnerable()) {
                    p.setInvulnerable(false);
                    p.sendMessage(MiniMessage.miniMessage().deserialize(god + "disabled!"));
                } else {
                    p.setInvulnerable(true);
                    p.sendMessage(MiniMessage.miniMessage().deserialize(god + "enabled!"));
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
        if (CoreData.isFrozen(player)) {
            CoreData.unfreeze(player, (Player) sender);
        } else {
            CoreData.freeze(player, (Player) sender);
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
            itemStack.setAmount(1);
            if (amount >= 1 && amount <= 64) {
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
            itemStack.setAmount(1);
            if (amount >= 1 && amount <= 64) {
                itemStack.setAmount(amount);
            }
            player.getInventory().addItem(itemStack);
            String gave = org.core.Core.getConfigValue("messages.gave");
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
    public void teleportcoords(final @NotNull CommandSender sender, final @Argument("x") long x, final @Argument("y") long y, final @Argument("z") long z, final @Argument("world") World world) {
        if (!(sender instanceof Player)) {
            String invalid = org.core.Core.getConfigValue("messages.invalid");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            return;
        }
        Player p = (Player) sender;
        if (world == null) {
            p.teleportAsync(new Location(p.getWorld(), x, y, z));
            String teleported = org.core.Core.getConfigValue("messages.teleported");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(teleported + " to " + x + ", " + y + ", " + z));
        } else {
            p.teleportAsync(new Location(world, x, y, z));
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
        if (sender instanceof Player) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(lag + "TPS: " + Double.parseDouble(numberFormat.format(Bukkit.getServer().getTPS()[0])) + " | Ping: " + ((Player) sender).getPing() + "ms" + " | MSPT: " + Double.parseDouble(numberFormat.format(Bukkit.getServer().getAverageTickTime())) + "ms" + " | RAM: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L + "MB"));
        } else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(lag + "TPS: " + Double.parseDouble(numberFormat.format(Bukkit.getServer().getTPS()[0])) + " | MSPT: " + Double.parseDouble(numberFormat.format(Bukkit.getServer().getAverageTickTime())) + "ms" + " | RAM: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L + "MB"));
        }
    }

    // Tell

    @ProxiedBy("msg")
    @CommandMethod("tell|whisper <player> <message>")
    @CommandDescription("Send a private message to a player")
    @CommandPermission("core.tell")
    public void tell(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player, final @NotNull @Greedy @Argument("message") String message) {
        String to = org.core.Core.getConfigValue("messages.msg.to");
        String from = org.core.Core.getConfigValue("messages.msg.from");
        sender.sendMessage(MiniMessage.miniMessage().deserialize(to, Placeholder.parsed("player", player.getName()), Placeholder.parsed("message", message)));
        if (sender instanceof Player) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(from, Placeholder.parsed("player", sender.getName()), Placeholder.parsed("message", message)));
            Player p = (Player) sender;
            CoreData.dms.put(player.getUniqueId(), p.getUniqueId());
            CoreData.dms.put(p.getUniqueId(), player.getUniqueId());
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize(from, Placeholder.parsed("player", "CONSOLE"), Placeholder.parsed("message", message)));
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
        if (CoreData.getDms().containsKey(p.getUniqueId())) {
            Player player = Bukkit.getPlayer(CoreData.getDms().get(p.getUniqueId()));
            if (player != null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(to, Placeholder.parsed("player", player.getName()), Placeholder.parsed("message", message)));
                player.sendMessage(MiniMessage.miniMessage().deserialize(from, Placeholder.parsed("player", sender.getName()), Placeholder.parsed("message", message)));
                CoreData.dms.put(player.getUniqueId(), p.getUniqueId());
                CoreData.dms.put(p.getUniqueId(), player.getUniqueId());
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
    @CommandMethod("staffchat [message]")
    @CommandDescription("Talk to other staff")
    @CommandPermission("core.staffchat")
    public void staffchat(final @NotNull CommandSender sender, final @Greedy @Argument("message") String message) {
        if (message == null) {
            if (CoreData.isStaffchatting((Player) sender)) {
                CoreData.unstaffchat((Player) sender);
            } else {
                CoreData.staffchat((Player) sender);
            }
            return;
        }
        String staffchat = org.core.Core.getConfigValue("messages.staff.prefix");
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
                float speedd = String.valueOf(speed).equals("1") ? 1.0f : this.getMoveSpeed(String.valueOf(speed));
                if (player.isFlying()) {
                    player.setFlySpeed(getRealMoveSpeed(speedd, true));
                    String flyspeed = org.core.Core.getConfigValue("messages.speed.fly");
                    player.sendMessage(MiniMessage.miniMessage().deserialize(flyspeed + speedd));
                } else {
                    player.setWalkSpeed(getRealMoveSpeed(speedd, false));
                    String walkspeed = org.core.Core.getConfigValue("messages.speed.walk");
                    player.sendMessage(MiniMessage.miniMessage().deserialize(walkspeed + speedd));
                }
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                float speedd = String.valueOf(speed).equals("1") ? 1.0f : this.getMoveSpeed(String.valueOf(speed));
                if (p.isFlying()) {
                    p.setFlySpeed(getRealMoveSpeed(speedd, true));
                    String flyspeed = org.core.Core.getConfigValue("messages.speed.fly");
                    p.sendMessage(MiniMessage.miniMessage().deserialize(flyspeed + speedd));
                } else {
                    p.setWalkSpeed(getRealMoveSpeed(speedd, false));
                    String walkspeed = org.core.Core.getConfigValue("messages.speed.walk");
                    p.sendMessage(MiniMessage.miniMessage().deserialize(walkspeed + speedd));
                }
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    private float getMoveSpeed(String moveSpeed) {
        float userSpeed = 1.0f;
        try {
            userSpeed = Float.parseFloat(moveSpeed);
            if (userSpeed > 10.0f) {
                userSpeed = 10.0f;
            } else if (userSpeed < 1.0E-4f) {
                userSpeed = 1.0E-4f;
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return userSpeed;
    }

    private float getRealMoveSpeed(float userSpeed, boolean isFly) {
        float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1.0f;
        if (userSpeed < 1.0f) {
            return defaultSpeed * userSpeed;
        }
        float ratio = (userSpeed - 1.0f) / 9.0f * (maxSpeed - defaultSpeed);
        return ratio + defaultSpeed;
    }

    @ProxiedBy("wspeed")
    @CommandMethod("walkspeed <speed> [player]")
    @CommandDescription("Change yours or another player's walking speed")
    @CommandPermission("core.speed")
    public void walkspeed(final @NotNull CommandSender sender, final @NotNull @Argument("speed") Float speed, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.speed.others")) {
                player.setWalkSpeed(getRealMoveSpeed(speed, false));
                String walkspeed = org.core.Core.getConfigValue("messages.speed.walk");
                player.sendMessage(MiniMessage.miniMessage().deserialize(walkspeed + speed));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setWalkSpeed(getRealMoveSpeed(speed, false));
                String walkspeed = org.core.Core.getConfigValue("messages.speed.walk");
                p.sendMessage(MiniMessage.miniMessage().deserialize(walkspeed + speed));
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
                player.setFlySpeed(getRealMoveSpeed(speed, true));
                String flyspeed = org.core.Core.getConfigValue("messages.speed.fly");
                player.sendMessage(MiniMessage.miniMessage().deserialize(flyspeed + speed));
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.setFlySpeed(getRealMoveSpeed(speed, true));
                String flyspeed = org.core.Core.getConfigValue("messages.speed.fly");
                p.sendMessage(MiniMessage.miniMessage().deserialize(flyspeed + speed));
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
        }
    }

    // Vanish

    @ProxiedBy("unvanish")
    @CommandMethod("vanish [player]")
    @CommandDescription("Un/Vanish yourself or another player")
    @CommandPermission("core.vanish")
    public void vanish(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (sender.hasPermission("core.vanish.others")) {
                if (CoreData.getVanishedPlayers().contains(player.getUniqueId())) {
                    CoreData.unvanish(player);
                } else {
                    CoreData.vanish(player);
                }
            } else {
                String noperms = org.core.Core.getConfigValue("messages.noperms");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(noperms));
            }
        } else {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (CoreData.getVanishedPlayers().contains(p.getUniqueId())) {
                    CoreData.unvanish(p);
                } else {
                    CoreData.vanish(p);
                }
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
            }
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

    // Ignore

    @ProxiedBy("block")
    @CommandMethod("ignore <action> <player>")
    @CommandDescription("Ignore/Block a player from messaging you and reading their chat")
    @CommandPermission("core.ignore")
    public void ignore(final @NotNull CommandSender sender, final @NotNull @Argument("action") String action, final @NotNull @Argument("player") Player player) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (action.equalsIgnoreCase("add")) {
                if (CoreData.isIgnored(p, player)) {
                    String alreadyignored = org.core.Core.getConfigValue("messages.ignore.already");
                    p.sendMessage(MiniMessage.miniMessage().deserialize(alreadyignored + player.getName()));
                } else {
                    CoreData.ignore(p, player);
                }
            } else if (action.equalsIgnoreCase("remove")) {
                if (!CoreData.isIgnored(p, player)) {
                    String notignored = org.core.Core.getConfigValue("messages.ignore.notignored");
                    p.sendMessage(MiniMessage.miniMessage().deserialize(notignored + player.getName()));
                } else {
                    CoreData.unignore(p, player);
                }
            } else {
                String invalid = org.core.Core.getConfigValue("messages.invalid");
                p.sendMessage(MiniMessage.miniMessage().deserialize(invalid + "\nUse /ignore <add/remove> <player>"));
            }
        } else {
            String invalid = org.core.Core.getConfigValue("messages.invalid");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(invalid));
        }
    }
}
