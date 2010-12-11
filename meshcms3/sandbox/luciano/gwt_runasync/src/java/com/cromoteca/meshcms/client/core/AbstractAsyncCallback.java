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
package com.cromoteca.meshcms.client.core;

import com.cromoteca.meshcms.client.server.AuthorizationException;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Implementation of AsyncCallback to manage exceptions.
 * @param <T>
 */
public abstract class AbstractAsyncCallback<T> implements AsyncCallback<Outcome<T>> {
	private Notification notification;

	public AbstractAsyncCallback() {
		notification = new Notification();
		notification.run();
	}

	public void onFailure(Throwable caught) {
		showErrorMessage(caught);
	}

	public void onSuccess(Outcome<T> result) {
		if (result == null) {
			notification.showOutcome(null, false);
			onResult(null);
		} else {
			notification.showOutcome(result.getMessage(), result.isError());
			onResult(result.getValue());
		}
	}

	/**
	 * Method to be implemented by subclasses: it is called in case of successful
	 * return.
	 * @param result the object returned by the server
	 */
	public abstract void onResult(T result);

	public void showErrorMessage(Throwable t) {
		String message = (t instanceof AuthorizationException)
			? MeshCMS.CONSTANTS.userNotAuthorized() : t.getMessage();
		notification.showOutcome(message, true);
	}

	public Notification getNotification() {
		return notification;
	}
}
