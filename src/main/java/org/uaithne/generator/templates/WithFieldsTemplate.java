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
package org.uaithne.generator.templates;

import java.io.IOException;
import java.util.ArrayList;
import org.uaithne.generator.commons.FieldInfo;

public abstract class WithFieldsTemplate extends ClassTemplate {

    protected void writeArguments(Appendable appender, ArrayList<FieldInfo> fields) throws IOException {
        boolean requireComma = false;
        for (FieldInfo field : fields) {
            if (requireComma) {
                appender.append(", ");
            }
            appender.append(field.getDataType().getSimpleName());
            appender.append(" ");
            appender.append(field.getName());
            requireComma = true;
        }
    }

    protected void writeArgumentsForAddMore(Appendable appender, ArrayList<FieldInfo> fields) throws IOException {
        for (FieldInfo field : fields) {
            appender.append(field.getDataType().getSimpleName());
            appender.append(" ");
            appender.append(field.getName());
            appender.append(", ");
        }
    }

    protected void writeCallArguments(Appendable appender, ArrayList<FieldInfo> fields) throws IOException {
        boolean requireComma = false;
        for (FieldInfo field : fields) {
            if (requireComma) {
                appender.append(", ");
            }
            appender.append(field.getName());
            requireComma = true;
        }
    }

    protected void writeCallArgumentsForAddMore(Appendable appender, ArrayList<FieldInfo> fields) throws IOException {
        for (FieldInfo field : fields) {
            appender.append(field.getName());
            appender.append(", ");
        }
    }
}
