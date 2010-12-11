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
package com.cromoteca.meshcms.client.ui.widgets;

import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.toolbox.Task;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class Popup extends PopupPanel {
	private ButtonBar buttonBar;
	private FlowPanel contentPanel;
	private Label message;
	private Label title;
	private Task<Integer> action;
	private boolean hideOnAction = true;
	private int height;
	private int width;

	public Popup() {
		setGlassEnabled(true);

		FlowPanel mainPanel = new FlowPanel();
		super.setWidget(mainPanel);
		title = new Label(MeshCMS.CONSTANTS.genericMessage());
		title.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					center();
				}
			});
		title.setStylePrimaryName("mesh-popup-title");
		mainPanel.add(title);
		contentPanel = new FlowPanel();
		contentPanel.setStylePrimaryName("mesh-popup-content");
		mainPanel.add(contentPanel);
		message = new Label();
		contentPanel.add(message);
		buttonBar = new ButtonBar();
		mainPanel.add(buttonBar);
	}

	public Popup(int width, int height) {
		this();
		this.width = width;
		this.height = height;
	}

	@Override
	public void setWidget(Widget mainWidget) {
		if (mainWidget != null) {
			if (width > 0 && height > 0) {
				width = Math.min(width, Window.getClientWidth() - 150);
				height = Math.min(height, Window.getClientHeight() - 150);

				Style style = mainWidget.getElement().getStyle();
				style.setWidth(width, Unit.PX);
				style.setHeight(height, Unit.PX);
			}

			contentPanel.add(mainWidget);
		}
	}

	public void setText(String text) {
		title.setText(text);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message.getText();
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String msg) {
		message.setText(msg);
	}

	public static Popup getAlertBox(String message) {
		return getAlertBox(message, null);
	}

	public static Popup getAlertBox(String message, Widget editor) {
		Popup box = new Popup();
		box.setWidget(editor);
		box.setMessage(message);
		box.addOkButton();

		return box;
	}

	public static Popup getOkCancelBox(String message) {
		return getInputBox(message, null);
	}

	public static Popup getInputBox(String message, Widget editor) {
		Popup box = new Popup();
		box.setWidget(editor);
		box.setMessage(message);
		box.addOkButton();
		box.addCancelButton();

		return box;
	}

	public static Popup getYesNoBox(String message) {
		Popup box = new Popup();
		box.setMessage(message);
		box.addYesButton();
		box.addNoButton();

		return box;
	}

	public static Popup getYesNoCancelBox(String message) {
		Popup box = getYesNoBox(message);
		box.addCancelButton();

		return box;
	}

	public void showDialog(Task<Integer> action) {
		this.action = action;
		center();
	}

	protected void addButton(String text, final boolean doAction) {
		final Button button = new Button();
		button.setText(text);
		button.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					boolean hide = true;

					if (doAction && action != null) {
						action.execute(buttonBar.getWidgetIndex(button));
						hide = hideOnAction;
					}

					if (hide) {
						hide();
					}
				}
			});
		buttonBar.add(button);
	}

	public Button getButton(int index) {
		return (Button) buttonBar.getWidget(index);
	}

	protected void addOkButton() {
		addButton(MeshCMS.CONSTANTS.genericOk(), true);
	}

	protected void addCancelButton() {
		addButton(MeshCMS.CONSTANTS.genericCancel(), false);
	}

	private void addNoButton() {
		addButton(MeshCMS.CONSTANTS.genericNo(), true);
	}

	private void addYesButton() {
		addButton(MeshCMS.CONSTANTS.genericYes(), true);
	}

	public boolean isHideOnAction() {
		return hideOnAction;
	}

	public void setHideOnAction(boolean hideOnAction) {
		this.hideOnAction = hideOnAction;
	}
}
