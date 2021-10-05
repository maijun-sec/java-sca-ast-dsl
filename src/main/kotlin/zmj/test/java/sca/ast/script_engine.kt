package zmj.test.java.sca.ast

import javax.script.ScriptEngineManager

fun main() {
    val manager = ScriptEngineManager()
    val factories = manager.engineFactories
    for (factory in factories) {
        println("Name: ${factory.engineName}")
        println("Version: ${factory.engineVersion}")
        println("Language: ${factory.languageName}")
        println("Language version: ${factory.languageVersion}")
        println("Extensions: ${factory.extensions}")
        println("Mime types: ${factory.mimeTypes}")
        println("Names: ${factory.names}")
    }
}