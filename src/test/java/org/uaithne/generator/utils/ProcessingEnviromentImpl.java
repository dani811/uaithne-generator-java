/*
 * Copyright 2012 and beyond, Juan Luis Paz
 *
 * This file is part of Uaithne.
 *
 * Uaithne is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Uaithne is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Uaithne. If not, see <http://www.gnu.org/licenses/>.
 */
package org.uaithne.generator.utils;

import java.util.Locale;
import java.util.Map;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class ProcessingEnviromentImpl implements ProcessingEnvironment {

    MessagerImpl messsager = new MessagerImpl();

    @Override
    public Map<String, String> getOptions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MessagerImpl getMessager() {
        return messsager;
    }

    @Override
    public Filer getFiler() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Elements getElementUtils() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Types getTypeUtils() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SourceVersion getSourceVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
