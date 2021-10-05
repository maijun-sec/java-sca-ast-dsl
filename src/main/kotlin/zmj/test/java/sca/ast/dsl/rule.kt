package zmj.test.java.sca.ast.dsl

import com.google.common.base.Strings
import org.jdom2.input.SAXBuilder
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.StringBuilder

data class Rule(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var nodeType: String = "",
    var base: String = "",
    var dsl: String = ""
)

object RuleUtil {
    fun loadRule(rulePath: String): List<Rule> {
        val rules = ArrayList<Rule>()
        val saxBuilder = SAXBuilder()
        val document = saxBuilder.build(InputStreamReader(FileInputStream(rulePath), "UTF-8"))
        val rootElement = document.rootElement
        val rootChildElement = rootElement.children
        for (element in rootChildElement) {
            var rule = Rule()
            for (attr in element.attributes) {
                assignment(rule, attr.name, attr.value)
            }
            val children = element.children
            for (child in children) {
                assignment(rule, child.name, child.value)
            }
            rules.add(rule)
        }
        return rules
    }

    private fun assignment(rule: Rule, property: String, value: String) {
        when (property) {
            "id" -> rule.id = value
            "name" -> rule.name = value
            "description" -> rule.description = value
            "matcher" -> {
                val result = value.split("\r", "\n", "\r\n")
                var dsl = StringBuilder()
                for (line in result) {
                    if (!Strings.isNullOrEmpty(line)) {
                        dsl.append(line.trim()).append(" ")
                    }
                }
                val index = dsl.indexOf(":")
                rule.dsl = "import zmj.test.java.sca.ast.dsl.*\r\n" + dsl.substring(index + 1)
                val prefix = dsl.substring(0, index).trim()
                val prefixMsg = prefix.split(" ")
                rule.nodeType = prefixMsg[0]
                rule.base = prefixMsg[1]
            }
        }
    }
}