package com.briolink.lib.dictionary.validation

import com.briolink.lib.dictionary.enumeration.TagType
import com.briolink.lib.dictionary.model.BaseTag
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
@Constraint(validatedBy = [TagTypeMatchInCollectionValidator::class])
annotation class TagTypeMatchInCollection(
    val allowedValues: Array<TagType>,
    val message: String = "validation.tag.type-does-not-match.valid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class TagTypeMatchInCollectionValidator : ConstraintValidator<TagTypeMatchInCollection, Collection<BaseTag>> {
    private var allowedValues: Set<TagType> = emptySet()

    override fun initialize(constraintAnnotation: TagTypeMatchInCollection) {
        allowedValues = constraintAnnotation.allowedValues.toSet()
    }

    override fun isValid(
        value: Collection<BaseTag>?,
        context: ConstraintValidatorContext?
    ): Boolean {
        return value == null || value.all { allowedValues.contains(it.type) }
    }
}
