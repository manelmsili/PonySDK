/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *	Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *	Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ponysdk.sample.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.ponysdk.core.model.PUnit;
import com.ponysdk.core.server.application.UIContext;
import com.ponysdk.core.server.concurrent.PScheduler;
import com.ponysdk.core.ui.basic.Element;
import com.ponysdk.core.ui.basic.PAbsolutePanel;
import com.ponysdk.core.ui.basic.PAnchor;
import com.ponysdk.core.ui.basic.PButton;
import com.ponysdk.core.ui.basic.PCookies;
import com.ponysdk.core.ui.basic.PDateBox;
import com.ponysdk.core.ui.basic.PDockLayoutPanel;
import com.ponysdk.core.ui.basic.PFlowPanel;
import com.ponysdk.core.ui.basic.PFrame;
import com.ponysdk.core.ui.basic.PLabel;
import com.ponysdk.core.ui.basic.PListBox;
import com.ponysdk.core.ui.basic.PMenuBar;
import com.ponysdk.core.ui.basic.PRichTextArea;
import com.ponysdk.core.ui.basic.PScript;
import com.ponysdk.core.ui.basic.PStackLayoutPanel;
import com.ponysdk.core.ui.basic.PTabLayoutPanel;
import com.ponysdk.core.ui.basic.PTextBox;
import com.ponysdk.core.ui.basic.PTree;
import com.ponysdk.core.ui.basic.PTreeItem;
import com.ponysdk.core.ui.basic.PWebSocket;
import com.ponysdk.core.ui.basic.PWidget;
import com.ponysdk.core.ui.basic.PWindow;
import com.ponysdk.core.ui.basic.event.PClickEvent;
import com.ponysdk.core.ui.basic.event.PKeyUpEvent;
import com.ponysdk.core.ui.basic.event.PKeyUpHandler;
import com.ponysdk.core.ui.datagrid.ColumnDescriptor;
import com.ponysdk.core.ui.datagrid.DataGrid;
import com.ponysdk.core.ui.datagrid.impl.PLabelCellRenderer;
import com.ponysdk.core.ui.eventbus2.EventBus.EventHandler;
import com.ponysdk.core.ui.grid.AbstractGridWidget;
import com.ponysdk.core.ui.grid.GridTableWidget;
import com.ponysdk.core.ui.list.DataGridColumnDescriptor;
import com.ponysdk.core.ui.list.refreshable.Cell;
import com.ponysdk.core.ui.list.refreshable.RefreshableDataGrid;
import com.ponysdk.core.ui.list.renderer.cell.CellRenderer;
import com.ponysdk.core.ui.list.valueprovider.IdentityValueProvider;
import com.ponysdk.core.ui.main.EntryPoint;
import com.ponysdk.core.ui.model.PKeyCodes;
import com.ponysdk.core.ui.rich.PConfirmDialog;
import com.ponysdk.core.ui.rich.POptionPane;
import com.ponysdk.core.ui.rich.PToolbar;
import com.ponysdk.core.ui.rich.PTwinListBox;
import com.ponysdk.sample.client.event.UserLoggedOutEvent;
import com.ponysdk.sample.client.event.UserLoggedOutHandler;
import com.ponysdk.sample.client.page.addon.LoggerAddOn;

public class UISampleEntryPoint implements EntryPoint, UserLoggedOutHandler {

    private PLabel mainLabel;

    // HighChartsStackedColumnAddOn highChartsStackedColumnAddOn;
    int a = 0;

