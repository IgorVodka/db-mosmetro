package vodka.igor.mosmetro.ui;

import vodka.igor.mosmetro.logic.MetroManager;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UIConditions {
    private static boolean can(String permission) {
        return MetroManager.getInstance().getAccessGroup().can(permission);
    }

}
