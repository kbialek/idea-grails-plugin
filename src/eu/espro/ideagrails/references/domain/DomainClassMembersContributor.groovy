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

import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import eu.espro.ideagrails.GrailsClassHelper
import eu.espro.ideagrails.GrailsDomainClassApi
import eu.espro.ideagrails.GrailsMethodUtil
import eu.espro.ideagrails.GrailsPsiUtil
import groovy.transform.CompileStatic
import org.apache.commons.lang.WordUtils
import org.jetbrains.annotations.NotNull
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.GrClassDefinition
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GrLightField
import org.jetbrains.plugins.groovy.lang.resolve.NonCodeMembersContributor

@CompileStatic
class DomainClassMembersContributor extends NonCodeMembersContributor {

    @Override
    void processDynamicElements(
            @NotNull PsiType qualifierType, PsiClass aClass, @NotNull PsiScopeProcessor processor,
            @NotNull PsiElement place, @NotNull ResolveState state) {
        if (aClass instanceof GrClassDefinition && place instanceof GrReferenceExpression) {
            PsiClass resolvedCls = GrailsPsiUtil.resolveReference(place as GrReferenceExpression)
            PsiElement property = place.getLastChild()
            if (resolvedCls instanceof GrClassDefinition) {
                GrClassDefinition cd = (GrClassDefinition) resolvedCls
                def memberName = property.text
                if (GrailsClassHelper.hasMapping(cd)) {
                    processGormStaticApi(processor, cd, property, state)
                    processGormDynamicApi(processor, cd, property, state)
                    GrailsClassHelper.listHasManyProperties(cd).each { f ->
                        def addToMethodName = "addTo" + WordUtils.capitalize(f.getName())
                        def removeFromMethodName = "removeFrom" + WordUtils.capitalize(f.getName())
                        if (f.name == memberName) {
                            if (!cd.findFieldByName(memberName, true)) {
                                processor.execute(f, state)
                            }
                        } else if (addToMethodName == memberName) {
                            PsiType fieldType = ((PsiClassType) f.getType()).getParameters()[0]
                            def mb = GrailsMethodUtil.createAddToMethod(cd, f.getName(), fieldType)
                            processor.execute(mb, state)
                        } else if (removeFromMethodName == memberName) {
                            PsiType fieldType = ((PsiClassType) f.getType()).getParameters()[0]
                            def mb = GrailsMethodUtil.createRemoveFromMethod(cd, f.getName(), fieldType)
                            processor.execute(mb, state)
                        }
                    }
                    if (!cd.findFieldByName(memberName, true)) {
                        GrailsClassHelper.listBelongsToProperties(cd).findAll { it.name == memberName }.each {
                            processor.execute(it, state)
                        }
                        GrailsClassHelper.listHasOneProperties(cd).findAll { it.name == memberName }.each {
                            processor.execute(it, state)
                        }
                    }
                }
            }
        }
    }

    private void processGormDynamicApi(PsiScopeProcessor processor,
                                       PsiClass domainClass,
                                       PsiElement element,
                                       ResolveState state) {
        String name = element.getText()
        if (name in GrailsDomainClassApi.Fields) {
            processor.execute(new GrLightField(domainClass, name, GrailsDomainClassApi.Fields[name]), state)
        } else {
            def methods = GrailsDomainClassApi.dynamicMethods(domainClass)
            if (methods[name]) {
                methods[name].each { processor.execute(it, state) }
            }
        }
    }

    private void processGormStaticApi(PsiScopeProcessor processor,
                                      PsiClass domainClass,
                                      PsiElement element,
                                      ResolveState state) {
        String methodName = element.getText()
        def methods = GrailsDomainClassApi.staticMethods(domainClass)
        if (methods[methodName]) {
            methods[methodName].each { processor.execute(it, state) }
        }
    }

}