    @Override
    public void start(final UIContext uiContext) {
        uiContext.setClientDataOutput((object, instruction) -> System.err.println(object + " : " + instruction));

        testWebSocket();

        if (true) return;

        testPAddon();

        createWindow().open();

        downloadFile();

        testNewEvent();
        testUIDelegator();

        testNewGrid();

        PWindow.getMain().add(createGrid());

        testPAddon();

        PScript.execute(PWindow.getMain(), "alert('coucou Main');");

        final PWindow window = createWindow();
        window.open();

        PWindow.getMain().add(Element.newA());

        // PWindow.getMain().add(new PHistory());
        // PWindow.getMain().add(new PNotificationManager());
        // PWindow.getMain().add(new PSuggestBox());

        PWindow.getMain().add(createBlock(createAbsolutePanel()));
        // PWindow.getMain().add(createPAddOn().asWidget());
        PWindow.getMain().add(Element.newPAnchor());
        PWindow.getMain().add(Element.newPAnchor("Anchor"));
        PWindow.getMain().add(Element.newPAnchor("Anchor 1", "anchor2"));
        PWindow.getMain().add(Element.newPButton());
        PWindow.getMain().add(createButton());
        PWindow.getMain().add(Element.newPCheckBox());
        PWindow.getMain().add(Element.newPCheckBox("Checkbox"));
        final PCookies cookies = new PCookies();
        cookies.setCookie("Cook", "ies");
        PWindow.getMain().add(createDateBox());
        PWindow.getMain().add(Element.newPDateBox(new SimpleDateFormat("dd/MM/yyyy")));
        PWindow.getMain().add(Element.newPDateBox(Element.newPDatePicker(), new SimpleDateFormat("yyyy/MM/dd")));
        PWindow.getMain().add(Element.newPDatePicker());
        PWindow.getMain().add(Element.newPDecoratedPopupPanel(false));
        PWindow.getMain().add(Element.newPDecoratedPopupPanel(true));
        PWindow.getMain().add(Element.newPDecoratorPanel());
        PWindow.getMain().add(Element.newPDialogBox());
        PWindow.getMain().add(Element.newPDialogBox(true));
        PWindow.getMain().add(Element.newPDisclosurePanel("Disclosure"));
        PWindow.getMain().add(createDockLayoutPanel());
        final PFrame frame = Element.newPFrame("http://localhost:8081/sample/");
        frame.add(Element.newPLabel("Inside the frame"));
        PWindow.getMain().add(frame);
        PWindow.getMain().add(Element.newPFileUpload());
        PWindow.getMain().add(Element.newPFlexTable());
        PWindow.getMain().add(createPFlowPanel());
        PWindow.getMain().add(Element.newPFocusPanel());
        PWindow.getMain().add(Element.newPGrid());
        PWindow.getMain().add(Element.newPGrid(2, 3));
        PWindow.getMain().add(Element.newPHeaderPanel());
        // PWindow.getMain().add(new PHistory());
        PWindow.getMain().add(Element.newPHorizontalPanel());
        PWindow.getMain().add(Element.newPHTML());
        PWindow.getMain().add(Element.newPHTML("Html"));
        PWindow.getMain().add(Element.newPHTML("Html 1", true));
        PWindow.getMain().add(Element.newPImage()); // FIXME Test with image
        PWindow.getMain().add(Element.newPLabel());
        PWindow.getMain().add(Element.newPLabel("Label"));
        PWindow.getMain().add(Element.newPLayoutPanel());
        PWindow.getMain().add(Element.newPListBox());
        PWindow.getMain().add(createListBox());
        PWindow.getMain().add(Element.newPMenuBar());
        PWindow.getMain().add(createMenu());
        // PWindow.getMain().add(new PNotificationManager());
        PWindow.getMain().add(Element.newPPasswordTextBox());
        PWindow.getMain().add(Element.newPPasswordTextBox("Password"));

        PWindow.getMain().add(Element.newPPopupPanel());
        PWindow.getMain().add(Element.newPPopupPanel(true));

        PWindow.getMain().add(Element.newPPushButton(Element.newPImage())); // FIXME
        // Test
        // with
        // image

        PWindow.getMain().add(Element.newPRadioButton("RadioLabel"));
        PWindow.getMain().add(Element.newPRadioButton("RadioLabel"));
        final PRichTextArea richTextArea = Element.newPRichTextArea();
        PWindow.getMain().add(richTextArea);
        PWindow.getMain().add(Element.newPRichTextToolbar(richTextArea));
        PWindow.getMain().add(Element.newPScrollPanel());
        PWindow.getMain().add(Element.newPSimpleLayoutPanel());
        PWindow.getMain().add(Element.newPSimplePanel());
        PWindow.getMain().add(Element.newPSplitLayoutPanel());
        PWindow.getMain().add(createStackLayoutPanel());
        // PWindow.getMain().add(new PSuggestBox());
        PWindow.getMain().add(createTabLayoutPanel());
        PWindow.getMain().add(Element.newPTabPanel());
        PWindow.getMain().add(Element.newPTextArea());
        PWindow.getMain().add(createPTextBox());
        PWindow.getMain().add(new PToolbar());
        PWindow.getMain().add(createTree());
        PWindow.getMain().add(new PTwinListBox<>());
        PWindow.getMain().add(Element.newPVerticalPanel());

        mainLabel = Element.newPLabel("Label2");
        mainLabel.addClickHandler(event -> System.out.println("bbbbb"));
        PWindow.getMain().add(mainLabel);

        try {
            window.add(mainLabel);
        } catch (final Exception e) {
        }

        PWindow.getMain().add(Element.newPLabel("Label3"));

        final PLabel label = Element.newPLabel("Label4");

        try {
            window.add(label);
            PWindow.getMain().add(label);
        } catch (final Exception e) {

        }

        PConfirmDialog.show(PWindow.getMain(), "AAA", Element.newPLabel("AA"));

        POptionPane.showConfirmDialog(PWindow.getMain(), null, "BBB");

        // uiContext.getHistory().newItem("", false);
    }

