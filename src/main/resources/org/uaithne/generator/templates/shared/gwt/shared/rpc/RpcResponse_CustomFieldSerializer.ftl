<#-- 
Copyright 2012 and beyond, Juan Luis Paz

This file is part of Uaithne.

Uaithne is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Uaithne is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Uaithne. If not, see <http://www.gnu.org/licenses/>.
-->
package ${packageName};

import com.google.gwt.user.client.rpc.CustomFieldSerializer;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import java.util.ArrayList;

public class RpcResponse_CustomFieldSerializer extends CustomFieldSerializer<RpcResponse> {

    public static void deserialize(SerializationStreamReader streamReader, RpcResponse instance) throws SerializationException {
        
        int size = streamReader.readInt();
        if (size < 0) {
            instance.setResult(null);
            return;
        }
        
        ArrayList<Object> result = new ArrayList<Object>();
        for (int i = 0; i < size; ++i) {
            Object obj = streamReader.readObject();
            result.add(obj);
        }
        instance.setResult(result);
    }

    public static void serialize(SerializationStreamWriter streamWriter, RpcResponse instance) throws SerializationException {
        ArrayList<Object> result = instance.getResult();
        if (result == null) {
            streamWriter.writeInt(-1);
            return;
        }
        
        int size = result.size();
        streamWriter.writeInt(size);
        for (Object obj : result) {
            streamWriter.writeObject(obj);
        }
    }
    
    @Override
    public void deserializeInstance(SerializationStreamReader streamReader, RpcResponse instance) throws SerializationException {
        deserialize(streamReader, instance);
    }

    @Override
    public void serializeInstance(SerializationStreamWriter streamWriter, RpcResponse instance) throws SerializationException {
        serialize(streamWriter, instance);
    }
    
}
