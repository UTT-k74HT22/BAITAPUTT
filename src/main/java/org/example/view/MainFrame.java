package org.example.view;

import org.example.controller.ProfileController;
import org.example.dto.response.ProfileResponse;
import org.example.entity.enums.Role;
import org.example.repository.CustomerProfileRepository;
import org.example.repository.EmployeeProfileRepository;
import org.example.repository.UserProfileRepository;
import org.example.repository.AccountRepository;
import org.example.repository.impl.CustomerProfileRepositoryImpl;
import org.example.repository.impl.EmployeeProfileRepositoryImpl;
import org.example.repository.impl.UserProfileRepositoryImpl;
import org.example.repository.impl.AccountRepositoryImpl;

import org.example.router.*;
import org.example.service.ProfileService;
import org.example.service.impl.ProfileServiceImpl;
import org.example.service.AuthService;
import org.example.service.impl.AuthServiceImpl;
import org.example.view.layout.Footer;
import org.example.view.layout.Header;
import org.example.view.layout.SideBar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.Objects;
import static org.example.utils.SideBarConstraint.*;

public class MainFrame extends JFrame {

    private final Header header;
    private final SideBar sideBar;

    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    private JPanel profilePanel;

    private String currentModuleTitle = "Dashboard";

    private final Map<String, AppRoutes.ScreenSpec> routes;
    private final ScreenFactory screenFactory = new ScreenFactory();

    public MainFrame(String username, Role role) {
        setTitle("HMS - Minh Châu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        header = new Header(username, role);
        sideBar = new SideBar(role);

        JScrollPane sideBarScroll = new JScrollPane(sideBar);
        sideBarScroll.setBorder(null);
        sideBarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sideBarScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel bgContent = new JPanel(new BorderLayout());
        bgContent.setBackground(new Color(237, 239, 252));
        bgContent.setBorder(new EmptyBorder(18, 24, 18, 24));

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
// Khởi tạo Repository, Service, Controller
        UserProfileRepository userRepo = new UserProfileRepositoryImpl();
        EmployeeProfileRepository empRepo = new EmployeeProfileRepositoryImpl();
        CustomerProfileRepository custRepo = new CustomerProfileRepositoryImpl();
        AccountRepository accRepo = new AccountRepositoryImpl();

        ProfileService profileService = new ProfileServiceImpl(custRepo, empRepo, userRepo, accRepo);
        ProfileController profileController = new ProfileController(profileService);

// Load profile hiện tại
        ProfileResponse profile = profileController.loadProfile(username, role);

        long accountId = AuthServiceImpl.getCurrentAccount().getId();

// Tạo ProfilePanel với dữ liệu thật
        profilePanel = new ProfilePanel(profile, profileController, accountId);
        contentPanel.add(wrapInCard(profilePanel), "PROFILE");



        ScreenNavigator.init(this::showByMenuKey);
        routes = AppRoutes.build();
        ScreenRegistry.registerAll(screenFactory);
        /**
         * Chỉ khách hàng mới có menu shop (test)
         * */
        if (role == Role.CUSTOMER) {
            CustomerScreenRegistry.registerAll(screenFactory);
        }

        initScreens(role);

        bgContent.add(contentPanel, BorderLayout.CENTER);

        add(new JScrollPane(sideBar), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(bgContent, BorderLayout.CENTER);
        add(new Footer(), BorderLayout.SOUTH);

        initMenuActions(role);
        initHeaderActions();
    }

    // ===================== SCREENS =====================
    private void initScreens(Role role) {
        for (AppRoutes.ScreenSpec spec : routes.values()) {
            if (spec.cardKey() == null) continue;

            JPanel inner = screenFactory.has(spec.cardKey())
                    ? screenFactory.get(spec.cardKey())
                    : createPlaceholder(spec.title());

            contentPanel.add(wrapInCard(inner), spec.cardKey());
        }

        // default screen theo role
        if (role == Role.CUSTOMER) showByMenuKey(MENU_SHOP_PRODUCTS);
        else showByMenuKey(MENU_DASHBOARD);
    }

    private JPanel createPlaceholder(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(new Color(120, 120, 160));

        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapInCard(JComponent inner) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 22, 22, 22));
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    // ===================== MENU ACTIONS =====================
    private void initMenuActions(Role role) {
        for (String menuKey : routes.keySet()) {
            JButton btn = sideBar.getMenu(menuKey);
            if (btn == null) continue; // role không có menu đó

            if (Objects.equals(menuKey, MENU_LOGOUT)) {
                btn.addActionListener(e -> doLogout());
            } else {
                btn.addActionListener(e -> showByMenuKey(menuKey));
            }
        }

        // đảm bảo đúng role
        if (role == Role.CUSTOMER) showByMenuKey(MENU_SHOP_PRODUCTS);
        else showByMenuKey(MENU_DASHBOARD);
    }

    private void doLogout() {
        dispose();
    }

    // ===================== HEADER ACTIONS =====================
    private void initHeaderActions() {
        header.getBtnProfile().addActionListener(e -> {
            cardLayout.show(contentPanel, "PROFILE");
            header.setModuleTitle("Profile");
        });
    }

    // ===================== ROUTER =====================
    private void showByMenuKey(String menuKey) {
        AppRoutes.ScreenSpec spec = routes.get(menuKey);
        if (spec == null || spec.cardKey() == null) return;
        showScreen(spec.cardKey(), spec.title(), menuKey);
    }

    private void showScreen(String cardKey, String moduleTitle, String activeMenuKey) {
        currentModuleTitle = moduleTitle;
        header.setModuleTitle(moduleTitle);
        sideBar.setActiveMenu(activeMenuKey);
        cardLayout.show(contentPanel, cardKey);
    }

    // ===================== RoundedPanel (GIỮ UI) =====================
    static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setColor(new Color(0, 0, 0, 18));
            graphics.fillRoundRect(4, 4, width - 8, height - 8, arcs.width, arcs.height);

            graphics.setColor(backgroundColor);
            graphics.fillRoundRect(0, 0, width - 8, height - 8, arcs.width, arcs.height);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame("minhchau", Role.ADMIN);
            frame.setVisible(true);
        });
    }
}
