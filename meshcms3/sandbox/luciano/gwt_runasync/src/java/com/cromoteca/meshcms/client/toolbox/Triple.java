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
 * Encapsulates three objects.
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
public class Triple<T1 extends Serializable, T2 extends Serializable, T3 extends Serializable>
	extends Pair<T1, T2> {
	private T3 thirdObject;

	public Triple() {}

	public Triple(T1 firstObject, T2 secondObject, T3 thirdObject) {
		super(firstObject, secondObject);
		this.thirdObject = thirdObject;
	}

	public T3 getThirdObject() {
		return thirdObject;
	}

	public void setThirdObject(T3 thirdObject) {
		this.thirdObject = thirdObject;
	}
}
