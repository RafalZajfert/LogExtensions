@file:Suppress("unused")
@file:JvmName("Logger")

package software.rsquared.logextensions

import java.io.PrintWriter
import java.io.StringWriter
import android.util.Log
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.math.max


private const val logcatChunkSize = 3000

class LogConfiguration {
	companion object {
		@JvmField
		var minLogLevel: LogLevel = LogLevel.VERBOSE

		@JvmField
		var defaultTag = LogConst.CODE_LINE

		@JvmField
		var messagesSeparator: String = " "

		@JvmField
		var throwableSeparator: String = "\n"

		@JvmField
		var timePattern: String = "%1\$tH:%1\$tM:%1\$tS.%1\$tL"

		@JvmField
		var overwrittenLevels: Map<LogLevel, LogLevel> = emptyMap()
	}
}


fun logVerbose(message: Any?) {
	log(LogLevel.VERBOSE, null, message, null)
}

fun logVerbose(tag: Tag, message: Any?) {
	log(LogLevel.VERBOSE, tag, message, null)
}

fun logVerbose(vararg message: Any?) {
	log(LogLevel.VERBOSE, null, message.joinToString(separator = " "), null)
}

fun logVerbose(tag: Tag, vararg message: Any?) {
	log(LogLevel.VERBOSE, tag, message.joinToString(separator = " "), null)
}

fun logVerbose(th: Throwable?) {
	log(LogLevel.VERBOSE, null, null, th)
}

fun logVerbose(tag: Tag, th: Throwable?) {
	log(LogLevel.VERBOSE, tag, null, th)
}

fun logVerbose(message: Any?, th: Throwable?) {
	log(LogLevel.VERBOSE, null, message, th)
}

fun logVerbose(tag: Tag, message: Any?, th: Throwable?) {
	log(LogLevel.VERBOSE, tag, message, th)
}

fun logTrace() {
	log(LogLevel.DEBUG, null, "at " + LogConst.FULL_CLASS_NAME + "." + LogConst.METHOD_NAME + LogConst.CODE_LINE, null)
}

fun logTrace(tag: Tag) {

	log(LogLevel.DEBUG, tag, "at " + LogConst.FULL_CLASS_NAME + "." + LogConst.METHOD_NAME + LogConst.CODE_LINE, null)
}

fun logDebug(message: Any?) {
	log(LogLevel.DEBUG, null, message, null)
}

fun logDebug(tag: Tag, message: Any?) {
	log(LogLevel.DEBUG, tag, message, null)
}

fun logDebug(vararg message: Any?) {
	log(LogLevel.DEBUG, null, message.joinToString(separator = " "), null)
}

fun logDebug(tag: Tag, vararg message: Any?) {
	log(LogLevel.DEBUG, tag, message.joinToString(separator = " "), null)
}

fun logDebug(th: Throwable?) {
	log(LogLevel.DEBUG, null, null, th)
}

fun logDebug(tag: Tag, th: Throwable?) {
	log(LogLevel.DEBUG, tag, null, th)
}

fun logDebug(message: Any?, th: Throwable?) {
	log(LogLevel.DEBUG, null, message, th)
}

fun logDebug(tag: Tag, message: Any?, th: Throwable?) {
	log(LogLevel.DEBUG, tag, message, th)
}

fun logInfo(message: Any?) {

	log(LogLevel.INFO, null, message, null)
}

fun logInfo(tag: Tag, message: Any?) {
	log(LogLevel.INFO, tag, message, null)
}

fun logInfo(vararg message: Any?) {
	log(LogLevel.INFO, null, message.joinToString(separator = " "), null)
}

fun logInfo(tag: Tag, vararg message: Any?) {
	log(LogLevel.INFO, tag, message.joinToString(separator = " "), null)
}

fun logInfo(th: Throwable?) {
	log(LogLevel.INFO, null, null, th)
}

fun logInfo(tag: Tag, th: Throwable?) {
	log(LogLevel.INFO, tag, null, th)
}

fun logInfo(message: Any?, th: Throwable?) {
	log(LogLevel.INFO, null, message, th)
}

fun logInfo(tag: Tag, message: Any?, th: Throwable?) {
	log(LogLevel.INFO, tag, message, th)
}

fun logWarning(message: Any?) {
	log(LogLevel.WARNING, null, message, null)
}

fun logWarning(tag: Tag, message: Any?) {
	log(LogLevel.WARNING, tag, message, null)
}

fun logWarning(vararg message: Any?) {
	log(LogLevel.WARNING, null, message.joinToString(separator = " "), null)
}

fun logWarning(tag: Tag, vararg message: Any?) {
	log(LogLevel.WARNING, tag, message.joinToString(separator = " "), null)
}

