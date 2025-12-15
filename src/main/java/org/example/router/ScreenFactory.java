package org.example.router;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *Tạo và quản lý
 */
public class ScreenFactory {

    private final Map<String, Supplier<JPanel>> suppliers = new HashMap<>();
    private final Map<String, JPanel> cache = new HashMap<>();

    public void register(String cardKey, Supplier<JPanel> supplier) {
        suppliers.put(cardKey, supplier);
    }

    public JPanel get(String cardKey) {
        return cache.computeIfAbsent(cardKey, ck -> {
            Supplier<JPanel> sup = suppliers.get(ck);
            if (sup == null) return new JPanel(); // fallback
            return sup.get();
        });
    }

    public boolean has(String cardKey) {
        return suppliers.containsKey(cardKey);
    }
}
