package com.example.phoneapp.service

import com.example.phoneapp.data.ContactDto
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import javax.inject.Inject

class DuplicateContactMapper @Inject constructor() {

    fun normalizeAndMapDtoToDuplicateContactKey(contactDto: ContactDto, defaultRegion: String): DuplicateContactKey{
        return DuplicateContactKey(
            displayName = contactDto.displayName,
            phoneNumber = normalizePhoneNumber(contactDto.phoneNumber, defaultRegion),
            phoneType = contactDto.phoneType,
            photoUri = contactDto.photoUri,
            isPrimary = contactDto.isPrimary,
            isSuperPrimary = contactDto.isSuperPrimary
        )
    }

    private fun normalizePhoneNumber(
        phoneNumber: String,
        defaultRegion: String
    ): String {
        val phoneUtil = PhoneNumberUtil.getInstance()

        return try {
            val parsedNumber = phoneUtil.parse(phoneNumber, defaultRegion)

            if (phoneUtil.isValidNumber(parsedNumber)) {
                phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
            } else {
                phoneNumber.trim()
            }
        } catch (e: NumberParseException) {
            phoneNumber.trim()
        }
    }

}
