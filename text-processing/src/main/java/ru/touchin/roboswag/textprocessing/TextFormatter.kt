package ru.touchin.roboswag.textprocessing

import android.widget.TextView
import ru.touchin.roboswag.textprocessing.generators.DecoroMaskGenerator
import ru.touchin.roboswag.textprocessing.generators.PlaceholderGenerator
import ru.touchin.roboswag.textprocessing.generators.regexgenerator.RegexReplaceGenerator

class TextFormatter(private val regex: String) {

    private val regexReplaceGenerator = RegexReplaceGenerator()
    private val decoroMaskGenerator = DecoroMaskGenerator()
    private val pcreGeneratorItem = regexReplaceGenerator.regexToRegexReplace(regex)
    private val regexReplaceString = pcreGeneratorItem.regexReplaceString
    private val listForPlaceholder = pcreGeneratorItem.list
    private val placeholderGenerator = PlaceholderGenerator(listForPlaceholder)

    fun getFormatText(inputText: String) = inputText.replace(Regex(regex), regexReplaceString)

    fun getPlaceholder() = placeholderGenerator.getPlaceholder()

    fun getRegexReplace() = regexReplaceString

    fun mask(textView: TextView) {
        val formatWatcher = decoroMaskGenerator.mask(
            placeholderGenerator.getPlaceholder(),
            listForPlaceholder
        )
        formatWatcher.installOn(textView)
    }
}