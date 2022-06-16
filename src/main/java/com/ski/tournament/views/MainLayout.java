package com.ski.tournament.views;

import com.ski.tournament.config.UserRole;
import com.ski.tournament.model.Person;
import com.ski.tournament.service.PersonService;
import com.ski.tournament.views.classification.ClassificationManagementView;
import com.ski.tournament.views.classification.ClassificationsView;
import com.ski.tournament.views.persons.PersonsManagementView;
import com.ski.tournament.views.places.PlacesManagementView;
import com.ski.tournament.views.places.PlacesView;
import com.ski.tournament.views.security.AccountSettingsForm;
import com.ski.tournament.views.signs.SignsManagementView;
import com.ski.tournament.views.tournaments.TournamentManagementView;
import com.ski.tournament.views.tournaments.TournamentsView;
import com.ski.tournament.views.units.UnitsManagementView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The main view is a top-level placeholder for other views.
 */
@PageTitle("Main")
public class MainLayout extends AppLayout {

    public static class MenuItemInfo {

        private String text;
        private String iconClass;
        private Class<? extends Component> view;

        public MenuItemInfo(String text, String iconClass, Class<? extends Component> view) {
            this.text = text;
            this.iconClass = iconClass;
            this.view = view;
        }

        public String getText() {
            return text;
        }

        public String getIconClass() {
            return iconClass;
        }

        public Class<? extends Component> getView() {
            return view;
        }

    }

    private final Tabs menu;
    private H1 viewTitle;
    private Person currentUser;


    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));

    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setClassName("sidemenu-header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        viewTitle = new H1();
        layout.add(viewTitle);

        MenuBar menuBar = new MenuBar();
        menuBar.addClassNames("ms-auto", "me-m");
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);
        MenuItem share = createIconItem(menuBar, VaadinIcon.USER, "", null);
        SubMenu shareSubMenu = share.getSubMenu();

        createIconItem(shareSubMenu, VaadinIcon.COG, "Ustawienia", null, true).addClickListener( e -> {
            getUI().ifPresent(ui ->
                    ui.navigate(
                            AccountSettingsForm.class));
        });
        createIconItem(shareSubMenu, VaadinIcon.POWER_OFF, "Wyloguj", null, true).addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/logout");
        });

        layout.add(menuBar);

        return layout;
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label, String ariaLabel) {
        return createIconItem(menu, iconName, label, ariaLabel, false);
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label, String ariaLabel, boolean isChild) {
        Icon icon = new Icon(iconName);

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }

        MenuItem item = menu.addItem(icon, e -> {
        });

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setClassName("sidemenu-menu");
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        Image logo = new Image("images/pk_logo.png", "Ski Tournament logo");
        logo.setSizeFull();
        logoLayout.add(logo);
        layout.add(logoLayout, menu);
        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        for (Tab menuTab : createMenuItems()) {
            tabs.add(menuTab);
        }
        return tabs;

    }

    private List<Tab> createMenuItems() {
        MenuItemInfo[] menuItems = new MenuItemInfo[]{ //

                new MenuItemInfo("Zawody", "la la-skiing", TournamentsView.class), //

                new MenuItemInfo("Miejsca", "la la-globe", PlacesView.class), //

                new MenuItemInfo("Klasyfikacje", "la la-star-o", ClassificationsView.class), //

                new MenuItemInfo("Zarządzanie użytkownikami", "la la-users", PersonsManagementView.class), //

                new MenuItemInfo("Zarządzanie zapisami", "la la-file-signature", SignsManagementView.class), //

                new MenuItemInfo("Zarządzanie miejscami", "la la-columns", PlacesManagementView.class), //

                new MenuItemInfo("Zarządzanie jednostkami", "la la-university", UnitsManagementView.class), //

                new MenuItemInfo("Zarządzanie zawodami", "la la-skiing-nordic", TournamentManagementView.class), //

                new MenuItemInfo("Zarządzanie klasyfikacjami", "la la-star", ClassificationManagementView.class), //

            //    -th-list

        };
        List<Tab> tabs = new ArrayList<>();
        for (MenuItemInfo menuItemInfo : menuItems) {
            tabs.add(createTab(menuItemInfo));

        }
        return tabs;
    }

    private static Tab createTab(MenuItemInfo menuItemInfo) {
        Tab tab = new Tab();
        RouterLink link = new RouterLink();
        link.setRoute(menuItemInfo.getView());
        Span iconElement = new Span();
        iconElement.addClassNames("text-l", "pr-s");
        if (!menuItemInfo.getIconClass().isEmpty()) {
            iconElement.addClassNames(menuItemInfo.getIconClass());
        }
        link.add(iconElement, new Text(menuItemInfo.getText()));
        tab.add(link);
        ComponentUtil.setData(tab, Class.class, menuItemInfo.getView());
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());

        currentUser = PersonService.getCurrentLoggedUser();
        setMenuItemsVisibleByRoles();
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private void setMenuItemsVisibleByRoles() {
        List<Component> tabs = menu.getChildren().collect(Collectors.toList());

        boolean userRole =  currentUser.getAuthorities().contains(UserRole.ROLE_User);
        boolean staffRole =  currentUser.getAuthorities().contains(UserRole.ROLE_Staff);
        boolean adminRole =  currentUser.getAuthorities().contains(UserRole.ROLE_Admin);

        tabs.get(0).setVisible(userRole);
        tabs.get(1).setVisible(userRole);
        tabs.get(2).setVisible(userRole);

        tabs.get(3).setVisible(adminRole);

        tabs.get(4).setVisible(staffRole);
        tabs.get(5).setVisible(staffRole);
        tabs.get(6).setVisible(staffRole);
        tabs.get(7).setVisible(staffRole);
        tabs.get(8).setVisible(staffRole);
    }
}
