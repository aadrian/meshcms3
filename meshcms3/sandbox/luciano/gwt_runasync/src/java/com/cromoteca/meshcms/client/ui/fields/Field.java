/*
 * Copyright 2004-2010 Luciano Vernaschi
 *
 * This file is part of MeshCMS.
 *
 * MeshCMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MeshCMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cromoteca.meshcms.client.ui.fields;

import com.cromoteca.meshcms.client.toolbox.Strings;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;

public abstract class Field<T extends FocusWidget> extends FlowPanel {
	private FieldConnector connector;
	private T widget;
	private boolean enterToAction;
	private boolean required;

	public Field(T widget, boolean required, String label) {
		this.widget = widget;
		this.required = required;
		setStylePrimaryName("mesh-field");
		init(widget, label);
	}

	public boolean isInvalid() {
		return required && Strings.isNullOrEmpty(getValue());
	}

	protected void init(T widget, String label) {
		if (label != null) {
			add(new Label(label, true));
		}

		add(widget);
	}

	@SuppressWarnings("unchecked")
	public void enableVerify(final FieldList propertyList) {
		if (widget instanceof HasBlurHandlers) {
			widget.addBlurHandler(new BlurHandler() {
					public void onBlur(BlurEvent event) {
						if (propertyList != null) {
							propertyList.verifyAll();
						}
					}
				});
		}

		if (widget instanceof HasKeyUpHandlers) {
			widget.addKeyUpHandler(new KeyUpHandler() {
					public void onKeyUp(KeyUpEvent event) {
						// order of verifications is very important here
						if (propertyList != null
									&& propertyList.verifyAll()
									&& enterToAction
									&& event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							Button button = propertyList.getActionButton();

							if (button != null) {
								button.click();
							}
						}
					}
				});
		}
	}

	public boolean checkInvalid() {
		boolean invalid = isInvalid();

		if (invalid) {
			addStyleName("mesh-invalid");
		} else {
			removeStyleName("mesh-invalid");
		}

		return invalid;
	}

	public FieldConnector getConnector() {
		return connector;
	}

	public void setConnector(FieldConnector connector) {
		this.connector = connector;
		retrieveValue();
	}

	public void useVoidConnector() {
		setConnector(new VoidConnector());
	}

	public void setFocus(boolean focused) {
		widget.setFocus(focused);
	}

	public void setEnabled(boolean enabled) {
		widget.setEnabled(enabled);
	}

	public boolean isEnabled() {
		return widget.isEnabled();
	}

	public void removeConnector() {
		connector = null;
	}

	public boolean isEnterToAction() {
		return enterToAction;
	}

	public void setEnterToAction(boolean enterToAction) {
		this.enterToAction = enterToAction;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public T getFieldWidget() {
		return widget;
	}

	public void storeValue() {
		connector.storeValue(getValue());
	}

	public void retrieveValue() {
		setValue(connector.retrieveValue());
	}

	public abstract String getValue();

	public abstract void setValue(String value);

	static class VoidConnector implements FieldConnector {
		public void storeValue(String value) {}

		public String retrieveValue() {
			return null;
		}
	}
}