    private void testWebSocket() {
        // final PTWebSocket ptWebSocket = new PTWebSocket();
        //   final URL url = new URL("ws://localhost:8084")
        final PWebSocket pWebSocket = new PWebSocket("ws://localhost:8081/sample/chat/test");
        final PFlowPanel panel = Element.newPFlowPanel();

        final PLabel ConnectLabel = Element.newPLabel("Click here to send on ws");
        ConnectLabel.addStyleName("red");
        panel.setAttribute("align", "center");
        panel.setAttribute("font-size", "15px ");

        panel.add(ConnectLabel);
        PWindow.getMain().add(panel);

        ConnectLabel.addClickHandler(event -> {
            pWebSocket.attach(PWindow.getMain());

        });

        final PLabel sendMsgLabel = Element.newPLabel("Click here to send message to the client");
        sendMsgLabel.addStyleName("green");
        panel.add(sendMsgLabel);
        PWindow.getMain().add(panel);

        sendMsgLabel.addClickHandler(event -> {
            try {
                pWebSocket.sendMessage("hello");
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });

        final PLabel sendMsgLabel2 = Element.newPLabel("Click here to send SOUND to the server");

        sendMsgLabel2.addStyleName("pLabel2");
        sendMsgLabel2.addStyleName("blue");
        panel.add(sendMsgLabel2);
        PWindow.getMain().add(panel);

    }

    private void downloadFile() {
        final PButton downloadImageButton = Element.newPButton("Download Pony image");
        downloadImageButton.addClickHandler(event -> UIContext.get().stackStreamRequest((request, response, uiContext1) -> {
            response.reset();
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "attachment; filename=pony_image.png");

            try {
                final OutputStream output = response.getOutputStream();
                final InputStream input = getClass().getClassLoader().getResourceAsStream("images/pony.png");

                final byte[] buff = new byte[1024];
                while (input.read(buff) != -1) {
                    output.write(buff);
                }

                output.flush();
                output.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }));
        PWindow.getMain().add(downloadImageButton);
    }

    private void testPAddon() {
        final LoggerAddOn addon = createPAddOn();
        addon.attach(PWindow.getMain());

        // final PElementAddOn elementAddOn = new PElementAddOn();
        // elementAddOn.setInnerText("Coucou");
        // flowPanel.add(elementAddOn);

        // highChartsStackedColumnAddOn = new HighChartsStackedColumnAddOn();
        // PWindow.getMain().add(highChartsStackedColumnAddOn);
        // highChartsStackedColumnAddOn.setSeries("");

        // final HighChartsStackedColumnAddOn h2 = new
        // HighChartsStackedColumnAddOn();
        // a.add(h2);
        // h2.setSeries("");
        // final PElementAddOn elementAddOn2 = new PElementAddOn();
        // elementAddOn2.setInnerText("Coucou dans window");
        // a.add(elementAddOn2);
    }

    private void testNewGrid() {
        final AtomicInteger i = new AtomicInteger();

        final DataGrid<Integer> grid = new DataGrid<>();

        for (int cpt = 0; cpt < 20; cpt++) {
            final ColumnDescriptor<Integer> column = new ColumnDescriptor<>();
            final PAnchor anchor = Element.newPAnchor("Header " + i.incrementAndGet());
            anchor.addClickHandler(e -> grid.removeColumn(column));
            column.setCellRenderer(new PLabelCellRenderer<>(from -> a + ""));
            column.setHeaderRenderer(() -> anchor);
            grid.addColumnDescriptor(column);
        }
        final PTextBox textBox = Element.newPTextBox();

        final PButton add = Element.newPButton("add");
        add.addClickHandler(e -> {
            grid.setData(Integer.valueOf(textBox.getText()));
        });

        PWindow.getMain().add(add);

        PWindow.getMain().add(textBox);
        PWindow.getMain().add(grid);

        /**
         * PScheduler.scheduleAtFixedRate(() -> { grid.setData((int)
         * (Math.random() * 50)); grid.removeData((int) (Math.random() * 50));
         * grid.removeColumn(grid.getColumns().get((int) (Math.random() *
         * grid.getColumns().size() - 1)));
         *
         * final ColumnDescriptor<Integer> column = new ColumnDescriptor<>();
         * final PAnchor anchor = new PAnchor("Header " + id.incrementAndGet());
         * anchor.addClickHandler(click -> grid.removeColumn(column));
         * column.setCellRenderer(new PLabelCellRenderer<>(from -> (int)
         * (Math.random() * 1000) + "")); column.setHeaderRenderer(() ->
         * anchor); grid.addColumnDescriptor(column); },
         * Duration.ofMillis(2000));
         **/
    }

    private void testNewEvent() {
        final EventHandler<PClickEvent> handler = UIContext.getNewEventBus().subscribe(PClickEvent.class,
            event -> System.err.println("B " + event));
        UIContext.getNewEventBus().post(new PClickEvent(this));
        UIContext.getNewEventBus().post(new PClickEvent(this));
        UIContext.getNewEventBus().unsubscribe(handler);
        UIContext.getNewEventBus().post(new PClickEvent(this));
    }

    private class Data {

        protected Integer key;
        protected String value;

        public Data(final Integer key, final String value) {
            this.key = key;
            this.value = value;
        }

    }

    private RefreshableDataGrid<Integer, Data> createGrid() {
        final AbstractGridWidget listView = new GridTableWidget();
        listView.setStyleProperty("table-layout", "fixed");
        final RefreshableDataGrid<Integer, Data> grid = new RefreshableDataGrid<>(listView);

        final DataGridColumnDescriptor<Data, Data> columnDescriptor1 = new DataGridColumnDescriptor<>();
        columnDescriptor1.setCellRenderer(new CellRenderer<UISampleEntryPoint.Data, PLabel>() {

            @Override
            public void update(final Data value, final Cell<Data, PLabel> current) {
                current.getWidget().setText(value.key + "");
            }

            @Override
            public PLabel render(final int row, final Data value) {
                return Element.newPLabel(value.key + "");
            }
        });
        columnDescriptor1.setHeaderCellRenderer(() -> Element.newPLabel("A"));
        columnDescriptor1.setValueProvider(new IdentityValueProvider<>());
        grid.addDataGridColumnDescriptor(columnDescriptor1);
        grid.addDataGridColumnDescriptor(columnDescriptor1);
        grid.addDataGridColumnDescriptor(columnDescriptor1);
        grid.addDataGridColumnDescriptor(columnDescriptor1);
        grid.addDataGridColumnDescriptor(columnDescriptor1);
        grid.addDataGridColumnDescriptor(columnDescriptor1);
        grid.addDataGridColumnDescriptor(columnDescriptor1);

        for (int i = 0; i < 40; i++) {

            final DataGridColumnDescriptor<Data, Data> columnDescriptor3 = new DataGridColumnDescriptor<>();
            columnDescriptor3.setCellRenderer(new CellRenderer<UISampleEntryPoint.Data, PLabel>() {

                @Override
                public void update(final Data value, final Cell<Data, PLabel> current) {
                    current.getWidget().setText(value.value);
                }

                @Override
                public PLabel render(final int row, final Data value) {
                    return Element.newPLabel(value.value);
                }
            });
            columnDescriptor3.setHeaderCellRenderer(() -> Element.newPLabel("B"));
            columnDescriptor3.setValueProvider(new IdentityValueProvider<>());
            grid.addDataGridColumnDescriptor(columnDescriptor3);
        }

        grid.setData(0, 1, new Data(1, "AA"));
        grid.setData(1, 2, new Data(2, "BB"));
        final Data data = new Data(3, "CC");
        grid.setData(2, 3, data);

        final AtomicInteger i = new AtomicInteger();
        PScheduler.scheduleWithFixedDelay(() -> {
            for (int key = 1; key < 50; key++) {
                grid.setData(key - 1, key, new Data(key, "" + i.incrementAndGet()));
            }
        }, Duration.ofSeconds(1), Duration.ofMillis(100));

        return grid;
    }

    private void testUIDelegator() {
        final PLabel a = Element.newPLabel();
        PWindow.getMain().add(a);
        final AtomicInteger ai = new AtomicInteger();
        PScheduler.scheduleAtFixedRate(() -> a.setText("a " + ai.incrementAndGet()), Duration.ofMillis(0), Duration.ofMillis(10));

        final PLabel p = Element.newPLabel();
        PWindow.getMain().add(p);

        final boolean delegatorMode = true;

        if (delegatorMode) {
            // final UIDelegator<String> delegator = PScheduler.delegate(new
            // Callback<String>() {
            //
            // @Override
            // public void onSuccess(final String result) {
            // p.setText(result);
            // }
            //
            // @Override
            // public void onError(final String result, final Exception
            // exception) {
            // }
            // });

            new Thread() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (final InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // delegator.onSuccess("Test");
                }
            }.start();

        }

    }

