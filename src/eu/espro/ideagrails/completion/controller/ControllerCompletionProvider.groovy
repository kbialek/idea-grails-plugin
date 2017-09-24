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
package eu.espro.ideagrails.completion.controller

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import eu.espro.ideagrails.GrailsControllerApi
import eu.espro.ideagrails.GrailsPsiUtil
import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.GrClassDefinition
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.TypesUtil
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GrLightVariable

import static eu.espro.ideagrails.GrailsCompletionUtil.methodLookupElement
import static eu.espro.ideagrails.GrailsCompletionUtil.variableLookupElement

@CompileStatic
class ControllerCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement parent = parameters.getPosition().getParent()
        if (parent instanceof GrReferenceExpression) {
            def controllerClass = PsiTreeUtil.getParentOfType(parent, GrClassDefinition)
            def cd = GrailsPsiUtil.resolveReference(parent as GrReferenceExpression)
            if (cd && cd.name?.endsWith("Controller") && cd == controllerClass &&
                    !GrailsPsiUtil.isClassImplementationOf(cd, 'grails.artefact.Controller')) {
                def method = GrailsPsiUtil.findEnclosingMethod(parent)
                if (method && !method.hasModifierProperty('static')) {
                    GrailsControllerApi.ControllerActionVars.each { name, typeFqn ->
                        def type = TypesUtil.createType(typeFqn, cd)
                        result.addElement(variableLookupElement(new GrLightVariable(cd.manager, name, type, cd.navigationElement)))
                    }
                    GrailsControllerApi.controllerActionMethods(cd).each { name, actionMethods ->
                        actionMethods.each { result.addElement(methodLookupElement(it)) }
                    }
                }
            }
        }
    }
}
