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
package org.uaithne.generator.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.Utils;

public abstract class ClassTemplate {
    public static final String RESULT_BASE_DEFINITION = "RESULT";
    public static final String OPERATION_BASE_DEFINITION = "<RESULT, OPERATION extends Operation<RESULT>>";
    private String packageName;
    private String className;
    private String extend;
    private String documentation[];
    private final HashSet<String> implement = new HashSet<String>(0);
    private final HashSet<String> imports = new HashSet<String>(0);
    private final ArrayList<String> genericArguments = new ArrayList<String>(0);
    private boolean isInterface;
    private boolean isAbstract;
    private boolean isDeprecated;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public HashSet<String> getImplement() {
        return implement;
    }

    public void addImplement(String implement) {
        this.implement.add(implement);
    }

    public HashSet<String> getImport() {
        return imports;
    }
    
    public void addImport(String className, String currentPackage) {
        Utils.appendImportIfRequired(imports, currentPackage, className);
    }
    
    public void addImport(DataTypeInfo dataType, String currentPackage) {
        dataType.appendImports(currentPackage, imports);
    }

    public String[] getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String[] doc) {
        this.documentation = doc;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }

    public ArrayList<String> getGenericArguments() {
        return genericArguments;
    }

    public void addGenericArgument(String genericArgument) {
        this.genericArguments.add(genericArgument);
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean isDeprecated() {
        return isDeprecated;
    }

    public void setDeprecated(boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }
    
    public void write(Appendable appender) throws IOException {
        writeStartClass(appender);
        writeContent(appender);
        writeEndClass(appender);
    }
    
    protected abstract void writeContent(Appendable appender) throws IOException;
    
    protected void writeStartClass(Appendable appender) throws IOException {
        appender.append("package ").append(packageName).append(";\n\n");
        
        if (!imports.isEmpty()) {
            ArrayList<String> sortedImports = new ArrayList<String>(imports);
            Collections.sort(sortedImports);
            for (String s : sortedImports) {
                appender.append("import ").append(s).append(";\n");
            }
            appender.append("\n");
        }
        
        if (documentation != null) {
            appender.append("/**\n");
            for (String s : documentation) {
                appender.append(" * ").append(s).append("\n");
            }
            appender.append(" */\n");
        }
        
        writeClassAnnotations(appender);
        appender.append("public ");
        if (isAbstract) {
            appender.append("abstract ");
        }
        if (isInterface) {
            appender.append("interface ");
        } else {
            appender.append("class ");
        }
        appender.append(className);
        if (!genericArguments.isEmpty()) {
            appender.append("<");
            boolean requireComma = false;
            for (String s : genericArguments) {
                if (requireComma) {
                    appender.append(", ");
                }
                appender.append(s);
                requireComma = true;
            }
            appender.append(">");
        }
        if (extend != null) {
            appender.append(" extends ");
            appender.append(extend);
        }
        if (!implement.isEmpty()) {
            ArrayList<String> sortedImplement = new ArrayList<String>(implement);
            Collections.sort(sortedImplement);
            if (isInterface) {
                appender.append(" extends ");
            } else {
                appender.append(" implements ");
            }
            boolean requireComma = false;
            for (String s : sortedImplement) {
                if (requireComma) {
                    appender.append(", ");
                }
                appender.append(s);
                requireComma = true;
            }
        }
        appender.append(" {\n\n");
    }
    
    protected void writeEndClass(Appendable appender) throws IOException {
        appender.append("\n\n}");
    }
    
    protected void writeClassAnnotations(Appendable appender) throws IOException {
        if (isDeprecated) {
            appender.append("@Deprecated\n");
        }
    }
}
