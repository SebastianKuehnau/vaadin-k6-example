package com.example.application.views.advanced;

import com.example.application.services.StockDataService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jspecify.annotations.NonNull;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import reactor.core.Disposable;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@PageTitle("Advanced Use Cases")
@Route(value = "advanced")
@Menu(order = 2, icon = LineAwesomeIconUrl.TOOLS_SOLID)
@Uses(Icon.class)
public class AdvancedView extends VerticalLayout {

    public static final String CURRENT_PRICE_FORMAT = "Current Price: %s €";
    private final StockDataService stockDataService;
    private final Span realtimeSpan;
    private final Span resultSpan;
    private Disposable subscription;

    public AdvancedView(StockDataService stockDataService) {
        this.stockDataService = stockDataService;

        // Async data
        add(new H3("Async Data"));
        resultSpan = new Span();

        var asyncButton = new Button("Fetch Async Data", this::asyncButtonClicked);
        add(new HorizontalLayout(Alignment.BASELINE, asyncButton, resultSpan));

        //Realtime Data
        add(new H3("Realtime Stock Price"));
        realtimeSpan = new Span(String.format(CURRENT_PRICE_FORMAT, BigDecimal.ZERO));
        add(realtimeSpan);

        // Container for dnd
        add(new H3("Drag and Drop Example"));
        add(createDndLayout());
    }

    private void asyncButtonClicked(ClickEvent<Button> e) {

            // Get UI reference before async operation
            UI ui = e.getSource().getUI().orElseThrow();
            Button button = e.getSource();

            // Show loading state immediately (before async task starts)
            resultSpan.setText("please wait ...");
            button.setEnabled(false);

            // Start async task in background
            CompletableFuture
                    .supplyAsync(stockDataService::longRunningTask, Executors.newVirtualThreadPerTaskExecutor())
                    .thenAccept(result ->
                            ui.access(() -> {
                                resultSpan.setText(result);
                                button.setEnabled(true);
                            }));
            }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI ui = attachEvent.getUI();

        // Hook up to service for live updates
        subscription =
                stockDataService
                        .getStockPrice()
                        .subscribe(
                                price -> {
                                    ui.access(
                                            () -> realtimeSpan.setText(String.format(CURRENT_PRICE_FORMAT, price))
                                    );
                                }
                        );
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cancel subscription when the view is detached
        subscription.dispose();

        super.onDetach(detachEvent);
    }

    private @NonNull HorizontalLayout createDndLayout() {
        var dndLayout = new HorizontalLayout();

        // Container for the draggable boxes
        FlexLayout sourceContainer = new FlexLayout();
        sourceContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        sourceContainer.addClassName("source-container");

        // Create multiple draggable boxes
        Div box1 = createDraggableBox("Box 1", "box-blue");
        Div box2 = createDraggableBox("Box 2", "box-red");
        Div box3 = createDraggableBox("Box 3", "box-green");

        sourceContainer.add(box1, box2, box3);

        // Create drop target
        Div dropZone = new Div();
        dropZone.add(new Span("Drop-Zone: Drop the box here"));
        dropZone.addClassName("drop-zone");

        // Configure DropTarget
        DropTarget<Div> dropTarget = DropTarget.create(dropZone);
        dropTarget.addDropListener(event ->
            event.getDragSourceComponent().ifPresent(component -> {
                String boxName = component.getElement().getText();
                Notification.show(
                        "'" + boxName + "' successfully dropped!"
                );

                // Visual feedback: move box into the drop zone
                dropZone.removeAll();
                dropZone.add((Div) component);
            })
        );

        dndLayout.add(sourceContainer, dropZone);
        dndLayout.setFlexGrow(1, sourceContainer);
        dndLayout.setSpacing(true);
        dndLayout.setPadding(false);
        return dndLayout;
    }

    private Div createDraggableBox(String name, String colorClass) {
        Div box = new Div();
        box.setText(name);
        box.addClassNames("draggable-box", colorClass);

        // Configure as DragSource
        DragSource<Div> dragSource = DragSource.create(box);
        dragSource.setDragData(name);
        dragSource.addDragStartListener(e -> box.addClassName("dragging"));
        dragSource.addDragEndListener(e -> box.removeClassName("dragging"));

        return box;
    }
}
