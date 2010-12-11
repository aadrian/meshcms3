package com.cromoteca.meshcms.client.core;

import com.cromoteca.meshcms.client.toolbox.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class Notification extends Timer {
	private final FocusPanel panel;
	private boolean over;
	private int opacity;
	private int step;

	public Notification() {
		panel = new FocusPanel();
		panel.addMouseOverHandler(new MouseOverHandler() {
				public void onMouseOver(MouseOverEvent event) {
					over = true;
				}
			});
		panel.addMouseOutHandler(new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent event) {
					over = false;
				}
			});
		panel.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					setOpacity(0);
				}
			});
		panel.add(new Image(GWT.getModuleBaseURL() + "assets/loading.gif"));
		MeshCMS.getStatusPanel().insert(panel, 0);
		panel.setStylePrimaryName("mesh-notification");
		panel.setTitle(MeshCMS.CONSTANTS.notificationClickToHide());
		opacity = 0;
		step = 1;
		scheduleRepeating(100);
	}

	@Override
	public void run() {
		int newOpacity = Math.max(Math.min(opacity + (over ? 100 : step), 100), 0);

		if (newOpacity != opacity) {
			setOpacity(newOpacity);
		}
	}

	public void showOutcome(String message, boolean error) {
		if (Strings.isNullOrEmpty(message)) {
			step = -10;
		} else {
			panel.setWidget(new Label(message));

			if (error) {
				panel.addStyleName("mesh-notification-bad");
				setOpacity(100);
				cancel();
			} else {
				panel.addStyleName("mesh-notification-good");
				setOpacity(100);
				step = -2;
			}
		}
	}

	private void setOpacity(int percent) {
		Style style = panel.getElement().getStyle();

		if (percent <= 0) {
			style.setProperty("visibility", "hidden");
			panel.removeFromParent();
			cancel();
		} else {
			opacity = percent;
			style.clearProperty("visibility");

			if (percent < 10) {
				style.setProperty("filter", "alpha(opacity=0" + percent + ')');
				style.setProperty("opacity", ".0" + percent);
			} else if (percent < 100) {
				style.setProperty("filter", "alpha(opacity=" + percent + ')');
				style.setProperty("opacity", "." + percent);
			} else {
				style.clearProperty("filter");
				style.clearProperty("opacity");
			}
		}
	}
}
