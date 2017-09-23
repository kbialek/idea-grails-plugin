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

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.psiutils.TypeUtils
import eu.espro.ideagrails.GrailsClassHelper
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.extensions.GroovyNamedArgumentProvider
import org.jetbrains.plugins.groovy.extensions.NamedArgumentDescriptor
import org.jetbrains.plugins.groovy.extensions.impl.NamedArgumentDescriptorImpl
import org.jetbrains.plugins.groovy.extensions.impl.StringTypeCondition
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.GrClassDefinition

@CompileStatic
class ConstraintsNamedArgumentProvider extends GroovyNamedArgumentProvider {

    @Immutable(knownImmutableClasses = [NamedArgumentDescriptor])
    static class ConstraintDef {
        String className
        NamedArgumentDescriptor argDescriptor
    }

    private static final Map<String, ConstraintDef> ConstraintsMap = [
            creditCard: new ConstraintDef('org.grails.validation.CreditCardConstraint', NamedArgumentDescriptor.TYPE_BOOL),
            email     : new ConstraintDef('org.grails.validation.EmailConstraint', NamedArgumentDescriptor.TYPE_BOOL),
            blank     : new ConstraintDef('org.grails.validation.BlankConstraint', NamedArgumentDescriptor.TYPE_BOOL),
            range     : new ConstraintDef('org.grails.validation.RangeConstraint', new StringTypeCondition('groovy.lang.Range')),
            inList    : new ConstraintDef('org.grails.validation.InListConstraint', new StringTypeCondition('java.util.List')),
            url       : new ConstraintDef('org.grails.validation.UrlConstraint', NamedArgumentDescriptor.SIMPLE_ON_TOP),
            size      : new ConstraintDef('org.grails.validation.SizeConstraint', new StringTypeCondition('groovy.lang.IntRange')),
            matches   : new ConstraintDef('org.grails.validation.MatchesConstraint', NamedArgumentDescriptor.TYPE_STRING),
            min       : new ConstraintDef('org.grails.validation.MinConstraint', NamedArgumentDescriptor.TYPE_INTEGER),
            max       : new ConstraintDef('org.grails.validation.MaxConstraint', NamedArgumentDescriptor.TYPE_INTEGER),
            maxSize   : new ConstraintDef('org.grails.validation.MaxSizeConstraint', NamedArgumentDescriptor.TYPE_INTEGER),
            minSize   : new ConstraintDef('org.grails.validation.MinSizeConstraint', NamedArgumentDescriptor.TYPE_INTEGER),
            scale     : new ConstraintDef('org.grails.validation.ScaleConstraint', NamedArgumentDescriptor.TYPE_INTEGER),
            notEqual  : new ConstraintDef('org.grails.validation.NotEqualConstraint', NamedArgumentDescriptor.SIMPLE_ON_TOP),
            nullable  : new ConstraintDef('org.grails.validation.NullableConstraint', NamedArgumentDescriptor.TYPE_BOOL),
            validator : new ConstraintDef('org.grails.validation.ValidatorConstraint', new StringTypeCondition('groovy.lang.Closure')),
            unique    : new ConstraintDef('org.grails.orm.hibernate.validation.UniqueConstraint', NamedArgumentDescriptor.SIMPLE_ON_TOP)
    ]

    @Override
    void getNamedArguments(
            @NotNull GrCall call,
            @Nullable PsiElement resolve,
            @Nullable String argumentName, boolean forCompletion,
            @NotNull Map<String, NamedArgumentDescriptor> result) {
        def cd = PsiTreeUtil.getParentOfType(call, GrClassDefinition)
        if (cd && GrailsClassHelper.inConstraints(cd, call)) {
            if (forCompletion) {
                ConstraintsMap.each { constraintName, descriptor ->
                    addDescriptor(cd, result, constraintName, descriptor.argDescriptor)
                }
            }
            if (argumentName) {
                def descriptor = ConstraintsMap[argumentName]
                if (descriptor) {
                    def constraintClassType = TypeUtils.getType(descriptor.className, cd).resolve()
                    if (constraintClassType) {
                        def argDesc = new NamedArgumentDescriptorImpl(constraintClassType.navigationElement)
                        addDescriptor(cd, result, argumentName, argDesc)
                    } else {
                        addDescriptor(cd, result, argumentName, descriptor.argDescriptor)
                    }
                }
            } else if (call instanceof GrMethodCall) {
                // value validation
                GrMethodCall methodCall = call as GrMethodCall
                GrExpression invokedExpression = methodCall.getInvokedExpression()
                if (invokedExpression instanceof GrReferenceExpression) {
                    PsiElement[] children = invokedExpression.parent.children
                    def argList = children.length > 1 ? children[1] : null
                    if (argList instanceof GrArgumentList) {
                        def grArgList = argList as GrArgumentList
                        grArgList.getAllArguments().findAll { it instanceof GrNamedArgument }.each { arg ->
                            def namedArg = arg as GrNamedArgument
                            def descriptor = ConstraintsMap[namedArg.labelName]
                            if (descriptor) {
                                addDescriptor(cd, result, namedArg.labelName, descriptor.argDescriptor)
                            }
                        }
                    }
                }

            }

        }
    }

    private void addDescriptor(GrClassDefinition cd, Map<String, NamedArgumentDescriptor> result, String argumentName, NamedArgumentDescriptor descriptor) {
        def isDomainClass = GrailsClassHelper.hasMapping(cd)
        if (argumentName == 'unique' && !isDomainClass) {
            return
        } else {
            result.put(argumentName, descriptor)
        }
    }
}

