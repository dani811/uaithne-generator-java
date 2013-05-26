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

public class RpcResponseTemplate extends ClassTemplate {

    public RpcResponseTemplate(String sharedGwtPackageDot, String sharedPackageDot) {
        String packageName = sharedGwtPackageDot + "shared.rpc";
        addImport(SERIALIZABLE_DATA_TYPE, packageName);
        addImport(ARRAYLIST_DATA_TYPE, packageName);
        addImport("com.google.gwt.user.client.rpc.GwtTransient", packageName);
        setPackageName(packageName);
        setClassName("RpcResponse");
        addImplement(SERIALIZABLE_DATA);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    @GwtTransient\n"
                + "    private ArrayList<Object> result;\n"
                + "\n"
                + "    public ArrayList<Object> getResult() {\n"
                + "        return result;\n"
                + "    }\n"
                + "\n"
                + "    public void setResult(ArrayList<Object> result) {\n"
                + "        this.result = result;\n"
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
                + "        final RpcResponse other = (RpcResponse) obj;\n"
                + "        if (this.result != other.result && (this.result == null || !this.result.equals(other.result))) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 7;\n"
                + "        hash = 83 * hash + (this.result != null ? this.result.hashCode() : 0);\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    public RpcResponse(ArrayList<Object> result) {\n"
                + "        this.result = result;\n"
                + "    }\n"
                + "\n"
                + "    public RpcResponse() {\n"
                + "    }");
    }
    
}
