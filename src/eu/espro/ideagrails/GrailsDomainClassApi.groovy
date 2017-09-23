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
import com.intellij.psi.PsiEllipsisType
import com.intellij.psi.PsiMethod
import com.siyeh.ig.psiutils.TypeUtils
import groovy.transform.CompileStatic
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.TypesUtil

@CompileStatic
class GrailsDomainClassApi {

    static final Map<String, String> Fields = [
            id        : Long.name,
            version   : Integer.name,
            errors    : 'org.springframework.validation.Errors',
            properties: Map.name
    ]

    static Map<String, List<PsiMethod>> staticMethods(PsiClass dc) {
        def dcType = TypeUtils.getType(dc)
        def listOfDC = TypesUtil.createGenericType(List.name, dc.context, dcType)
        def detachedCriteriaOfDC = TypesUtil.createGenericType('grails.gorm.DetachedCriteria', dc.context, dcType)
        def listOfSerializable = TypesUtil.createGenericType(List.name, dc.context, TypeUtils.getType(Serializable.name, dc.context))
        [
                'count'                  : [
                        GrailsMethodUtil.createStaticMethod(dc, 'count', Integer.name)
                ],
                'createCriteria'         : [
                        GrailsMethodUtil.createStaticMethod(dc, 'createCriteria', 'org.grails.datastore.mapping.query.api.BuildableCriteria')
                ],
                'executeQuery'           : [
                        GrailsMethodUtil.createStaticMethod(dc, 'executeQuery', List.name).with {
                            it.addParameter('query', String.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'executeQuery', List.name).with {
                            it.addParameter('query', String.name)
                            it.addParameter('positionalParams', List.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'executeQuery', List.name).with {
                            it.addParameter('query', String.name)
                            it.addParameter('positionalParams', List.name)
                            it.addParameter('metaParams', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'executeQuery', List.name).with {
                            it.addParameter('query', String.name)
                            it.addParameter('namedParams', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'executeQuery', List.name).with {
                            it.addParameter('query', String.name)
                            it.addParameter('namedParams', Map.name)
                            it.addParameter('metaParams', Map.name)
                        }
                ],
                'executeUpdate'          : [
                        GrailsMethodUtil.createStaticMethod(dc, 'executeUpdate', Integer.name).with {
                            it.addParameter('query', String.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'executeUpdate', Integer.name).with {
                            it.addParameter('query', String.name)
                            it.addParameter('positionalParams', List.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'executeUpdate', Integer.name).with {
                            it.addParameter('query', String.name)
                            it.addParameter('namedParams', Map.name)
                        }
                ],
                'exists'                 : [
                        GrailsMethodUtil.createStaticMethod(dc, "exists", boolean.name).with {
                            it.addParameter("callable", Closure.name)
                        }
                ],
                'find'                   : [
                        GrailsMethodUtil.createStaticMethod(dc, 'find', dc.qualifiedName).with {
                            it.addParameter('query', String.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'find', dc.qualifiedName).with {
                            it.addParameter('query', String.name)
                            it.addParameter('positionalParams', List.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'find', dc.qualifiedName).with {
                            it.addParameter('query', String.name)
                            it.addParameter('positionalParams', List.name)
                            it.addParameter('queryParams', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'find', dc.qualifiedName).with {
                            it.addParameter('query', String.name)
                            it.addParameter('namedParams', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'find', dc.qualifiedName).with {
                            it.addParameter('query', String.name)
                            it.addParameter('namedParams', Map.name)
                            it.addParameter('queryParams', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'find', dc.qualifiedName).with {
                            it.addParameter('example', dc.qualifiedName)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'find', dc.qualifiedName).with {
                            it.addParameter('whereCriteria', Closure.name)
                        }
                ],
                'findAll'                : [
                        GrailsMethodUtil.createStaticMethod(dc, 'findAll', listOfDC),
                        GrailsMethodUtil.createStaticMethod(dc, 'findAll', listOfDC).with {
                            it.addParameter('query', String.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'findAll', listOfDC).with {
                            it.addParameter('query', String.name)
                            it.addParameter('positionalParams', List.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'findAll', listOfDC).with {
                            it.addParameter('query', String.name)
                            it.addParameter('positionalParams', List.name)
                            it.addParameter('queryParams', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'findAll', listOfDC).with {
                            it.addParameter('query', String.name)
                            it.addParameter('namedParams', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'findAll', listOfDC).with {
                            it.addParameter('query', String.name)
                            it.addParameter('namedParams', Map.name)
                            it.addParameter('queryParams', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'findAll', listOfDC).with {
                            it.addParameter('example', listOfDC)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'findAll', listOfDC).with {
                            it.addParameter('whereCriteria', Closure.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'findAll', listOfDC).with {
                            it.addParameter('queryParams', Map.name)
                            it.addParameter('whereCriteria', Closure.name)
                        }
                ],
                'findAllWhere'           : [
                        GrailsMethodUtil.createStaticMethod(dc, 'findAllWhere', listOfDC).with {
                            it.addParameter('queryMap', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'findAllWhere', listOfDC).with {
                            it.addParameter('queryMap', Map.name)
                            it.addParameter('args', Map.name)
                        }
                ],
                'findOrCreateWhere'      : [
                        GrailsMethodUtil.createStaticMethod(dc, 'findOrCreateWhere', dcType).with {
                            it.addParameter('queryMap', Map.name)
                        }
                ],
                'findOrSaveWhere'        : [
                        GrailsMethodUtil.createStaticMethod(dc, 'findOrSaveWhere', dcType).with {
                            it.addParameter('queryMap', Map.name)
                        }
                ],
                'findWhere'              : [
                        GrailsMethodUtil.createStaticMethod(dc, 'findWhere', dcType).with {
                            it.addParameter('queryMap', Map.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'findWhere', dcType).with {
                            it.addParameter('queryMap', Map.name)
                            it.addParameter('args', Map.name)
                        }
                ],
                'first'                  : [
                        GrailsMethodUtil.createStaticMethod(dc, 'first', dcType),
                        GrailsMethodUtil.createStaticMethod(dc, 'first', dcType).with {
                            it.addParameter('propertyName', String.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'first', dcType).with {
                            it.addParameter('queryMap', Map.name)
                        }
                ],
                'get'                    : [
                        GrailsMethodUtil.createStaticMethod(dc, 'get', dcType).with {
                            it.addParameter('id', Serializable.name)
                        }
                ],
                'getAll'                 : [
                        GrailsMethodUtil.createStaticMethod(dc, 'getAll', listOfDC),
                        GrailsMethodUtil.createStaticMethod(dc, 'getAll', listOfDC).with {
                            it.addParameter('ids', listOfSerializable)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'getAll', listOfDC).with {
                            it.addParameter('ids', new PsiEllipsisType(TypeUtils.getType(Serializable.name, dc.context)))
                        }
                ],
                'getAsync'               : [
                        GrailsMethodUtil.createStaticMethod(dc, 'getAsync',
                                TypesUtil.createGenericType('org.grails.datastore.gorm.async.GormAsyncStaticApi', dc.context, dcType))
                ],
                'getCount'               : [
                        GrailsMethodUtil.createStaticMethod(dc, 'getCount', Integer.name)
                ],
                'getGormDynamicFinders'  : [
                        GrailsMethodUtil.createStaticMethod(dc, 'getGormDynamicFinders',
                                TypesUtil.createGenericType(List.name, dc.context,
                                        TypeUtils.getType('org.grails.datastore.gorm.finders.FinderMethod', dc.context))
                        )
                ],
                'getGormPersistentEntity': [
                        GrailsMethodUtil.createStaticMethod(dc, 'getGormPersistentEntity',
                                'org.grails.datastore.mapping.model.PersistentEntity')
                ],
                'instanceOf'             : [
                        GrailsMethodUtil.createStaticMethod(dc, 'instanceOf', boolean.name).with {
                            it.addParameter('instance', dcType)
                            it.addParameter('cls', 'java.lang.Class')
                        }
                ],
                'isAttached'             : [
                        GrailsMethodUtil.createStaticMethod(dc, 'isAttached', boolean.name).with {
                            it.addParameter('instance', dcType)
                        }
                ],
                'last'                   : [
                        GrailsMethodUtil.createStaticMethod(dc, 'last', dcType),
                        GrailsMethodUtil.createStaticMethod(dc, 'last', dcType).with {
                            it.addParameter('propertyName', String.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'last', dcType).with {
                            it.addParameter('queryMap', Map.name)
                        }
                ],
                'list'                   : [
                        GrailsMethodUtil.createStaticMethod(dc, 'list', listOfDC),
                        GrailsMethodUtil.createStaticMethod(dc, 'list', listOfDC).with {
                            it.addParameter('params', Map.name)
                        }
                ],
                'load'                   : [
                        GrailsMethodUtil.createStaticMethod(dc, 'load', dcType).with {
                            it.addParameter('id', Serializable.name)
                        }
                ],
                'lock'                   : [
                        GrailsMethodUtil.createStaticMethod(dc, 'lock', dcType).with {
                            it.addParameter('id', Serializable.name)
                        }
                ],
                'merge'                  : [
                        GrailsMethodUtil.createStaticMethod(dc, 'merge', dcType).with {
                            it.addParameter('d', dcType)
                        }
                ],
                'proxy'                  : [
                        GrailsMethodUtil.createStaticMethod(dc, 'proxy', dcType).with {
                            it.addParameter('id', Serializable.name)
                        }
                ],
                'read'                   : [
                        GrailsMethodUtil.createStaticMethod(dc, 'read', dcType).with {
                            it.addParameter('id', Serializable.name)
                        }
                ],
                'refresh'                : [
                        GrailsMethodUtil.createStaticMethod(dc, 'refresh', dcType).with {
                            it.addParameter('instance', dcType)
                        }
                ],
                'saveAll'                : [
                        GrailsMethodUtil.createStaticMethod(dc, 'saveAll', listOfSerializable).with {
                            it.addParameter('objectsToSave', new PsiEllipsisType(TypeUtils.getType(Object.name, dc.context)))
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'saveAll', listOfSerializable).with {
                            it.addParameter('objectsToSave', Iterable.name)
                        }
                ],
                'where'                  : [
                        GrailsMethodUtil.createStaticMethod(dc, 'where', detachedCriteriaOfDC).with {
                            it.addParameter('callable', Closure.name)
                        }
                ],
                'whereAny'               : [
                        GrailsMethodUtil.createStaticMethod(dc, 'whereAny', detachedCriteriaOfDC).with {
                            it.addParameter('callable', Closure.name)
                        }
                ],
                'whereLazy'              : [
                        GrailsMethodUtil.createStaticMethod(dc, 'whereLazy', detachedCriteriaOfDC).with {
                            it.addParameter('callable', Closure.name)
                        }
                ],
                'withCriteria'           : [
                        GrailsMethodUtil.createStaticMethod(dc, 'withCriteria', Object.name).with {
                            it.addParameter('callable', Closure.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'withCriteria', Object.name).with {
                            it.addParameter('builderArgs', Map.name)
                            it.addParameter('callable', Closure.name)
                        }
                ],
                'withDatastoreSession'   : [
                        GrailsMethodUtil.createStaticMethod(dc, 'withDatastoreSession', Object.name).with {
                            it.addParameter('callable', Closure.name)
                        }
                ],
                'withNewSession'         : [
                        GrailsMethodUtil.createStaticMethod(dc, 'withNewSession', Object.name).with {
                            it.addParameter('callable', Closure.name)
                        }
                ],
                'withNewTransaction'     : [
                        GrailsMethodUtil.createStaticMethod(dc, 'withNewTransaction', Object.name).with {
                            it.addParameter('callable', Closure.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'withNewTransaction', Object.name).with {
                            it.addParameter('transactionProperties', Map.name)
                            it.addParameter('callable', Closure.name)
                        }
                ],
                'withSession'            : [
                        GrailsMethodUtil.createStaticMethod(dc, "withSession", Object.name).with {
                            it.addParameter("callable", Closure.name)
                        }
                ],
                'withStatelessSession'   : [
                        GrailsMethodUtil.createStaticMethod(dc, "withStatelessSession", Object.name).with {
                            it.addParameter("callable", Closure.name)
                        }
                ],
                'withTenant'             : [
                        GrailsMethodUtil.createStaticMethod(dc, "withTenant",
                                TypesUtil.createGenericType('grails.gorm.api.GormAllOperations', dc.context, dcType)).with {
                            it.addParameter("tenantId", Serializable.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, "withTenant", Object.name).with {
                            it.addParameter("tenantId", Serializable.name)
                            it.addParameter("callable", Closure.name)
                        }
                ],
                'withTransaction'        : [
                        GrailsMethodUtil.createStaticMethod(dc, 'withTransaction', Object.name).with {
                            it.addParameter('callable', Closure.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'withTransaction', Object.name).with {
                            it.addParameter('transactionProperties', Map.name)
                            it.addParameter('callable', Closure.name)
                        },
                        GrailsMethodUtil.createStaticMethod(dc, 'withTransaction', Object.name).with {
                            it.addParameter('definition', 'org.springframework.transaction.TransactionDefinition')
                            it.addParameter('callable', Closure.name)
                        }
                ]

        ]

    }

    static Map<String, List<PsiMethod>> dynamicMethods(PsiClass dc) {
        def dcType = TypeUtils.getType(dc)
        [
                'attach'     : [
                        GrailsMethodUtil.createMethod(dc, 'attach', dc.qualifiedName)
                ],
                'clearErrors': [
                        GrailsMethodUtil.createMethod(dc, 'clearErrors', void.name)
                ],
                'delete'     : [
                        GrailsMethodUtil.createMethod(dc, 'delete', void.name),
                        GrailsMethodUtil.createMethod(dc, 'delete', void.name).with {
                            it.addParameter('params', Map.name)
                        }
                ],
                'discard'    : [
                        GrailsMethodUtil.createMethod(dc, 'discard', void.name)
                ],
                'ident'      : [
                        GrailsMethodUtil.createMethod(dc, 'ident', Serializable.name)
                ],
                'insert'     : [
                        GrailsMethodUtil.createMethod(dc, 'insert', dcType),
                        GrailsMethodUtil.createMethod(dc, 'insert', dcType).with {
                            it.addParameter('params', Map.name)
                        }
                ],
                'isAttached' : [
                        GrailsMethodUtil.createMethod(dc, 'isAttached', boolean.name)
                ],
                'lock'       : [
                        GrailsMethodUtil.createMethod(dc, 'lock', dcType)
                ],
                'merge'      : [
                        GrailsMethodUtil.createMethod(dc, 'merge', dcType),
                        GrailsMethodUtil.createMethod(dc, 'merge', dcType).with {
                            it.addParameter('params', Map.name)
                        }
                ],
                'save'       : [
                        GrailsMethodUtil.createMethod(dc, 'save', dcType),
                        GrailsMethodUtil.createMethod(dc, 'save', dcType).with {
                            it.addParameter('validate', boolean.name)
                        },
                        GrailsMethodUtil.createMethod(dc, 'save', dcType).with {
                            it.addParameter('params', Map.name)
                        }
                ]
        ]

    }

    static final Map<String, Map<String, String>> MappingMethods = [
            autoImport   : [enabled: boolean.name],
            autoTimestamp: [enabled: boolean.name],
            batchSize    : [value: int.name],
            cache        : [enabled: boolean.name],
            comment      : [value: String.name],
            discriminator: [value: String.name],
            dynamicInsert: [enabled: boolean.name],
            dynamicUpdate: [enabled: boolean.name],
            id           : [generator: String.name, params: Map.name],
            order        : [name: String.name],
            sort         : [name: String.name],
            table        : [name: String.name],
            version      : [enabled: boolean.name],
    ] as Map<String, Map<String, String>>

}
