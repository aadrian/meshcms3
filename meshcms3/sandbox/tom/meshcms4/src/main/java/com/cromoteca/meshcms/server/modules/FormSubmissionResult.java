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

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.toolbox.Time;
import com.cromoteca.meshcms.server.webview.Expiring;

public class FormSubmissionResult implements Expiring {
	private final Path redirect;
	private final String message;
	private final boolean error;
	private final long expirationTime;
	private String formId;

	public FormSubmissionResult(String formId, String message, boolean error,
		Path redirect) {
		this.formId = formId;
		this.message = message;
		this.error = error;
		this.redirect = redirect;
		expirationTime = System.currentTimeMillis() + 15 * Time.SECOND;
	}

	public String getFormId() {
		return formId;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public String getMessage() {
		return message;
	}

	public boolean isError() {
		return error;
	}

	public String getStyle() {
		return error ? "mesh-error" : "mesh-success";
	}

	public Path getRedirect() {
		return redirect;
	}

	public static FormSubmissionResult getDefaultError(String formId) {
		return new FormSubmissionResult(formId,
			Context.getConstants().getString("formSubmissionError"), true, null);
	}

	public static FormSubmissionResult getPreviousResult(String formId) {
		Expiring flashObject = Context.getRequestContext().getFlashObject();

		if (flashObject != null && flashObject instanceof FormSubmissionResult) {
			FormSubmissionResult result = (FormSubmissionResult) flashObject;

			if (result.getFormId().equals(formId)) {
				return result;
			}
		}

		return null;
	}
}
