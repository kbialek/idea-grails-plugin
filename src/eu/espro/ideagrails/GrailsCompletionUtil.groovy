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

import com.intellij.codeInsight.completion.util.MethodParenthesesHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiVariable
import groovy.transform.CompileStatic
import icons.JetgroovyIcons
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GrLightMethodBuilder

@CompileStatic
class GrailsCompletionUtil {

    private static final String DefaultTailText = ' via Grails'

    static LookupElement classLookupElement(PsiClass clazz) {
        LookupElementBuilder.createWithSmartPointer(clazz.name, clazz.getNavigationElement()).
                withTypeText(clazz.qualifiedName).
                withBoldness(false).
                withIcon(JetgroovyIcons.Groovy.Class).
                withCaseSensitivity(true).withTailText(DefaultTailText, true)
    }

    static LookupElement variableLookupElement(PsiVariable variable) {
        LookupElementBuilder.createWithSmartPointer(variable.name, variable.getNavigationElement()).
                withTypeText(variable.getType().getPresentableText()).
                withBoldness(true).
                withIcon(JetgroovyIcons.Groovy.Variable).
                withCaseSensitivity(true).withTailText(DefaultTailText, true)
    }

    static LookupElement fieldLookupElement(PsiField field) {
        LookupElementBuilder.createWithSmartPointer(field.name, field.getNavigationElement()).
                withTypeText(field.getType().getPresentableText()).
                bold().appendTailText('', false).
                withIcon(JetgroovyIcons.Groovy.DynamicProperty).
                withCaseSensitivity(true).withTailText(DefaultTailText, true)
    }

    static LookupElement methodLookupElement(PsiMethod method) {
        def mb = LookupElementBuilder.createWithSmartPointer(method.name, method).
                withTypeText(method.getReturnType().getPresentableText())
        if (method instanceof GrLightMethodBuilder) {
            mb = mb.withTailText(GrailsMethodUtil.renderParameters((method as GrLightMethodBuilder).parameters))
        }
        mb = mb.appendTailText(DefaultTailText, true).
                withBoldness(true).
                withCaseSensitivity(true).
                withIcon(JetgroovyIcons.Groovy.Method).
                withInsertHandler(new MethodParenthesesHandler(method, false))
        return mb
    }
}
