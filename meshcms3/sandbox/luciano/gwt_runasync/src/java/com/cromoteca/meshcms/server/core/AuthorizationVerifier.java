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
package com.cromoteca.meshcms.server.core;

import com.cromoteca.meshcms.client.server.AuthorizationException;
import com.cromoteca.meshcms.client.toolbox.Path;

public class AuthorizationVerifier {
	private SessionUser user;

	public AuthorizationVerifier() throws AuthorizationException {
		user = Context.getUser();

		if (user == null) {
			throw new AuthorizationException();
		}
	}

	public SessionUser getUser() {
		return user;
	}

	public AuthorizationVerifier verify() throws AuthorizationException {
		return verify(null);
	}

	public AuthorizationVerifier verify(Path path) throws AuthorizationException {
		if (!(path == null || user.getUser().canWrite(path))) {
			throw new AuthorizationException(user.getUserProfile());
		}

		return this;
	}

	public AuthorizationVerifier verifyAdmin() throws AuthorizationException {
		if (!user.getUser().isAdmin()) {
			throw new AuthorizationException(user.getUserProfile());
		}

		return this;
	}

	public AuthorizationVerifier verifyEditor() throws AuthorizationException {
		if (!user.getUser().isEditor()) {
			throw new AuthorizationException(user.getUserProfile());
		}

		return this;
	}
}
