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

import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import java.util.Date;

public class DateField extends TextField {
	public static DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat
				.getShortDateTimeFormat();
	private FieldList propertyList;

	public DateField(boolean required) {
		this(required, MeshCMS.MESSAGES.dateFieldDefaultLabel(currentDateTime()));
	}

	public DateField(boolean required, String label) {
		super(required, label);
		smaller();
	}

	@Override
	protected void init(TextBox widget, String label) {
		if (label != null) {
			add(new Label(label, true));
		}

		add(widget);

		Image nowImage = new Image(MeshCMS.ICONS_BUNDLE.calendarDay());
		nowImage.setTitle(MeshCMS.CONSTANTS.dateFieldSetCurrentTime());
		nowImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					setValue(DATE_TIME_FORMAT.format(new Date()));
				}
			});
		add(nowImage);

		final Image calImage = new Image(MeshCMS.ICONS_BUNDLE.calendarMonth());
		calImage.setTitle(MeshCMS.CONSTANTS.dateFieldOpenCalendar());
		calImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					DatePicker datePicker = new DatePicker();
					Date date;

					try {
						date = DATE_TIME_FORMAT.parseStrict(getValue());
					} catch (Exception ex) {
						date = new Date();
					}

					datePicker.setValue(date);
					datePicker.setCurrentMonth(date);

					final PopupPanel popup = new PopupPanel(true, false);
					popup.setWidget(datePicker);

					int left = calImage.getAbsoluteLeft();
					int top = calImage.getAbsoluteTop();
					int height = calImage.getOffsetHeight();
					popup.setPopupPosition(left, top + height);
					datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
							public void onValueChange(ValueChangeEvent<Date> event) {
								setValue(DATE_TIME_FORMAT.format(event.getValue()));
								popup.hide();
							}
						});
					popup.show();
				}
			});
		add(calImage);
	}

	@Override
	public boolean isInvalid() {
		boolean invalid = super.isInvalid();

		if (!invalid) {
			String value = getValue();

			if (!Strings.isNullOrEmpty(value)) {
				try {
					DATE_TIME_FORMAT.parseStrict(value);
				} catch (IllegalArgumentException ex) {
					invalid = true;
				}
			}
		}

		return invalid;
	}

	/**
	 * Returns the current time formatted correctly (can be used as an example to
	 * show the date format.
	 * @return
	 */
	public static String currentDateTime() {
		return DATE_TIME_FORMAT.format(new Date());
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);

		if (propertyList != null) {
			propertyList.verifyAll();
		}
	}

	@Override
	public void enableVerify(FieldList propertyList) {
		super.enableVerify(propertyList);
		this.propertyList = propertyList;
	}

	public abstract static class DateConnector implements FieldConnector {
		public void storeValue(String value) {
			storeValue(Strings.isNullOrEmpty(value) ? null
				: DATE_TIME_FORMAT.parseStrict(value));
		}

		public String retrieveValue() {
			Date date = loadValue();

			return date == null ? "" : DATE_TIME_FORMAT.format(date);
		}

		public abstract void storeValue(Date value);

		public abstract Date loadValue();
	}
}
