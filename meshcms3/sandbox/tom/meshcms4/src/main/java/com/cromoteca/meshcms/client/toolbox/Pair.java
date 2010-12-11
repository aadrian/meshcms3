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
package com.cromoteca.meshcms.client.toolbox;

import java.io.Serializable;

/**
 * Encapsulates two objects.
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1 extends Serializable, T2 extends Serializable>
	implements Serializable {
	private T1 firstObject;
	private T2 secondObject;

	public Pair() {}

	public Pair(T1 firstObject, T2 secondObject) {
		this.firstObject = firstObject;
		this.secondObject = secondObject;
	}

	public T1 getFirstObject() {
		return firstObject;
	}

	public void setFirstObject(T1 firstObject) {
		this.firstObject = firstObject;
	}

	public T2 getSecondObject() {
		return secondObject;
	}

	public void setSecondObject(T2 secondObject) {
		this.secondObject = secondObject;
	}
}
