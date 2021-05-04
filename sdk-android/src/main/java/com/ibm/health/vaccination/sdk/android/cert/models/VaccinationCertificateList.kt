package com.ibm.health.vaccination.sdk.android.cert.models

import kotlinx.serialization.Serializable

/**
 * Data model which contains a list of [ExtendedVaccinationCertificate] and a pointer to the favorite / own certificate.
 */
// FIXME: Maybe make immutable?
@Serializable
public data class VaccinationCertificateList(
    var certificates: MutableList<ExtendedVaccinationCertificate> = mutableListOf(),
    var favoriteCertId: String? = null,
) {

    // FIXME: Split logic up from data.
    public fun addCertificate(certificate: ExtendedVaccinationCertificate) {
        if (certificates.none { it.vaccinationCertificate.id == certificate.vaccinationCertificate.id }) {
            certificates.add(certificate)
            if (certificates.size == 1) {
                favoriteCertId = certificate.vaccinationCertificate.id
            }
        } else {
            throw CertAlreadyExistsException()
        }
    }
}

/** This exception is thrown when certificates list already has such certificate */
public class CertAlreadyExistsException : RuntimeException()