package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
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
import java.util.Optional;

public class pasteExecutor implements CommandExecutor {
    private String mode = "pasted";
    private mainController mc = new mainController();
    private KaroglanSignEditor plugin;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        plugin = KaroglanSignEditor.getInstance();
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
        }
        Player player = ((Player) src).getPlayer().get();
        if (!(KaroglanSignEditor.copylist.containsKey(player.getName()))) {
            mc.nothingToPaste(player);
        }
        Optional<TileEntity> signopt = mc.getSign(player);
        if (signopt == null || !signopt.isPresent()) {
            mc.signNotFound(player);
        }
        TileEntity sign = signopt.get();
        Text oldText;

        Optional<Integer> lineOpt = args.<Integer>getOne(Text.of("line"));
        if (lineOpt.isPresent()) {
            Integer line = lineOpt.get();
            if (mc.isLinesValid(line)) {
                Text[] textArray = KaroglanSignEditor.copylist.get(player.getName());
                for (int i = 0; i < 4; i++) {
                    if (line == i + 1) {
                        oldText = mc.getTargetText(sign, line);
                        mc.setText(sign, line, TextSerializers.FORMATTING_CODE.serialize(textArray[i]));
                        mc.notice(player, line, mode, oldText, mc.getTargetText(sign, line));
                        return CommandResult.success();
                    }
                }
                return CommandResult.success();
            }
            mc.linesWrong(player);
            return CommandResult.empty();
        }

        Text[] textArray = KaroglanSignEditor.copylist.get(player.getName());
        for (int i = 0; i < 4; i++) {
            oldText = mc.getTargetText(sign, i + 1);
            mc.setText(sign, i + 1, TextSerializers.FORMATTING_CODE.serialize(textArray[i]));
            mc.notice(player, i + 1, mode, oldText, mc.getTargetText(sign, i + 1));
        }
        return CommandResult.success();
    }
}
