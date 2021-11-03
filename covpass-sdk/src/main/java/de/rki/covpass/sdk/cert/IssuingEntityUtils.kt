/*
 * (C) Copyright IBM Deutschland GmbH 2021
 * (C) Copyright IBM Corp. 2021
 */

package de.rki.covpass.sdk.cert

import de.rki.covpass.sdk.storage.IssuingEntityRepository
import de.rki.covpass.sdk.utils.sha512
import de.rki.covpass.sdk.utils.toHex
import java.time.LocalDate

@Suppress("NestedBlockDepth")
public fun validateEntity(uvci: String, ignoreDate: Boolean = false) {
    IssuingEntityRepository.entityBlacklist.forEach { (blacklistedEntity, dateLimit) ->
        extractEntity(uvci)?.let { entity ->
            val entitySHA512 = entity.sha512().toHex()
            if (blacklistedEntity == entitySHA512) {
                when {
                    dateLimit?.isAfter(LocalDate.now()) == true && !ignoreDate ->
                        throw BlacklistedEntityFromFutureDateException()
                    dateLimit?.isAfter(LocalDate.now()) == true && ignoreDate -> return
                    else -> throw BlacklistedEntityException()
                }
            }
        }
    }
}

private fun extractEntity(uvci: String): String? {
    val regex = "[a-zA-Z]{2}/.+?(?=/)".toRegex()
    return regex.find(uvci)?.value
}

/**
 * This exception is thrown when a entity is blacklisted.
 */
public class BlacklistedEntityException : DgcDecodeException("Blacklisted Issuing Entity")

/**
 * This exception is thrown when a entity is blacklisted from a future date.
 */
public class BlacklistedEntityFromFutureDateException :
    DgcDecodeException("Blacklisted Issuing Entity From Future Date")
