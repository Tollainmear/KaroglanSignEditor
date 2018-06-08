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

import java.io.IOException;
import java.util.Optional;

public class SetExecutor implements CommandExecutor {
    private MainController mc = KaroglanSignEditor.getInstance().getMainController();
    private KSEStack kseStack;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if (!(src instanceof Player)) {
                mc.playerNotFound(src);
                return;
            }

            int line = args.<Integer>getOne(Text.of("line")).get();
            String text = args.<String>getOne(Text.of("Text")).get();

            if (!mc.isLinesValid(line)) {
                mc.linesWrong(src);
                return;
            }
            if (!mc.getPlayerOpt(src).isPresent()) {
                mc.playerNotFound(src);
                return ;
            }
            boolean notice;
            if (args.getOne(Text.of("notice")).isPresent()){
                notice = (boolean)args.getOne(Text.of("notice")).get();
            }else notice = true;

            Player player = ((Player) src).getPlayer().get();
            Optional<TileEntity> signOpt = mc.getSign(player);

            if (signOpt == null || !signOpt.isPresent()) {
                mc.signNotFound(player);
                return ;
            }

            TileEntity sign = signOpt.get();

            kseStack = mc.getKseStack(sign);

            if (!mc.couldModify(player,kseStack)){
                mc.notPermitted(player,kseStack);
                return;
            }
            try {
                kseStack.update(mc.getTextArray(sign), sign.getLocation());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Text signText = mc.getTargetText(sign, line);
            mc.setText(sign, line, text);
            if (notice){
                mc.notice(player, line, signText, mc.getTargetText(sign, line));
            }

            try {
                kseStack.add(mc.getTextArray(sign), sign.getLocation());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).submit(KaroglanSignEditor.getInstance());


        return CommandResult.success();
    }
}