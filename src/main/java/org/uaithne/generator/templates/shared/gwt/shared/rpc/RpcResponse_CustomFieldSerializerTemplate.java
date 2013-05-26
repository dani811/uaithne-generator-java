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

public class RpcResponse_CustomFieldSerializerTemplate extends ClassTemplate {

    public RpcResponse_CustomFieldSerializerTemplate(String sharedGwtPackageDot, String sharedPackageDot) {
        String packageName = sharedGwtPackageDot + "shared.rpc";
        addImport("com.google.gwt.user.client.rpc.CustomFieldSerializer", packageName);
        addImport("com.google.gwt.user.client.rpc.SerializationException", packageName);
        addImport("com.google.gwt.user.client.rpc.SerializationStreamReader", packageName);
        addImport("com.google.gwt.user.client.rpc.SerializationStreamWriter", packageName);
        addImport(ARRAYLIST_DATA_TYPE, packageName);
        setPackageName(packageName);
        setClassName("RpcResponse_CustomFieldSerializer");
        setExtend("CustomFieldSerializer<RpcResponse>");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public static void deserialize(SerializationStreamReader streamReader, RpcResponse instance) throws SerializationException {\n"
                + "\n"
                + "        int size = streamReader.readInt();\n"
                + "        if (size < 0) {\n"
                + "            instance.setResult(null);\n"
                + "            return;\n"
                + "        }\n"
                + "\n"
                + "        ArrayList<Object> result = new ArrayList<Object>();\n"
                + "        for (int i = 0; i < size; ++i) {\n"
                + "            Object obj = streamReader.readObject();\n"
                + "            result.add(obj);\n"
                + "        }\n"
                + "        instance.setResult(result);\n"
                + "    }\n"
                + "\n"
                + "    public static void serialize(SerializationStreamWriter streamWriter, RpcResponse instance) throws SerializationException {\n"
                + "        ArrayList<Object> result = instance.getResult();\n"
                + "        if (result == null) {\n"
                + "            streamWriter.writeInt(-1);\n"
                + "            return;\n"
                + "        }\n"
                + "\n"
                + "        int size = result.size();\n"
                + "        streamWriter.writeInt(size);\n"
                + "        for (Object obj : result) {\n"
                + "            streamWriter.writeObject(obj);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public void deserializeInstance(SerializationStreamReader streamReader, RpcResponse instance) throws SerializationException {\n"
                + "        deserialize(streamReader, instance);\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public void serializeInstance(SerializationStreamWriter streamWriter, RpcResponse instance) throws SerializationException {\n"
                + "        serialize(streamWriter, instance);\n"
                + "    }");
    }
    
}
