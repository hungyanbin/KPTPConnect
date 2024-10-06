package com.yanbin.ptplib.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.yanbin.ptplib.annotation.PtpPacket
import java.io.OutputStream

/**
 *
 *  When editing and debugging SymbolProcessor, please set the flag `org.gradle.caching=false` in gradle.properties
 *  to prevent IDE generates old cache for you.
 *
 *  Gradle task for running this processor: `./gradlew :app:kspDebugKotlin`
 */
class PtpPacketAnnotationProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private var isFirstRound = true

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!isFirstRound) {
            return emptyList()
        }

        val kClassDeclarations = resolver
            .getSymbolsWithAnnotation(PtpPacket::class.java.name)
            .filterIsInstance<KSClassDeclaration>()

        logger.warn("kClassDeclarations size ${kClassDeclarations.toList().size}")

        kClassDeclarations.forEach { ksClassDeclaration ->
            generatePtpPacketExtFile(ksClassDeclaration)
        }

        isFirstRound = false

        return emptyList()
    }

    private fun generatePtpPacketExtFile(ksClassDeclaration: KSClassDeclaration) {
        val packageName =ksClassDeclaration.packageName.asString()
        val fileName = ksClassDeclaration.simpleName.asString() + "Ext"
        val className = ksClassDeclaration.simpleName.asString()
        val constructorParameters = generateConstructorParameters(ksClassDeclaration)

        val file: OutputStream = codeGenerator.createNewFile(
            dependencies = Dependencies(true, *listOf(ksClassDeclaration.containingFile).mapNotNull { it }.toTypedArray()),
            packageName = packageName,
            fileName = fileName
        )

        logger.warn("className $className")

        file += """
package $packageName

import com.linein.ptplib.packets.utils.FieldReader
import com.linein.ptplib.packets.utils.*

fun ${className}(byteArray: ByteArray): $className {
    val fieldReader = FieldReader(byteArray)
    
    return $className(
$constructorParameters
    )
}
        """.trimIndent()

        file.close()
    }

    private fun generateConstructorParameters(classDeclaration: KSClassDeclaration): String {
        val properties = classDeclaration.declarations.filterIsInstance<KSPropertyDeclaration>()
        return properties.joinToString(", \n") { property ->
            val propertyName = property.simpleName.asString()
            "        $propertyName = ${fieldReaderMapping(property)}"
        }
    }

    private fun fieldReaderMapping(property: KSPropertyDeclaration): String {
        val propertyType = property.type.resolve().declaration.qualifiedName?.asString()
        return when (propertyType) {
            "kotlin.Int" -> "fieldReader.readInt()"
            "kotlin.Short" -> "fieldReader.readShort()"
            "kotlin.String" -> "fieldReader.readString()"
            "com.linein.ptplib.constants.ObjectFormat" -> "fieldReader.readObjectFormat()"
            "com.linein.ptplib.packets.PtpEvent" -> "fieldReader.readPtpEvent()"
            "java.time.LocalDateTime" -> "fieldReader.readDateTime()"
            "kotlin.collections.List" -> { fieldReaderListMapping(property) }
            else -> {
                logger.warn("unknown type $propertyType")
                "TODO()"
            }
        }
    }

    private fun fieldReaderListMapping(property: KSPropertyDeclaration): String {
        val propertyType = property.type.resolve()
        return if (propertyType.arguments.isNotEmpty()) {
            val elementType = propertyType.arguments.first().type?.resolve()
            when (elementType?.declaration?.qualifiedName?.asString()) {
                "kotlin.Short" -> "fieldReader.readShortArray().toList()"
                else -> {
                    logger.warn("unknown type $propertyType in List")
                    "TODO()"
                }
            }
        } else {
            throw IllegalStateException("List type must have at least one argument")
        }
    }

    private operator fun OutputStream.plusAssign(str: String) = write(str.toByteArray())
}