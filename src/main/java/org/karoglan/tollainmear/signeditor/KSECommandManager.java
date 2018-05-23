package org.karoglan.tollainmear.signeditor;

import org.karoglan.tollainmear.signeditor.commandexecutor.*;
import org.karoglan.tollainmear.signeditor.utils.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class KSECommandManager {
    private final KaroglanSignEditor kse;
    private Translator translator;

    private CommandSpec set;
    private CommandSpec clear;
    private CommandSpec trust;
    private CommandSpec copy;
    private CommandSpec clipboard;
    private CommandSpec paste;
    private CommandSpec swap;
    private CommandSpec undo;
    private CommandSpec redo;
    private CommandSpec reload;
    private CommandSpec version;

    public KSECommandManager(KaroglanSignEditor plugin) {
        this.kse = plugin;

        set = CommandSpec.builder()
                .permission("kse.edit")
                .description(Text.of("Set the text for the target sign"))
                .arguments(
                        GenericArguments.seq(
                                GenericArguments.integer(Text.of("line")),
                                GenericArguments.remainingJoinedStrings(Text.of("Text"))
                        )
                )
                .executor(new SetExecutor())
                .build();

        clear = CommandSpec.builder()
                .permission("kse.clear")
                .description(Text.of("clear the text from the target sign"))
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("line"))))
                .executor(new ClearExecutor())
                .build();

        trust = CommandSpec.builder()
                .permission("kse.trust")
                .description(Text.of("trust a play to edit target sign you own"))
                .arguments(GenericArguments.player(Text.of("player")))
                .executor(new TrustExecutor())
                .build();

        copy = CommandSpec.builder()
                .permission("kse.copy")
                .description(Text.of("copy the text from the target sign"))
                .arguments(GenericArguments.none())
                .executor(new CopyExecutor())
                .build();

        clipboard = CommandSpec.builder()
                .permission("kse.clipboard")
                .description(Text.of("check the text "))
                .arguments(GenericArguments.none())
                .executor(new ClipboardExecutor())
                .build();

        paste = CommandSpec.builder()
                .permission("kse.paste")
                .description(Text.of("paste the text for the target sign"))
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("line"))))
                .executor(new PasteExecutor())
                .build();

        swap = CommandSpec.builder()
                .permission("kse.swap")
                .description(Text.of("Specifying two lines and swap their position"))
                .arguments(GenericArguments.seq(
                        GenericArguments.integer(Text.of("line")),
                        GenericArguments.integer(Text.of("another line"))
                ))
                .executor(new SwapExecutor())
                .build();

        undo = CommandSpec.builder()
                .permission("kse.undo")
                .description(Text.of("Undo the latest operation"))
                .arguments(GenericArguments.none())
                .executor(new UndoExecutor())
                .build();

        redo = CommandSpec.builder()
                .permission("kse.redo")
                .description(Text.of("Redo the latest operation"))
                .arguments(GenericArguments.none())
                .executor(new RedoExecutor())
                .build();

        reload = CommandSpec.builder()
                .permission("kse.reload")
                .description(Text.of("relaod the KSE."))
                .arguments(GenericArguments.none())
                .executor(new ReloadExecutor())
                .build();

        version = CommandSpec.builder()
                .permission("kse.version")
                .description(Text.of("Show the version of KSE"))
                .arguments(GenericArguments.none())
                .executor(new VersionExecutor())
                .build();


    }

    public void init(KaroglanSignEditor plugin) {
        CommandManager cmdManager = Sponge.getCommandManager();
        cmdManager.register(plugin, this.get(), "kse", "sign", "signeditor", "se");
        translator = kse.getTranslator();
        translator.logInfo("reportBug");
        translator.logInfo("github");
    }

    public CommandCallable get() {
        return CommandSpec.builder()
                .description(Text.of("KSE's main command."))
                .child(set, "set")
                .child(clear, "clear")
                .child(copy, "copy")
                .child(clipboard, "clipboard", "clip", "cb")
                .child(paste, "paste")
                .child(swap, "swap", "sw")
                .child(reload, "reload", "r")
                .child(version, "version", "ver", "v")
                .child(undo, "undo")
                .child(redo, "redo")
                .child(trust,"trust","t")
                .executor(new MainExecutor())
                .arguments(GenericArguments.none())
                .build();
    }
}
