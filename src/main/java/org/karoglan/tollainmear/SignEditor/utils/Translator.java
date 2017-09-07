package org.karoglan.tollainmear.SignEditor.utils;

import com.google.common.base.Charsets;
import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Translator {
    private static Logger logger;
    private static ResourceBundle resourceBundle;
    private AssetManager assetManager;
    private String langPath;
    private File langFile;
    private String lang;
    private Optional<Asset> assetOpt;
    private Asset asset;

    public Translator init(KaroglanSignEditor plugin) throws IOException {
        logger = plugin.getLogger();
        assetManager = Sponge.getAssetManager();
        lang = plugin.getConfigNode().getNode("KaroglanSignEditor").getNode("Language").getString();
        langPath = plugin.getConfigPath().toString() + "/lang/";
        assetOpt = assetManager.getAsset(plugin, "lang/" + lang + ".properties");
        if (!(Files.exists(Paths.get(langPath)))) {
            Files.createDirectory(Paths.get(langPath));
        }
        if (!(langFile = new File(langPath + lang + ".properties")).exists()) {
            if (!assetOpt.isPresent()) {
                plugin.getLogger().warn("Could not found the Language file\"" + lang + "\",KSE will using the \"en_US\" by default");
                assetOpt = assetManager.getAsset(plugin, "lang/en_US.properties");
                if (!assetOpt.isPresent()) {
                    logger.warn("Ops....Could not load en_US else,please submit issues at:");
                    logger.warn("https://github.com/Tollainmear/KaroglanSignEditor/issues");
                    return this;
                }
                langFile = new File(langPath + "en_US.properties");
                assetOpt.get().copyToFile(langFile.toPath());
                logger.info("Release Language file successfully.");
            } else {
                assetOpt.get().copyToFile(langFile.toPath());
                logger.info("Release Language file successfully.");
            }
        }
        resourceBundle = new PropertyResourceBundle(new InputStreamReader(langFile.toURI().toURL().openStream(), Charsets.UTF_8));
        logInfo("LanguageLoaded");
        asset = assetOpt.get();
        return this;
    }

    public void checkUpdate() throws IOException {
        String verNow = getstring("version");
        if (verNow.equals(null)) verNow = "1.0";
        String verInPackage = new PropertyResourceBundle(new InputStreamReader(asset.getUrl().openStream(), Charsets.UTF_8))
                .getString("version");
        if (verInPackage.compareTo(verNow) > 0) {
            new File(langFile.toString()).renameTo(new File(langPath + lang + "_old.properties"));
            asset.copyToDirectory(Paths.get(langPath));
            resourceBundle = new PropertyResourceBundle(new InputStreamReader(langFile.toURI().toURL().openStream(), Charsets.UTF_8));
            logInfo("Language.Updated");
            return;
        }
        logInfo("Language.NeedNotUpdate");
    }

    public static String getstring(String key) {
        if (key == null) {
            return "Language resource Not Found";
        }
        return resourceBundle.getString(key);
    }

    public Text getText(String key) {
        if (key == null) {
            return TextSerializers.FORMATTING_CODE.deserialize("Language Not Found");
        }
        return TextSerializers.FORMATTING_CODE.deserialize(getstring(key));
    }

    public static void logInfo(String key) {
        logger.info("\033[36m" + getstring(key));
    }

    public static void logWarn(String key) {
        logger.info("\031[36m" + getstring(key));
    }

}