fun logWarning(th: Throwable?) {
	log(LogLevel.WARNING, null, null, th)
}

fun logWarning(tag: Tag, th: Throwable?) {
	log(LogLevel.WARNING, tag, null, th)
}

fun logWarning(message: Any?, th: Throwable?) {
	log(LogLevel.WARNING, null, message, th)
}

fun logWarning(tag: Tag, message: Any?, th: Throwable?) {
	log(LogLevel.WARNING, tag, message, th)
}

fun logError(message: Any?) {
	log(LogLevel.ERROR, null, message, null)
}

fun logError(tag: Tag, message: Any?) {
	log(LogLevel.ERROR, tag, message, null)
}

fun logError(vararg message: Any?) {
	log(LogLevel.ERROR, null, message.joinToString(separator = " "), null)
}

fun logError(tag: Tag, vararg message: Any?) {
	log(LogLevel.ERROR, tag, message.joinToString(separator = " "), null)
}

fun logError(th: Throwable?) {
	log(LogLevel.ERROR, null, null, th)
}

fun logError(tag: Tag, th: Throwable?) {
	log(LogLevel.ERROR, tag, null, th)
}

fun logError(message: Any?, th: Throwable?) {
	log(LogLevel.ERROR, null, message, th)
}

fun logError(tag: Tag, message: Any?, th: Throwable?) {
	log(LogLevel.ERROR, tag, message, th)
}

private fun log(lvl: LogLevel, t: Tag?, msg: Any?, throwable: Throwable?) {
	(LogConfiguration.overwrittenLevels[lvl] ?: lvl)
			.takeIf { it.isAtLeast(LogConfiguration.minLogLevel) }?.also { level ->
				val data = getDataMapFor("$msg${t?.tag ?: LogConfiguration.defaultTag}", level)

				val tag = format(t?.tag ?: LogConfiguration.defaultTag, data)

				val message = StringBuilder().apply {
					appendNotEmpty(format(msg?.toString(), data))
					append(throwable.stackToString(LogConfiguration.throwableSeparator.takeIf { this.isNotEmpty() }).orEmpty())
					if (isEmpty()) {
						append(if (msg == null && throwable == null) null else "[empty log message]")
					}
				}.toString()


				if (message.length > logcatChunkSize) {
					wrap(message, logcatChunkSize).forEach { logcat(level, tag, it) }
				} else {
					logcat(level, tag, message)
				}
			}
}

private fun logcat(level: LogLevel, tag: String, msg: String) {
	when (level) {
		LogLevel.VERBOSE -> Log.v(tag, msg)
		LogLevel.DEBUG -> Log.d(tag, msg)
		LogLevel.INFO -> Log.i(tag, msg)
		LogLevel.WARNING -> Log.w(tag, msg)
		LogLevel.ERROR -> Log.e(tag, msg)
		LogLevel.SILENT -> {
		}
	}
}

private fun StringBuilder.appendNotEmpty(text: String?) {
	if (!text.isNullOrEmpty()) append(text)
}


private fun Throwable?.stackToString(prefix: String? = null): String? = this?.let {
	try {
		StringWriter().use { writer ->
			PrintWriter(writer).use { printWriter ->
				printStackTrace(printWriter)
				return@let "${prefix ?: ""}$writer"
			}
		}
	} catch (ignored: Exception) {
		return@let null
	}
}

private fun wrap(str: String?, wrapLength: Int): List<String> {
	str ?: return emptyList()
	val maxLength = max(1, wrapLength)
	val patternToWrapOn = Pattern.compile("\\s")
	val inputLineLength = str.length
	var offset = 0

	val wrappedLines = ArrayList<String>()
	while (offset < inputLineLength) {
		var spaceToWrapAt = -1
		val matcher = patternToWrapOn.matcher(str.substring(offset, Math.min(offset + maxLength + 1, inputLineLength)))
		if (matcher.find()) {
			if (matcher.start() == 0) {
				offset += matcher.end()
				continue
			} else {
				spaceToWrapAt = matcher.start()
			}
		}

		// only last line without leading spaces is left
		if (inputLineLength - offset <= maxLength) {
			break
		}

		while (matcher.find()) {
			spaceToWrapAt = matcher.start() + offset
		}

		if (spaceToWrapAt >= offset) {
			wrappedLines.add(str.substring(offset, spaceToWrapAt + 1))
			offset = spaceToWrapAt + 1

		} else {
			// really long word or URL
			// wrap really long word one line at a time
			wrappedLines.add(str.substring(offset, maxLength + offset))
			offset += maxLength

		}
	}

	// Whatever is left in line is short enough to just pass through
	wrappedLines.add(str.substring(offset))

	return wrappedLines
}

