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

public class RpcRequest_CustomFieldSerializerTemplate extends ClassTemplate {

    public RpcRequest_CustomFieldSerializerTemplate(String sharedGwtPackageDot) {
        String packageName = sharedGwtPackageDot + "shared.rpc";
        setPackageName(packageName);
        addImport("com.google.gwt.user.client.rpc.CustomFieldSerializer", packageName);
        addImport("com.google.gwt.user.client.rpc.SerializationException", packageName);
        addImport("com.google.gwt.user.client.rpc.SerializationStreamReader", packageName);
        addImport("com.google.gwt.user.client.rpc.SerializationStreamWriter", packageName);
        addImport(ARRAYLIST_DATA_TYPE, packageName);
        addImport(OPERATION_DATA_TYPE, packageName);
        setClassName("RpcRequest_CustomFieldSerializer");
        setExtend("CustomFieldSerializer<RpcRequest>");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public static void deserialize(SerializationStreamReader streamReader, RpcRequest instance) throws SerializationException {\n"
                + "\n"
                + "        int size = streamReader.readInt();\n"
                + "        if (size < 0) {\n"
                + "            instance.setOperations(null);\n"
                + "            return;\n"
                + "        }\n"
                + "\n"
                + "        ArrayList<Operation> result = new ArrayList<Operation>();\n"
                + "        for (int i = 0; i < size; ++i) {\n"
                + "            Operation obj = (Operation) streamReader.readObject();\n"
                + "            result.add(obj);\n"
                + "        }\n"
                + "        instance.setOperations(result);\n"
                + "    }\n"
                + "\n"
                + "    public static void serialize(SerializationStreamWriter streamWriter, RpcRequest instance) throws SerializationException {\n"
                + "        ArrayList<Operation> result = instance.getOperations();\n"
                + "        if (result == null) {\n"
                + "            streamWriter.writeInt(-1);\n"
                + "            return;\n"
                + "        }\n"
                + "\n"
                + "        int size = result.size();\n"
                + "        streamWriter.writeInt(size);\n"
                + "        for (Operation obj : result) {\n"
                + "            streamWriter.writeObject(obj);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public void deserializeInstance(SerializationStreamReader streamReader, RpcRequest instance) throws SerializationException {\n"
                + "        deserialize(streamReader, instance);\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public void serializeInstance(SerializationStreamWriter streamWriter, RpcRequest instance) throws SerializationException {\n"
                + "        serialize(streamWriter, instance);\n"
                + "    }");
    }
    
}
