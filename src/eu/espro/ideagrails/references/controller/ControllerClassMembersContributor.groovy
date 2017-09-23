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
package eu.espro.ideagrails.references.controller

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import eu.espro.ideagrails.GrailsControllerApi
import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.GrClassDefinition
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrMethod
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GrLightVariable
import org.jetbrains.plugins.groovy.lang.resolve.NonCodeMembersContributor

@CompileStatic
class ControllerClassMembersContributor extends NonCodeMembersContributor {


    @Override
    void processDynamicElements(
            @NotNull PsiType qualifierType, PsiClass aClass,
            @NotNull PsiScopeProcessor processor, @NotNull PsiElement place, @NotNull ResolveState state) {

        if (aClass instanceof GrClassDefinition && aClass.name.endsWith("Controller")) {
            if (place instanceof GrReferenceExpression) {
                def method = PsiTreeUtil.getParentOfType(place, GrMethod)
                if (method && !method.hasModifierProperty('static')) {
                    def memberName = place.lastChild.text
                    def actionMethods = GrailsControllerApi.controllerActionMethods(aClass)
                    if (memberName in actionMethods.keySet()) {
                        actionMethods[memberName].each { processor.execute(it, state) }
                    } else if (memberName in GrailsControllerApi.ControllerActionVars.keySet()) {
                        def varType = GrailsControllerApi.ControllerActionVars[memberName]
                        processor.execute(new GrLightVariable(aClass.manager, memberName, varType, aClass), state)
                    }
                }
            }
        }

    }

}


