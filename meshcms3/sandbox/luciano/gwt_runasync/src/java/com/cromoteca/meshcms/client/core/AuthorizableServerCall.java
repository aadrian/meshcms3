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
import com.cromoteca.meshcms.client.toolbox.Task;
import com.cromoteca.meshcms.client.ui.widgets.LoginPanel;
import com.cromoteca.meshcms.client.ui.widgets.LoginPanel.LoginHandler;
import com.cromoteca.meshcms.client.ui.widgets.Popup;

/**
 * Base class for a call to the server that can be tried again if user
 * authorization fails (for example due to an expired session).
 *
 * @param <T> the type of object expected from the server
 */
public abstract class AuthorizableServerCall<T> implements LoginHandler {
	private AbstractAsyncCallback<T> asyncCallback;

	public AuthorizableServerCall() {
		asyncCallback = new AbstractAsyncCallback<T>() {
					@Override
					public void onResult(T result) {
						AuthorizableServerCall.this.onResult(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof AuthorizationException) {
							AuthorizationException authEx = (AuthorizationException) caught;

							if (authEx.getUser() == null) {
								showLogin();
							} else {
								Popup.getYesNoBox(MeshCMS.CONSTANTS.loginAsAnotherUser()).showDialog(new Task<Integer>() {
										public void execute(Integer param) {
											if (param == 0) {
												showLogin();
											}
										}
									});
							}
						} else {
							asyncCallback.showErrorMessage(caught);
						}
					}
				};
	}

	/**
	 * Method to be implemented by subclasses: must define the RPC call to be
	 * made (note that this method might be called more than once).&nbsp;This
	 * method <strong>must</strong> use the AsyncCallback returned by
	 * getAsyncCallback.
	 */
	public abstract void callServer();

	/**
	 * Method to be implemented by subclasses: it is called in case of successful
	 * return.
	 * @param result the object returned by the server
	 */
	public abstract void onResult(T result);

	public void run() {
		callServer();
	}

	private void showLogin() {
		new LoginPanel().showDialog(this);
	}

	public void onLogin() {
		callServer();
	}

	/**
	 * Returns the AsyncCallback to be used in callServer.
	 * @return
	 */
	public AbstractAsyncCallback<T> getAsyncCallback() {
		return asyncCallback;
	}
}
