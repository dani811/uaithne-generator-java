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

import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import org.uaithne.annotations.gwt.SharedGwtLibrary;
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.TemplateProcessor;
import org.uaithne.generator.templates.shared.gwt.client.AsyncExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.gwt.client.AsyncExecutorTemplate;
import org.uaithne.generator.templates.shared.gwt.client.ChainedAsyncExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.gwt.client.ChainedAsyncExecutorTemplate;
import org.uaithne.generator.templates.shared.gwt.client.ChainedGroupingAsyncExecutorTemplate;
import org.uaithne.generator.templates.shared.gwt.client.ChainedMappedAsyncExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.gwt.client.LoggedAsyncExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.gwt.client.MappedAsyncExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.gwt.client.PostOperationAsyncExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.gwt.client.PostOperationAsyncExecutorTemplate;
import org.uaithne.generator.templates.shared.gwt.client.SyncAsyncExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.gwt.client.SyncAsyncExecutorTemplate;
import org.uaithne.generator.templates.shared.gwt.client.rpc.RpcAsyncExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.gwt.client.rpc.RpcResultTemplate;
import org.uaithne.generator.templates.shared.gwt.server.rpc.ExecutorGroupRpcImplTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.AwaitGwtOperationTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.AwaitGwtResultTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.CombinedGwtOperationTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.CombinedGwtResultTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.ExecutorGroupRpcAsyncTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.ExecutorGroupRpcTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.GwtOperationExecutorTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.RpcExceptionTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.RpcRequestTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.RpcRequest_CustomFieldSerializerTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.RpcResponseTemplate;
import org.uaithne.generator.templates.shared.gwt.shared.rpc.RpcResponse_CustomFieldSerializerTemplate;

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

                SharedGwtLibrary sl = element.getAnnotation(SharedGwtLibrary.class);
                if (sl != null) {
                    generationInfo.setIncludeGwtClientExecutors(sl.includeClientExecutors());
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

                boolean includeGwtClientExecutors = generationInfo.isIncludeGwtClientExecutors();

                String sharedPackageDot = generationInfo.getSharedPackageDot();
                String sharedGwtPackageDot = generationInfo.getSharedGwtPackageDot();
                
                processClassTemplate(new AsyncExecutorGroupTemplate(sharedGwtPackageDot, sharedPackageDot), element);

                if (includeGwtClientExecutors) {
                    processClassTemplate(new AsyncExecutorTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new ChainedAsyncExecutorTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new ChainedAsyncExecutorGroupTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new ChainedGroupingAsyncExecutorTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new ChainedMappedAsyncExecutorGroupTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new MappedAsyncExecutorGroupTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new PostOperationAsyncExecutorTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new PostOperationAsyncExecutorGroupTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new LoggedAsyncExecutorGroupTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new SyncAsyncExecutorTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                    processClassTemplate(new SyncAsyncExecutorGroupTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                }

                processClassTemplate(new RpcResultTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new RpcAsyncExecutorGroupTemplate(sharedGwtPackageDot, sharedPackageDot), element);

                processClassTemplate(new ExecutorGroupRpcTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new ExecutorGroupRpcAsyncTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new CombinedGwtOperationTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new AwaitGwtResultTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new AwaitGwtOperationTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new GwtOperationExecutorTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new CombinedGwtResultTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new RpcRequestTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new RpcResponseTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new RpcRequest_CustomFieldSerializerTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new RpcResponse_CustomFieldSerializerTemplate(sharedGwtPackageDot, sharedPackageDot), element);
                processClassTemplate(new RpcExceptionTemplate(sharedGwtPackageDot, sharedPackageDot), element);

                processClassTemplate(new ExecutorGroupRpcImplTemplate(sharedGwtPackageDot, sharedPackageDot), element);
            }
        }
        return true; // no further processing of this annotation type
    }

}
