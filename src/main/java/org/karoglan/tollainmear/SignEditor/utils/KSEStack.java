package org.karoglan.tollainmear.SignEditor.utils;

import org.karoglan.tollainmear.SignEditor.KSERecordsManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.Objects;


public class KSEStack {
    private KSERecordsManager recordsManager;
    //textStack[][0-3]will storage the Sign's text.
    private Text[][] textStack;
    private Integer now;
    private Integer tail;
    private Integer head;

    public KSEStack() {
        now = 0;
        head = 0;
        tail = 1;
        textStack = new Text[10][4];
    }

    public KSEStack set(Text[][] textStack, Integer now) {
        this.textStack = textStack;
        this.now = now;
        return this;
    }

    public void setNow(Integer now) {
        this.now = now;
    }

    public void add(Text[] textArray, Location<World> loc) throws IOException {
        now++;
        if (now == 10) {
            now = 0;
        }
        tail = now;
        if (tail.equals(head)) {
            head++;
        }
        int emptyStack = tail + 1 == 10 ? 0 : tail + 1;
        while (emptyStack != head) {
            for (int i = 0; i < 4; i++) {
                textStack[emptyStack][i] = Text.of("");
            }
            emptyStack++;
            if (emptyStack == 10) {
                emptyStack = 0;
            }
        }
        update(textArray, loc);
        KSERecordsManager.getOperationStack().put(loc, this);
        save();
    }

    public Text[][] getTextStack() {
        return textStack;
    }

    public Text[] getTextStack(Integer now) {
        Text[] textArray = new Text[4];
        for (int i = 0; i < 4; i++) {
            textArray[i] = textStack[now][i];
        }
        return textArray;
    }

    public Integer getNow() {
        return now;
    }

    public void update(Text[] textArray, Location<World> loc) throws IOException {
        recordsManager = KSERecordsManager.getInstance();

        for (int i = 0; i < 4; i++) {
            textStack[now][i] = textArray[i];
        }
        KSERecordsManager.getOperationStack().put(loc, this);
        save();
    }

    public Integer getTail() {
        return tail;
    }

    public Integer getHead() {
        return head;
    }

    public void save() throws IOException {
        recordsManager.save();
    }
}
