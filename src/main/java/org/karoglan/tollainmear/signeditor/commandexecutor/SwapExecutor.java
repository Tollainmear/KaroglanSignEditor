package org.karoglan.tollainmear.signeditor.commandexecutor;

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

public class SwapExecutor implements CommandExecutor {
    private KSEStack kseStack;
    private MainController mc = KaroglanSignEditor.getInstance().getMainController();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
            return;
        }
        Player player = ((Player) src).getPlayer().get();
        Text textLine1, textLine2;
        Integer line1, line2;

        line1 = args.<Integer>getOne(Text.of("line")).get();
        line2 = args.<Integer>getOne(Text.of("another line")).get();
        if (!mc.isLinesValid(line1) || !mc.isLinesValid(line2)) {
            mc.linesWrong(player);
            return;
        }
        Optional<TileEntity> signOpt = mc.getSign(player);
        if (signOpt == null || !signOpt.isPresent()) {
            mc.signNotFound(player);
            return;
        }
        TileEntity sign = signOpt.get();

        kseStack = mc.getKseStack(sign,player);

        try {
            kseStack.update(mc.getTextArray(sign), sign.getLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }

        textLine1 = mc.getTargetText(sign, line2);
        textLine2 = mc.getTargetText(sign, line1);
        mc.setText(sign, line1, TextSerializers.FORMATTING_CODE.serialize(textLine1));
        mc.setText(sign, line2, TextSerializers.FORMATTING_CODE.serialize(textLine2));
        mc.notice(player, line1, line2, textLine2, textLine1);
        try {
            kseStack.add(mc.getTextArray(sign), sign.getLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }}).submit(KaroglanSignEditor.getInstance());
        return CommandResult.success();
    }
}
