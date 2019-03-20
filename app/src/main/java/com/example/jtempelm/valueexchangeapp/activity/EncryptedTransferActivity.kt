package com.example.jtempelm.valueexchangeapp.activity

import androidx.appcompat.app.AppCompatActivity
import com.example.spring.kotlin.dto.EncryptedPayload
import com.example.spring.kotlin.dto.HybridEncryptedPayload
import com.example.spring.kotlin.util.CipherAlgorithm
import com.google.gson.Gson
import java.nio.ByteBuffer
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

abstract class EncryptedTransferActivity : AppCompatActivity() {

    private val AES_CIPHER = "AES/GCM/NoPadding"
    private val TAG_LENGTH_BITS = 128
    private val AES_IV_LENGTH_BYTES = 12
    private val AES_KEY_LENGTH_BYTES = 16 //128 bit key, AES is 128, 192 and 256

    private val RSA_CIPHER = "RSA/ECB/PKCS1Padding"

    //TODO We obviously should package this in our app in a better way
    private val base64EncodedSharedAppPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmAMTXnLy2BAZQw/trJSqYpO/e8VX7vzfJThQSV8KF3AMvllZ3qLSzRkFfhHPtlVGfBr3+aY42F2mPKy3x3wuc2QusRniMG2SXZ3vSABI63HUakLUZizwPK+HeW5L3bwf5UOFDJYr7oYkesBXjRapg5XFtrEpEarY0D8hYhHkeOJiPBCJ+dnjS+mq5OG9B+jj1FWrQzyUxqlZrU+Nv6idya8wmEGBvNNyGXbGbFT/fWo6Zgc8mdvhvU6gLup/RT4tI4LbslS/Gl7HtreQMS2Vr9kn7dlrZiLpD1szWC0ThFlXbOPz4PVDgT4+NG7grifbHO2isQCqqoum2YDS1NhPYQIDAQAB"

    fun generateBase64EncodedKey(length: Int): String {
        val secureRandom = SecureRandom()
        val key = ByteArray(length)
        secureRandom.nextBytes(key)

        return Base64.getEncoder().encodeToString(key)
    }

    fun encryptAES(payload: String, encodedKey: String): EncryptedPayload {
        val secretKey = getAESSecretKey(encodedKey)
        val iv = generateInitializationVector(AES_IV_LENGTH_BYTES)

        val cipher = Cipher.getInstance(AES_CIPHER)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            GCMParameterSpec(TAG_LENGTH_BITS, iv)
        )

        val cipherBytes: ByteArray = cipher.doFinal(payload.toByteArray(Charsets.UTF_8))

        //we encode the iv at the front of the cipherText message so it is packaged together for transit
        val byteBuffer = ByteBuffer.allocate(4 + iv.size + cipherBytes.size)
        byteBuffer.putInt(iv.size)
        byteBuffer.put(iv)
        byteBuffer.put(cipherBytes)

        val ivWithCipherBytes = byteBuffer.array()
        val encodedCipherText = Base64.getEncoder().encodeToString(ivWithCipherBytes)

        //key being "" here is important! Otherwise we would build it into out return payload
        return EncryptedPayload(cipherText = encodedCipherText, iv = Base64.getEncoder().encodeToString(iv), key = "", algorithm = CipherAlgorithm.AES.cipher, keyPair = null)
    }

    private fun getAESSecretKey(encodedKey: String): SecretKeySpec {
        val decodedKey = Base64.getDecoder().decode(encodedKey)

        return SecretKeySpec(decodedKey, CipherAlgorithm.AES.cipher)
    }

    private fun generateInitializationVector(length: Int): ByteArray {
        val secureRandom = SecureRandom() //SecureRandom.getInstanceStrong() //wait for more entropy by invoking "strong" mode, but block the thread for a long time
        val iv = ByteArray(length)
        secureRandom.nextBytes(iv)

        return iv
    }

    fun toPublicRSAKey(base64EncodedKey: String): PublicKey {
        val keyBytes: ByteArray = Base64.getDecoder().decode(base64EncodedKey)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(CipherAlgorithm.RSA.cipher)

        return keyFactory.generatePublic(spec)
    }

    fun toPrivateRSAKey(base64EncodedKey: String): PrivateKey {
        val keyBytes: ByteArray = Base64.getDecoder().decode(base64EncodedKey)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(CipherAlgorithm.RSA.cipher)

        return keyFactory.generatePrivate(spec)
    }

    fun encryptRSA(payload: String, pubKey: PublicKey): String {
        val cipher: Cipher = Cipher.getInstance(RSA_CIPHER)

        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        val encryptedBytes = cipher.doFinal(payload.toByteArray(Charsets.UTF_8))

        val cipherText = Base64.getEncoder().encodeToString(encryptedBytes)

        return cipherText
    }

    fun decryptRSA(encryptedPayload: EncryptedPayload): String {
        val cipher: Cipher = Cipher.getInstance(RSA_CIPHER)
        cipher.init(Cipher.DECRYPT_MODE, encryptedPayload.keyPair?.private)

        val cipherMessage: ByteArray = Base64.getDecoder().decode(encryptedPayload.cipherText)
        return cipher.doFinal(cipherMessage).toString(Charsets.UTF_8)
    }

    fun encryptRequestBody(requestBody: String): String {
        val aesKey = generateBase64EncodedKey(AES_KEY_LENGTH_BYTES)

        val encryptedAesPayload = encryptAES(requestBody, aesKey)

        val rsaEncryptedAesKey = encryptRSA(aesKey, toPublicRSAKey(base64EncodedSharedAppPublicKey))

        val hybridEncryptedPayload = HybridEncryptedPayload(encryptedKey = rsaEncryptedAesKey, encryptedPayload = encryptedAesPayload)

        val gson = Gson()
        return gson.toJson(hybridEncryptedPayload)
    }

}
