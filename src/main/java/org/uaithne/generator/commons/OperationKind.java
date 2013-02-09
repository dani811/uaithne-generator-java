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
package org.uaithne.generator.commons;

public enum OperationKind {
    CUSTOM(0, "Custom"),
    SELECT_ONE(1, "SelectOne"),
    SELECT_MANY(2, "SelectMany"),
    SELECT_PAGE(3, "SelectPage"),
    DELETE_BY_ID(4, "DeleteById"),
    INSERT(5, "Insert"),
    JUST_INSERT(6, "JustInsert"),
    SAVE(7, "Save"),
    JUST_SAVE(8, "JustSave"),
    SELECT_BY_ID(9, "SelectById"),
    UPDATE(10, "Update"),
    CUSTOM_INSERT(11, "CustomInsert"),
    CUSTOM_UPDATE(12, "CustomUpdate"),
    CUSTOM_DELETE(13, "CustomDelete"),
    CUSTOM_INSERT_WITH_ID(14, "CustomInsertWithId"),
    MERGE(15, "Merge");
    
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    private OperationKind(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
}
