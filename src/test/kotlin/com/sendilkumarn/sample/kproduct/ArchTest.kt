package com.sendilkumarn.sample.kproduct

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.sendilkumarn.sample.kproduct")

        noClasses()
            .that()
                .resideInAnyPackage("com.sendilkumarn.sample.kproduct.service..")
            .or()
                .resideInAnyPackage("com.sendilkumarn.sample.kproduct.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.sendilkumarn.sample.kproduct.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses)
    }
}
