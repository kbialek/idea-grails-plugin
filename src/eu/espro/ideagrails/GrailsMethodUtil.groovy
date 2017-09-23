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
import com.intellij.psi.PsiType
import com.intellij.psi.search.GlobalSearchScope
import groovy.transform.CompileStatic
import org.apache.commons.lang.WordUtils
import org.jetbrains.plugins.groovy.lang.psi.api.statements.params.GrParameter
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GrLightMethodBuilder

import java.lang.reflect.Modifier

@CompileStatic
class GrailsMethodUtil {

    static GrLightMethodBuilder createAddToMethod(PsiClass domainClass, String fieldName, PsiType fieldType) {
        def methodName = "addTo" + WordUtils.capitalize(fieldName)
        GrLightMethodBuilder mb = createMethod(domainClass, methodName, domainClass.getQualifiedName())
        mb.addParameter("entity", fieldType)
        return mb
    }

    static GrLightMethodBuilder createRemoveFromMethod(PsiClass domainClass, String fieldName, PsiType fieldType) {
        def methodName = "removeFrom" + WordUtils.capitalize(fieldName)
        GrLightMethodBuilder mb = createMethod(domainClass, methodName, domainClass.getQualifiedName())
        mb.addParameter("entity", fieldType)
        return mb
    }

    static String renderParameters(GrParameter[] parameters) {
        '(' + parameters.collect { GrParameter it -> "${it.type.getPresentableText()} $it.name" }.join(", ") + ')'
    }

    static GrLightMethodBuilder createMethod(PsiClass cls, String methodName, String returnType) {
        GrLightMethodBuilder mb = new GrLightMethodBuilder(cls.getManager(), methodName)
        mb.setModifiers(Modifier.PUBLIC)
        if (returnType != null) {
            mb.setReturnType(returnType, GlobalSearchScope.allScope(cls.getProject()))
        }
        return mb
    }

    static GrLightMethodBuilder createMethod(PsiClass cls, String methodName, PsiType returnType) {
        GrLightMethodBuilder mb = new GrLightMethodBuilder(cls.getManager(), methodName)
        mb.setModifiers(Modifier.PUBLIC)
        if (returnType != null) {
            mb.setReturnType(returnType)
        }
        return mb
    }

    static GrLightMethodBuilder createStaticMethod(PsiClass cls, String methodName, String returnType) {
        GrLightMethodBuilder mb = new GrLightMethodBuilder(cls.getManager(), methodName)
        mb.setModifiers(Modifier.PUBLIC | Modifier.STATIC)
        if (returnType != null) {
            mb.setReturnType(returnType, GlobalSearchScope.allScope(cls.getProject()))
        }
        return mb
    }

    static GrLightMethodBuilder createStaticMethod(PsiClass cls, String methodName, PsiType returnType) {
        GrLightMethodBuilder mb = new GrLightMethodBuilder(cls.getManager(), methodName)
        mb.setModifiers(Modifier.PUBLIC | Modifier.STATIC)
        if (returnType != null) {
            mb.setReturnType(returnType)
        }
        return mb
    }

}
