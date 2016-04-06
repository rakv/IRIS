package com.temenos.useragent.generic.internal;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.InputStream;

import com.temenos.useragent.generic.Entity;

/**
 * Defines a wrapper to the {@link Entity entity} with the possibility of
 * setting the appropriate {@link EntityHandler handler} and the current
 * {@link SessionContext session context}.
 * 
 * @author ssethupathi
 *
 */
public interface EntityWrapper extends Entity {

	void setHandler(EntityHandler handler);

	void setSessionCallback(SessionContext sessionCallback);

	void setValue(String fqPropertyName, String value);

	InputStream getContent();
}
