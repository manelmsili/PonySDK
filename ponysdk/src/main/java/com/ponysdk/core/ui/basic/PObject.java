/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *  Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *  Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
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

package com.ponysdk.core.ui.basic;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.json.JsonObject;

import com.ponysdk.core.model.ClientToServerModel;
import com.ponysdk.core.model.HandlerModel;
import com.ponysdk.core.model.ServerToClientModel;
import com.ponysdk.core.model.WidgetType;
import com.ponysdk.core.server.application.UIContext;
import com.ponysdk.core.server.stm.Txn;
import com.ponysdk.core.ui.basic.event.PTerminalEvent;
import com.ponysdk.core.ui.model.ServerBinaryModel;
import com.ponysdk.core.writer.ModelWriter;
import com.ponysdk.core.writer.ModelWriterCallback;

/**
 * The superclass for all PonySDK objects.
 */
public abstract class PObject {

    protected final int ID = UIContext.get().nextID();
    protected PWindow window;
    protected PFrame frame;
    protected Set<InitializeListener> initializeListeners;
    protected Set<DestroyListener> destroyListeners;
    protected Object data;

    protected LinkedHashMap<Integer, Runnable> stackedInstructions;
    private String nativeBindingFunction;

    private PTerminalEvent.Handler terminalHandler;

    protected boolean initialized = false;
    protected boolean destroy = false;

    protected final AtomicInteger atomicKey = new AtomicInteger(ServerToClientModel.DESTROY.getValue());

    PObject() {
    }

    protected abstract WidgetType getWidgetType();

    protected void attach(final PFrame frame) {
        this.frame = frame;
    }

    protected boolean attach(final PWindow window) {
        if (this.window == null && window != null) {
            this.window = window;
            init();
            return true;
        } else if (this.window != window) {
            throw new IllegalAccessError(
                "Widget already attached to an other window, current window : #" + this.window + ", new window : #" + window);
        }

        return false;
    }

    void init() {
        if (initialized) return;
        if (window.isOpened()) {
            applyInit();
        } else {
            window.addOpenHandler(window -> applyInit());
        }
    }

    protected void applyInit() {
        final ModelWriter writer = Txn.getWriter();
        writer.beginObject();
        if (window != PWindow.getMain()) writer.write(ServerToClientModel.WINDOW_ID, window.getID());
        if (frame != null) writer.write(ServerToClientModel.FRAME_ID, frame.getID());
        writer.write(ServerToClientModel.TYPE_CREATE, ID);
        writer.write(ServerToClientModel.WIDGET_TYPE, getWidgetType().getValue());
        enrichOnInit(writer);
        writer.endObject();

        UIContext.get().registerObject(this);

        init0();

        if (stackedInstructions != null) stackedInstructions.values().forEach(Runnable::run);

        if (initializeListeners != null) initializeListeners.forEach(listener -> listener.onInitialize(this));

        initialized = true;
    }

    protected void enrichOnInit(final ModelWriter writer) {
    }

    protected void init0() {
    }

    public PWindow getWindow() {
        return window;
    }

    public PFrame getFrame() {
        return frame;
    }

    public final int getID() {
        return ID;
    }

    /**
     * Bind to a Terminal function, usefull to link the objectID and the widget
     * reference
     * <p>
     * <h2>Example :</h2>
     * <p>
     *
     * <pre>
     * --- Java ---
     *
     * bindTerminalFunction("myFunction")
     * </pre>
     * <p>
     *
     * <pre>
     * --- JavaScript ---
     *
     * myFunction(id, object) {
     * ....
     * ....
     * }
     * </pre>
     */
    public void bindTerminalFunction(final String functionName) {
        if (nativeBindingFunction != null)
            throw new IllegalAccessError("Object already bind to native function: " + nativeBindingFunction);

        nativeBindingFunction = functionName;

        saveUpdate(writer -> writer.write(ServerToClientModel.BIND, functionName));
    }

    public void sendToNative(final JsonObject data) {
        if (destroy) return;
        if (nativeBindingFunction == null) throw new IllegalAccessError("Object not bind to a native function");

        saveUpdate(writer -> writer.write(ServerToClientModel.NATIVE, data));
    }

    public void setTerminalHandler(final PTerminalEvent.Handler terminalHandler) {
        this.terminalHandler = terminalHandler;
    }

    /**
     * JSON received from the Terminal using pony.sendDataToServer(objectID, JSON)
     */
    public void onClientData(final JsonObject event) {
        if (destroy) return;
        if (terminalHandler != null) {
            final String nativeKey = ClientToServerModel.NATIVE.toStringValue();
            if (event.containsKey(nativeKey)) {
                terminalHandler.onTerminalEvent(new PTerminalEvent(this, event.getJsonObject(nativeKey)));
            }
        }
    }

    protected LinkedHashMap<Integer, Runnable> safeStackedInstructions() {
        if (stackedInstructions == null) stackedInstructions = new LinkedHashMap<>();
        return stackedInstructions;
    }

    protected void saveUpdate(final ModelWriterCallback callback) {
        saveUpdate(atomicKey.incrementAndGet(), callback);
    }

    protected void saveUpdate(final ServerToClientModel serverToClientModel, final Object value) {
        saveUpdate(serverToClientModel.getValue(), writer -> writer.write(serverToClientModel, value));
    }

    private void saveUpdate(final int atomicKey, final ModelWriterCallback callback) {
        if (destroy) return;

        if (initialized) writeUpdate(callback);
        else safeStackedInstructions().put(atomicKey, () -> writeUpdate(callback));
    }

