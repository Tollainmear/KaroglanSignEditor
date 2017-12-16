package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.karoglan.tollainmear.SignEditor.KSERecordsManager;
import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.karoglan.tollainmear.SignEditor.utils.KSEStack;
import org.karoglan.tollainmear.SignEditor.utils.Translator;
import org.karoglan.tollainmear.SignEditor.utils.mainController;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.image.TileObserver;
import java.io.IOException;
import java.util.Optional;

public class pasteExecutor implements CommandExecutor {
    private KSEStack kseStack;
    private mainController mc = new mainController();
    private KaroglanSignEditor plugin;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        plugin = KaroglanSignEditor.getInstance();
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
        }
        Player player = ((Player) src).getPlayer().get();
        if (!(KSERecordsManager.getCopylist().containsKey(player.getName()))) {
            mc.nothingToPaste(player);
            return CommandResult.success();
        }
        Optional<TileEntity> signopt = mc.getSign(player);
        if (signopt == null || !signopt.isPresent()) {
            mc.signNotFound(player);
        }
        TileEntity sign = signopt.get();

        kseStack = mc.getKseStack(sign);

        try {
            kseStack.update(mc.getTextArray(sign), sign.getLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Text oldText;

        Optional<Integer> lineOpt = args.<Integer>getOne(Text.of("line"));
        if (lineOpt.isPresent()) {
            Integer line = lineOpt.get();
            if (mc.isLinesValid(line)) {
                Text[] textArray = KSERecordsManager.getCopylist().get(player.getName()).get();
                mc.notice(player, plugin.getTranslator().getText("message.onChangeText"));
                for (int i = 0; i < 4; i++) {
                    if (line == i + 1) {
                        oldText = mc.getTargetText(sign, line);
                        mc.setText(sign, line, TextSerializers.FORMATTING_CODE.serialize(textArray[i]));
                        mc.notice(player, line, oldText, mc.getTargetText(sign, line));
                        try {
                            kseStack.add(mc.getTextArray(sign), sign.getLocation());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return CommandResult.success();
                    }
                }
                return CommandResult.success();
            }
            mc.linesWrong(player);
            return CommandResult.empty();
        }

        mc.notice(player, plugin.getTranslator().getText("message.onChangeText"));
        Text[] textArray = KSERecordsManager.getCopylist().get(player.getName()).get();
        for (int i = 0; i < 4; i++) {
            oldText = mc.getTargetText(sign, i + 1);
            mc.setText(sign, i + 1, TextSerializers.FORMATTING_CODE.serialize(textArray[i]));
            mc.notice(player, i + 1, oldText, mc.getTargetText(sign, i + 1));
        }
        try {
            kseStack.add(mc.getTextArray(sign), sign.getLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommandResult.success();
    }
}
