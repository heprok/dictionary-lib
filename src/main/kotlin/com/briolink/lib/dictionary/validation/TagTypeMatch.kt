package com.briolink.lib.dictionary.validation

import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.Tag
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER
)
@MustBeDocumented
@Constraint(validatedBy = [TagTypeMatchValidator::class])
annotation class TagTypeMatch(
    val allowedValues: Array<TagType>,
    val message: String = "validation.tag.type-does-not-match.valid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class TagTypeMatchValidator : ConstraintValidator<TagTypeMatch, Tag> {
    private var allowedValues: List<TagType> = emptyList()

    override fun initialize(constraintAnnotation: TagTypeMatch) {
        allowedValues = constraintAnnotation.allowedValues.toList()
    }

    override fun isValid(
        value: Tag,
        context: ConstraintValidatorContext?
    ): Boolean = allowedValues.contains(value.id.type)
}
