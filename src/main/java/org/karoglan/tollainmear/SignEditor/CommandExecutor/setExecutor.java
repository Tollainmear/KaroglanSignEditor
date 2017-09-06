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
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

public class setExecutor implements CommandExecutor {
    private mainController mc = new mainController();
    private String mode = "changed";

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
        if (mc.isPlayer(src).isPresent()) {
            Player player = ((Player) src).getPlayer().get();
            Optional<TileEntity> signOpt = mc.getSign(player);
            if (signOpt != null && signOpt.isPresent()) {
                TileEntity sign = signOpt.get();
                Text signText = mc.getTargetText(sign, line);
                mc.setText(sign, line, text);
                mc.notice(player, line, mode, signText, mc.getTargetText(sign, line));
                return CommandResult.success();
            }
            mc.signNotFound(player);
            return CommandResult.empty();
        }
        mc.playerNotFound(src);
        return CommandResult.empty();
    }
}