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
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import groovy.transform.CompileStatic
import org.jetbrains.plugins.groovy.lang.psi.impl.synthetic.GrLightField

@CompileStatic
class GrailsControllerApi {

    static final Map<String, String> ControllerActionVars = [
            actionName       : 'java.lang.String',
            controllerName   : 'java.lang.String',
            errors           : 'org.springframework.validation.Errors',
            flash            : 'java.util.Map',
            grailsApplication: 'grails.core.GrailsApplication',
            params           : 'java.util.Map',
            request          : 'javax.servlet.http.HttpServletRequest',
            response         : 'javax.servlet.http.HttpServletResponse',
            servletContext   : 'javax.servlet.ServletContext',
            session          : 'javax.servlet.http.HttpSession'
    ]

    static final Map<String, String> ControllerStaticFields = [
            allowedMethods : 'java.util.Map',
            defaultAction  : 'java.lang.String',
            namespace      : 'java.lang.String',
            responseFormats: 'java.util.List<java.lang.String>',
            scope          : 'java.lang.String'
    ]

    static Map<String, List<PsiMethod>> controllerActionMethods(PsiClass controllerClass) {
        [
                bindData  : [
                        GrailsMethodUtil.createMethod(controllerClass, 'bindData', Void.name).with {
                            it.addParameter('target', 'java.lang.Object')
                            it.addParameter('params', 'java.util.Map')
                            it.addParameter('prefix', 'java.lang.String', true)
                        },
                        GrailsMethodUtil.createMethod(controllerClass, 'bindData', Void.name).with {
                            it.addParameter('target', 'java.lang.Object')
                            it.addParameter('params', 'java.util.Map')
                            it.addParameter('includesExcludes', 'java.util.Map', true)
                            it.addParameter('prefix', 'java.lang.String', true)
                        }
                ],
                forward   : [
                        GrailsMethodUtil.createMethod(controllerClass, 'forward', Void.name).with {
                            it.addParameter('args', 'java.util.Map')
                        }
                ],
                hasErrors : [
                        GrailsMethodUtil.createMethod(controllerClass, 'hasErrors', Boolean.name)
                ],
                redirect  : [
                        GrailsMethodUtil.createMethod(controllerClass, 'redirect', Void.name).with {
                            it.addParameter('args', 'java.util.Map')
                        }
                ],
                render    : [
                        GrailsMethodUtil.createMethod(controllerClass, 'render', Void.name).with {
                            it.addParameter('args', 'java.util.Map')
                        },
                        GrailsMethodUtil.createMethod(controllerClass, 'render', Void.name).with {
                            it.addParameter('text', 'java.lang.String')
                        },
                        GrailsMethodUtil.createMethod(controllerClass, 'render', Void.name).with {
                            it.addParameter('json', 'grails.converters.JSON')
                        }
                ],
                respond   : [
                        GrailsMethodUtil.createMethod(controllerClass, 'respond', Void.name).with {
                            it.addParameter('args', 'java.util.Map')
                        }
                ],
                withFormat: [
                        GrailsMethodUtil.createMethod(controllerClass, 'withFormat', Void.name).with {
                            it.addParameter('callable', 'groovy.lang.Closure')
                        }
                ]
        ] as Map<String, List<PsiMethod>>
    }

    static Map<String, PsiField> controllerTestMembers(PsiClass testClass, PsiClass controllerPsiClass) {
        [
                controller        : new GrLightField(testClass, 'controller', controllerPsiClass.qualifiedName),
                applicationContext: new GrLightField(testClass, 'applicationContext', 'org.springframework.context.ConfigurableApplicationContext'),
                mainContext       : new GrLightField(testClass, 'mainContext', 'org.springframework.context.ConfigurableApplicationContext'),
                grailsApplication : new GrLightField(testClass, 'grailsApplication', 'grails.core.GrailsApplication'),
                config            : new GrLightField(testClass, 'config', 'grails.config.Config'),
                messageSource     : new GrLightField(testClass, 'messageSource', 'org.springframework.context.MessageSource'),
                webRequest        : new GrLightField(testClass, 'webRequest', 'org.grails.web.servlet.mvc.GrailsWebRequest'),
                request           : new GrLightField(testClass, 'request', 'org.grails.plugins.testing.GrailsMockHttpServletRequest'),
                response          : new GrLightField(testClass, 'response', 'org.grails.plugins.testing.GrailsMockHttpServletResponse'),
                servletContext    : new GrLightField(testClass, 'servletContext', 'org.springframework.mock.web.MockServletContext'),
                groovyPages       : new GrLightField(testClass, 'groovyPages', 'java.util.Map<java.lang.String,java.lang.String>'),
                views             : new GrLightField(testClass, 'views', 'java.util.Map<java.lang.String,java.lang.String>'),
                session           : new GrLightField(testClass, 'session', 'org.springframework.mock.web.MockHttpSession'),
                status            : new GrLightField(testClass, 'status', int.name),
                params            : new GrLightField(testClass, 'params', 'grails.web.servlet.mvc.GrailsParameterMap'),
                model             : new GrLightField(testClass, 'model', 'java.util.Map'),
                view              : new GrLightField(testClass, 'view', 'java.lang.String'),
                flash             : new GrLightField(testClass, 'flash', 'grails.web.mvc.FlashScope')
        ] as Map<String, PsiField>

    }
}
