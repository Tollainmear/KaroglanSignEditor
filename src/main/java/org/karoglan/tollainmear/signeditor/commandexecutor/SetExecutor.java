package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.utils.KSEStack;
import org.karoglan.tollainmear.signeditor.utils.MainController;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.Optional;

public class SetExecutor implements CommandExecutor {
    private MainController mc = new MainController();
    private KSEStack kseStack;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
            return CommandResult.success();
        }
        int line = args.<Integer>getOne(Text.of("line")).get();
        String text = args.<String>getOne(Text.of("Text")).get();

        if (!mc.isLinesValid(line)) {
            mc.linesWrong(src);
            return CommandResult.empty();
        }
        if (!mc.getPlayerOpt(src).isPresent()) {
            mc.playerNotFound(src);
            return CommandResult.empty();
        }

        Player player = ((Player) src).getPlayer().get();
        Optional<TileEntity> signOpt = mc.getSign(player);

        if (signOpt == null || !signOpt.isPresent()) {
            mc.signNotFound(player);
            return CommandResult.empty();
        }

        TileEntity sign = signOpt.get();

        kseStack = mc.getKseStack(sign);

        try {
            kseStack.update(mc.getTextArray(sign), sign.getLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Text signText = mc.getTargetText(sign, line);
        mc.setText(sign, line, text);
        mc.notice(player, line, signText, mc.getTargetText(sign, line));

        try {
            kseStack.add(mc.getTextArray(sign), sign.getLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return CommandResult.success();
    }
}