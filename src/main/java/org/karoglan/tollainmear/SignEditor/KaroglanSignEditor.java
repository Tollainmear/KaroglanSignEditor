package org.karoglan.tollainmear.SignEditor;

import com.google.inject.Inject;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Plugin(id = "karoglansigneditor",name = "KaroglanSignEditor",authors = "Tollainmear",version = "2.0",description = "Make sign edition esaier!")
public class KaroglanSignEditor {

    private static String pluginName = "KaroglanSignEditor";
    private static String version = "2.0";
    private static KaroglanSignEditor instance;
    private KSECommandManager kseCmdManager;

    public static Map<String,Text[]> copylist = new LinkedHashMap<>();

    @Inject
    private static Logger logger;

    @Listener
    public void onPreInit(GamePostInitializationEvent event){
        this.kseCmdManager = new KSECommandManager(this);
        instance = this;
    }

    @Listener
    public void onStart(GameStartingServerEvent event)throws IOException{
        this.kseCmdManager.init(this);
    }

    public static KaroglanSignEditor getInstance() {
        return instance;
    }
    public static String getPluginName() {
        return pluginName;
    }
    public static Logger getLogger() {
        return logger;
    }

    public static String getVersion() {
        return version;
    }

}
