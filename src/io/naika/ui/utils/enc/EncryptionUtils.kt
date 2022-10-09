package io.naika.ui.utils.enc

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets

fun Context.writeEncryptedFile(content: String, fileName: String) {
    // Although you can define your own key generation parameter specification, it's
    // recommended that you use the value specified here.
    val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

    // Create a file with this name or replace an entire existing file
    // that has the same name. Note that you cannot append to an existing file,
    // and the filename cannot contain path separators.
    val encryptedFile = EncryptedFile.Builder(
        File(dataDir, fileName),
        applicationContext,
        mainKeyAlias,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()

    if (File(dataDir, fileName).exists()) File(dataDir, fileName).delete()

    val fileContent = content
        .toByteArray(StandardCharsets.UTF_8)
    encryptedFile.openFileOutput().apply {
        write(fileContent)
        flush()
        close()
    }
}

fun Context.readEncryptedFile(fileName: String): String {
    // Although you can define your own key generation parameter specification, it's
    // recommended that you use the value specified here.
    val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

    val encryptedFile = EncryptedFile.Builder(
        File(dataDir, fileName),
        applicationContext,
        mainKeyAlias,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()

    val inputStream = encryptedFile.openFileInput()
    val byteArrayOutputStream = ByteArrayOutputStream()
    var nextByte: Int = inputStream.read()
    while (nextByte != -1) {
        byteArrayOutputStream.write(nextByte)
        nextByte = inputStream.read()
    }

    return byteArrayOutputStream.toString()
}

val Context.encryptedPreferences: SharedPreferences
    get() {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    return EncryptedSharedPreferences.create(
        "encw3data",
        masterKeyAlias,
        this,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}