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
package com.cromoteca.meshcms.server.modules;

import com.cromoteca.meshcms.client.server.ZoneItem;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.core.SessionUser;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.servlet.ServletException;

public class MailForm extends ServerModule {
	public static final Pattern DATE_REGEX = Pattern.compile("\\d{14}");
	private String destination;
	private boolean captcha;

	public void setCaptcha(boolean captcha) {
		this.captcha = captcha;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public void run() throws IOException, ServletException {
		ResourceBundle constants = Context.getConstants();
		SessionUser user = Context.getUser();
		List<ZoneItem> items = getZoneOutput().getZone().getItems();
		int idx = getPosition();

		ZoneItem formItem = new ZoneItem();
		formItem.setModuleName("form");
		formItem.getParameters().put("destination", destination);
		formItem.getParameters().put("clientScript", "true");
		items.add(++idx, formItem);

		ZoneItem nameItem = new ZoneItem();
		nameItem.setModuleName("textfield");
		nameItem.getParameters().put("label", constants.getString("mailName"));
		nameItem.getParameters().put("required", "true");

		if (user != null) {
			nameItem.getParameters().put("value", user.getDisplayName());
		}

		items.add(++idx, nameItem);

		ZoneItem emailItem = new ZoneItem();
		emailItem.setModuleName("textfield");
		emailItem.getParameters().put("label", constants.getString("mailAddress"));
		emailItem.getParameters().put("sender", "true");

		if (user != null) {
			emailItem.getParameters().put("value", user.getUser().getEmail());
		}

		items.add(++idx, emailItem);

		ZoneItem commentItem = new ZoneItem();
		commentItem.setModuleName("textarea");
		commentItem.getParameters().put("label", constants.getString("mailMessage"));
		commentItem.getParameters().put("required", "true");
		commentItem.getParameters().put("columns", "60");
		commentItem.getParameters().put("rows", "8");
		items.add(++idx, commentItem);

		if (captcha) {
			ZoneItem captchaItem = new ZoneItem();
			captchaItem.setModuleName("recaptcha");
			items.add(++idx, captchaItem);
		}

		ZoneItem submitItem = new ZoneItem();
		submitItem.setModuleName("formsubmit");
		submitItem.getParameters()
				.put("buttonLabel", constants.getString("mailSend"));
		submitItem.getParameters()
				.put("successMessage", "<em>" + constants.getString("sendOk") + "</em>");
		items.add(++idx, submitItem);
	}
}
