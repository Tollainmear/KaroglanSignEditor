package org.karoglan.tollainmear.SignEditor.utils;

import org.karoglan.tollainmear.SignEditor.KSERecordsManager;
import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class mainController {
    private Translator translator = KaroglanSignEditor.getInstance().getTranslator();
    Text[] textArray = new Text[4];

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

        int count = 0;
        while (blockRay.hasNext()) {
            BlockRayHit<World> blockRayHit = blockRay.next();
            Location<World> Location = blockRayHit.getLocation();
            BlockType targetBlock = Location.getBlockType();
            if (targetBlock.equals(BlockTypes.AIR)) {
                count++;
                continue;
            } else if (targetBlock.equals(BlockTypes.STANDING_SIGN) || targetBlock.equals(BlockTypes.WALL_SIGN)) {
                count++;
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
        player.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.onChangeText"))
                .concat(Text.of("\n"))
                .concat(translator.getText("message.changeLine.front"))
                .concat(Text.of(" "))
                .concat(Text.of(line))
                .concat(Text.of(" "))
                .concat(translator.getText("message.changeLine.back"))
                .concat(oldText)
                .concat(translator.getText("message.symbolTo"))
                .concat(newText));
    }

    public void notice(Player player, Integer line1, Integer line2, Text oldText, Text newText) {
        player.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.onChangeText"))
                .concat(Text.of("\n"))
                .concat(translator.getText("message.changeLine.front"))
                .concat(Text.of(line1))
                .concat(translator.getText("message.symbolSwap"))
                .concat(Text.of(line2))
                .concat(translator.getText("message.changeLine.back"))
                .concat(oldText)
                .concat(translator.getText("message.symbolSwap"))
                .concat(newText));
    }

    public void notice(Player player, Text text) {
        player.sendMessage(translator.getText("message.KSEprefix")
                .concat(text));

    }

    public void notice(Player player, Integer line, Text text) {
        player.sendMessage(translator.getText("message.changeLine.front")
                .concat(text.of(line))
                .concat(translator.getText("message.changeLine.back"))
                .concat(text));
    }

    public void signNotFound(Player player) {
        player.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.signNotFound")));
    }

    public void playerNotFound(CommandSource src) {
        src.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.playerNotFound")));
    }

    public void linesWrong(CommandSource src) {
        src.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.linesWrong")));
    }

    public void nothingToPaste(CommandSource src) {
        src.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.nothingToPaste")));
    }

    public void noticeVersion(CommandSource src) {
        src.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.version"))
                .concat(Text.of(KaroglanSignEditor.getVersion())));
    }

    public KSEStack getKseStack(TileEntity sign) {
        if (KSERecordsManager.getOperationStack().containsKey(sign.getLocation().toString())) {
            return KSERecordsManager.getOperationStack().get(sign.getLocation().toString());
        } else {
            return new KSEStack();
        }
    }

    public Boolean hasKseStack(TileEntity sign){
        if (KSERecordsManager.getOperationStack().containsKey(sign.getLocation().toString())) {
            return true;
        } else {
            return false;
        }
    }
}

