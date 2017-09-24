# idea-grails-plugin
## Overview
Grails plugin compatible with Community Edition.

Supports the following Grails features:
<ol>
    <li>Url Mappings:
        <ul>
            <li>Navigate to controller class
            <li>Navigate to controller action
        </ul>
    <li>Controllers:
        <ul>
            <li>Action variables
            <li>Action methods
        </ul>
    </li>
    <li>Command classes:
        <ul>
            <li>recognizes properties in constraints closure
            <li>constraints navigation, type checking and autocompletion
        </ul>
    </li>
    <li>Domain Classes:
        <ul>
            <li>hasMany, hasOne and belongsTo properties
            <li>addTo* and removeFrom* methods
            <li>dynamic methods (grails.gorm.api.GormInstanceOperations)
            <li>static methods (grails.gorm.api.GormStaticOperations)
            <li>recognizes properties in constraints closure
            <li>recognizes properties in mapping closure
            <li>supports framework methods in mapping closure
            <li>constraints navigation, type checking and autocompletion
        </ul>
    </li>
    <li>Testing:
        <ul>
            <li>Controller unit tests
        </ul>
    </li>
</ol>


## Build instructions

 1. Open the project in IDEA
 2. Open project configuration window and set IDEA plugin SDK
 3. Build the project
 4. Use Build->Prepare Plugin Module ... to build plugin jar file



