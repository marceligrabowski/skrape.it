package it.skrape

import it.skrape.core.*
import java.io.File
import java.nio.charset.Charset
import kotlin.reflect.KClass


/**
 * make http-request with given parameters or defaults
 * @param mode defaults to HTTP and represents the scraping mode (either pure http request or rendering via browser)
 * @return Result
 */
@SkrapeItDslMarker
fun <T> skrape(init: Request.() -> T): T {
    return Scraper().request.init()
}

/**
 * read and parse a html file from local file-system
 * @param file
 * @param charset defaults to UTF-8
 */
@SkrapeItDslMarker
fun skrape(file: File, charset: Charset = Charsets.UTF_8, init: Doc.() -> Unit) {
    Reader(file, charset).read().init()
}

/**
 * read and parse html from a String
 * @param html represents a html snippet
 */
@SkrapeItDslMarker
fun skrape(html: String, init: Doc.() -> Unit) { Parser(html).parse().init() }

@SkrapeItDslMarker
fun Request.expect(init: Result.() -> Unit) {
    Scraper(request = this).scrape().also(init)
}

@SkrapeItDslMarker
fun <T> Request.extract(extractor: Result.() -> T): T {
    val result = Scraper(request = this).scrape()
    return result.extractor()
}

@SkrapeItDslMarker
inline fun <reified T: Any> Request.extractIt(extractor: Result.(T) -> Unit): T {
    val instance = create(T::class)
    Scraper(request = this).scrape().apply { extractor(instance) }
    return instance
}

inline fun <reified T: Any> create(clazz: KClass<T>): T {
    return clazz.constructors.first { it.parameters.isEmpty() }.call()
}

@DslMarker
annotation class SkrapeItDslMarker
