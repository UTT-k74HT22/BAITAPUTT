package org.example.router;

import java.util.function.Consumer;

public final class ScreenNavigator {

    private static Consumer<String> menuRouter;

    private ScreenNavigator() {}

    /** MainFrame inject vào */
    public static void init(Consumer<String> router) {
        menuRouter = router;
    }

    /** Điều hướng theo MENU_KEY (CHUẨN) */
    public static void showByMenu(String menuKey) {
        if (menuRouter == null) {
            throw new IllegalStateException("ScreenNavigator not initialized");
        }
        menuRouter.accept(menuKey);
    }
}
