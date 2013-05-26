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
import org.uaithne.generator.templates.ClassTemplate;

public class RpcExceptionTemplate extends ClassTemplate {

    public RpcExceptionTemplate(String sharedGwtPackageDot, String sharedPackageDot) {
        String packageName = sharedGwtPackageDot + "shared.rpc";
        setPackageName(packageName);
        setClassName("RpcException");
        setExtend("RuntimeException");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public RpcException() {\n"
                + "    }\n"
                + "\n"
                + "    public RpcException(String msg) {\n"
                + "        super(msg);\n"
                + "    }\n"
                + "\n"
                + "    public RpcException(Throwable thrwbl) {\n"
                + "        super(thrwbl);\n"
                + "    }\n"
                + "\n"
                + "    public RpcException(String string, Throwable thrwbl) {\n"
                + "        super(string, thrwbl);\n"
                + "    }");
    }
    
}