    private PWindow createWindow() {
        final PWindow w = Element.newPWindow("Window 1", "resizable=yes,location=0,status=0,scrollbars=0");

        // PScript.execute(w, "alert('coucou Window1');");
        PScript.execute(w, "console.log('coucou Window1');");

        final PFlowPanel windowContainer = Element.newPFlowPanel();
        w.add(windowContainer);

        final PLabel child = Element.newPLabel("Window 1");
        child.setText("Modified Window 1");
        windowContainer.add(child);

        final PButton button = Element.newPButton("Modified main label on main window");
        windowContainer.add(button);
        button.addClickHandler(event -> {
            mainLabel.setText("Touched by God : " + child.getWindow());
            PScript.execute(PWindow.getMain(), "alert('coucou');");
            child.setText("Clicked Window 1");
        });
        windowContainer.add(button);

        final AtomicInteger i = new AtomicInteger();

        final PButton button1 = Element.newPButton("Open linked window");
        windowContainer.add(button1);
        button1.addClickHandler(event -> {
            final PWindow newPWindow = Element.newPWindow(w, "Sub Window 1 " + i.incrementAndGet(),
                "resizable=yes,location=0,status=0,scrollbars=0");
            newPWindow.add(Element.newPLabel("Sub window"));
            newPWindow.open();
        });

        final PButton button2 = Element.newPButton("Open not linked window");
        windowContainer.add(button2);
        button2.addClickHandler(event -> {
            final PWindow newPWindow = Element.newPWindow("Not Sub Window 1 " + i.incrementAndGet(),
                "resizable=yes,location=0,status=0,scrollbars=0");
            newPWindow.add(Element.newPLabel("Sub window"));
            newPWindow.open();
        });

        PScheduler.scheduleAtFixedRate(() -> {
            final PLabel label = Element.newPLabel();
            label.setText("Window 1 " + i.incrementAndGet());
            windowContainer.add(label);
            windowContainer.add(Element.newPCheckBox("Checkbox"));
        }, Duration.ofSeconds(1), Duration.ofSeconds(10));

        final PFrame frame = Element.newPFrame("http://localhost:8081/sample/");
        frame.add(Element.newPLabel("Inside the frame"));
        w.add(frame);

        return w;
    }

