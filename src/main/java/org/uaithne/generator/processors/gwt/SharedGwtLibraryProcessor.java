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
package org.uaithne.generator.processors.gwt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.uaithne.annotations.gwt.SharedGwtLibrary;
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.TemplateProcessor;
import org.uaithne.generator.commons.Utils;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.gwt.SharedGwtLibrary")
public class SharedGwtLibraryProcessor extends TemplateProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        GenerationInfo generationInfo = getGenerationInfo();
        for (Element element : re.getElementsAnnotatedWith(SharedGwtLibrary.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                String packageName = NamesGenerator.createPackageNameFromFullName(classElement.getQualifiedName());

                boolean rpcErrorAsString = false;
                SharedGwtLibrary sl = element.getAnnotation(SharedGwtLibrary.class);
                if (sl != null) {
                    generationInfo.setIncludeGwtClientExecutors(sl.includeClientExecutors());
                    rpcErrorAsString = sl.rpcErrorAsString();
                    if (packageName == null || packageName.isEmpty()) {
                        generationInfo.setSharedGwtPackage("");
                        generationInfo.setSharedGwtPackageDot("");
                    } else {
                        generationInfo.setSharedGwtPackage(packageName);
                        generationInfo.setSharedGwtPackageDot(packageName + ".");
                    }
                    if (!sl.generate()) {
                        continue;
                    }
                }

                packageName = generationInfo.getSharedGwtPackageDot() + "client";
                HashMap<String, Object> data = createDefaultData();
                HashSet<String> imports = new HashSet<String>();
                Utils.appendImportIfRequired(imports, packageName, "com.google.gwt.user.client.rpc.AsyncCallback");
                Utils.appendImportIfRequired(imports, packageName, generationInfo.getSharedPackageDot() + "Operation");
                data.put("imports", imports);
                data.put("rpcErrorAsString", rpcErrorAsString);

                boolean includeGwtClientExecutors = generationInfo.isIncludeGwtClientExecutors();

                processRpcClientClassTemplate("AsyncExecutorGroup", packageName, data, element);

                if (includeGwtClientExecutors) {
                    processRpcClientClassTemplate("AsyncExecutor", packageName, data, element);
                    processRpcClientClassTemplate("ChainedAsyncExecutor", packageName, data, element);
                    processRpcClientClassTemplate("ChainedAsyncExecutorGroup", packageName, data, element);
                    processRpcClientClassTemplate("ChainedGroupingAsyncExecutor", packageName, data, element);
                    processRpcClientClassTemplate("ChainedMappedAsyncExecutorGroup", packageName, data, element);
                    processRpcClientClassTemplate("MappedAsyncExecutorGroup", packageName, data, element);
                    processRpcClientClassTemplate("PostOperationAsyncExecutor", packageName, data, element);
                    processRpcClientClassTemplate("PostOperationAsyncExecutorGroup", packageName, data, element);
                    processRpcClientClassTemplate("LoggedAsyncExecutorGroup", packageName, data, element);

                    HashSet<String> imports2 = new HashSet<String>(imports);
                    Utils.appendImportIfRequired(imports, packageName, generationInfo.getSharedPackageDot() + "Executor");
                    processRpcClientClassTemplate("SyncAsyncExecutor", packageName, data, element);

                    Utils.appendImportIfRequired(imports2, packageName, generationInfo.getSharedPackageDot() + "ExecutorGroup");
                    data.put("imports", imports2);
                    processRpcClientClassTemplate("SyncAsyncExecutorGroup", packageName, data, element);
                }

                data = createDefaultData();
                data.put("rpcErrorAsString", rpcErrorAsString);

                packageName = generationInfo.getSharedGwtPackageDot() + "client.rpc";
                processRpcClientRpcClassTemplate("RpcResult", packageName, data, element);
                processRpcClientRpcClassTemplate("RpcAsyncExecutorGroup", packageName, data, element);

                packageName = generationInfo.getSharedGwtPackageDot() + "shared.rpc";
                processRpcSharedRpcClassTemplate("ExecutorGroupRpc", packageName, data, element);
                processRpcSharedRpcClassTemplate("ExecutorGroupRpcAsync", packageName, data, element);
                processRpcSharedRpcClassTemplate("CombinedGwtOperation", packageName, data, element);
                processRpcSharedRpcClassTemplate("AwaitGwtResult", packageName, data, element);
                processRpcSharedRpcClassTemplate("AwaitGwtOperation", packageName, data, element);
                processRpcSharedRpcClassTemplate("GwtOperationExecutor", packageName, data, element);
                processRpcSharedRpcClassTemplate("CombinedGwtResult", packageName, data, element);
                processRpcSharedRpcClassTemplate("RpcRequest", packageName, data, element);
                processRpcSharedRpcClassTemplate("RpcResponse", packageName, data, element);
                processRpcSharedRpcClassTemplate("RpcRequest_CustomFieldSerializer", packageName, data, element);
                processRpcSharedRpcClassTemplate("RpcResponse_CustomFieldSerializer", packageName, data, element);
                processRpcSharedRpcClassTemplate("ErrorResult", packageName, data, element);
                if (rpcErrorAsString) {
                    processRpcSharedRpcClassTemplate("RpcException", packageName, data, element);
                }

                packageName = generationInfo.getSharedGwtPackageDot() + "server.rpc";
                processRpcServerRpcClassTemplate("ExecutorGroupRpcImpl", packageName, data, element);
            }
        }
        return true; // no further processing of this annotation type
    }

    public boolean processRpcClientClassTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("shared/gwt/client/" + className + ".ftl", packageName, className, data, element);
    }

    public boolean processRpcClientRpcClassTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("shared/gwt/client/rpc/" + className + ".ftl", packageName, className, data, element);
    }

    public boolean processRpcSharedRpcClassTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("shared/gwt/shared/rpc/" + className + ".ftl", packageName, className, data, element);
    }

    public boolean processRpcServerRpcClassTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("shared/gwt/server/rpc/" + className + ".ftl", packageName, className, data, element);
    }

}
