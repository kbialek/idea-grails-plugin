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
package eu.espro.ideagrails.references.domain

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import eu.espro.ideagrails.GrailsClassHelper
import eu.espro.ideagrails.GrailsDomainClassApi
import eu.espro.ideagrails.GrailsMethodUtil
import groovy.transform.CompileStatic
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrField
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.resolve.ClosureMissingMethodContributor

@CompileStatic
class MappingMissingMethodContributor extends ClosureMissingMethodContributor {

    @Override
    boolean processMembers(GrClosableBlock closure, PsiScopeProcessor processor, GrReferenceExpression expression, ResolveState resolveState) {
        PsiElement el = closure.parent
        if (!(el instanceof GrField)) {
            return true
        }

        GrField f = (GrField) el
        if ("mapping" == f.name && f.hasModifierProperty("static")) {
            PsiClass c = f.getContainingClass()
            if (GrailsClassHelper.hasMapping(c)) {
                def propertyName = expression.text
                if (propertyName in GrailsDomainClassApi.MappingMethods.keySet()) {
                    def mappingMethodParams = GrailsDomainClassApi.MappingMethods[propertyName]
                    def m = GrailsMethodUtil.createMethod(c, propertyName, Void.name)
                    mappingMethodParams.each { argName, argType ->
                        m.addParameter(argName, argType)
                    }
                    m.navigationElement = c.navigationElement
                    processor.execute(m, ResolveState.initial())
                    return false
                }
                def cf = c.findFieldByName(propertyName, true)
                if (!cf) {
                    cf = GrailsClassHelper.listGormProperties(c).find { it.name == propertyName }
                }
                if (cf) {
                    def m = GrailsMethodUtil.createMethod(c, cf.getName(), Void.name)
                    m.addParameter("mapping", "java.util.Map")
                    m.navigationElement = cf
                    processor.execute(m, ResolveState.initial())
                }
                return false
            }
            return true
        }
    }
}