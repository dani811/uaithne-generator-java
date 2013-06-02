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
package org.uaithne.generator.templates.shared.gwt.shared.rpc;

import java.io.IOException;
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.templates.ClassTemplate;

public class RpcRequestTemplate extends ClassTemplate {

    public RpcRequestTemplate(String sharedGwtPackageDot) {
        String packageName = sharedGwtPackageDot + "shared.rpc";
        setPackageName(packageName);
        addImport(SERIALIZABLE_DATA_TYPE, packageName);
        addImport(ARRAYLIST_DATA_TYPE, packageName);
        addImport("com.google.gwt.user.client.rpc.GwtTransient", packageName);
        addImport(OPERATION_DATA_TYPE, packageName);
        setClassName("RpcRequest");
        addImplement(SERIALIZABLE_DATA);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    @GwtTransient\n"
                + "    private ArrayList<Operation> operations;\n"
                + "\n"
                + "    public ArrayList<Operation> getOperations() {\n"
                + "        return operations;\n"
                + "    }\n"
                + "\n"
                + "    public void setOperations(ArrayList<Operation> operations) {\n"
                + "        this.operations = operations;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        final RpcRequest other = (RpcRequest) obj;\n"
                + "        if (this.operations != other.operations && (this.operations == null || !this.operations.equals(other.operations))) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 5;\n"
                + "        hash = 23 * hash + (this.operations != null ? this.operations.hashCode() : 0);\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    public RpcRequest(ArrayList<Operation> operations) {\n"
                + "        this.operations = operations;\n"
                + "    }\n"
                + "\n"
                + "    public RpcRequest() {\n"
                + "    }");
    }
    
}
