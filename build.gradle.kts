/**
 * 整个工程的 Gradle Kotlin DSL 构建脚本
 * 大部分配置由 RetroFuturaGradle 模板提供，不建议大改，只在需要时小范围调整
 */
import com.gtnewhorizons.retrofuturagradle.mcp.InjectTagsTask
import org.jetbrains.changelog.Changelog
import org.jetbrains.gradle.ext.Gradle
import java.util.Properties
import groovy.lang.GroovyObject
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc

plugins {
    // 基础 Java、库、发布插件
    java
    `java-library`
    `maven-publish`
    // IDEA 运行配置扩展 & RetroFuturaGradle（1.12.2 环境核心插件）
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("com.gtnewhorizons.retrofuturagradle") version "1.3.35"
    id("com.matthewprenger.cursegradle") version "1.4.0" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
    id("org.jetbrains.changelog") version "2.2.0"
    // id("org.jetbrains.kotlin.jvm")
}

// 引入 GTRFG 提供的一些 Groovy 辅助函数（propertyString 等）
apply(from = "gradle/scripts/helpers.gradle")

// 早期断言：在配置阶段就校验必须存在的 gradle.properties / tags.properties 字段
assertProperty("mod_version")
assertProperty("root_package")
assertProperty("mod_id")
assertProperty("mod_name")

assertSubProperties("use_tags", "tag_class_name")
assertSubProperties("use_access_transformer", "access_transformer_locations")
assertSubProperties("use_mixins", "mixin_booter_version", "mixin_refmap")
assertSubProperties("is_coremod", "coremod_includes_mod", "coremod_plugin_class_name")
assertSubProperties("use_asset_mover", "asset_mover_version")

// 设置一些可选特性（不存在时给默认值）
setDefaultProperty("use_modern_java_syntax", false, false)
setDefaultProperty("generate_sources_jar", true, false)
setDefaultProperty("generate_javadocs_jar", true, false)
setDefaultProperty("mapping_channel", true, "stable")
setDefaultProperty("mapping_version", true, "39")
setDefaultProperty("use_dependency_at_files", true, true)
setDefaultProperty("minecraft_username", true, "Developer")
setDefaultProperty("extra_jvm_args", false, "")
setDefaultProperty("extra_tweak_classes", false, "")
setDefaultProperty("change_minecraft_sources", false, false)

// 项目版本 & Maven groupId
version = propertyString("mod_version")
group = propertyString("root_package")

base {
    archivesName.set(propertyString("mod_id"))
}

// 是否跳过 MC 源码解压任务（减少构建时间）
tasks.named("decompressDecompiledSources").configure {
    enabled = !propertyBool("change_minecraft_sources")
}

java {
    // Java toolchain（JDK 版本与供应商）
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(if (propertyBool("use_modern_java_syntax")) 16 else 8))
        vendor.set(JvmVendorSpec.AZUL)
    }
    // 是否生成 sources / javadoc jar
    if (propertyBool("generate_sources_jar")) {
        withSourcesJar()
    }
    if (propertyBool("generate_javadocs_jar")) {
        withJavadocJar()
    }
}

// embed：需要被打进 mod jar 的依赖（类似 shaded jar）
val embed: Configuration by configurations.creating
configurations.named("implementation") { extendsFrom(embed) }

minecraft {
    // RetroFuturaGradle 的 MC/映射/运行配置
    mcVersion.set("1.12.2")

    mcpMappingChannel.set(propertyString("mapping_channel"))
    mcpMappingVersion.set(propertyString("mapping_version"))

    useDependencyAccessTransformers.set(propertyBool("use_dependency_at_files"))

    username.set(propertyString("minecraft_username"))

    val args = mutableListOf("-ea:$group")
    if (propertyBool("use_mixins")) {
        args += "-Dmixin.hotSwap=true"
        args += "-Dmixin.checks.interfaces=true"
        args += "-Dmixin.debug.export=true"
    }
    extraRunJvmArguments.addAll(args)
    extraRunJvmArguments.addAll(propertyStringList("extra_jvm_args"))

    // 自动注入 tags.properties 到 MOD 代码中（常量表）
    val tagFile = file("tags.properties")
    if (tagFile.exists()) {
        val props = Properties().apply { load(tagFile.inputStream()) }
        if (props.isNotEmpty()) {
            injectedTags.set(props.entries.associate { (k, v) -> k.toString() to interpolate(v.toString()) })
        }
    }
}
}

// 依赖仓库
repositories {
maven {
name = "CleanroomMC Maven"
url = uri("https://maven.cleanroommc.com")
}
mavenCentral()
}