    void writeUpdate(final ModelWriterCallback callback) {
        if (destroy) return;

        final ModelWriter writer = Txn.getWriter();
        writer.beginObject();
        if (!PWindow.isMain(window)) writer.write(ServerToClientModel.WINDOW_ID, window.getID());
        if (frame != null) writer.write(ServerToClientModel.FRAME_ID, frame.getID());
        writer.write(ServerToClientModel.TYPE_UPDATE, ID);

        callback.doWrite(writer);
        writer.endObject();
    }

    protected void saveAdd(final int objectID, final int parentObjectID) {
        saveAdd(objectID, parentObjectID, (ServerBinaryModel) null);
    }

    protected void saveAdd(final int objectID, final int parentObjectID, final ServerBinaryModel... binaryModels) {
        if (destroy) return;

        final ModelWriterCallback callback = writer -> {
            writer.write(ServerToClientModel.TYPE_ADD, objectID);
            writer.write(ServerToClientModel.PARENT_OBJECT_ID, parentObjectID);
            if (binaryModels != null) {
                for (final ServerBinaryModel binaryModel : binaryModels) {
                    if (binaryModel != null) writer.write(binaryModel.getKey(), binaryModel.getValue());
                }
            }
        };
        if (initialized) writeAdd(callback);
        else safeStackedInstructions().put(atomicKey.incrementAndGet(), () -> writeAdd(callback));
    }

    private void writeAdd(final ModelWriterCallback callback) {
        if (destroy) return;

        final ModelWriter writer = Txn.getWriter();
        writer.beginObject();
        if (!PWindow.isMain(window)) writer.write(ServerToClientModel.WINDOW_ID, window.getID());
        if (frame != null) writer.write(ServerToClientModel.FRAME_ID, frame.getID());

        callback.doWrite(writer);
        writer.endObject();
    }

    protected void saveAddHandler(final HandlerModel type) {
        if (destroy) return;

        final ModelWriterCallback callback = writer -> writer.write(ServerToClientModel.HANDLER_TYPE, type.getValue());
        if (initialized) writeAddHandler(callback);
        else safeStackedInstructions().put(atomicKey.incrementAndGet(), () -> writeAddHandler(callback));
    }

    void writeAddHandler(final ModelWriterCallback callback) {
        if (destroy) return;

        final ModelWriter writer = Txn.getWriter();
        writer.beginObject();
        if (!PWindow.isMain(window)) writer.write(ServerToClientModel.WINDOW_ID, window.getID());
        if (frame != null) writer.write(ServerToClientModel.FRAME_ID, frame.getID());
        writer.write(ServerToClientModel.TYPE_ADD_HANDLER, ID);

        callback.doWrite(writer);
        writer.endObject();
    }

    protected void saveRemoveHandler(final HandlerModel type) {
        if (destroy) return;

        final ModelWriterCallback callback = writer -> {
            writer.write(ServerToClientModel.TYPE_REMOVE_HANDLER, ID);
            writer.write(ServerToClientModel.HANDLER_TYPE, type.getValue());
        };
        if (initialized) writeRemoveHandler(callback);
        else safeStackedInstructions().put(atomicKey.incrementAndGet(), () -> writeRemoveHandler(callback));
    }

    private void writeRemoveHandler(final ModelWriterCallback callback) {
        if (destroy) return;

        final ModelWriter writer = Txn.getWriter();
        writer.beginObject();
        if (!PWindow.isMain(window)) writer.write(ServerToClientModel.WINDOW_ID, window.getID());
        if (frame != null) writer.write(ServerToClientModel.FRAME_ID, frame.getID());

        callback.doWrite(writer);
        writer.endObject();
    }

    void saveRemove(final int objectID, final int parentObjectID) {
        if (destroy) return;

        final ModelWriterCallback callback = writer -> {
            writer.write(ServerToClientModel.TYPE_REMOVE, objectID);
            writer.write(ServerToClientModel.PARENT_OBJECT_ID, parentObjectID);
        };
        if (initialized) writeRemove(callback);
        else safeStackedInstructions().put(atomicKey.incrementAndGet(), () -> writeRemove(callback));
    }

    private void writeRemove(final ModelWriterCallback callback) {
        if (destroy) return;

        final ModelWriter writer = Txn.getWriter();
        writer.beginObject();
        if (!PWindow.isMain(window)) writer.write(ServerToClientModel.WINDOW_ID, window.getID());
        if (frame != null) writer.write(ServerToClientModel.FRAME_ID, frame.getID());

        callback.doWrite(writer);
        writer.endObject();
    }

    public void addInitializeListener(final InitializeListener listener) {
        if (this.initializeListeners == null) initializeListeners = new LinkedHashSet<>();
        this.initializeListeners.add(listener);
    }

    public void addDestroyListener(final DestroyListener listener) {
        if (this.destroyListeners == null) destroyListeners = new LinkedHashSet<>();
        this.destroyListeners.add(listener);
    }

    public void onDestroy() {
        destroy = true;
        terminalHandler = null;
        initializeListeners = null;
        if (this.destroyListeners != null) this.destroyListeners.forEach(listener -> listener.onDestroy(this));
        this.destroyListeners = null;
        window = null;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final PObject other = (PObject) obj;
        return ID == other.ID;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + ID;
    }

    @FunctionalInterface
    public interface InitializeListener {

        void onInitialize(PObject object);
    }

    @FunctionalInterface
    public interface DestroyListener {

        void onDestroy(PObject object);
    }

    public boolean isInitialized() {
        return initialized;
    }

}
