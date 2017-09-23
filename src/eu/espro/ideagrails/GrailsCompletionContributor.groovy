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

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import eu.espro.ideagrails.completion.controller.ControllerCompletionProvider
import eu.espro.ideagrails.completion.controller.ControllerTestCompletionProvider
import eu.espro.ideagrails.completion.domain.DomainClassCompletionProvider
import groovy.transform.CompileStatic
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression

@CompileStatic
class GrailsCompletionContributor extends CompletionContributor {

    GrailsCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(GrReferenceExpression.class), new ControllerCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(GrReferenceExpression.class), new ControllerTestCompletionProvider());
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(PsiElement.class), new DomainClassCompletionProvider());
    }

}