dependencies {
// 使用“现代语法”（JDK16 编译→字节码兼容 1.8）时需要的插件和 shim
if (propertyBool("use_modern_java_syntax")) {
annotationProcessor("com.github.bsideup.jabel:jabel-javac-plugin:1.0.0")
annotationProcessor("net.java.dev.jna:jna-platform:5.13.0")
compileOnly("com.github.bsideup.jabel:jabel-javac-plugin:1.0.0") {
isTransitive = false
}
patchedMinecraft("me.eigenraven.java8unsupported:java-8-unsupported-shim:1.0.0")
testAnnotationProcessor("com.github.bsideup.jabel:jabel-javac-plugin:1.0.0")
testCompileOnly("com.github.bsideup.jabel:jabel-javac-plugin:1.0.0") {
isTransitive = false
}
}
// 可选：Cleanroom 的资源搬运工具
implementation("com.cleanroommc:assetmover:${propertyString("asset_mover_version")}")
}
// Mixin 相关依赖与注解处理器
val mixin = modUtils.enableMixins("zone.rong:mixinbooter:${propertyString("mixin_booter_version")}", propertyString("mixin_refmap"))
api(mixin) {
isTransitive = false
}
annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
annotationProcessor("com.google.guava:guava:24.1.1-jre")
annotationProcessor("com.google.code.gson:gson:2.8.6")
annotationProcessor(mixin) {
isTransitive = false
}
}
// JUnit 5 测试依赖
testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
}

// 额外依赖（按模板拆分在 scripts/dependencies.gradle 中）
apply(from = "gradle/scripts/dependencies.gradle")

// 为 reobf/deobf 任务添加 Access Transformer 文件
if (propertyBool("use_access_transformer")) {
for (location in propertyStringList("access_transformer_locations")) {
val fileLocation = file("${projectDir}/src/main/resources/$location")
if (fileLocation.exists()) {
tasks.named("deobfuscateMergedJarToSrg").configure {
val at = (this as GroovyObject).getProperty("accessTransformerFiles") as org.gradle.api.file.ConfigurableFileCollection
at.from(fileLocation)
}
tasks.named("srgifyBinpatchedJar").configure {
val at = (this as GroovyObject).getProperty("accessTransformerFiles") as org.gradle.api.file.ConfigurableFileCollection
at.from(fileLocation)
}
} else {
throw GradleException("Access Transformer file [$fileLocation] does not exist!")
}
}
}

// 处理资源文件：替换 mcmod.info / pack.mcmeta / mixin 配置中的占位符
tasks.processResources {
val filterList = mutableListOf("mcmod.info", "pack.mcmeta")
filterList.addAll(propertyStringList("mixin_configs").map { "mixins.$it.json" })

filesMatching(filterList) {
expand(
mapOf(
"mod_id" to propertyString("mod_id"),
"mod_name" to propertyString("mod_name"),
"mod_version" to propertyString("mod_version"),
"mod_description" to propertyString("mod_description"),
"mod_authors" to "[${propertyStringList("mod_authors", ",").joinToString(", ")}]",
"mod_credits" to propertyString("mod_credits"),
"mod_url" to propertyString("mod_url"),
"mod_update_json" to propertyString("mod_update_json"),
"mod_logo_path" to propertyString("mod_logo_path"),
"mixin_refmap" to propertyString("mixin_refmap"),
"mixin_package" to propertyString("mixin_package")
)
)
}

if (propertyBool("use_access_transformer")) {
// 将 *_at.cfg 移动到 META-INF 中，供 FML 识别
rename("(.+_at.cfg)", "META-INF/\$1")
}
}

// 打包最终 mod jar
tasks.jar {
manifest {
// 生成 jar MANIFEST 中的 FML / coremod / AT 相关属性
val attributeMap = mutableMapOf<String, Any>()
if (propertyBool("is_coremod")) {
attributeMap["FMLCorePlugin"] = propertyString("coremod_plugin_class_name")
if (propertyBool("coremod_includes_mod")) {
attributeMap["FMLCorePluginContainsFMLMod"] = true
val currentTasks = gradle.startParameter.taskNames
if (currentTasks.firstOrNull() in listOf("build", "prepareObfModsFolder", "runObfClient")) {
attributeMap["ForceLoadAsMod"] = true
}
}
}
if (propertyBool("use_access_transformer")) {
attributeMap["FMLAT"] = propertyString("access_transformer_locations")
}
attributes(attributeMap)
}
// 把 embed 配置下的依赖打进 jar（fat jar）
from(provider { configurations.getByName("embed").map { if (it.isDirectory) it else zipTree(it) } })
}

