package org.prismcore.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.Greedy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.prismcore.Core;
import org.prismcore.data.CoreData;
import org.prismcore.messages.CoreMessages;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static net.kyori.adventure.title.Title.Times.times;
import static net.kyori.adventure.title.Title.title;

public class CoreCommands {



    BukkitTask task;

    // Alert
    @ProxiedBy("broadcast")
    @CommandMethod("alert <message>")
    @CommandDescription("Send an alert to all players")
    @CommandPermission("prismcore.alert")
    public void alert(final @NotNull CommandSender sender, final @NotNull @Greedy @Argument("message") String message) {
        sender.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.broadcast + message));
    }

    // Rules

    @CommandMethod("rules")
    @CommandDescription("Get the rules")
    @CommandPermission("prismcore.rules")
    public void rules(final @NotNull CommandSender sender) {
        for (String rules : CoreMessages.rules) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(rules));
        }
    }

    // Day

    @CommandMethod("day [world]")
    @CommandDescription("Set the time to day")
    @CommandPermission("prismcore.time")
    public void day(final @NotNull CommandSender sender, @Argument("world") World world) {
        if (world == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            world = Objects.requireNonNull(sender.getServer().getPlayer(sender.getName())).getWorld();
        }
        Objects.requireNonNull(sender.getServer().getWorld(world.getUID())).setTime(6000);
        sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.day));
    }

    // Night

    @CommandMethod("night [world]")
    @CommandDescription("Set the time to night")
    @CommandPermission("prismcore.time")
    public void night(final @NotNull CommandSender sender, @Argument("world") World world) {
        if (world == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            world = Objects.requireNonNull(sender.getServer().getPlayer(sender.getName())).getWorld();
        }
        Objects.requireNonNull(sender.getServer().getWorld(world.getUID())).setTime(18000);
        sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.night));
    }

    // Discord, Store, Socials, Buy

    @CommandMethod("discord|store|socials|buy")
    @CommandDescription("Get the social links")
    @CommandPermission("prismcore.socials")
    public void discord(final @NotNull CommandSender sender) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.website));
        sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.discord));
        sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.store));
    }

    // Feed

    @CommandMethod("feed [player]")
    @CommandDescription("Feed yourself or another player")
    @CommandPermission("prismcore.feed")
    public void feed(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.feed.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setFoodLevel(20);
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.saturated));
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            ((Player) sender).setFoodLevel(20);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.saturated));
        }
    }


    // Fly

    @CommandMethod("fly [player]")
    @CommandDescription("Toggle fly for yourself or another player")
    @CommandPermission("prismcore.fly")
    public void fly(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.fly.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setAllowFlight(!player.getAllowFlight());
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.flight,
                    Placeholder.parsed("state", player.getAllowFlight() ? "enabled!" : "disabled!")));
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setAllowFlight(!p.getAllowFlight());
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.flight,
                    Placeholder.parsed("state", p.getAllowFlight() ? "enabled!" : "disabled!")));
        }
    }

    // God

    @CommandMethod("god [player]")
    @CommandDescription("Toggle invulnerability for yourself or another player")
    @CommandPermission("prismcore.god")
    public void god(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.god.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setInvulnerable(!player.isInvulnerable());
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.god,
                    Placeholder.parsed("state", player.isInvulnerable() ? "enabled!" : "disabled!")));
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setInvulnerable(!p.isInvulnerable());
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.god,
                    Placeholder.parsed("state", p.isInvulnerable() ? "enabled!" : "disabled!")));
        }
    }

    // Freeze
    @ProxiedBy("unfreeze")
    @CommandMethod("freeze|ss <player>")
    @CommandDescription("Freeze a player")
    @CommandPermission("prismcore.freeze")
    public void freeze(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player) {
        if (CoreData.isFrozen(player)) {
            CoreData.unfreeze(player, (Player) sender);
            return;
        }
        CoreData.freeze(player, (Player) sender);
    }

    // Gamemode

    @ProxiedBy("gm")
    @CommandMethod("gamemode <gamemode> [player]")
    @CommandDescription("Change someone's gamemode")
    @CommandPermission("prismcore.gamemode")
    public void gamemode(final @NotNull CommandSender sender,
                         final @NotNull @Argument("gamemode") GameMode gamemode,
                         final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.gamemode.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setGameMode(gamemode);
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", gamemode.toString().toUpperCase()), Placeholder.parsed("player", player.getName() + "'s")));
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setGameMode(gamemode);
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", gamemode.toString().toUpperCase()), Placeholder.parsed("player", "Your")));
        }
    }

    @ProxiedBy("gm1")
    @CommandMethod("gmc [player]")
    @CommandDescription("Change someone's gamemode to creative")
    @CommandPermission("prismcore.gamemode")
    public void gmc(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.gamemode.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", "CREATIVE"), Placeholder.parsed("player", player.getName() + "'s")));
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", "CREATIVE"), Placeholder.parsed("player", "Your")));
        }

    }

    @ProxiedBy("gm0")
    @CommandMethod("gms [player]")
    @CommandDescription("Change someone's gamemode to survival")
    @CommandPermission("prismcore.gamemode")
    public void gms(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.gamemode.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", "SURVIVAL"), Placeholder.parsed("player", player.getName() + "'s")));
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", "SURVIVAL"), Placeholder.parsed("player", "Your")));
        }
    }

    @ProxiedBy("gm3")
    @CommandMethod("gmsp [player]")
    @CommandDescription("Change someone's gamemode to spectator")
    @CommandPermission("prismcore.gamemode")
    public void gmsp(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.gamemode.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", "SPECTATOR"), Placeholder.parsed("player", player.getName() + "'s")));
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", "SPECTATOR"), Placeholder.parsed("player", "Your")));
        }
    }

    @ProxiedBy("gm2")
    @CommandMethod("gma [player]")
    @CommandDescription("Change someone's gamemode to adventure")
    @CommandPermission("prismcore.gamemode")
    public void gma(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.gamemode.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", "ADVENTURE"), Placeholder.parsed("player", player.getName() + "'s")));
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setGameMode(GameMode.ADVENTURE);
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gamemode,
                    Placeholder.parsed("gamemode", "ADVENTURE"), Placeholder.parsed("player", "Your")));
        }
    }

    // Heal

    @CommandMethod("heal [player]")
    @CommandDescription("Heal yourself or another player")
    @CommandPermission("prismcore.heal")
    public void heal(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player == null) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setHealth(20);
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.healed));
        } else {
            if (!sender.hasPermission("prismcore.heal.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setHealth(20);
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.healed));
        }
    }

    // Invsee

    @ProxiedBy("peek")
    @CommandMethod("invsee <player>")
    @CommandDescription("View another player's inventory")
    @CommandPermission("prismcore.invsee")
    public void invsee(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
            return;
        }
        p.openInventory(player.getInventory());
    }

    // Item
    @ProxiedBy("i")
    @CommandMethod("item <item> [amount]")
    @CommandDescription("Give yourself some items")
    @CommandPermission("prismcore.item")
    public void item(final @NotNull CommandSender sender,
                     final @NotNull @Argument("item") Material item,
                     final @Argument("amount") int amount) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
            return;
        }
        ItemStack itemStack = new ItemStack(item);
        itemStack.setAmount(1);
        if (amount > 0 && amount < 65) itemStack.setAmount(amount);
        p.getInventory().addItem(itemStack);
        p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gave,
                Placeholder.parsed("amount", String.valueOf(itemStack.getAmount())),
                Placeholder.parsed("item", itemStack.getType().name()),
                Placeholder.parsed("player", "you")));
    }

    // Give

    @CommandMethod("give <player> <item> [amount]")
    @CommandDescription("Give a player or yourself some items")
    @CommandPermission("prismcore.give")
    public void give(final @NotNull CommandSender sender,
                     final @NotNull @Argument("player") Player player,
                     final @NotNull @Argument("item") Material item,
                     final @Argument("amount") int amount) {
        if (!sender.hasPermission("prismcore.give.others")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
            return;
        }
        ItemStack itemStack = new ItemStack(item);
        itemStack.setAmount(1);
        if (amount >= 1 && amount <= 64) itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.gave,
                Placeholder.parsed("amount", String.valueOf(itemStack.getAmount())),
                Placeholder.parsed("item", itemStack.getType().name()),
                Placeholder.parsed("player", player.getName())));
    }

    // Teleport and Teleport Here

    @ProxiedBy("tp")
    @CommandMethod("teleport <player> [target]")
    @CommandDescription("Teleport yourself or another player to another player")
    @CommandPermission("prismcore.teleport")
    public void teleport(final @NotNull CommandSender sender,
                         final @NotNull @Argument("player") Player player,
                         final @Argument("target") Player target) {
        if (target == null) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            Core.getPlugin().getServer().getScheduler().runTask(Core.getPlugin(), () -> p.teleport(player));
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.teleported,
                    Placeholder.parsed("target", player.getName()), Placeholder.parsed("destination", "you")));
        } else {
            if (!sender.hasPermission("prismcore.teleport.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            Core.getPlugin().getServer().getScheduler().runTask(Core.getPlugin(), () -> player.teleport(target));
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.teleported,
                    Placeholder.parsed("target", player.getName()), Placeholder.parsed("destination", target.getName())));
        }
    }

    @ProxiedBy("tph")
    @CommandMethod("tphere <player>")
    @CommandDescription("Teleport another player to yourself")
    @CommandPermission("prismcore.teleporthere")
    public void teleporthere(final @NotNull CommandSender sender, final @NotNull @Argument("player") Player player) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
            return;
        }
        player.teleport(p);
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.teleported,
                Placeholder.parsed("target", player.getName()),
                Placeholder.parsed("destination", "you")));
    }

    // Lag

    @CommandMethod("lag")
    @CommandDescription("Get the server's performance status")
    @CommandPermission("prismcore.lag")
    public void lag(final @NotNull CommandSender sender) {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.lag,
                Placeholder.parsed("tps", String.valueOf(Double.parseDouble(numberFormat.format(Bukkit.getServer().getTPS()[0])))),
                Placeholder.parsed("mspt", String.valueOf(Double.parseDouble(numberFormat.format(Bukkit.getServer().getAverageTickTime())))),
                Placeholder.parsed("memory", String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() / 1024L / 1024L))));
    }

    // Tell

    @ProxiedBy("msg")
    @CommandMethod("tell|whisper <player> <message>")
    @CommandDescription("Send a private message to a player")
    @CommandPermission("prismcore.tell")
    public void tell(final @NotNull CommandSender sender,
                     final @NotNull @Argument("player") Player player,
                     final @NotNull @Greedy @Argument("message") String message) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.msg,
                Placeholder.parsed("direction", "To"),
                Placeholder.parsed("player", player.getName()),
                Placeholder.parsed("message", message)));
        if (!(sender instanceof Player p)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.msg,
                    Placeholder.parsed("direction", "From"),
                    Placeholder.parsed("player", "CONSOLE"),
                    Placeholder.parsed("message", message)));
            return;
        }
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.msg,
                Placeholder.parsed("direction", "From"),
                Placeholder.parsed("player", sender.getName()),
                Placeholder.parsed("message", message)));
        CoreData.dms.put(player.getUniqueId(), p.getUniqueId());
        CoreData.dms.put(p.getUniqueId(), player.getUniqueId());
    }

    // Reply

    @ProxiedBy("r")
    @CommandMethod("reply <message>")
    @CommandDescription("Reply to a private message")
    @CommandPermission("prismcore.reply")
    public void reply(final @NotNull CommandSender sender,
                      final @NotNull @Greedy @Argument("message") String message) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
            return;
        }
        Player player = Bukkit.getPlayer(CoreData.getDms().get(p.getUniqueId()));
        if (!CoreData.getDms().containsKey(p.getUniqueId()) || player == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.noreply));
            return;
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.msg,
                Placeholder.parsed("direction", "To"),
                Placeholder.parsed("player", player.getName()),
                Placeholder.parsed("message", message)));
        player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.msg,
                Placeholder.parsed("direction", "From"),
                Placeholder.parsed("player", sender.getName()),
                Placeholder.parsed("message", message)));
        CoreData.dms.put(player.getUniqueId(), p.getUniqueId());
        CoreData.dms.put(p.getUniqueId(), player.getUniqueId());
    }

    // Staff Chat

    @ProxiedBy("sc")
    @CommandMethod("staffchat [message]")
    @CommandDescription("Talk to other staff")
    @CommandPermission("prismcore.staffchat")
    public void staffchat(final @NotNull CommandSender sender,
                          final @Greedy @Argument("message") String message) {
        if (message == null) {
            if (CoreData.isStaffchatting((Player) sender)) {
                CoreData.unstaffchat((Player) sender);
            } else {
                CoreData.staffchat((Player) sender);
            }
            return;
        }
        Core.getPlugin().getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.chatformat,
                Placeholder.parsed("player", sender.getName()),
                Placeholder.parsed("message", message)), "prismcore.staffchat");
    }

    // Ping

    @CommandMethod("ping [player]")
    @CommandDescription("Get your ping or another player's ping")
    @CommandPermission("prismcore.ping")
    public void ping(final @NotNull CommandSender sender,
                     final @Argument("player") Player player) {
        if (player == null) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.ping,
                    Placeholder.parsed("ping", String.valueOf(p.getPing())),
                    Placeholder.parsed("player", p.getName())));
        } else {
            if (!sender.hasPermission("prismcore.ping.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.ping,
                    Placeholder.parsed("ping", String.valueOf(player.getPing())),
                    Placeholder.parsed("player", player.getName())));
        }
    }

    // Speed, Walkspeed and Flyspeed

    @CommandMethod("speed <speed> [player]")
    @CommandDescription("Change yours or another player's flight/movement speed depending on if they are walking or flying")
    @CommandPermission("prismcore.speed")
    public void speed(final @NotNull CommandSender sender,
                      final @NotNull @Argument("speed") Float speed,
                      final @Argument("player") Player player) {
        float speedd = String.valueOf(speed).equals("1") ? 1.0f : this.getMoveSpeed(String.valueOf(speed));
        if (player == null) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            if (p.isFlying()) {
                p.setFlySpeed(getRealMoveSpeed(speedd, true));
                p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.speed,
                        Placeholder.parsed("speed", String.valueOf(speedd)),
                        Placeholder.parsed("type", "Fly")));
            } else {
                p.setWalkSpeed(getRealMoveSpeed(speedd, false));
                p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.speed,
                        Placeholder.parsed("speed", String.valueOf(speedd)),
                        Placeholder.parsed("type", "Walk")));
            }
        } else {
            if (!sender.hasPermission("prismcore.speed.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            if (player.isFlying()) {
                player.setFlySpeed(getRealMoveSpeed(speedd, true));
                player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.speed,
                        Placeholder.parsed("speed", String.valueOf(speedd)),
                        Placeholder.parsed("type", "Fly")));
            } else {
                player.setWalkSpeed(getRealMoveSpeed(speedd, false));
                player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.speed,
                        Placeholder.parsed("speed", String.valueOf(speedd)),
                        Placeholder.parsed("type", "Walk")));
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
    @CommandPermission("prismcore.speed")
    public void walkspeed(final @NotNull CommandSender sender, final @NotNull @Argument("speed") Float speed, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.speed.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setWalkSpeed(getRealMoveSpeed(speed, false));
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.speed,
                    Placeholder.parsed("speed", String.valueOf(speed)),
                    Placeholder.parsed("type", "Walk")));
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setWalkSpeed(getRealMoveSpeed(speed, false));
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.speed,
                    Placeholder.parsed("speed", String.valueOf(speed)),
                    Placeholder.parsed("type", "Walk")));
        }
    }

    @ProxiedBy("fspeed")
    @CommandMethod("flyspeed <speed> [player]")
    @CommandDescription("Change yours or another player's flying speed")
    @CommandPermission("prismcore.speed")
    public void flyspeed(final @NotNull CommandSender sender, final @NotNull @Argument("speed") Float speed, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.speed.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            player.setFlySpeed(getRealMoveSpeed(speed, true));
            player.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.speed,
                    Placeholder.parsed("speed", String.valueOf(speed)),
                    Placeholder.parsed("type", "Fly")));
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            p.setFlySpeed(getRealMoveSpeed(speed, true));
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.speed,
                    Placeholder.parsed("speed", String.valueOf(speed)),
                    Placeholder.parsed("type", "Fly")));
        }
    }

    // Vanish

    @ProxiedBy("unvanish")
    @CommandMethod("vanish [player]")
    @CommandDescription("Un/Vanish yourself or another player")
    @CommandPermission("prismcore.vanish")
    public void vanish(final @NotNull CommandSender sender, final @Argument("player") Player player) {
        if (player != null) {
            if (!sender.hasPermission("prismcore.vanish.others")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.nopermission));
                return;
            }
            if (CoreData.getVanishedPlayers().contains(player.getUniqueId())) {
                CoreData.unvanish(player);
            } else {
                CoreData.vanish(player);
            }
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
                return;
            }
            if (CoreData.getVanishedPlayers().contains(p.getUniqueId())) {
                CoreData.unvanish(p);
            } else {
                CoreData.vanish(p);
            }
        }
    }

    // Reboot

    @ProxiedBy("restart")
    @CommandMethod("reboot [cancel]")
    @CommandDescription("Reboot the server with a 20 second delay")
    @CommandPermission("prismcore.reboot")
    public void reboot(final @NotNull CommandSender sender, final @Argument("cancel") boolean cancel) {
        this.task.cancel();
        this.task = null;
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f));
        if (!cancel) {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.restarting,
                    Placeholder.parsed("time", " in 20")));
            AtomicInteger countdown = new AtomicInteger(20);
            this.task = new BukkitRunnable(){
                public void run() {
                    int localInt = countdown.getAndDecrement();
                    Title.Times times = times(Duration.ofMillis(100L),
                            Duration.ofMillis(1200L),
                            Duration.ofMillis(0L));
                    if (localInt <= 0) {
                        this.cancel();
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            Title title = title(MiniMessage.miniMessage().deserialize(""),
                                    MiniMessage.miniMessage().deserialize(CoreMessages.restarting), times);
                            p.showTitle(title);
                        }
                        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.restarting));
                        CoreCommands.this.task = null;
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
                        return;
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        Title title = title(MiniMessage.miniMessage().deserialize(""),
                                MiniMessage.miniMessage().deserialize(CoreMessages.restarting,
                                        Placeholder.parsed("time", " in " + localInt)), times);
                        p.showTitle(title);
                    }
                    Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.restarting,
                            Placeholder.parsed("time", " in " + localInt)));
                }
            }.runTaskTimer(Core.getPlugin(), 40L, 20L);
        }
    }

    // MuteChat & ClearChat

    @ProxiedBy("unmutechat")
    @CommandMethod("mutechat")
    @CommandDescription("Un/Mute the chat")
    @CommandPermission("prismcore.mutechat")
    public void mutechat(final @NotNull CommandSender sender) {
        org.prismcore.Core.muteChat = !org.prismcore.Core.muteChat;
        sender.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.chatstaffed,
                Placeholder.parsed("state", org.prismcore.Core.muteChat ? "muted" : "unmuted")));
    }

    @CommandMethod("clearchat")
    @CommandDescription("Clear the chat")
    @CommandPermission("prismcore.clearchat")
    public void clearchat(final @NotNull CommandSender sender) {
        IntStream.range(0, 100).forEach(i -> sender.getServer().broadcast(MiniMessage.miniMessage().deserialize("")));
        sender.getServer().broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.chatstaffed,
                Placeholder.parsed("state", "cleared")));
    }

    // Ignore

    @ProxiedBy("block")
    @CommandMethod("ignore <action> <player>")
    @CommandDescription("Ignore/Block a player from messaging you and reading their chat")
    @CommandPermission("prismcore.ignore")
    public void ignore(final @NotNull CommandSender sender, final @NotNull @Argument("action") String action, final @NotNull @Argument("player") Player player) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument));
            return;
        }
        if (action.equalsIgnoreCase("add")) {
            if (!CoreData.isIgnored(p, player)) {
                CoreData.ignore(p, player);
                p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.ignored,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("un", "started ignoring")));
                return;
            }
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.ignored,
                Placeholder.parsed("player", player.getName() + "already"),
                Placeholder.parsed("un", "ignored")));
        } else if (action.equalsIgnoreCase("remove")) {
            if (!CoreData.isIgnored(p, player)) {
                p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.ignored,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("state", "not ignored")));
                return;
            }
            CoreData.unignore(p, player);
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.ignored,
                    Placeholder.parsed("player", player.getName()),
                    Placeholder.parsed("state", "stopped ignoring")));
        } else {
            p.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument + "\nUse /ignore <add/remove> <player>"));
        }
    }


    // PUNISHMENTS


    // Kick

    @ProxiedBy("k")
    @CommandMethod("kick <player> <reason> [-s]")
    @CommandDescription("Kick a player with a reason")
    @CommandPermission("prismcore.kick")
    public void kick(final @NotNull CommandSender sender,
                     final @NotNull @Argument("player") Player player,
                     final @Argument("-s") String silent,
                     final @NotNull @Greedy @Argument("reason") String reason) {
        if (sender instanceof Player) {
            Bukkit.getScheduler().runTask(Core.getPlugin(), () -> player.kick(MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                    Placeholder.parsed("player", player.getName()),
                    Placeholder.parsed("punishment", "kicked"),
                    Placeholder.parsed("staff", sender.getName()),
                    Placeholder.parsed("time", ""),
                    Placeholder.parsed("reason", reason))));
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.punishmsg,
                    Placeholder.parsed("player", player.getName()),
                    Placeholder.parsed("punishment", "kicked"),
                    Placeholder.parsed("staff", sender.getName()),
                    Placeholder.parsed("time", ""),
                    Placeholder.parsed("reason", reason)), "prismcore.staffchat");
            if (!reason.endsWith("-s")) {
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "kicked"),
                        Placeholder.parsed("staff", sender.getName()),
                        Placeholder.parsed("time", ""),
                        Placeholder.parsed("reason", reason)));
            }
        } else {
            Bukkit.getScheduler().runTask(Core.getPlugin(), () -> player.kick(MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                    Placeholder.parsed("player", player.getName()),
                    Placeholder.parsed("punishment", "kicked"),
                    Placeholder.parsed("staff", "CONSOLE"),
                    Placeholder.parsed("time", ""),
                    Placeholder.parsed("reason", reason))));
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.punishmsg,
                    Placeholder.parsed("player", player.getName()),
                    Placeholder.parsed("punishment", "kicked"),
                    Placeholder.parsed("staff", "CONSOLE"),
                    Placeholder.parsed("time", ""),
                    Placeholder.parsed("reason", reason)), "prismcore.staffchat");
            if (!reason.endsWith("-s")) {
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "kicked"),
                        Placeholder.parsed("staff", "CONSOLE"),
                        Placeholder.parsed("time", ""),
                        Placeholder.parsed("reason", reason)));
            }
        }
    }

    // Punishment duration parsing
    private static final long SECOND_IN_MILLIS = 1000L;
    private static final long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;
    private static final long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;
    private static final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;
    private static final long YEAR_IN_MILLIS = 365 * DAY_IN_MILLIS;
    public Date getPunishDuration(String duration) {
        LocalDateTime dateTime = LocalDateTime.now();
        if (duration == null || duration.trim().isEmpty()) {
            LocalDateTime ldt = dateTime.plus(DAY_IN_MILLIS, ChronoUnit.MILLIS);
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
        if (duration.equalsIgnoreCase("perm")) {
            LocalDateTime ldt = dateTime.plus(10 * YEAR_IN_MILLIS, ChronoUnit.MILLIS);
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
        try {
            int time = Integer.parseInt(duration.substring(0, duration.length() - 1));
            String type = duration.substring(duration.length() - 1);
            LocalDateTime ldt;
            return switch (type.toLowerCase()) {
                case "s" -> {
                    ldt = dateTime.plusSeconds(time);
                    yield Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                }
                case "m" -> {
                    ldt = dateTime.plusMinutes(time);
                    yield Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                }
                case "h" -> {
                    ldt = dateTime.plusHours(time);
                    yield Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                }
                case "d" -> {
                    ldt = dateTime.plusDays(time);
                    yield Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                }
                case "w" -> {
                    ldt = dateTime.plusWeeks(time);
                    yield Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                }
                case "mon" -> {
                    ldt = dateTime.plusMonths(time);
                    yield Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                }
                case "y" -> {
                    ldt = dateTime.plusYears(time);
                    yield Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                }
                default -> {
                    ldt = dateTime.plus(DAY_IN_MILLIS, ChronoUnit.MILLIS);
                    yield Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                }
            };
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            LocalDateTime ldt = dateTime.plus(DAY_IN_MILLIS, ChronoUnit.MILLIS);
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    // Tempban and Ban

    @ProxiedBy("b")
    @CommandMethod("ban|tempban|tb <player> <duration> <reason> [-s]")
    @CommandDescription("Ban a player with a reason for a duration (perm for permanent)")
    @CommandPermission("prismcore.ban")
    public void ban(final @NotNull CommandSender sender,
                    final @NotNull @Argument("player") OfflinePlayer player,
                    final @NotNull @Argument("duration") String duration,
                    final @Argument ("-s") String silent,
                    final @NotNull @Greedy @Argument("reason") String reason) {
        try {
            Component punishmsg;
            if (sender instanceof Player) {
                punishmsg = MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "banned"),
                        Placeholder.parsed("staff", sender.getName()),
                        Placeholder.parsed("reason", reason));
            } else {
                punishmsg = MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "banned"),
                        Placeholder.parsed("staff", "CONSOLE"),
                        Placeholder.parsed("reason", reason));
            }
            if (!reason.endsWith("-s")) {
                Bukkit.broadcast(punishmsg);
            }
            Bukkit.getScheduler().runTask(Core.getPlugin(), () -> player.banPlayer(MiniMessage.miniMessage().serialize(punishmsg), getPunishDuration(duration), sender.getName(), true));
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + punishmsg), "prismcore.staffchat");
        } catch (NullPointerException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument + "\nThat player has never played before!"));
        }
    }

    // Unban

    @ProxiedBy("ub")
    @CommandMethod("unban <player>")
    @CommandDescription("Unban a player")
    @CommandPermission("prismcore.unban")
    public void unban(final @NotNull CommandSender sender,
                      final @NotNull @Argument("player") OfflinePlayer player) {
        try {
            if (sender instanceof Player) {
                ((ProfileBanList) sender.getServer().getBanList(BanList.Type.PROFILE)).pardon(player.getPlayerProfile());
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "unbanned"),
                        Placeholder.parsed("staff", sender.getName())), "prismcore.staffchat");
            } else {
                ((ProfileBanList) sender.getServer().getBanList(BanList.Type.PROFILE)).pardon(player.getPlayerProfile());
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "unbanned"),
                        Placeholder.parsed("staff", "CONSOLE")), "prismcore.staffchat");
            }
        } catch (NullPointerException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument + "\nThat player has never played before!"));
        }
    }


    // Tempban IP and Ban IP
    // COMMENTED DUE TO UNABILITY TO UNIPBAN BY NAME
    //@ProxiedBy("ipb")
    //@CommandMethod("ipban|tempipban|banip|tempbanip|tbip <player> <duration> <reason> [-s]")
    //@CommandDescription("Ban a player's IP with a reason for a duration (perm for permanent)")
    //@CommandPermission("prismcore.ipban")
    //public void ipban(final @NotNull CommandSender sender,
    //                  final @NotNull @Argument("player") Player player,
    //                  final @NotNull @Argument("duration") String duration,
    //                  final @Argument ("-s") String silent,
    //                  final @NotNull @Greedy @Argument("reason") String reason) {
    //    try {
    //        Component punishmsg;
    //        if (sender instanceof Player) {
    //            punishmsg = MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
    //                    Placeholder.parsed("player", player.getName()),
    //                    Placeholder.parsed("punishment", "IP-banned"),
    //                    Placeholder.parsed("staff", sender.getName()),
    //                    Placeholder.parsed("reason", reason));
    //        } else {
    //            punishmsg = MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
    //                    Placeholder.parsed("player", player.getName()),
    //                    Placeholder.parsed("punishment", "IP-banned"),
    //                    Placeholder.parsed("staff", "CONSOLE"),
    //                    Placeholder.parsed("reason", reason));
    //        }
    //        if (!reason.endsWith("-s")) {
    //            Bukkit.broadcast(punishmsg);
    //        }
    //        Bukkit.getScheduler().runTask(Core.getPlugin(), () -> player.banPlayerIP(MiniMessage.miniMessage().serialize(punishmsg), getPunishDuration(duration), sender.getName//(), true));
    //        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + punishmsg), "prismcore.staffchat");
    //    } catch (NullPointerException e) {
    //        sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument + "\nThat player is not online!"));
    //    }
    //}

    // Mute and Unmute

    @CommandMethod("mute|tempmute|tm <player> <duration> <reason> [-s]")
    @CommandDescription("Mute a player with a reason for a duration (perm for permanent)")
    @CommandPermission("prismcore.mute")
    public void mute(final @NotNull CommandSender sender,
                     final @NotNull @Argument("player") Player player,
                     final @NotNull @Argument("duration") String duration,
                     final @Argument ("-s") String silent,
                     final @NotNull @Greedy @Argument("reason") String reason) {
        try {
            Component punishmsg;
            if (sender instanceof Player) {
                punishmsg = MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "muted"),
                        Placeholder.parsed("staff", sender.getName()),
                        Placeholder.parsed("reason", reason));
            } else {
                punishmsg = MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "muted"),
                        Placeholder.parsed("staff", "CONSOLE"),
                        Placeholder.parsed("reason", reason));
            }
            if (!reason.endsWith("-s")) {
                Bukkit.broadcast(punishmsg);
            }
            CoreData.mute(player, reason, getPunishDuration(duration), sender);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + punishmsg), "prismcore.staffchat");
        } catch (NullPointerException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument + "\nThat player is not online!"));
        }
    }

    @CommandMethod("unmute <player>")
    @CommandDescription("Unmute a player")
    @CommandPermission("prismcore.unmute")
    public void unmute(final @NotNull CommandSender sender,
                       final @NotNull @Argument("player") Player player) {
        try {
            if (sender instanceof Player) {
                CoreData.unmute(player, sender);
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "unmuted"),
                        Placeholder.parsed("staff", sender.getName())), "prismcore.staffchat");
            } else {
                CoreData.unmute(player, sender);
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "unmuted"),
                        Placeholder.parsed("staff", "CONSOLE")), "prismcore.staffchat");
            }
        } catch (NullPointerException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument + "\nThat player is not online!"));
        }
    }

    // Blacklist

    @ProxiedBy("bl")
    @CommandMethod("blacklist <player> <reason>")
    @CommandDescription("Blacklist a player (Unappealable Permanent Ban)")
    @CommandPermission("prismcore.blacklist")
    public void blacklist(final @NotNull CommandSender sender,
                          final @NotNull @Argument("player") OfflinePlayer player,
                          final @NotNull @Greedy @Argument("reason") String reason) {
        try {
            Component punishmsg;
            if (sender instanceof Player) {
                punishmsg = MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "Blacklisted"),
                        Placeholder.parsed("staff", sender.getName()),
                        Placeholder.parsed("reason", reason));
            } else {
                punishmsg = MiniMessage.miniMessage().deserialize(CoreMessages.punishmsg,
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("punishment", "Blacklisted"),
                        Placeholder.parsed("staff", "CONSOLE"),
                        Placeholder.parsed("reason", reason));
            }
            if (!reason.endsWith("-s")) {
                Bukkit.broadcast(punishmsg);
            }
            Bukkit.getScheduler().runTask(Core.getPlugin(), () -> player.banPlayer(MiniMessage.miniMessage().serialize(punishmsg), new Date(Long.MAX_VALUE), sender.getName(), true));
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(CoreMessages.staffprefix + punishmsg), "prismcore.staffchat");
        } catch (NullPointerException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(CoreMessages.invalidargument + "\nThat player has never played before!"));
        }
    }
}
