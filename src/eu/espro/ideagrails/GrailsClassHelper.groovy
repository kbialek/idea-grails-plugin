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

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import groovy.transform.CompileStatic
import org.jetbrains.plugins.groovy.lang.psi.api.auxiliary.GrListOrMap
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.TypesUtil
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GrLightField

@CompileStatic
class GrailsClassHelper {

    static final String MAPPING = "mapping"
    static final String CONSTRAINTS = "constraints"
    static final String HAS_MANY = "hasMany"
    static final String BELONGS_TO = "belongsTo"
    static final String HAS_ONE = "hasOne"
    static final String STATIC = "static"

    static boolean hasMapping(PsiClass c) {
        PsiField field = c?.findFieldByName(MAPPING, false)
        return field != null && field.hasModifierProperty(STATIC)
    }

    static boolean inMapping(PsiClass c, PsiElement e) {
        PsiField field = c?.findFieldByName(MAPPING, false)
        PsiTreeUtil.isAncestor(field, e, true)
    }

    static boolean hasConstraints(PsiClass c) {
        PsiField field = c?.findFieldByName(CONSTRAINTS, false)
        return field != null && field.hasModifierProperty(STATIC)
    }

    static boolean inConstraints(PsiClass c, PsiElement e) {
        PsiField field = c?.findFieldByName(CONSTRAINTS, false)
        PsiTreeUtil.isAncestor(field, e, true)
    }

    static boolean isHasMany(PsiField field) {
        field && HAS_MANY == field.name && field.hasModifierProperty(STATIC)
    }

    static boolean isBelongsTo(PsiField field) {
        field && BELONGS_TO == field.name && field.hasModifierProperty(STATIC)
    }

    static boolean isHasOne(PsiField field) {
        field && HAS_ONE == field.name && field.hasModifierProperty(STATIC)
    }

    static List<PsiField> listHasManyProperties(PsiClass c) {
        PsiField hasManyField = c.findFieldByName(HAS_MANY, false)
        if (isHasMany(hasManyField)) {
            doListGormProperties(c, hasManyField).collect {
                def type = TypesUtil.createGenericType("java.util.Collection", c, it.type)
                new GrLightField(it.containingClass, it.name, type, it.navigationElement)
            } as List<PsiField>
        } else {
            [] as List<PsiField>
        }
    }

    static List<PsiField> listBelongsToProperties(PsiClass c) {
        def field = c.findFieldByName(BELONGS_TO, false)
        isBelongsTo(field) ? doListGormProperties(c, field) : [] as List<PsiField>
    }

    static List<PsiField> listHasOneProperties(PsiClass c) {
        def field = c.findFieldByName(HAS_ONE, false)
        isHasOne(field) ? doListGormProperties(c, field) : [] as List<PsiField>
    }

    static List<PsiField> listGormProperties(PsiClass domainClass) {
        listBelongsToProperties(domainClass) + listHasManyProperties(domainClass) + listHasOneProperties(domainClass)
    }

    private static List<PsiField> doListGormProperties(PsiClass domainClass, PsiField gormStaticField) {
        def fc = gormStaticField.lastChild
        if (fc instanceof GrListOrMap) {
            fc.children.findAll { it instanceof GrNamedArgument }.findResults {
                def arg = (GrNamedArgument) it
                def propertyName = arg.labelName
                def propertyType = arg.expression.type as PsiClassType
                if (propertyName && propertyType) {
                    PsiType t = propertyType.getParameters()[0]
                    new GrLightField(domainClass, propertyName, t, it.getNavigationElement())
                }
            } as List<PsiField>
        } else {
            [] as List<PsiField>
        }
    }
}
