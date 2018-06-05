package org.karoglan.tollainmear.signeditor;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.karoglan.tollainmear.signeditor.utils.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.plugin.meta.version.ComparableVersion;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Plugin(id = "karoglansigneditor", name = "KaroglanSignEditor", authors = "Tollainmear", version = "3.3", description = "Make sign edition esaier!")
public class KaroglanSignEditor {

    private static String pluginName = "KaroglanSignEditor";
    private static final String version = "3.3";

    private static KaroglanSignEditor instance;
    private  KSERecordsManager kseRecordsManager;
    private static KSECommandManager kseCmdManager;
    private  MainController mainController;
    private boolean hasNewVersion = false;
    private Translator translator;
    private static String newVersion = version;

    private static final String releasePage = "https://github.com/Tollainmear/KaroglanSignEditor/releases";
    private static final String API_URL = "https://api.github.com/repos/tollainmear/karoglansigneditor/releases";

    private CommentedConfigurationNode configNode;

    public static Map<String ,Boolean> playerState = new LinkedHashMap<>();

    @Inject
    @DefaultConfig(sharedRoot = false)
    ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;

    @Inject
    private Logger logger;

    @Inject
    private Metrics metrics;

    public void setKseCmdManager(KSECommandManager kseCmdManager) {
        this.kseCmdManager = kseCmdManager;
    }

    @Listener
    public void onPreInit(GamePostInitializationEvent event) throws IOException {
        instance = this;
        cfgInit();
        kseRecordsManager = new KSERecordsManager(this);
        mainController = new MainController();
        kseCmdManager = new KSECommandManager(this);
    }

    @Listener
    public void onStart(GameStartingServerEvent event) throws IOException {
        translator.checkUpdate();
        new Thread(this::checkUpdate).start();
        Sponge.getEventManager().registerListeners(this,new KSEListener());
    }

    @Listener
    public void onReload(GameReloadEvent event){
        MessageReceiver src =event.getCause().first(CommandSource.class).orElse(Sponge.getServer().getConsole());
        try {
            cfgInit();
            kseRecordsManager.init();
            translator =new Translator(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(translator.getstring("message.KSEprefix")+translator.getstring("message.reload")));
    }

    @Listener
    public void onPlayerjoin(ClientConnectionEvent.Join event,@First Player player) throws MalformedURLException {
        //if (hasNewVersion){
            if(player.hasPermission("kse.admin")&&hasNewVersion){
                player.sendMessage(translator.getText("message.KSEprefix").concat(translator.getText("update.hasNew").concat(Text.of(newVersion))));
                player.sendMessage(translator.getText("message.KSEprefix").concat(translator.getText("update.clickMSG").toBuilder().onClick(TextActions.openUrl(new URL(releasePage))).build()));
//                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(translator.getstring("update.hasNew") + newVersion));
            }
        //}
        logger.warn(player.getName());
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
            configNode.getNode(pluginName).getNode("ClipBoardCache").setValue(false)
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

    public MainController getMainController(){
        return mainController;
    }

    public KSERecordsManager getKSERecordsManager() {
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

    private void checkUpdate()
    {
        try
        {
            URL url = new URL(API_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.getResponseCode();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream(), Charsets.UTF_8);
            JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonArray().get(0).getAsJsonObject();
            String newVersion = jsonObject.get("tag_name").getAsString();
            //if (newVersion.startsWith("v"))
            //{
            //    newVersion = newVersion.substring(1);
                String releaseName = jsonObject.get("name").getAsString();
                //String releaseUrl = jsonObject.get("html_url").getAsString();
                if (new ComparableVersion(newVersion).compareTo(new ComparableVersion(version)) > 0)
                {
                    hasNewVersion = true;
                    this.newVersion = releaseName;
                    //this.releasePage = releaseUrl;
                    logger.info("\033[31m" + translator.getstring("update.hasNew") + releaseName);
                    logger.warn("\033[31m" + translator.getstring("update.downloadFrom") + releasePage);
//                    this.logger.warn("================================================================");
//                    this.logger.warn("An update was found: " + releaseName);
//                    this.logger.warn("You can get the latest version at: " + releaseUrl);
//                    this.logger.info("================================================================");
                }

            else logger.info("\033[31m" + translator.getstring("update.noUpdate") + releaseName);
           // }
        }
        catch (Exception e)
        {
            // <strike>do not bother offline users</strike> maybe bothering them is a better choice
            translator.logWarn("update.failure");
        }
    }
}
