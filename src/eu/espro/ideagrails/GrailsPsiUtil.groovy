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
package eu.espro.ideagrails

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import com.intellij.psi.search.PsiShortNamesCache
import groovy.transform.CompileStatic
import org.apache.commons.lang.WordUtils
import org.jetbrains.plugins.groovy.lang.completion.GroovyCompletionUtil
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrMethod
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrThisReferenceResolver

@CompileStatic
class GrailsPsiUtil {

    static GrMethod findEnclosingMethod(PsiElement node) {
        PsiElement result = node
        while (result && !(result instanceof GrMethod)) {
            result = result.parent
        }
        return result as GrMethod
    }

    static PsiClass resolveReference(GrReferenceExpression expression) {
        def thisResolved = GrThisReferenceResolver.resolveThisExpression(expression)
        def resolvedClass = (thisResolved ? thisResolved[0].element : null) as PsiClass
        if (resolvedClass == null) {
            def qualifier = expression.qualifierExpression
            PsiType qualifierType = GroovyCompletionUtil.getQualifierType(qualifier)
            if (qualifierType instanceof PsiClassType) {
                resolvedClass = ((PsiClassType) qualifierType)?.resolve()
            }
        }
        return resolvedClass
    }

    static PsiClass getControllerClassForUrlMapping(GrMethodCall mappingMethod) {
        PsiClass controllerPsiClass = null
        if (mappingMethod) {
            GrExpression invokedExpression = mappingMethod.getInvokedExpression()
            if (invokedExpression) {
                PsiElement[] children = invokedExpression.parent.children
                def argList = children.length > 1 ? children[1] : null
                if (argList instanceof GrArgumentList) {
                    def grArgList = argList as GrArgumentList
                    grArgList.getAllArguments().findAll { it instanceof GrNamedArgument }.any { arg ->
                        def namedArg = arg as GrNamedArgument
                        if (namedArg.labelName == 'controller' && namedArg.expression instanceof GrLiteral) {
                            def controllerName = (namedArg.expression as GrLiteral).value as String
                            def controllerClassName = WordUtils.capitalize(controllerName) + 'Controller'
                            def controllerPsiClasses = PsiShortNamesCache.getInstance(mappingMethod.project).getClassesByName(controllerClassName, mappingMethod.resolveScope)
                            if (controllerPsiClasses.length == 1) {
                                controllerPsiClass = controllerPsiClasses[0]
                                return true
                            }
                        }
                        return false
                    }
                }
            }
        }
        return controllerPsiClass
    }

    static String getActionNameForUrlMapping(GrMethodCall mappingMethod) {
        String actionName = null
        if (mappingMethod) {
            GrExpression invokedExpression = mappingMethod.getInvokedExpression()
            if (invokedExpression) {
                PsiElement[] children = invokedExpression.parent.children
                def argList = children.length > 1 ? children[1] : null
                if (argList instanceof GrArgumentList) {
                    def grArgList = argList as GrArgumentList
                    grArgList.getAllArguments().findAll { it instanceof GrNamedArgument }.any { arg ->
                        def namedArg = arg as GrNamedArgument
                        if (namedArg.labelName == 'action' && namedArg.expression instanceof GrLiteral) {
                            actionName = (namedArg.expression as GrLiteral).value as String
                        }
                        return false
                    }
                }
            }
        }
        return actionName
    }

    static boolean isClassImplementationOf(PsiClass cd, String interfaceFQN) {
        cd.implementsListTypes.any { PsiClassType it ->
            it.resolve()?.qualifiedName == interfaceFQN
        }
    }
}