    @Override
    public void onUserLoggedOut(final UserLoggedOutEvent event) {
        UIContext.get().close();
    }

    private static final PStackLayoutPanel createStackLayoutPanel() {
        final PStackLayoutPanel child = Element.newPStackLayoutPanel(PUnit.CM);
        child.add(Element.newPLabel("Text"), "Text", false, 1.0);
        return child;
    }

    private static final PListBox createListBox() {
        final PListBox pListBox = Element.newPListBox(true);
        pListBox.addItem("A");
        pListBox.addItem("B");
        pListBox.insertItem("C", 1);
        return pListBox;
    }

    private static final PTabLayoutPanel createTabLayoutPanel() {
        final PTabLayoutPanel child = Element.newPTabLayoutPanel();
        child.add(Element.newPLabel("text"), "text");
        return child;
    }

    private static final PMenuBar createMenu() {
        final PMenuBar pMenuBar = Element.newPMenuBar(true);
        pMenuBar.addItem(Element.newPMenuItem("Menu 1", Element.newPMenuBar()));
        pMenuBar.addItem(Element.newPMenuItem("Menu 2", true, Element.newPMenuBar()));
        pMenuBar.addItem(Element.newPMenuItem("Menu 3", () -> System.err.println("Menu click")));
        pMenuBar.addSeparator();
        return pMenuBar;
    }

