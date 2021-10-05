package zmj.test.java.sca.ast.dsl

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.comments.Comment
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.nodeTypes.NodeWithBlockStmt
import com.github.javaparser.ast.stmt.CatchClause
import com.github.javaparser.ast.type.Type
import com.github.javaparser.ast.type.TypeParameter
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration
import com.github.javaparser.resolution.types.ResolvedType
import org.apache.commons.collections4.CollectionUtils

infix fun Node.match(block: Node.() -> Boolean): Boolean {
    return block(this)
}

infix fun Node.contain(block: Node.() -> Boolean): Boolean {
    if (this is NodeWithBlockStmt<*>) {
        this.body.statements.forEach {
            if (block(it)) {
                return true
            }
        }
    }

    return false
}

infix fun Collection<Node>.contain(block: Node.() -> Boolean): Boolean {
    this.forEach {
        if (block(it)) {
            return true
        }
    }
    return false
}

fun SimpleName.equals(name: Any): Boolean {
    if (name is String) {
        return this.asString() == name
    }
    if (name is SimpleName) {
        return this.asString() == name.asString()
    }
    return false
}

val Node.simpleName: String
    get() = this.javaClass.simpleName

val Node.qualifiedName: String
    get() = this.javaClass.name

val Node.empty: Boolean
    get() {
        if (this is NodeWithBlockStmt<*>) {
            val body = this.body
            if (body == null || body.isEmptyStmt || CollectionUtils.isEmpty(body.statements)) {
                return true
            }
            return false
        }

        throw UnSupportedParameterException("empty", this)
    }

val Node.methodName: String
    get() {
        if (this is MethodDeclaration) {
            return this.name.asString()
        }

        throw UnSupportedParameterException("methodName", this)
    }

val Node.parameterTypes: MutableList<Type>
    get() {
        if (this is MethodDeclaration) {
            val parameterTypes = mutableListOf<Type>()
            this.parameters.forEach {
                parameterTypes.add(it.type)
            }
            return parameterTypes
        }

        throw UnSupportedParameterException("parameterTypes", this)
    }

val Node.method: ResolvedMethodDeclaration
    get() {
        if (this is MethodCallExpr) {
            return this.resolve()
        }

        throw UnSupportedParameterException("method", this)
    }

val Node.content: String
    get() {
        if (this is Comment) {
            return this.content
        }

        throw UnSupportedParameterException("content", this)
    }

val Node.enclosingFunction: MethodDeclaration
    get() {
        var current: Node? = this
        while (current != null) {
            if (current is MethodDeclaration) {
                return current
            }

            val parentNode = current.parentNode
            current = if (parentNode.isPresent) {
                parentNode.get()
            } else {
                null
            }
        }

        throw UnSupportedParameterException("enclosingFunction", this)
    }

val Node.exception: Parameter
    get() {
        if (this is CatchClause) {
            return this.parameter
        }

        throw UnSupportedParameterException("exception", this)
    }

val Node.static: Boolean
    get() {
        if (this is FieldDeclaration) {
            return this.isStatic
        }

        if (this is MethodDeclaration) {
            return this.isStatic
        }

        if (this is ClassOrInterfaceDeclaration) {
            return this.isStatic
        }

        throw UnSupportedParameterException("static", this)
    }

val Node.final: Boolean
    get() {
        if (this is FieldDeclaration) {
            return this.isFinal
        }

        if (this is MethodDeclaration) {
            return this.isFinal
        }

        if (this is ClassOrInterfaceDeclaration) {
            return this.isFinal
        }

        throw UnSupportedParameterException("final", this)
    }


val Node.type: ResolvedType
    get() {
        if (this is FieldDeclaration) {
            return this.resolve().type
        }

        throw UnSupportedParameterException("type", this)
    }

val ResolvedType.supers: List<String>
    get() {
        if (this.isArray || this.isPrimitive) {
            return listOf(this.describe())
        }

        if (this.isReferenceType) {
            return this.asReferenceType().allAncestors.map { it.typeName }
        }

        throw Exception()
    }

val ResolvedType.typeName: String
    get() {
        return this.describe()
    }

val ResolvedMethodDeclaration.enclosingClass: String
    get() = this.qualifiedName.substring(0, this.qualifiedName.lastIndexOf("."))

val TypeParameter.qualifiedName: String
    get() = this.name.asString()

val Type.resolveType: String
    get() = resolveType(this)

fun resolveType(type: Type): String {
    val resolvedType = type.resolve()
    if (resolvedType != null) {
        return resolvedType.describe()
    }
    return ""
}

class UnSupportedParameterException(private val parameter: String, private val node: Node): RuntimeException() {
    override val message: String?
        get() = "unsupported parameter $parameter of node ${node.simpleName}"
}