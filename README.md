## Non-null default collections template for generating Kotlin with OpenAPI

### Table of Contents
* [Description](README.md#description)
* [Usage](README.md#usage)
  * [Generating with Gradle](README.md#generating-with-gradle)
  * [Generating with the command line interface](README.md#generating-with-the-command-line-interface)
* [How it works](README.md#how-it-works)
***

### Description
This repository contains a [template](src/main/resources/templates/dataClassOptVar.mustache) that overrides part of the `kotlin-spring` generator from [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator). 

The template contains two modifications around how optional collection fields in data classes are generated:
* Their default values are `emptyList()`, `emptySet()`, or `emptyMap()` rather than `null`.
* Their types are no longer nullable.

***

### Usage
First copy the [dataClassOptVar.mustache](src/main/resources/templates/dataClassOptVar.mustache) template and paste it somewhere within the `resources` folder in your project. The specific path is arbitrary, but the file itself must be called `dataClassOptVar.mustache`. Here is a suggested project structure:
```
└── src
    ├── main
        ├── kotlin
        └── resources
            └── templates
                └── dataClassOptVar.mustache
``` 
Now make sure that you refer to your templates folder when generating Kotlin code. That will look different depending on how you use the OpenAPI Generator.

#### Generating with Gradle
If you generate the code using gradle, then you must set the template directory to the same folder you added the template to in the previous step. See Alternative 1 in [build.gradle.kts](build.gradle.kts) for a complete example.
```kts
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

tasks.register<GenerateTask>("generateExampleApiSpec") {
    // This is the important bit, make sure it refers to the correct folder
    templateDir.set("$projectDir/src/main/resources/templates")

    generatorName.set("kotlin-spring")
	
    /* The rest of the generation options go here like usual */
}
```

#### Generating with the command line interface
If you are generating the code using the command line tool directly, then you have to set the template directory with the `-t` option. Make sure it points to the same directory as in the previous step.
```sh
openapi-generator-cli generate \
  -t src/main/resources/templates \
  -g kotlin-spring
  # the rest of the generation options go here like usual #
```
Remember to also mark the generated directories as source directories, as that cannot be done with the CLI tool directly. Here is an example of how to do it in Gradle:
```kts
sourceSets {
    main {
        java.srcDirs(
            "build/generated/example-api/src/main",
            "build/generated/mutable-example-api/src/main"
        )
    }
}
```
See [generate-api.sh](generate-api.sh) and Alternative 2 in [build.gradle.kts](build.gradle.kts) for a complete example.

[//]: # (#### Generating with Maven)
[//]: # (TODO)

***

### How it works
OpenAPI Generator uses templates in the [Mustache template language](https://mustache.github.io/) to specify how to format json objects as native code. 

The [original dataClassOptVar.mustache](https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator/src/main/resources/kotlin-spring/dataClassOptVar.mustache) template from the `kotlin-spring` generator looks like this:
```mustache
{{#useBeanValidation}}{{>beanValidation}}{{>beanValidationModel}}{{/useBeanValidation}}{{#swagger2AnnotationLibrary}}
    @Schema({{#example}}example = "{{#lambdaRemoveLineBreak}}{{#lambdaEscapeInNormalString}}{{{.}}}{{/lambdaEscapeInNormalString}}{{/lambdaRemoveLineBreak}}", {{/example}}{{#isReadOnly}}readOnly = {{{isReadOnly}}}, {{/isReadOnly}}description = "{{{description}}}"){{/swagger2AnnotationLibrary}}{{#swagger1AnnotationLibrary}}
    @ApiModelProperty({{#example}}example = "{{#lambdaRemoveLineBreak}}{{#lambdaEscapeInNormalString}}{{{.}}}{{/lambdaEscapeInNormalString}}{{/lambdaRemoveLineBreak}}", {{/example}}{{#isReadOnly}}readOnly = {{{isReadOnly}}}, {{/isReadOnly}}value = "{{{description}}}"){{/swagger1AnnotationLibrary}}{{#deprecated}}
    @Deprecated(message = ""){{/deprecated}}{{#vendorExtensions.x-field-extra-annotation}}
    {{{.}}}{{/vendorExtensions.x-field-extra-annotation}}
    @get:JsonProperty("{{{baseName}}}"){{#isInherited}} override{{/isInherited}} {{>modelMutable}} {{{name}}}: {{#isEnum}}{{#isArray}}{{baseType}}<{{/isArray}}{{classname}}.{{{nameInPascalCase}}}{{#isArray}}>{{/isArray}}{{/isEnum}}{{^isEnum}}{{{dataType}}}{{/isEnum}}? = {{^defaultValue}}null{{/defaultValue}}{{#defaultValue}}{{^isNumber}}{{{defaultValue}}}{{/isNumber}}{{#isNumber}}{{{dataType}}}("{{{defaultValue}}}"){{/isNumber}}{{/defaultValue}}
```
You may interpret the entire file as a function that takes a non-required field on a JSON object and formats it into an optional field in a Kotlin data class.

The template in this repository is the same, but with a bit more logic on the last line:
```mustache
@get:JsonProperty("{{{baseName}}}"){{#isInherited}} override{{/isInherited}} {{>modelMutable}} {{{name}}}: {{#isEnum}}{{#isArray}}{{baseType}}<{{/isArray}}{{classname}}.{{{nameInPascalCase}}}{{#isArray}}>{{/isArray}}{{/isEnum}}{{^isEnum}}{{{dataType}}}{{/isEnum}}{{^isContainer}}?{{/isContainer}} = {{^defaultValue}}{{#isContainer}}{{#isArray}}emptyList(){{/isArray}}{{#isSet}}emptySet(){{/isSet}}{{#isMap}}emptyMap(){{/isMap}}{{/isContainer}}{{^isContainer}}null{{/isContainer}}{{/defaultValue}}{{#defaultValue}}{{^isNumber}}{{{defaultValue}}}{{/isNumber}}{{#isNumber}}{{{dataType}}}("{{{defaultValue}}}"){{/isNumber}}{{/defaultValue}}
```
It is easier to read it if we add some temporary whitespace (though it would also add whitespace to the generated code):  
```mustache
@get:JsonProperty("{{{baseName}}}")
{{#isInherited}}
    override
{{/isInherited}}
{{>modelMutable}} {{{name}}}:
{{#isEnum}}
    {{#isArray}}{{baseType}}<{{/isArray}}
    {{classname}}.{{{nameInPascalCase}}}
    {{#isArray}}>{{/isArray}}
{{/isEnum}}
{{^isEnum}}{{{dataType}}}{{/isEnum}}
{{^isContainer}}
    ?
{{/isContainer}}
=
{{^defaultValue}}
    {{#isContainer}}
        {{#isArray}}
            {{#modelMutable}}mutableListOf(){{/modelMutable}}
            {{^modelMutable}}emptyList(){{/modelMutable}}
        {{/isArray}}
        {{#isSet}}
            {{#modelMutable}}mutableSetOf(){{/modelMutable}}
            {{^modelMutable}}emptySet(){{/modelMutable}}
        {{/isSet}}
        {{#isMap}}
            {{#modelMutable}}mutableMapOf(){{/modelMutable}}
            {{^modelMutable}}emptyMap(){{/modelMutable}}
        {{/isMap}}
    {{/isContainer}}
    {{^isContainer}}null{{/isContainer}}
{{/defaultValue}}
{{#defaultValue}}
    {{^isNumber}}{{{defaultValue}}}{{/isNumber}}
    {{#isNumber}}{{{dataType}}}("{{{defaultValue}}}"){{/isNumber}}
{{/defaultValue}}
```
The new additions are all by the `{{isContainer}}` tags:
1. Nullable question marks are added only if the type is *not* a container.
2. If the field does not already have a default value, and is a container, then the corresponding empty collection is added in place of `null`.
