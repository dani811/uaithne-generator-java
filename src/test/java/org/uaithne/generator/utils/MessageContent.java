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

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class MessageContent {

    private Diagnostic.Kind kind;
    private String msg;
    private Element e;

    public MessageContent(Diagnostic.Kind kind, CharSequence msg, Element e) {
        this.kind = kind;
        this.msg = msg.toString();
        this.e = e;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.kind != null ? this.kind.hashCode() : 0);
        hash = 61 * hash + (this.msg != null ? this.msg.hashCode() : 0);
        hash = 61 * hash + (this.e != null ? this.e.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MessageContent other = (MessageContent) obj;
        if (this.kind != other.kind) {
            return false;
        }
        if ((this.msg == null) ? (other.msg != null) : !this.msg.equals(other.msg)) {
            return false;
        }
        if (this.e != other.e && (this.e == null || !this.e.equals(other.e))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return kind + " " + msg + " " + e;
    }
}
