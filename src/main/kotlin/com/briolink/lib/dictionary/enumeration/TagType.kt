package com.briolink.lib.dictionary.enumeration

import com.briolink.lib.common.jpa.type.PersistentEnum
import com.fasterxml.jackson.annotation.JsonProperty

enum class TagType(
    override val value: String,
    val idType: Int = value.toInt(),
    val withPath: Boolean = false
) : PersistentEnum {
    @JsonProperty
    Industry("1", withPath = true),

    @JsonProperty
    Keyword("2"),

    @JsonProperty
    Vertical("3"),

    @JsonProperty
    CPC("4"),

    @JsonProperty
    InvestorType("5"),

    @JsonProperty
    OwnerShipStatus("6"),

    @JsonProperty
    Universe("7"),

    @JsonProperty
    DealClass("8"),

    @JsonProperty
    ServiceProviderType("9"),

    @JsonProperty
    DealType("10", withPath = true),

    @JsonProperty
    SIC("11", withPath = true),

    @JsonProperty
    FinancingStatus("12"),

    @JsonProperty
    InvestorStatus("13"),

    @JsonProperty
    ValuationStatus("14"),

    @JsonProperty
    RealAssetType("14"),

    @JsonProperty
    OtherStated("15"),

    @JsonProperty
    InvestmentType("16");

    companion object {
        private val map = values().associateBy(TagType::value)

        fun ofValue(type: String): TagType = map[type] ?: throw IllegalArgumentException("$type is not a valid TagType")
    }
}