    private static final PFlowPanel createPFlowPanel() {
        final PFlowPanel panel1 = Element.newPFlowPanel();
        panel1.setAttribute("id", "panel1");
        final PFlowPanel panel2_1 = Element.newPFlowPanel();
        panel2_1.setAttribute("id", "panel2_1");
        final PFlowPanel panel3_1_1 = Element.newPFlowPanel();
        panel3_1_1.setAttribute("id", "panel3_1_1");
        final PFlowPanel panel3_1_2 = Element.newPFlowPanel();
        panel3_1_2.setAttribute("id", "panel3_1_2");
        final PFlowPanel panel2_2 = Element.newPFlowPanel();
        panel2_2.setAttribute("id", "panel2_2");
        final PFlowPanel panel3_2_1 = Element.newPFlowPanel();
        panel3_2_1.setAttribute("id", "panel3_2_1");
        final PFlowPanel panel3_2_2 = Element.newPFlowPanel();
        panel3_2_2.setAttribute("id", "panel3_2_2");

        panel1.add(panel2_1);
        panel2_1.add(panel3_1_1);
        panel2_1.add(panel3_1_2);
        panel1.add(panel2_2);
        panel2_2.add(panel3_2_1);
        panel2_2.add(panel3_2_2);

        return panel1;
    }

    private static final PWidget createBlock(final PWidget child) {
        final PFlowPanel panel = Element.newPFlowPanel();
        panel.add(child);
        return panel;
    }

    private static final PDockLayoutPanel createDockLayoutPanel() {
        final PDockLayoutPanel pDockLayoutPanel = Element.newPDockLayoutPanel(PUnit.CM);
        pDockLayoutPanel.addNorth(Element.newPLabel("LabelDock"), 1.5);
        return pDockLayoutPanel;
    }

    private static final PFlowPanel createDateBox() {
        final PFlowPanel flowPanel = Element.newPFlowPanel();
        final PDateBox dateBox = Element.newPDateBox();
        dateBox.setValue(new Date(0));
        flowPanel.add(dateBox);
        final PButton button = Element.newPButton("reset");
        button.addClickHandler(event -> dateBox.setValue(null));
        flowPanel.add(button);
        return flowPanel;
    }

    private static final LoggerAddOn createPAddOn() {
        final LoggerAddOn labelPAddOn = new LoggerAddOn();
        labelPAddOn.log("addon logger test");
        return labelPAddOn;
    }

    private static final PTextBox createPTextBox() {
        final PTextBox pTextBox = Element.newPTextBox();

        pTextBox.addKeyUpHandler(new PKeyUpHandler() {

            @Override
            public void onKeyUp(final PKeyUpEvent keyUpEvent) {
                PScript.execute(PWindow.getMain(), "alert('" + keyUpEvent.getEventID() + "');");
            }

            @Override
            public PKeyCodes[] getFilteredKeys() {
                return new PKeyCodes[] { PKeyCodes.ENTER };
            }
        });
        return pTextBox;
    }

    private static final PTree createTree() {
        final PTree tree = Element.newPTree();

        final PTreeItem firstFolder = tree.add("First");
        firstFolder.add("2");
        firstFolder.add(0, Element.newPTreeItem("1"));

        firstFolder.setState(true);

        final PTreeItem secondFolder = Element.newPTreeItem("Second");
        final PTreeItem subItem = secondFolder.add(Element.newPTreeItem());
        subItem.setText("3");
        secondFolder.add(Element.newPTreeItem(Element.newPLabel("4")));
        tree.add(secondFolder);

        secondFolder.setSelected(true);

        return tree;
    }

    private static final PAbsolutePanel createAbsolutePanel() {
        final PAbsolutePanel pAbsolutePanel = Element.newPAbsolutePanel();
        pAbsolutePanel.add(Element.newDiv());
        pAbsolutePanel.add(Element.newP());
        return pAbsolutePanel;
    }

    private static final PButton createButton() {
        final PButton pButton = Element.newPButton("Button 1");
        pButton.addClickHandler(handler -> pButton.setText("Button 1 clicked"));
        return pButton;
    }

}
