/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.espro.ideagrails.completion.domain

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import com.intellij.util.ProcessingContext
import eu.espro.ideagrails.GrailsClassHelper
import eu.espro.ideagrails.GrailsDomainClassApi
import eu.espro.ideagrails.GrailsMethodUtil
import eu.espro.ideagrails.GrailsPsiUtil
import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GrLightField
import org.jetbrains.plugins.groovy.lang.resolve.ResolveUtil

import static eu.espro.ideagrails.GrailsCompletionUtil.fieldLookupElement
import static eu.espro.ideagrails.GrailsCompletionUtil.methodLookupElement

@CompileStatic
class DomainClassCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement parent = parameters.getPosition().getParent()
        if (parent instanceof GrReferenceExpression) {
            def cd = GrailsPsiUtil.resolveReference(parent as GrReferenceExpression)
            if (cd) {
                if (GrailsClassHelper.hasMapping(cd)) {
                    if (GrailsClassHelper.inMapping(cd, parent)) {
                        GrailsDomainClassApi.MappingMethods.each { methodName, methodParams ->
                            def m = GrailsMethodUtil.createMethod(cd, methodName, Void.name)
                            methodParams.each { argName, argType ->
                                m.addParameter(argName, argType)
                            }
                            result.addElement(methodLookupElement(m))
                        }
                    }
                    boolean isClass = ResolveUtil.resolvesToClass(parent.firstChild)
                    if (isClass) {
                        GrailsDomainClassApi.staticMethods(cd).each { methodName, methodVariants ->
                            methodVariants.each { result.addElement(methodLookupElement(it)) }
                        }
                    } else {
                        GrailsClassHelper.listHasManyProperties(cd).each { f ->
                            if (!cd.findFieldByName(f.name, true)) {
                                result.addElement(fieldLookupElement(f))
                            }
                            // addTo and removeFrom
                            PsiType fieldType = ((PsiClassType) f.getType()).getParameters()[0]
                            def addToMethod = GrailsMethodUtil.createAddToMethod(cd, f.name, fieldType)
                            result.addElement(methodLookupElement(addToMethod))
                            def removeFromMethod = GrailsMethodUtil.createRemoveFromMethod(cd, f.name, fieldType)
                            result.addElement(methodLookupElement(removeFromMethod))
                        }
                        GrailsClassHelper.listBelongsToProperties(cd).each {
                            if (!cd.findFieldByName(it.name, true)) {
                                result.addElement(fieldLookupElement(it))
                            }
                        }
                        GrailsClassHelper.listHasOneProperties(cd).each {
                            if (!cd.findFieldByName(it.name, true)) {
                                result.addElement(fieldLookupElement(it))
                            }
                        }
                        GrailsDomainClassApi.dynamicMethods(cd).each { methodName, methodVariants ->
                            methodVariants.each { result.addElement(methodLookupElement(it)) }
                        }
                        GrailsDomainClassApi.Fields.each { fieldName, fieldTypeName ->
                            result.addElement(fieldLookupElement(new GrLightField(cd, fieldName, fieldTypeName)))
                        }
                    }
                }
            }
        }
    }


}