/**
 * Replace all constant tags with values
 */
private fun format(str: String?, data: Map<String, String>): String {
	var text = str.takeUnless { it.isNullOrBlank() } ?: return ""
	data.entries.forEach { entry ->
		text = text.replace(entry.key, entry.value)
	}
	return text
}


private fun getDataMapFor(input: String, level: LogLevel): Map<String, String> {
	val elements = Pattern.compile(LogConst.PATTERN)
			.matcher(input).let { matcher ->
				val fields = LinkedList<String>()
				while (matcher.find()){
					fields.add(matcher.group(0))
				}

				fields
			}.takeUnless { it.isEmpty() } ?: return emptyMap()

	val data = HashMap<String, String>()
	if (elements.contains(LogConst.LEVEL)) data[LogConst.LEVEL] = level.fullName
	if (elements.contains(LogConst.SHORT_LEVEL)) data[LogConst.SHORT_LEVEL] = level.shortName
	if (elements.contains(LogConst.CURRENT_TIME)) data[LogConst.CURRENT_TIME] = String.format(LogConfiguration.timePattern, Calendar.getInstance())

	getStackTraceElement()?.also { element ->
		if (elements.contains(LogConst.CLASS_NAME)) data[LogConst.CLASS_NAME] = element.className?.let { fullName ->
			val parts = fullName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			parts.getOrNull(parts.size - 1)
		}.orEmpty()
		if (elements.contains(LogConst.FULL_CLASS_NAME)) data[LogConst.FULL_CLASS_NAME] = element.className
		if (elements.contains(LogConst.METHOD_NAME)) data[LogConst.METHOD_NAME] = element.methodName
		if (elements.contains(LogConst.FILE_NAME)) data[LogConst.FILE_NAME] = element.fileName
		if (elements.contains(LogConst.LINE_NUMBER)) data[LogConst.LINE_NUMBER] = element.lineNumber.toString()

	}

	return data
}

private fun getStackTraceElement(): StackTraceElement? = Thread.currentThread().stackTrace?.takeIf { it.size > 6 }?.getOrNull(6)

class LogConst {
	companion object {
		/**
		 * This parameter can be used in tag pattern or in message to log and it will be replaced with simple name of class in which the logger will be called
		 */
		const val CLASS_NAME = "\$ClassName$"

		/**
		 * This parameter can be used in tag pattern or in message to log and it will be replaced with full name of class (with package) in which the logger will be called
		 */
		const val FULL_CLASS_NAME = "\$FullClassName$"

		/**
		 * This parameter can be used in tag pattern or in message to log and it will be replaced with name of method in which the logger will be called
		 */
		const val METHOD_NAME = "\$MethodName$"

		/**
		 * This parameter can be used in tag pattern or in message to log and it will be replaced with name of file in which the logger will be called
		 */
		const val FILE_NAME = "\$FileName$"

		/**
		 * This parameter can be used in tag pattern or in message to log and it will be replaced with line number in which the logger will be called
		 */
		const val LINE_NUMBER = "\$LineNumber$"

		/**
		 * This parameter can be used in tag pattern or in message to log and it will be replaced with level of the message
		 */
		const val LEVEL = "\$Level$"

		/**
		 * This parameter can be used in tag pattern or in message to log and it will be replaced with short version of the level (e.g. D for the LogLevel.DEBUG) of the message
		 */
		const val SHORT_LEVEL = "\$ShortLevel$"

		/**
		 * This parameter can be used in tag pattern or in message to log and it will be replaced with current time
		 */
		const val CURRENT_TIME = "\$CurrentTime$"

		/**
		 * This parameter can be used in tag pattern or in message to log and it will be replaced with file name and line number.
		 *
		 *
		 * This param allows you to jump to code line via logcat console.
		 *
		 *
		 * Same as: `"([FILE_NAME]:[LINE_NUMBER])"`
		 */
		const val CODE_LINE = "($FILE_NAME:$LINE_NUMBER)"

		internal const val PATTERN = "\\$(ClassName|FullClassName|MethodName|FileName|LineNumber|Level|ShortLevel|CurrentTime)\\$"
	}
}

enum class LogLevel constructor(val fullName: String, val shortName: String) {
	VERBOSE("Verbose", "V"),
	DEBUG("Debug", "D"),
	INFO("Info", "I"),
	WARNING("Warning", "W"),
	ERROR("Error", "E"),
	SILENT("Silent", "S");

	fun isAtLeast(level: LogLevel): Boolean = ordinal >= level.ordinal
}

class Tag constructor(val tag: String)
