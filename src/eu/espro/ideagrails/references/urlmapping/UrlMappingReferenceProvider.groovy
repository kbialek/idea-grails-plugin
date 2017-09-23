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
package eu.espro.ideagrails.references.urlmapping

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import eu.espro.ideagrails.GrailsCompletionUtil
import eu.espro.ideagrails.GrailsPsiUtil
import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull
import org.jetbrains.plugins.groovy.lang.psi.api.auxiliary.GrListOrMap
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrAssignmentExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.GrClassDefinition

@CompileStatic
class UrlMappingReferenceProvider extends PsiReferenceProvider {

    @Override
    PsiReference[] getReferencesByElement(
            @NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {

        List<PsiReference> references = null
        def cd = PsiTreeUtil.getParentOfType(psiElement, GrClassDefinition)
        if (cd && cd.name.endsWith('UrlMappings') && psiElement instanceof GrLiteral) {
            GrLiteral literalExpression = (GrLiteral) psiElement
            def mc = PsiTreeUtil.getParentOfType(literalExpression, GrMethodCall)
            def controllerPsiClass = GrailsPsiUtil.getControllerClassForUrlMapping(mc)
            if (controllerPsiClass) {
                String actionName = null
                if (psiElement.parent instanceof GrNamedArgument) {
                    def parent = psiElement.parent as GrNamedArgument
                    if (parent.labelName == 'controller') {
                        references = [new ControllerReference(controllerPsiClass, literalExpression, true)] as List<PsiReference>
                    } else if (parent.labelName == 'action') {
                        actionName = GrailsPsiUtil.getActionNameForUrlMapping(mc)
                    }
                }
                if (!references) {
                    if (!actionName) {
                        def ae = PsiTreeUtil.getParentOfType(literalExpression, GrAssignmentExpression)
                        if (controllerPsiClass && ae && ae.LValue instanceof GrReferenceExpression && ae.RValue instanceof GrListOrMap) {
                            def lv = ae.LValue as GrReferenceExpression
                            if (lv.referenceName == 'action') {
                                actionName = literalExpression.getValue() as String
                            }
                        }
                    }
                    if (actionName) {
                        def actionMethods = controllerPsiClass.findMethodsByName(actionName, true)
                        references = actionMethods.collect { PsiMethod it ->
                            new ActionReference(it, literalExpression, true)
                        } as List<PsiReference>
                    }
                }
            }
        }

        return (references ?: []) as PsiReference[]
    }

    private static class ControllerReference extends PsiReferenceBase<GrLiteral> {

        private final PsiClass controller

        ControllerReference(PsiClass controller, GrLiteral element, boolean soft) {
            super(element, soft)
            this.controller = controller
        }

        @Override
        PsiElement resolve() {
            controller
        }

        @Override
        Object[] getVariants() {
            [GrailsCompletionUtil.classLookupElement(controller)] as Object[]
        }

        @Override
        String getCanonicalText() {
            controller.qualifiedName
        }
    }

    private static class ActionReference extends PsiReferenceBase<GrLiteral> {

        private final PsiMethod actionMethod

        ActionReference(PsiMethod actionMethod, GrLiteral element, boolean soft) {
            super(element, soft)
            this.actionMethod = actionMethod
        }

        @Override
        PsiElement resolve() {
            actionMethod
        }

        @Override
        Object[] getVariants() {
            [GrailsCompletionUtil.methodLookupElement(actionMethod)] as Object[]
        }
    }
}
