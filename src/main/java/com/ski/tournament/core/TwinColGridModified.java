package com.ski.tournament.core;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.CssImport.Container;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridNoneSelectionModel;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Container({@CssImport(
        value = "./styles/multiselect-cb-hide.css",
        themeFor = "vaadin-grid"
), @CssImport("./styles/twin-col-grid-button.css")})
public class TwinColGridModified<T> extends VerticalLayout implements HasValue<ValueChangeEvent<Set<T>>, Set<T>>, HasComponents, HasSize {
    private final TwinColGridModified.TwinColModel<T> left;
    private final TwinColGridModified.TwinColModel<T> right;
    /** @deprecated */
    @Deprecated
    protected final Grid<T> leftGrid;
    /** @deprecated */
    @Deprecated
    protected final Grid<T> rightGrid;
    /** @deprecated */
    @Deprecated
    protected ListDataProvider<T> leftGridDataProvider;
    /** @deprecated */
    @Deprecated
    protected ListDataProvider<T> rightGridDataProvider;
    private final Button addAllButton;
    private final Button addButton;
    private final Button removeButton;
    private final Button removeAllButton;
    private Component buttonContainer;
    private Grid<T> draggedGrid;
    private Label fakeButtonContainerLabel;
    private com.ski.tournament.core.TwinColGridModified.Orientation orientation;

    public TwinColGridModified() {
        this((ListDataProvider)DataProvider.ofCollection(new LinkedHashSet()), (String)null);
    }

    public TwinColGridModified(ListDataProvider<T> dataProvider, String caption) {
        this.addAllButton = this.createActionButton();
        this.addButton = this.createActionButton();
        this.removeButton = this.createActionButton();
        this.removeAllButton = this.createActionButton();
        this.fakeButtonContainerLabel = new Label();
        this.orientation = com.ski.tournament.core.TwinColGridModified.Orientation.HORIZONTAL;
        this.left = new com.ski.tournament.core.TwinColGridModified.TwinColModel();
        this.right = new com.ski.tournament.core.TwinColGridModified.TwinColModel();
        this.leftGrid = this.left.grid;
        this.rightGrid = this.right.grid;
        this.setMargin(false);
        this.setPadding(false);
        if (caption != null) {
            this.add(new Component[]{new Label(caption)});
        }

        this.setDataProvider(dataProvider);
        this.rightGridDataProvider = DataProvider.ofCollection(new LinkedHashSet());
        this.getRightGrid().setDataProvider(this.rightGridDataProvider);
        this.getLeftGrid().setWidth("100%");
        this.getRightGrid().setWidth("100%");
        this.addAllButton.addClickListener((e) -> {
            List<T> filteredItems = (List)this.left.getDataProvider().withConfigurableFilter().fetch(new Query()).collect(Collectors.toList());
            this.updateSelection(new LinkedHashSet(filteredItems), new HashSet());
        });
        this.addButton.addClickListener((e) -> {
            this.updateSelection(new LinkedHashSet(this.getLeftGrid().getSelectedItems()), new HashSet());
        });
        this.removeButton.addClickListener((e) -> {
            this.updateSelection(new HashSet(), this.getRightGrid().getSelectedItems());
        });
        this.removeAllButton.addClickListener((e) -> {
            List<T> filteredItems = (List)this.right.getDataProvider().withConfigurableFilter().fetch(new Query()).collect(Collectors.toList());
            this.updateSelection(new HashSet(), new HashSet(filteredItems));
        });
        this.getElement().getStyle().set("display", "flex");
        this.forEachSide((side) -> {
            side.grid.setSelectionMode(SelectionMode.MULTI);
            side.columnLabel.setVisible(false);
            side.layout.setSizeFull();
            side.layout.setMargin(false);
            side.layout.setPadding(false);
            side.layout.setSpacing(false);
        });
        this.add(new Component[]{this.createContainerLayout()});
        this.setSizeUndefined();
    }

    public com.ski.tournament.core.TwinColGridModified<T> withOrientation(com.ski.tournament.core.TwinColGridModified.Orientation orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            this.updateContainerLayout();
        }

