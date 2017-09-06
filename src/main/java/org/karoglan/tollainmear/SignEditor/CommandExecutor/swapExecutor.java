package org.karoglan.tollainmear.SignEditor.CommandExecutor;

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

import java.util.Optional;

public class swapExecutor implements CommandExecutor {
    private String mode = "Swapped";
    private mainController mc = new mainController();
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)){
            mc.playerNotFound(src);
            return CommandResult.empty();
        }
        Player player = ((Player)src).getPlayer().get();
        Text textLine1,textLine2;
        Integer line1,line2;

        line1 = args.<Integer>getOne(Text.of("line")).get();
        line2 = args.<Integer>getOne(Text.of("another line")).get();
        if (!mc.isLinesValid(line1)||!mc.isLinesValid(line2)){
            mc.linesWrong(player);
            return CommandResult.empty();
        }
        Optional<TileEntity> signOpt = mc.getSign(player);
        if (signOpt==null||!signOpt.isPresent()){
            mc.signNotFound(player);
            return CommandResult.empty();
        }
        TileEntity sign = signOpt.get();
        textLine1 = mc.getTargetText(sign,line2);
        textLine2 = mc.getTargetText(sign,line1);
        mc.setText(sign,line1, TextSerializers.FORMATTING_CODE.serialize(textLine1));
        mc.setText(sign,line2, TextSerializers.FORMATTING_CODE.serialize(textLine2));
        mc.notice(player,line1,line2,mode,textLine2,textLine1);
        return CommandResult.success();
    }
}
