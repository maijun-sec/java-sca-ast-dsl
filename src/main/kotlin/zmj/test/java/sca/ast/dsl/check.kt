package zmj.test.java.sca.ast.dsl

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.comments.BlockComment
import com.github.javaparser.ast.comments.Comment
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.comments.LineComment
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.stmt.CatchClause
import com.github.javaparser.ast.visitor.GenericVisitorAdapter
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings


class JavaDslCheck: GenericVisitorAdapter<Void?, List<Rule>>() {
    override fun visit(catchClause: CatchClause, rules: List<Rule>): Void? {
        check(catchClause, rules)
        return super.visit(catchClause, rules)
    }

    override fun visit(methodDeclaration: MethodDeclaration, rules: List<Rule>): Void? {
        check(methodDeclaration, rules)
        return super.visit(methodDeclaration, rules)
    }

    override fun visit(methodCallExpr: MethodCallExpr, rules: List<Rule>): Void? {
        check(methodCallExpr, rules)
        return super.visit(methodCallExpr, rules)
    }

    override fun visit(comment: JavadocComment, rules: List<Rule>): Void? {
        check(comment, rules)
        return super.visit(comment, rules)
    }

    override fun visit(comment: LineComment, rules: List<Rule>): Void? {
        check(comment, rules)
        return super.visit(comment, rules)
    }

    override fun visit(comment: BlockComment, rules: List<Rule>): Void? {
        check(comment, rules)
        return super.visit(comment, rules)
    }

    override fun visit(fieldDeclaration: FieldDeclaration, rules: List<Rule>): Void? {
        check(fieldDeclaration, rules)
        return super.visit(fieldDeclaration, rules)
    }

    private fun check(node: Node, rules: List<Rule>) {
        val manager = ScriptEngineManager()
        val engine = manager.getEngineByName("kotlin")

        rules.forEach {
            if (isTypeMatch(node, it.nodeType)) {
                val bindings = SimpleBindings()
                bindings[it.base] = node
                try {
                    val result = engine.eval(it.dsl, bindings)
                    if (result == true) {
                        println(it.description + ": " + node.begin.get().line)
                    }
                } catch (e: Exception) {
                    // TODO() handle log
                    println("====> " + e.message)
                }
            }
        }
    }

    private fun isTypeMatch(node: Node, nodeType: String): Boolean {
        if (nodeType == node.simpleName) {
            return true
        }

        if (nodeType == "Comment" && node is Comment) {
            return true
        }

        return false
    }
}