        return this;
    }

    public com.ski.tournament.core.TwinColGridModified.Orientation getOrientation() {
        return this.orientation;
    }

    private void updateContainerLayout() {
        Component oldContainerComponent = (Component)this.left.layout.getParent().get();
        Component newContainerComponent = this.createContainerLayout();
        this.replace(oldContainerComponent, newContainerComponent);
    }

    private Component createContainerLayout() {
        return (Component)(this.orientation == com.ski.tournament.core.TwinColGridModified.Orientation.VERTICAL ? this.createVerticalContainer() : this.createHorizontalContainer());
    }

    private HorizontalLayout createHorizontalContainer() {
        this.buttonContainer = this.getVerticalButtonContainer();
        HorizontalLayout hl = new HorizontalLayout(new Component[]{this.left.layout, this.buttonContainer, this.right.layout});
        hl.getElement().getStyle().set("min-height", "0px");
        hl.getElement().getStyle().set("flex", "1 1 0px");
        hl.setMargin(false);
        hl.setWidthFull();
        return hl;
    }

    private VerticalLayout createVerticalContainer() {
        this.buttonContainer = this.getHorizontalButtonContainer();
        VerticalLayout vl = new VerticalLayout(new Component[]{this.left.layout, this.buttonContainer, this.right.layout});
        vl.getElement().getStyle().set("min-width", "0px");
        vl.getElement().getStyle().set("flex", "1 1 0px");
        vl.setMargin(false);
        vl.setPadding(false);
        vl.setHeightFull();
        return vl;
    }

    private VerticalLayout getVerticalButtonContainer() {
        this.addButton.setIcon(VaadinIcon.ANGLE_RIGHT.create());
        this.addAllButton.setIcon(VaadinIcon.ANGLE_DOUBLE_RIGHT.create());
        this.removeButton.setIcon(VaadinIcon.ANGLE_LEFT.create());
        this.removeAllButton.setIcon(VaadinIcon.ANGLE_DOUBLE_LEFT.create());
        this.fakeButtonContainerLabel.getElement().setProperty("innerHTML", "&nbsp;");
        this.fakeButtonContainerLabel.setVisible(false);
        VerticalLayout vButtonContainer = new VerticalLayout(new Component[]{this.fakeButtonContainerLabel, this.addAllButton, this.addButton, this.removeButton, this.removeAllButton});
        vButtonContainer.setPadding(false);
        vButtonContainer.setSpacing(false);
        vButtonContainer.setSizeUndefined();
        return vButtonContainer;
    }

    private HorizontalLayout getHorizontalButtonContainer() {
        this.addButton.setIcon(VaadinIcon.ANGLE_DOWN.create());
        this.addAllButton.setIcon(VaadinIcon.ANGLE_DOUBLE_DOWN.create());
        this.removeButton.setIcon(VaadinIcon.ANGLE_UP.create());
        this.removeAllButton.setIcon(VaadinIcon.ANGLE_DOUBLE_UP.create());
        HorizontalLayout hButtonContainer = new HorizontalLayout(new Component[]{this.addAllButton, this.addButton, this.removeButton, this.removeAllButton});
        hButtonContainer.setPadding(false);
        hButtonContainer.setSizeUndefined();
        return hButtonContainer;
    }

    public Grid<T> getLeftGrid() {
        return this.leftGrid;
    }

    public Grid<T> getRightGrid() {
        return this.rightGrid;
    }

    private void forEachSide(Consumer<com.ski.tournament.core.TwinColGridModified.TwinColModel<T>> consumer) {
        consumer.accept(this.left);
        consumer.accept(this.right);
    }

    public void setItems(Collection<T> items) {
        this.setDataProvider(DataProvider.ofCollection(items));
    }

    public void setItems(Stream<T> items) {
        this.setDataProvider(DataProvider.fromStream(items));
    }

    /** @deprecated */
    @Deprecated
    public void setLeftGridClassName(String classname) {
        this.getLeftGrid().setClassName(classname);
    }

    /** @deprecated */
    @Deprecated
    public void addLeftGridClassName(String classname) {
        this.getLeftGrid().addClassName(classname);
    }

    /** @deprecated */
    @Deprecated
    public void removeLeftGridClassName(String classname) {
        this.getLeftGrid().removeClassName(classname);
    }

    /** @deprecated */
    @Deprecated
    public void setRightGridClassName(String classname) {
        this.getRightGrid().setClassName(classname);
    }

    /** @deprecated */
    @Deprecated
    public void addRightGridClassName(String classname) {
        this.getRightGrid().addClassName(classname);
    }

    /** @deprecated */
    @Deprecated
    public void removeRightGridClassName(String classname) {
        this.getRightGrid().removeClassName(classname);
    }

    public void clearAll() {
        this.updateSelection(new HashSet(), new HashSet(this.right.getItems()));
    }

    private void setDataProvider(ListDataProvider<T> dataProvider) {
        this.leftGridDataProvider = dataProvider;
        this.getLeftGrid().setDataProvider(dataProvider);
        if (this.right.getDataProvider() != null) {
            this.right.getItems().clear();
            this.right.getDataProvider().refreshAll();
        }

    }

    public TwinColGridModified(Collection<T> options) {
        this((ListDataProvider)DataProvider.ofCollection(new LinkedHashSet(options)), (String)null);
    }

    public TwinColGridModified(Collection<T> options, String caption) {
        this(DataProvider.ofCollection(new LinkedHashSet(options)), caption);
    }

    public com.ski.tournament.core.TwinColGridModified<T> withRightColumnCaption(String rightColumnCaption) {
        this.right.columnLabel.setText(rightColumnCaption);
        this.right.columnLabel.setVisible(true);
        this.fakeButtonContainerLabel.setVisible(true);
        return this;
    }

    public com.ski.tournament.core.TwinColGridModified<T> withLeftColumnCaption(String leftColumnCaption) {
        this.left.columnLabel.setText(leftColumnCaption);
        this.left.columnLabel.setVisible(true);
        this.fakeButtonContainerLabel.setVisible(true);
        return this;
    }

    public com.ski.tournament.core.TwinColGridModified<T> addColumn(ItemLabelGenerator<T> itemLabelGenerator, String header) {
        this.getLeftGrid().addColumn(new TextRenderer(itemLabelGenerator)).setHeader(header);
        this.getRightGrid().addColumn(new TextRenderer(itemLabelGenerator)).setHeader(header);
        return this;
    }

    public com.ski.tournament.core.TwinColGridModified<T> addSortableColumn(ItemLabelGenerator<T> itemLabelGenerator, Comparator<T> comparator, String header) {
        this.forEachSide((side) -> {
            side.grid.addColumn(new TextRenderer(itemLabelGenerator)).setHeader(header).setComparator(comparator).setSortable(true);
        });
        return this;
    }

    public com.ski.tournament.core.TwinColGridModified<T> addSortableColumn(ItemLabelGenerator<T> itemLabelGenerator, Comparator<T> comparator, String header, String key) {
        this.forEachSide((side) -> {
            side.grid.addColumn(new TextRenderer(itemLabelGenerator)).setHeader(header).setComparator(comparator).setSortable(true).setKey(key);
        });
        return this;
    }

    public com.ski.tournament.core.TwinColGridModified<T> withoutAddAllButton() {
        this.addAllButton.setVisible(false);
        this.checkContainerVisibility();
        return this;
    }

    public com.ski.tournament.core.TwinColGridModified<T> withoutRemoveAllButton() {
        this.removeAllButton.setVisible(false);
        this.checkContainerVisibility();
        return this;
    }

    public com.ski.tournament.core.TwinColGridModified<T> withoutAddButton() {
        this.addButton.setVisible(false);
        this.checkContainerVisibility();
        return this;
    }

    public com.ski.tournament.core.TwinColGridModified<T> withoutRemoveButton() {
        this.removeButton.setVisible(false);
        this.checkContainerVisibility();
        return this;
    }

    private void checkContainerVisibility() {
        boolean atLeastOneIsVisible = this.removeButton.isVisible() || this.addButton.isVisible() || this.removeAllButton.isVisible() || this.addAllButton.isVisible();
        this.buttonContainer.setVisible(atLeastOneIsVisible);
    }

    public TwinColGridModified<T> withSizeFull() {
        this.setWidthFull();
        this.getElement().getStyle().set("flex-grow", "1");
        return this;
    }

    public TwinColGridModified<T> withDragAndDropSupport() {
        this.configDragAndDrop(this.left, this.right);
        this.configDragAndDrop(this.right, this.left);
        return this;
    }

    public String getRightColumnCaption() {
        return this.right.columnLabel.getText();
    }

    public String getLeftColumnCaption() {
        return this.left.columnLabel.getText();
    }

    public void setValue(Set<T> value) {
        Objects.requireNonNull(value);
        Set<T> newValues = (Set)value.stream().map(Objects::requireNonNull).collect(Collectors.toCollection(LinkedHashSet::new));
        this.updateSelection(newValues, new LinkedHashSet(this.getLeftGrid().getSelectedItems()));
    }

    public Set<T> getValue() {
        return Collections.unmodifiableSet((Set)this.collectValue(Collectors.toCollection(LinkedHashSet::new)));
    }

    <C> C collectValue(Collector<T, ?, C> collector) {
        Stream<T> stream = this.right.getItems().stream();
        SerializableComparator<T> comparator = this.right.grid.createSortingComparator();
        return comparator != null ? stream.sorted(comparator).collect(collector) : stream.collect(collector);
    }

    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<Set<T>>> listener) {
        return this.right.getDataProvider().addDataProviderListener((e) -> {
            ComponentValueChangeEvent<com.ski.tournament.core.TwinColGridModified<T>, Set<T>> e2 = new ComponentValueChangeEvent(this, this, (Object)null, true);
            listener.valueChanged(e2);
        });
    }

    public boolean isReadOnly() {
        return this.isReadOnly();
    }

    public boolean isRequiredIndicatorVisible() {
        return this.isRequiredIndicatorVisible();
    }

    public void setReadOnly(boolean readOnly) {
        this.getLeftGrid().setSelectionMode(readOnly ? SelectionMode.NONE : SelectionMode.MULTI);
        this.getRightGrid().setSelectionMode(readOnly ? SelectionMode.NONE : SelectionMode.MULTI);
        this.addButton.setEnabled(!readOnly);
        this.removeButton.setEnabled(!readOnly);
        this.addAllButton.setEnabled(!readOnly);
        this.removeAllButton.setEnabled(!readOnly);
    }

    public void setRequiredIndicatorVisible(boolean visible) {
        this.setRequiredIndicatorVisible(visible);
    }

    protected void updateSelection(Set<T> addedItems, Set<T> removedItems) {
        this.left.getItems().addAll(removedItems);
        this.left.getItems().removeAll(addedItems);
        this.right.getItems().addAll(addedItems);
        this.right.getItems().removeAll(removedItems);
        refreshGrid();
    }

    protected void refreshGrid(){
        this.forEachSide((side) -> {
            side.getDataProvider().refreshAll();
            side.grid.getSelectionModel().deselectAll();
        });
    }

    private void configDragAndDrop(TwinColGridModified.TwinColModel<T> sourceModel, TwinColGridModified.TwinColModel<T> targetModel) {
        Set<T> draggedItems = new LinkedHashSet();
        sourceModel.grid.setRowsDraggable(true);
        sourceModel.grid.addDragStartListener((event) -> {
            this.draggedGrid = null;
            if (!(sourceModel.grid.getSelectionModel() instanceof GridNoneSelectionModel)) {
                draggedItems.addAll(event.getDraggedItems());
            }

            targetModel.grid.setDropMode(GridDropMode.ON_GRID);
        });
        sourceModel.grid.addDragEndListener((event) -> {
            if (targetModel.droppedInsideGrid && sourceModel.grid == this.draggedGrid) {
                if (this.draggedGrid == null) {
                    draggedItems.clear();
                    return;
                }

                ListDataProvider<T> dragGridSourceDataProvider = sourceModel.getDataProvider();
                dragGridSourceDataProvider.getItems().removeAll(draggedItems);
                dragGridSourceDataProvider.refreshAll();
                targetModel.droppedInsideGrid = false;
                this.draggedGrid = null;
                draggedItems.clear();
                sourceModel.grid.deselectAll();
            } else {
                draggedItems.clear();
            }

        });
        targetModel.grid.addDropListener((event) -> {
            this.draggedGrid = sourceModel.grid;
            targetModel.droppedInsideGrid = true;
            ListDataProvider<T> dragGridTargetDataProvider = targetModel.getDataProvider();
            dragGridTargetDataProvider.getItems().addAll(draggedItems);
            dragGridTargetDataProvider.refreshAll();
        });
    }

    /** @deprecated */
    @Deprecated
    public void addLeftGridSelectionListener(SelectionListener<Grid<T>, T> listener) {
        this.getLeftGrid().addSelectionListener(listener);
    }

    /** @deprecated */
    @Deprecated
    public void addRightGridSelectionListener(SelectionListener<Grid<T>, T> listener) {
        this.getRightGrid().addSelectionListener(listener);
    }

    public com.ski.tournament.core.TwinColGridModified<T> addFilterableColumn(ItemLabelGenerator<T> itemLabelGenerator, SerializableFunction<T, String> filterableValue, String header, String filterPlaceholder, boolean enableClearButton) {
        this.forEachSide((side) -> {
            Column<T> column = side.grid.addColumn(new TextRenderer(itemLabelGenerator)).setHeader(header);
            TextField filterTF = new TextField();
            filterTF.setClearButtonVisible(enableClearButton);
            filterTF.addValueChangeListener((event) -> {
                side.getDataProvider().addFilter((filterableEntity) -> {
                    return StringUtils.containsIgnoreCase((CharSequence)filterableValue.apply(filterableEntity), filterTF.getValue());
                });
            });
            if (side.headerRow == null) {
                side.headerRow = side.grid.appendHeaderRow();
            }

            ((HeaderCell)side.headerRow.getCell(column)).setComponent(filterTF);
            filterTF.setValueChangeMode(ValueChangeMode.EAGER);
            filterTF.setSizeFull();
            filterTF.setPlaceholder(filterPlaceholder);
        });
        return this;
    }

    public com.ski.tournament.core.TwinColGridModified<T> addFilterableColumn(ItemLabelGenerator<T> itemLabelGenerator, String header, String filterPlaceholder, boolean enableClearButton) {
        return this.addFilterableColumn(itemLabelGenerator, itemLabelGenerator, header, filterPlaceholder, enableClearButton);
    }

    public com.ski.tournament.core.TwinColGridModified<T> selectRowOnClick() {
        this.forEachSide((side) -> {
            side.grid.addClassName("hide-selector-col");
            side.grid.addItemClickListener((c) -> {
                if (side.grid.getSelectedItems().contains(c.getItem())) {
                    side.grid.deselect(c.getItem());
                } else {
                    side.grid.select(c.getItem());
                }

            });
        });
        return this;
    }

    public HasValue<? extends ValueChangeEvent<List<T>>, List<T>> asList() {
        return new TwinColGridListAdapterModified(this);
    }

    public Set<T> getEmptyValue() {
        return Collections.emptySet();
    }

    private Button createActionButton() {
        Button button = new Button();
        button.addThemeName("twin-col-grid-button");
        return button;
    }

    public static enum Orientation {
        HORIZONTAL,
        VERTICAL;

        private Orientation() {
        }
    }

    private static final class TwinColModel<T> implements Serializable {
        final TwinColGridModified.GridEx<T> grid;
        final Label columnLabel;
        final VerticalLayout layout;
        HeaderRow headerRow;
        boolean droppedInsideGrid;

        private TwinColModel() {
            this.grid = new TwinColGridModified.GridEx();
            this.columnLabel = new Label();
            this.layout = new VerticalLayout(new Component[]{this.columnLabel, this.grid});
            this.droppedInsideGrid = false;
        }

        ListDataProvider<T> getDataProvider() {
            return (ListDataProvider)this.grid.getDataProvider();
        }

        Collection<T> getItems() {
            return this.getDataProvider().getItems();
        }
    }

    private static final class GridEx<T> extends Grid<T> {
        private GridEx() {
        }

        protected SerializableComparator<T> createSortingComparator() {
            return super.createSortingComparator();
        }
    }
}
