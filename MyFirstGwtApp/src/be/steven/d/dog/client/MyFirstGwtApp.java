package be.steven.d.dog.client;

import be.steven.d.dog.internationalization.MyFirstGwtAppConstants;
import be.steven.d.dog.internationalization.MyFirstGwtAppMessages;
import be.steven.d.dog.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MyFirstGwtApp implements EntryPoint {
    private static final int REFRESH_INTERVAL = 5000;

    private final MyFirstGwtAppConstants constants = GWT.create(MyFirstGwtAppConstants.class);
    private final MyFirstGwtAppMessages messages = GWT.create(MyFirstGwtAppMessages.class);

    private StockPriceServiceAsync stockPriceService = GWT.create(StockPriceService.class);

    private final VerticalPanel mainPanel = new VerticalPanel();
    private final FlexTable stocksFlexTable = new FlexTable();
    private final HorizontalPanel addPanel = new HorizontalPanel();
    private final TextBox newSymbolTextBox = new TextBox();
    private final Button addStockButton = new Button(constants.add());
    private final Label lastUpdatedLabel = new Label();
    private final List<String> stocks = new ArrayList<String>();

    public void onModuleLoad() {
        // Set the window title, the header text, and the Add button text.
        Window.setTitle(constants.stonkWatcher());
        RootPanel.get("appTitle").add(new Label(constants.stonkWatcher()));

        // Create table for stock data.
        stocksFlexTable.setText(0, 0, constants.symbol());
        stocksFlexTable.setText(0, 1, constants.price());
        stocksFlexTable.setText(0, 2, constants.change());
        stocksFlexTable.setText(0, 3, constants.remove());

        // Add styles to elements in the stock list table.
        stocksFlexTable.setCellPadding(6);

        // Add styles to elements in the stock list table.
        stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
        stocksFlexTable.addStyleName("watchList");
        stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");

        // Assemble Add Stock panel.
        newSymbolTextBox.addStyleName("textField");
        addStockButton.addStyleName("addButton");
        addPanel.add(newSymbolTextBox);
        addPanel.add(addStockButton);
        addPanel.addStyleName("addPanel");

        // Assemble Main panel.
        mainPanel.add(stocksFlexTable);
        mainPanel.add(addPanel);
        mainPanel.add(lastUpdatedLabel);

        // Associate the Main panel with the HTML host page.
        RootPanel.get("stockList").add(mainPanel);

        // Move cursor focus to the input box.
        newSymbolTextBox.setFocus(true);

        // Setup timer to refresh list automatically.
        Timer refreshTimer = new Timer() {
            @Override
            public void run() {
                refreshWatchList();
            }
        };
        refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

        // Listen for mouse events on the Add button.
        addStockButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addStock();
            }
        });

        newSymbolTextBox.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    addStock();
                }
            }
        });
    }

    /**
     * Generate random stock prices.
     */
    private void refreshWatchList() {
        // Initialize the service proxy.
        if (stockPriceService == null) {
            stockPriceService = GWT.create(StockPriceService.class);
        }

        // Set up the callback object.
        AsyncCallback<StockPrice[]> callback = new AsyncCallback<StockPrice[]>() {
            public void onFailure(Throwable e) {
                e.printStackTrace();
            }

            public void onSuccess(StockPrice[] result) {
                updateTable(result);
            }
        };

        // Make the call to the stock price service.
        stockPriceService.getPrices(stocks.toArray(new String[0]), callback);
    }

    /**
     * Update the Price and Change fields all the rows in the stock table.
     *
     * @param prices Stock data for all rows.
     */
    private void updateTable(StockPrice[] prices) {
        for (StockPrice price : prices) {
            updateTable(price);
        }

        // Display timestamp showing last refresh.
//        DateTimeFormat dateFormat = DateTimeFormat.getFormat(
//                DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        lastUpdatedLabel.setText(messages.lastUpdate(new Date()));
    }

    /**
     * Update a single row in the stock table.
     *
     * @param price Stock data for a single row.
     */
    private void updateTable(StockPrice price) {
        // Make sure the stock is still in the stock table.
        if (!stocks.contains(price.getSymbol())) {
            return;
        }

        int row = stocks.indexOf(price.getSymbol()) + 1;

        // Format the data in the Price and Change fields.
        String priceText = NumberFormat.getFormat("#,##0.00").format(
                price.getPrice());
        NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
        String changeText = changeFormat.format(price.getChange());
        String changePercentText = changeFormat.format(price.getChangePercent());

        // Populate the Price and Change fields with new data.
        stocksFlexTable.setText(row, 1, priceText);
        Label changeWidget = (Label) stocksFlexTable.getWidget(row, 2);
        changeWidget.setText(changeText + " (" + changePercentText + "%)");

        // Change the color of text in the Change field based on its value.
        String changeStyleName = "noChange";
        if (price.getChangePercent() < -0.1f) {
            changeStyleName = "negativeChange";
        } else if (price.getChangePercent() > 0.1f) {
            changeStyleName = "positiveChange";
        }

        changeWidget.setStyleName(changeStyleName);
    }

    private void addStock() {
        final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
        newSymbolTextBox.setFocus(true);

        // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
        if (FieldVerifier.isInvalidTickerSymbol(symbol)) {
            Window.alert(messages.invalidSymbol(symbol));
            newSymbolTextBox.selectAll();
            return;
        }

        newSymbolTextBox.setText("");

        // Don't add the stock if it's already in the table.
        if (stocks.contains(symbol))
            return;

        // Add the stock to the table.
        int row = stocksFlexTable.getRowCount();
        stocks.add(symbol);
        stocksFlexTable.setText(row, 0, symbol);
        stocksFlexTable.setWidget(row, 2, new Label());
        stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");

        // Add a button to remove this stock from the table.
        Button removeStockButton = new Button("x");
        removeStockButton.addStyleDependentName("remove");
        removeStockButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                int removedIndex = stocks.indexOf(symbol);
                stocks.remove(removedIndex);
                stocksFlexTable.removeRow(removedIndex + 1);
            }
        });
        stocksFlexTable.setWidget(row, 3, removeStockButton);
    }
}