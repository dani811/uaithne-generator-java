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
    CUSTOM(0, "Custom", false),
    SELECT_ONE(1, "SelectOne", false),
    SELECT_MANY(2, "SelectMany", false),
    SELECT_PAGE(3, "SelectPage", false),
    DELETE_BY_ID(4, "DeleteById", false),
    INSERT(5, "Insert", false),
    JUST_INSERT(6, "JustInsert", false),
    SAVE(7, "Save", false),
    JUST_SAVE(8, "JustSave", false),
    SELECT_BY_ID(9, "SelectById", false),
    UPDATE(10, "Update", false),
    CUSTOM_INSERT(11, "CustomInsert", false),
    CUSTOM_UPDATE(12, "CustomUpdate", false),
    CUSTOM_DELETE(13, "CustomDelete", false),
    CUSTOM_INSERT_WITH_ID(14, "CustomInsertWithId", false),
    MERGE(15, "Merge", false),
    SELECT_COUNT(16, "SelectCount", false),
    COMPLEX_SELECT_CALL(17, "ComplexSelectCall", true),
    COMPLEX_INSERT_CALL(18, "ComplexInsertCall", true),
    COMPLEX_UPDATE_CALL(19, "ComplexUpdateCall", true),
    COMPLEX_DELETE_CALL(20, "ComplexDeleteCall", true);
    
    private final int id;
    private final String name;
    private final boolean complexCall;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isComplexCall() {
        return complexCall;
    }
    
    private OperationKind(int id, String name, boolean complexCall) {
        this.id = id;
        this.name = name;
        this.complexCall = complexCall;
    }
    
}
