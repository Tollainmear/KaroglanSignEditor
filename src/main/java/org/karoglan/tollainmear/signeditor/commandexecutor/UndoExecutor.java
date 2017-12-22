package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.karoglan.tollainmear.signeditor.utils.KSEStack;
import org.karoglan.tollainmear.signeditor.utils.Translator;
import org.karoglan.tollainmear.signeditor.utils.MainController;
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

public class UndoExecutor implements CommandExecutor {
    private MainController mc = new MainController();
    private KSEStack kseStack;
    private Translator translator;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        translator = KaroglanSignEditor.getInstance().getTranslator();
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
            return CommandResult.empty();
        }

        Optional<Player> playerOpt;
        if (!(playerOpt = mc.getPlayerOpt(src)).isPresent() || playerOpt == null) {
            mc.playerNotFound(src);
            return CommandResult.empty();
        }
        Player player = playerOpt.get();

        Optional<TileEntity> signopt = mc.getSign(player);
        if (signopt == null || !signopt.isPresent()) {
            mc.signNotFound(player);
            return CommandResult.success();
        }
        TileEntity sign = signopt.get();

        if (mc.hasKseStack(sign)) {
            kseStack = KSERecordsManager.getOperationStack().get(sign.getLocation().toString());
        } else {
            mc.notice(player, translator.getText("message.stackUndoEmpty"));
            return CommandResult.empty();
        }

        if (kseStack.getNow() == kseStack.getHead()) {
            mc.notice(player, translator.getText("message.stackUndoEmpty"));
            CommandResult.empty();
        } else {
            kseStack.setNow(kseStack.getNow() - 1 < 0 ? 9 : kseStack.getNow() - 1);
            Text[] textArray = kseStack.getTextStack(kseStack.getNow());
            for (int i = 0; i < 4; i++) {
                mc.setText(sign, i + 1, TextSerializers.FORMATTING_CODE.serialize(textArray[i] == null ? Text.of("") : textArray[i]));
            }
            mc.notice(player, translator.getText("message.undoDone"));
            KSERecordsManager.getOperationStack().put(sign.getLocation().toString(), kseStack);
            try {
                kseStack.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return CommandResult.success();
    }
}
