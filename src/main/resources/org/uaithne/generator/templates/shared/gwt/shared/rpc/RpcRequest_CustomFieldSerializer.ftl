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
import ${generation.sharedPackageDot}Operation;

public class RpcRequest_CustomFieldSerializer extends CustomFieldSerializer<RpcRequest> {

    public static void deserialize(SerializationStreamReader streamReader, RpcRequest instance) throws SerializationException {
        
        int size = streamReader.readInt();
        if (size < 0) {
            instance.setOperations(null);
            return;
        }
        
        ArrayList<Operation> result = new ArrayList<Operation>();
        for (int i = 0; i < size; ++i) {
            Operation obj = (Operation) streamReader.readObject();
            result.add(obj);
        }
        instance.setOperations(result);
    }

    public static void serialize(SerializationStreamWriter streamWriter, RpcRequest instance) throws SerializationException {
        ArrayList<Operation> result = instance.getOperations();
        if (result == null) {
            streamWriter.writeInt(-1);
            return;
        }
        
        int size = result.size();
        streamWriter.writeInt(size);
        for (Operation obj : result) {
            streamWriter.writeObject(obj);
        }
    }
    
    @Override
    public void deserializeInstance(SerializationStreamReader streamReader, RpcRequest instance) throws SerializationException {
        deserialize(streamReader, instance);
    }

    @Override
    public void serializeInstance(SerializationStreamWriter streamWriter, RpcRequest instance) throws SerializationException {
        serialize(streamWriter, instance);
    }
    
}
