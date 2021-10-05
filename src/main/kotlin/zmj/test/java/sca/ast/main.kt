package zmj.test.java.sca.ast

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import zmj.test.java.sca.ast.dsl.JavaDslCheck
import zmj.test.java.sca.ast.dsl.RuleUtil
import java.io.File

fun main() {
    val basePath = File("").absolutePath + File.separator
    val rulePath = basePath + File.separator + "doc" + File.separator + "rule_template.xml"
    val filePath = basePath + File.separator + "testcase" + File.separator + "Test.java"

    val rules = RuleUtil.loadRule(rulePath)
    val cu = read(filePath)

    JavaDslCheck().visit(cu, rules)
}

fun read(filePath: String): CompilationUnit {
    val symbolSolver = JavaSymbolSolver(ReflectionTypeSolver())
    val configuration = ParserConfiguration()
    configuration.setSymbolResolver(symbolSolver)
    val parseResult = JavaParser(configuration).parse(File(filePath))

    return parseResult.result.get()
}