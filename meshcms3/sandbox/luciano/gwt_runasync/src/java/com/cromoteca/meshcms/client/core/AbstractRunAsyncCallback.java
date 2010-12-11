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

import com.google.gwt.core.client.RunAsyncCallback;

public abstract class AbstractRunAsyncCallback implements RunAsyncCallback {
	private Notification notification;

	public AbstractRunAsyncCallback() {
		notification = new Notification();
		notification.run();
	}

	public void onFailure(Throwable reason) {
		notification.showOutcome(reason.getMessage(), true);
	}

	public final void onSuccess() {
		notification.showOutcome(null, false);
		onCodeReady();
	}

	public Notification getNotification() {
		return notification;
	}

	public abstract void onCodeReady();
}
