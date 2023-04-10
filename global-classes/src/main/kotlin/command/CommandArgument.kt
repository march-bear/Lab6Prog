package command

import Organization
import exceptions.InvalidArgumentsForCommandException
import java.lang.NullPointerException
import java.util.LinkedList
import java.util.regex.Pattern


class CommandArgument(argumentString: String? = null) {
    private companion object {
        val argsPattern: Pattern = Pattern.compile("(?<=^|\\s)\"(.*?)\"(?=\\s|\$)|(?<=^|\\s)(.*?)(?=\\s|\$)")
    }

    val primitiveTypeArguments: List<String>?
    val organizations: LinkedList<Organization> = LinkedList()
    val organizationLimit: Int

    init {
        primitiveTypeArguments = if (argumentString == null || argumentString.trim() == "") {
            organizationLimit = 0
            null
        } else {
            val matcher = argsPattern.matcher(argumentString.trim())

            val tmpArgs = ArrayList<String>()
            while (matcher.find())
                tmpArgs.add(matcher.group())
            organizationLimit = if (tmpArgs.last() == "\\") {
                tmpArgs.removeLast()
                1
            } else {
                0
            }
            tmpArgs
        }
    }
    fun addOrganization(org: Organization): Boolean {
        if (organizations.size == organizationLimit)
            return false

        organizations.add(org)
        return true
    }
}