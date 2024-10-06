package com.yanbin.ptplib.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class PtpAnnotationProcessorProvider: SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return PtpPacketAnnotationProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}