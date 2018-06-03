package org.karoglan.tollainmear.signeditor.utils;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.IOException;

public class ClipBoardContents {
    private KSERecordsManager recordsManager;

    public void setTextArray(Text[] textArray) {
        this.textArray = textArray;
    }

    private Text[] textArray = new Text[4];

    public void put(Player player, Text[] rawArray) throws IOException {
        recordsManager = KaroglanSignEditor.getInstance().getKSERecordsManager();
        for (int i = 0; i < 4; i++) {
            textArray[i] = rawArray[i];
        }
        KSERecordsManager.getCopylist().put(player.getName(), this);
        recordsManager.saveOperationHistory();
    }

    public Text[] get() {
        return textArray;
    }
}
