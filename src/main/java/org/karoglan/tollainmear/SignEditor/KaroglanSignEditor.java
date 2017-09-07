package org.karoglan.tollainmear.SignEditor;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.karoglan.tollainmear.SignEditor.utils.Translator;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Plugin(id = "karoglansigneditor", name = "KaroglanSignEditor", authors = "Tollainmear", version = "2.1", description = "Make sign edition esaier!")
public class KaroglanSignEditor {

    private static String pluginName = "KaroglanSignEditor";
    private static String version = "2.1";

    private static KaroglanSignEditor instance;
    private static PluginContainer plugin;
    private KSECommandManager kseCmdManager;
    private Translator translator;
    private CommentedConfigurationNode configNode;
    public static Map<String, Text[]> copylist = new LinkedHashMap<>();

    @Inject
    @DefaultConfig(sharedRoot = false)
    ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;

    @Inject
    private Logger logger;

    @Listener
    public void onPreInit(GamePostInitializationEvent event) throws IOException {
        instance = this;
        configNode = configLoader.load();
        if (configNode.getNode(pluginName).getNode("Language").isVirtual()) {
            configNode.getNode(pluginName).getNode("Language").setValue(Locale.getDefault().toString());
        }
        translator = new Translator().init(this);
        kseCmdManager = new KSECommandManager(this);
        kseCmdManager.init(this);
    }

    @Listener
    public void onStart(GameStartingServerEvent event) throws IOException {
        if (configNode.getNode(pluginName).getNode("Author").isVirtual()) {
            Translator.logInfo("cfg.notFound");
            configNode.getNode(pluginName).getNode("Author").setValue("Tollainmear");
            configNode.getNode(pluginName).setComment(translator.getstring("cfg.auther"));
            configNode.getNode(pluginName).getNode("Language").setValue(Locale.getDefault().toString())
                    .setComment(translator.getstring("cfg.comment.Language"));
            configNode.getNode(pluginName).getNode("TraceRange").setValue("10")
                    .setComment(translator.getstring("cfg.comment.traceRange"));
            configNode.getNode(pluginName).getNode("ClipBoardCache").setValue(true)
                    .setComment(translator.getstring("cfg.clipboard"));
            configNode.getNode(pluginName).getNode("Log").setValue(false)
                    .setComment(translator.getstring("cfg.logHistory"));
        }
        configLoader.save(configNode);
        translator.checkUpdate();
    }

    public static KaroglanSignEditor getInstance() {
        return instance;
    }

    public static String getPluginName() {
        return pluginName;
    }

    public Logger getLogger() {
        return logger;
    }

    public static String getVersion() {
        return version;
    }

    public Path getConfigPath() {
        return configPath;
    }

    public CommentedConfigurationNode getConfigNode() {
        return configNode;
    }

    public static PluginContainer getPlugin() {
        return plugin;
    }

}
