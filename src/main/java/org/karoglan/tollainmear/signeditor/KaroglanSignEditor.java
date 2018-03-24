package org.karoglan.tollainmear.signeditor;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.karoglan.tollainmear.signeditor.utils.ClipBoardContents;
import org.karoglan.tollainmear.signeditor.utils.KSEStack;
import org.karoglan.tollainmear.signeditor.utils.Translator;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

@Plugin(id = "karoglansigneditor", name = "KaroglanSignEditor", authors = "Tollainmear", version = "3.1", description = "Make sign edition esaier!")
public class KaroglanSignEditor {

    private static String pluginName = "KaroglanSignEditor";
    private static String version = "3.1";

    private static KaroglanSignEditor instance;
    private static KSERecordsManager kseRecordsManager;
    private KSECommandManager kseCmdManager;
    private Translator translator;
    private static KSEStack kseStack;
    private static ClipBoardContents clipBoardContents;

    private CommentedConfigurationNode configNode;

    @Inject
    @DefaultConfig(sharedRoot = false)
    ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;

    @Inject
    private Logger logger;

    public void setKseCmdManager(KSECommandManager kseCmdManager) {
        this.kseCmdManager = kseCmdManager;
    }

    @Listener
    public void onPreInit(GamePostInitializationEvent event) throws IOException {
        instance = this;
        cfgInit();
        kseRecordsManager = new KSERecordsManager(this);
        kseCmdManager = new KSECommandManager(this);
        kseCmdManager.init(this);
        kseStack = new KSEStack();
        clipBoardContents = new ClipBoardContents();
    }

    @Listener
    public void onStart(GameStartingServerEvent event) throws IOException {
        kseRecordsManager.init(this);
        translator.checkUpdate();
    }

    @Listener
    public void onReload(GameReloadEvent event){
        MessageReceiver src =event.getCause().first(CommandSource.class).orElse(Sponge.getServer().getConsole());
        try {
            cfgInit();
            kseRecordsManager.init(this);
            translator =new Translator(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(translator.getstring("message.KSEprefix")+translator.getstring("message.reload")));
    }

    public void cfgInit() throws IOException {
        configNode = configLoader.load();
        if (configNode.getNode(pluginName).getNode("Language").isVirtual()) {
            configNode.getNode(pluginName).getNode("Language").setValue(Locale.getDefault().toString());
            translator = new Translator(this);
            configNode.getNode(pluginName).getNode("Language").setValue(Locale.getDefault().toString())
                    .setComment(translator.getstring("cfg.comment.Language"));
            translator.logInfo("cfg.notFound");
        } else translator = new Translator(this);


        if (configNode.getNode(pluginName).getNode("Author").isVirtual()) {
            configNode.getNode(pluginName).getNode("Author").setValue("Tollainmear");
            configNode.getNode(pluginName).setComment(translator.getstring("cfg.auther"));
        }

        if (configNode.getNode(pluginName).getNode("TraceRange").isVirtual()) {
            configNode.getNode(pluginName).getNode("TraceRange").setValue("10")
                    .setComment(translator.getstring("cfg.comment.traceRange"));
        }

        if (configNode.getNode(pluginName).getNode("ClipBoardCache").isVirtual()) {
            configNode.getNode(pluginName).getNode("ClipBoardCache").setValue(true)
                    .setComment(translator.getstring("cfg.comment.clipboard"));
            configLoader.save(configNode);
        }
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

    public Translator getTranslator() {
        return translator;
    }

    public static ClipBoardContents getClipBoardContents() {
        return clipBoardContents;
    }

    public static KSEStack getKseStack() {
        return kseStack;
    }

    public static KSERecordsManager getKseRecordsManager() {
        return kseRecordsManager;
    }

    public void log(String str) {
        logger.info("\033[36m" + str);
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
        return configLoader;
    }

    public void setTranslator(Translator translator) {
        this.translator = translator;
    }
}
