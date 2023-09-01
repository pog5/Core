package org.core;

import cloud.commandframework.CommandTree;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import cloud.commandframework.annotations.AnnotationParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.commands.CoreCommands;
import org.core.listeners.CoreListeners;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

public final class Core extends JavaPlugin {
    private AnnotationParser<CommandSender> annotationParser;
    public static ProtocolManager protocolManager;
    public File configFile = new File(this.getDataFolder(), "config.yml");
    public FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    private static Core plugin;
    public static Path plugindir;
    private static File dataFile;
    private static FileConfiguration data;
    public static FileConfiguration getData() {
        return data;
    }
    public void createData() {
        dataFile = new File(this.getDataFolder(), "player.yml");
        if (!dataFile.exists()) {
            this.saveResource("player.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public static void saveData() {
        try {
            data.save(dataFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean muteChat = false;
    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
    public static Core getPlugin() {return plugin;}

    @Override
    public void onEnable() {
        plugin = this;
        File dataFolder = new File(getDataFolder().getAbsolutePath());
        if (!dataFolder.exists()) {
            boolean status = dataFolder.mkdir();
            if (!status) {
                Bukkit.getLogger().severe("Failed to create data folder!");
            }
        }
        this.saveDefaultConfig();
        this.config.options().copyDefaults(true);
        this.reloadConfig();
        this.createData();
        PluginManager pm = this.getServer().getPluginManager();
        protocolManager = ProtocolLibrary.getProtocolManager();

        // Setup Cloud Commands API

        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build();
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        PaperCommandManager<CommandSender> manager;
        try {
            manager = new PaperCommandManager<>(
                    this,
                    executionCoordinatorFunction,
                    mapperFunction,
                    mapperFunction
            );
        } catch (Exception e) {
            this.getLogger().severe("Failed to initialize command manager!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
                FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()
        ));
        BukkitAudiences bukkitAudiences = BukkitAudiences.create(this);
        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            manager.registerBrigadier();
        }
        if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }
        CommandConfirmationManager<CommandSender> confirmationManager = new CommandConfirmationManager<>(
                15L,
                TimeUnit.SECONDS,
                /* Action when confirmation is required */ context -> context.getCommandContext().getSender().sendMessage(
                ChatColor.RED + "Confirmation required. Confirm using /core confirm."),
                /* Action when no confirmation is pending */ sender -> sender.sendMessage(
                ChatColor.RED + "You don't have any pending commands.")
        );
        confirmationManager.registerConfirmationProcessor(manager);
        final Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple()
                        .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();
        this.annotationParser = new AnnotationParser<>(
                manager,
                CommandSender.class,
                commandMetaFunction
        );
        new MinecraftExceptionHandler<CommandSender>()
                .withInvalidSyntaxHandler()
                .withInvalidSenderHandler()
                .withNoPermissionHandler()
                .withArgumentParsingHandler()
                .withCommandExecutionHandler()
                .withDecorator(
                        component -> text().append(component).build()
                ).apply(manager, bukkitAudiences::sender);

        // End Cloud Command API

        for (Object modules : org.core.Core.getConfigListValue("modules")) {
            if (modules.toString().equalsIgnoreCase("core")) {
                this.annotationParser.parse(new CoreCommands());
                pm.registerEvents(new CoreListeners(), this);
            }
        }
    }

    public static String getConfigValue(@NotNull String value) {
        YamlConfiguration c = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        try {
            return c.get(value).toString();
        } catch (NullPointerException e) {
            return "Error! Value " + value + " not found in config or was empty! | ";
        }
    }
    public static List getConfigListValue(@NotNull String value) {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml")).getList(value);
    }

    @Override
    public void onDisable() {
        plugin = null;
    }
}
