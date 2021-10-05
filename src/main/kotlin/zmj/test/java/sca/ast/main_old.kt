package zmj.test.java.sca.ast

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.stmt.CatchClause
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import zmj.test.java.sca.ast.dsl.*
import java.io.File

fun main() {
    val rules = RuleUtil.loadRule("D:\\workspace\\idea\\engine\\java-sca-ast-dsl\\doc\\rule_template.xml")
    rules.forEach { println(it) }

    val filePath = File("").absolutePath + File.separator + "testcase" + File.separator + "Test.java"
    val cu = readAst(filePath)

    // 1. empty catch block
    val emptyCatchClauses = cu.findAll(CatchClause::class.java).filter {
        it match { empty }
    }
    emptyCatchClauses.forEach { println("empty catch clause at line ${it.begin.get().line}") }

    // 2. left over debug code
//    val debugMethods = cu.findAll(MethodDeclaration::class.java).filter {
//        it match {
//            (methodName startWith "debug") and
//            (parameterTypes.size == 1) and
//            (parameterTypes[0].resolveType matches "java.util.List<.*?>")
//        }
//    }
//    debugMethods.forEach { println("leftover debug code at line ${it.begin.get().line}") }

    // 3. dangerous method call
    val methodCallExprs = cu.findAll(MethodCallExpr::class.java).filter {
        it match {
            (method.name == "printStackTrace") && (method.enclosingClass == "java.lang.Throwable")
        }
    }
    methodCallExprs.forEach { println("e.printStackTrace() at line ${it.begin.get().line}") }

    // 4. return in catch clause
    val catchWithReturns = cu.findAll(CatchClause::class.java).filter {
        it contain {
            simpleName == "ReturnStmt"
        }
    }
    catchWithReturns.forEach { println("return in catch block at line ${it.begin.get().line}") }

    // 5. password in comment
    val comments = cu.allComments.filter {
        it match {
            content matches "(?!).*password.*"
        }
    }
    comments.forEach { println("password in comment at line ${it.begin.get().line}") }

    // 6. overly broad catch
    val overlyCatchClauses = cu.findAll(CatchClause::class.java).filter {
        (it match {
            (exception.type.resolveType == "java.lang.Exception") and (enclosingFunction.methodName != "main")
        }) and
        (!(it contain {simpleName == "ThrowStmt"}))
    }
    overlyCatchClauses.forEach { println("overly broad catch at line ${it.begin.get().line}") }

    // 7. poor logging practice
    val poorLoggings = cu.findAll(FieldDeclaration::class.java).filter {
        it match {
            (!(static and final)) and (type.supers contain "java.util.logging.Logger")
        }
    }
    poorLoggings.forEach { println("poor logging at line ${it.begin.get().line}") }
}

fun readAst(filePath: String): CompilationUnit {
    val symbolSolver = JavaSymbolSolver(ReflectionTypeSolver())
    val configuration = ParserConfiguration()
    configuration.setSymbolResolver(symbolSolver)
    val parseResult = JavaParser(configuration).parse(File(filePath))

    return parseResult.result.get()
}