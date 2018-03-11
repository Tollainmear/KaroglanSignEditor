package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.utils.MainController;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class TrustExecutor implements CommandExecutor {
    private MainController mc = new MainController();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //todo-是玩家吗？有牌子吗？牌子是你的吗？trust的玩家是你自己吗？trust列表有这个玩家吗？
//        if(mc.isPLayer(src)||);
        //todo-给玩家的名字添加进信任列表
        return CommandResult.success();
    }
}
