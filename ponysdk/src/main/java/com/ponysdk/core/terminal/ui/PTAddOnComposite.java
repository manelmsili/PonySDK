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

package com.ponysdk.core.terminal.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Widget;
import com.ponysdk.core.terminal.JavascriptAddOnFactory;
import com.ponysdk.core.terminal.UIBuilder;
import com.ponysdk.core.terminal.model.BinaryModel;
import com.ponysdk.core.terminal.model.ReaderBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PTAddOnComposite extends PTAddOn {

    private List<JSONObject> pendingUpdates = new ArrayList<>();

    private Widget widget;

    @Override
    protected void doCreate(ReaderBuffer buffer, int objectId, UIBuilder uiService) {
        // ServerToClientModel.FACTORY
        final String signature = buffer.readBinaryModel().getStringValue();
        final Map<String, JavascriptAddOnFactory> factories = uiService.getJavascriptAddOnFactory();
        final JavascriptAddOnFactory factory = factories.get(signature);
        if (factory == null)
            throw new RuntimeException("AddOn factory not found for signature: " + signature + ". Addons registered: "
                    + factories.keySet());

        final JSONObject params = new JSONObject();
        params.put("id", new JSONNumber(objectId));

        final BinaryModel binaryModel = buffer.readBinaryModel();
        final int widgetID = binaryModel.getIntValue();
        final PTWidget<?> object = (PTWidget<?>) uiService.getPTObject(widgetID);
        widget = object.cast();
        final Element element = widget.getElement();
        params.put("widgetID", new JSONString(String.valueOf(widgetID)));
        params.put("widgetElement", new JSONObject(element));

        widget.addAttachHandler(new AttachEvent.Handler() {

            @Override
            public void onAttachOrDetach(final AttachEvent event) {
                addOn.onAttachOrDetach(event.isAttached());
                flushPendingUpdates();
            }
        });

        addOn = factory.newAddOn(params.getJavaScriptObject());
        addOn.onInit();
        if (widget.isAttached()) {
            addOn.onAttachOrDetach(true);
        }
    }

    @Override
    protected void doUpdate(JSONObject data) {
        if (widget.isAttached()) {
            flushPendingUpdates();
            super.doUpdate(data);
        } else {
            pendingUpdates.add(data);
        }
    }

    private void flushPendingUpdates() {
        if (!pendingUpdates.isEmpty()) {
            for (JSONObject update : pendingUpdates) {
                super.doUpdate(update);
            }
            pendingUpdates.clear();
        }
    }
}