package org.karoglan.tollainmear.SignEditor;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.karoglan.tollainmear.SignEditor.utils.ClipBoardContents;
import org.karoglan.tollainmear.SignEditor.utils.KSEStack;
import org.karoglan.tollainmear.SignEditor.utils.Translator;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class KSERecordsManager {
    private String operationLog = "Operation_Log";

    private static KSERecordsManager instance;
    private KaroglanSignEditor kse;
    private KSEStack kseStack;

    private Translator translator;
    private File recorderFile;
    private String pluginName;
    private CommentedConfigurationNode rootNode;
    private CommentedConfigurationNode operationLogNode;
    private CommentedConfigurationNode clipBoardNode;
    private ConfigurationLoader<CommentedConfigurationNode> recordLoader;

    private static Map<Location<World>, KSEStack> operationStack = new LinkedHashMap<>();
    private static Map<String, ClipBoardContents> copylist = new LinkedHashMap<>();

    KSERecordsManager(KaroglanSignEditor plugin) throws IOException {
        kse = plugin;
        instance = this;
        recorderFile = new File(plugin.getConfigPath().toString() + "/records.yml");
        recordLoader = HoconConfigurationLoader.builder().setFile(recorderFile).build();
        rootNode = recordLoader.load();
        pluginName = KaroglanSignEditor.getPluginName();
        translator = KaroglanSignEditor.getTranslator();
        operationLogNode = rootNode.getNode(pluginName).getNode("Operation_Log");
        clipBoardNode = rootNode.getNode(pluginName).getNode("Clipboard");
    }

    void init() throws IOException {

        if (!recorderFile.exists()) {
            if (!recorderFile.createNewFile()) {
                Translator.logWarn("CouldNotCreate");
            }
        }
        if (rootNode.getNode(pluginName).isVirtual()) {
            rootNode.getNode(pluginName).setComment(translator.getstring("rec.main"));
        }
        if (clipBoardNode.isVirtual()) {
            clipBoardNode.setComment(Translator.getstring("rec.Clipboard"));
        }
        if (operationLogNode.isVirtual()) {
            operationLogNode.setComment(Translator.getstring("rec.OperationLog"));
        }
        recordLoader.save(rootNode);

        if (operationLogNode.hasMapChildren()) {
//TODO-Complete while have the save test.
//            for (Object obj: operationLogNode.getChildrenMap().keySet()){
//                Integer now = operationLogNode.getNode(obj)
//
//                KSEStack kseStack = new KSEStack()
//                operationStack.put((Location<World>) obj, (KSEStack) operationLogNode.getChildrenMap().get(obj));
//            }
//        }
//        if (kse.getConfigNode().getNode(pluginName).getNode("ClipBoardCache").getBoolean()){
//            if (clipBoardNode.hasMapChildren()){
//                for (Object obj:clipBoardNode.getChildrenMap().keySet()){
//                    copylist.put((String) obj, (ClipBoardContents) clipBoardNode.getChildrenMap().get(obj));
//                }
//            }
        }
    }

    public void save() throws IOException {
        for (Location<World> locNode : operationStack.keySet()) {
            kseStack = operationStack.get(locNode);
            Text[][] stackArray = kseStack.getTextStack();
            operationLogNode.getNode(locNode).getNode("now").setValue(kseStack.getNow());
            operationLogNode.getNode(locNode).getNode("tail").setValue(kseStack.getTail());
            operationLogNode.getNode(locNode).getNode("head").setValue(kseStack.getHead());
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 4; j++) {
                    operationLogNode
                            .getNode(locNode)
                            .getNode("Stack[" + i + "]")
                            .getNode("Line[" + j + "]")
                            .setValue(TextSerializers.FORMATTING_CODE.serialize(stackArray[i][j] == null ? Text.of("") : stackArray[i][j]));
                }
            }
        }
        if (kse.getConfigNode().getNode(pluginName).getNode("ClipBoardCache").getBoolean()) {
            for (String playerName : copylist.keySet()) {
                Text[] clipArray = copylist.get(playerName).get();
                for (int i = 0; i < 4; i++) {
                    clipBoardNode
                            .getNode(playerName)
                            .getNode("Line[" + i + "]")
                            .setValue(TextSerializers.FORMATTING_CODE.serialize(clipArray[i] == null ? Text.of("") : clipArray[i]));
                }
            }
        }

        recordLoader.save(rootNode);
    }

    public static KSERecordsManager getInstance() {
        return instance;
    }

    public static Map<String, ClipBoardContents> getCopylist() {
        return copylist;
    }

    public static Map<Location<World>, KSEStack> getOperationStack() {
        return operationStack;
    }
}
