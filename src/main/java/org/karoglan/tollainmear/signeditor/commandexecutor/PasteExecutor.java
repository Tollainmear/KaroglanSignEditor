package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.karoglan.tollainmear.signeditor.utils.KSEStack;
import org.karoglan.tollainmear.signeditor.utils.MainController;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.util.Optional;

public class PasteExecutor implements CommandExecutor {
    private KSEStack kseStack;
    private MainController mc = new MainController();
    private KaroglanSignEditor plugin;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
        plugin = KaroglanSignEditor.getInstance();
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
            return;
        }
        Player player = ((Player) src).getPlayer().get();
        if (!(KSERecordsManager.getCopylist().containsKey(player.getName()))) {
            mc.nothingToPaste(player);
            return;
        }
        Optional<TileEntity> signopt = mc.getSign(player);
        if (signopt == null || !signopt.isPresent()) {
            mc.signNotFound(player);
            return;
        }
        TileEntity sign = signopt.get();

        kseStack = mc.getKseStack(sign,player);

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
                        return;
                    }
                }
                return;
            }
            mc.linesWrong(player);
            return;
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
        }}).submit(KaroglanSignEditor.getInstance());
        return CommandResult.success();
    }
}
