package org.karoglan.tollainmear.signeditor.utils;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;

public class MainController {
    private KaroglanSignEditor kse = KaroglanSignEditor.getInstance();
    public static MainController instance;
    Text[] textArray = new Text[4];

    public MainController(){
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }

    public Text[] getTextArray(TileEntity sign) {
        for (int i = 0; i < 4; i++) {
            textArray[i] = getTargetText(sign, i + 1);
        }
        return textArray;
    }

    public Optional<Player> getPlayerOpt(CommandSource src) {
        Optional<Player> playerOpt = ((Player) src).getPlayer();
        if (playerOpt.isPresent())
            return playerOpt;
        return null;
    }

    public Optional<TileEntity> getSign(Player player) {
        BlockRay<World> blockRay = BlockRay.from(player)
                .distanceLimit(
                        KaroglanSignEditor.getInstance()
                                .getConfigNode()
                                .getNode(KaroglanSignEditor.getPluginName())
                                .getNode("TraceRange").getInt()
                ).build();

        while (blockRay.hasNext()) {
            BlockRayHit<World> blockRayHit = blockRay.next();
            Location<World> Location = blockRayHit.getLocation();
            BlockType targetBlock = Location.getBlockType();
            if (targetBlock.equals(BlockTypes.STANDING_SIGN) || targetBlock.equals(BlockTypes.WALL_SIGN)) {
                Optional<TileEntity> signOpt = Location.getTileEntity();
                return signOpt;
            }
        }

        return null;
    }

    public void setText(TileEntity sign, int lines, String str) {
        if (sign.supports(SignData.class)) {
            Text maintext = TextSerializers.FORMATTING_CODE.deserialize(str);
            SignData newdata = sign.getOrCreate(SignData.class).get();
            newdata.setElement(lines - 1, Text.of(maintext));
            sign.offer(newdata);
        }
    }

    public Text getTargetText(TileEntity Sign, int lines) {
        Optional<SignData> data = Sign.getOrCreate(SignData.class);
        if (data.isPresent()) {
            Text text = data.get().lines().get(lines - 1);
            return text == null ? Text.of("") : text;
        }
        return null;
    }

    public boolean isLinesValid(Integer lines) {
        return lines < 5 && lines > 0;
    }

    public void notice(Player player, Integer line, Text oldText, Text newText) {
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.onChangeText"))
                .concat(Text.of("\n"))
                .concat(kse.getTranslator().getText("message.changeLine.front"))
                .concat(Text.of(" "))
                .concat(Text.of(line))
                .concat(Text.of(" "))
                .concat(kse.getTranslator().getText("message.changeLine.back"))
                .concat(oldText)
                .concat(kse.getTranslator().getText("message.symbolTo"))
                .concat(newText));
    }

    public void notice(Player player, Integer line1, Integer line2, Text oldText, Text newText) {
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.onChangeText"))
                .concat(Text.of("\n"))
                .concat(kse.getTranslator().getText("message.changeLine.front"))
                .concat(Text.of(line1))
                .concat(kse.getTranslator().getText("message.symbolSwap"))
                .concat(Text.of(line2))
                .concat(kse.getTranslator().getText("message.changeLine.back"))
                .concat(oldText)
                .concat(kse.getTranslator().getText("message.symbolSwap"))
                .concat(newText));
    }

    public void notice(Player player, Text text) {
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(text));

    }

    public void notice(Player player, Integer line, Text text) {
        player.sendMessage(kse.getTranslator().getText("message.changeLine.front")
                .concat(text.of(line))
                .concat(kse.getTranslator().getText("message.changeLine.back"))
                .concat(text));
    }

    public void signNotFound(Player player) {
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.signNotFound")));
    }
    public void historyNotFound(Player player) {
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.historyNotFound")));

    }

    public void playerNotFound(CommandSource src) {
        src.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.playerNotFound")));
    }

    public void linesWrong(CommandSource src) {
        src.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.linesWrong")));
    }

    public void nothingToPaste(CommandSource src) {
        src.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.nothingToPaste")));
    }

    public void noticeVersion(CommandSource src) {
        src.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.version"))
                .concat(Text.of(KaroglanSignEditor.getVersion())));
    }

    public KSEStack getKseStack(TileEntity sign, Player src) {
        if (KSERecordsManager.getOperationStack().containsKey(sign.getLocation().toString())) {
            return KSERecordsManager.getOperationStack().get(sign.getLocation().toString());
        } else {
            return new KSEStack(src.getName());
        }
    }

    public boolean hasKseStack(TileEntity sign) {
        return  (KSERecordsManager.getOperationStack().containsKey(sign.getLocation().toString()));
    }

    public boolean isOwner(TileEntity sign, Player player) {
        //todo-
        KSEStack kseStack = KSERecordsManager.getOperationStack().get(sign.getLocation().toString());
        return kseStack.isOwner(player.toString());
    }

    public boolean isPLayer(CommandSource src) {
        return (src instanceof Player);
    }

    public void alreadyTrusted(Player player) {
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.alreadyTrusted")));
    }

    public void cantTrustSelf(Player player) {
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().getText("message.cantTrustSelf")));
    }

    public void untrustSuccessful(Player player,String targetPlayer) {
        String msg = kse.getTranslator().getstring("message.untrustSuccessful");
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().deserialize(msg.replace("%player%",targetPlayer))));
    }

    public void untrustFailed(Player player, String targetPlayer) {
        String msg = kse.getTranslator().getstring("message.untrustFailed");
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().deserialize(msg.replace("%player%",targetPlayer))));
    }

    public void trustSuccessful(Player player, String targetPlayer) {
        String msg = kse.getTranslator().getstring("message.trustSuccessful");
        player.sendMessage(kse.getTranslator().getText("message.KSEprefix")
                .concat(kse.getTranslator().deserialize(msg.replace("%player%",targetPlayer))));
    }

    public boolean couldModify(Player player,KSEStack kseStack) {
        return player.hasPermission("kse.bypass") || kseStack.isOwner(player.getName())|| (KSERecordsManager.getWhiteList().containsKey(kseStack.getOwner()) && KSERecordsManager.getWhiteList().get(kseStack.getOwner()).contains(player.getName()));
    }
}

