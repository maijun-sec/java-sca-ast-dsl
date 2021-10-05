package zmj.test.java.sca.ast.dsl

infix fun String.startWith(that: String): Boolean {
    return this.startsWith(that)
}

infix fun String.matches(that: String): Boolean {
    var regex = that.toRegex()
    if (that startWith "(?!)") {
        regex = that.substring(4).toRegex(RegexOption.IGNORE_CASE)
    }
    return regex.containsMatchIn(input = this)
}

infix fun <T> Collection<T>.contain(that: T): Boolean {
    return this.contains(that)
}