// IDEA 工程 & 运行配置
idea {
module {
isInheritOutputDirs = true
}
project {
settings {
runConfigurations {
create("1. Run Client", Gradle::class.java) {
taskNames = listOf("runClient")
}
create("2. Run Server", Gradle::class.java) {
taskNames = listOf("runServer")
}
create("3. Run Obfuscated Client", Gradle::class.java) {
taskNames = listOf("runObfClient")
}
create("4. Run Obfuscated Server", Gradle::class.java) {
taskNames = listOf("runObfServer")
}
}
compiler.javac {
afterEvaluate {
javacAdditionalOptions = "-encoding utf8"
moduleJavacAdditionalOptions = mapOf(
(project.name + ".main") to tasks.named("compileJava").get().options.compilerArgs.joinToString(" ") { "\"$it\"" }
)
}
}
}
}
}

// 测试源码固定使用 Java 8
tasks.named<JavaCompile>("compileTestJava").configure {
sourceCompatibility = "1.8"
targetCompatibility = "1.8"
}

// JUnit5 测试任务配置
tasks.test {
useJUnitPlatform()
javaLauncher.set(javaToolchains.launcherFor {
languageVersion.set(JavaLanguageVersion.of(8))
})
if (propertyBool("show_testing_output")) {
testLogging {
showStandardStreams = true
}
}
}

// 发布时用于读取 CHANGELOG.md 某个版本段落的工具函数
fun parserChangelog(): String {
if (!file("CHANGELOG.md").exists()) {
throw GradleException("publish_with_changelog is true, but CHANGELOG.md does not exist in the workspace!")
}
val parsedChangelog = changelog.renderItem(
changelog.get(propertyString("mod_version")).withHeader(false).withEmptySections(false),
Changelog.OutputType.MARKDOWN
)
if (parsedChangelog.isEmpty()) {
throw GradleException("publish_with_changelog is true, but the changelog for the latest version is empty!")
}
return parsedChangelog
}

// 如果启用了 mixin 且需要自动生成 mixins.xxx.json，则在这里生成一个基础模板
tasks.register("generateMixinJson") {
group = "cleanroom helpers"
val missingConfig = propertyStringList("mixin_configs").filter { !file("src/main/resources/mixins.$it.json").exists() }
onlyIf {
propertyBool("use_mixins") && propertyBool("generate_mixins_json") && missingConfig.isNotEmpty()
}
doLast {
for (mixinConfig in missingConfig) {
val file = file("src/main/resources/mixins.$mixinConfig.json")
val refmap = propertyString("mixin_refmap")
file.appendText("""{"package": "","required": true,"refmap": "$refmap","target": "@env(DEFAULT)","minVersion": "0.8.5","compatibilityLevel": "JAVA_8","mixins": [],"server": [],"client": []}""")
}
}
}

// 全局 Java 编译选项（编码；是否启用“现代语法”路径）
tasks.withType<JavaCompile>().configureEach {
options.encoding = "UTF-8"
if (propertyBool("use_modern_java_syntax")) {
if (name in listOf("compileMcLauncherJava", "compilePatchedMcJava")) return@configureEach
sourceCompatibility = "17"
options.release.set(8)
javaCompiler.set(javaToolchains.compilerFor {
languageVersion.set(JavaLanguageVersion.of(16))
vendor.set(JvmVendorSpec.AZUL)
})
}
}

// IDEA 同步后自动执行的一些清理/生成任务集合
tasks.register("cleanroomAfterSync") {
group = "cleanroom helpers"
dependsOn("injectTags", "generateMixinJson")
}

// 若启用现代语法，则 Javadoc 也改用更高的 source 版本
if (propertyBool("use_modern_java_syntax")) {
tasks.withType<Javadoc>().configureEach {
sourceCompatibility = "17"
}
}

// 配置 RetroFuturaGradle 的 injectTags 任务：仅在启用 tags 且非空时运行
tasks.named("injectTags", InjectTagsTask::class).configure {
onlyIf {
propertyBool("use_tags") && tags.get().isNotEmpty()
}
outputClassName.set(propertyString("tag_class_name"))
}

// 在准备混淆环境的 mods 目录后，额外执行 coremod 排序任务
tasks.named("prepareObfModsFolder").configure {
finalizedBy("prioritizeCoremods")
}

// 将关键 coremod（mixinbooter / configanytime）文件名加前缀 "!"，让其加载顺序更靠前
tasks.register("prioritizeCoremods") {
dependsOn("prepareObfModsFolder")
doLast {
fileTree("run/obfuscated").forEach {
if (it.isFile && it.name.matches(Regex("(mixinbooter|configanytime)(-)([0-9])+(\\.+)([0-9])+(\\.jar)"))) {
it.renameTo(File(it.parentFile, "!${it.name}"))
}
}
}
}

// IDEA 项目级设置：同步后自动跑 cleanroomAfterSync
idea.project.settings {
taskTriggers {
afterSync("cleanroomAfterSync")
}
}

apply(from = "gradle/scripts/publishing.gradle")
apply(from = "gradle/scripts/extra.gradle")

