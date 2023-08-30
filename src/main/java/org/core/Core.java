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
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.annotations.AnnotationParser;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.commands.CoreCommands;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

public final class Core extends JavaPlugin {
    private AnnotationParser<CommandSender> annotationParser;
    public File configFile = new File(this.getDataFolder(), "config.yml");
    public FileConfiguration config = this.getConfig();
    private static Core plugin;
    public static Path plugindir;
    public static boolean muteChat = false;
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
        String mode = getConfigValue("mode");
        registerCommands();
    }

    private void registerCommands() {
        this.annotationParser.parse(new CoreCommands());
    }
    public static String getConfigValue(@NotNull String value) {
        YamlConfiguration c = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        return c.get(value).toString();
    }

    @Override
    public void onDisable() {
        plugin = null;
    }
}
