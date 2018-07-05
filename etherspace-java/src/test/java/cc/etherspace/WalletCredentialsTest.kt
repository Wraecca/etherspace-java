package cc.etherspace

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import org.web3j.crypto.Keys

class WalletCredentialsTest {
    @Test
    fun create() {
        val credentials = WalletCredentials("0xab1e199623aa5bb2c381c349b1734e31b5be08de0486ffab68e3af4853d9980b")
        credentials.address.`should be equal to`(Keys.toChecksumAddress("0x39759a3c0ada2d61b6ca8eb6afc8243075307ed3"))
    }

    @Test
    fun create_wallet() {
        val credentials = WalletCredentials("etherspace",
                "{\"address\":\"c1574afd83ad551280f692f448679a2e7633ed5d\",\"id\":\"7f3d1f13-ee1d-4370-b6eb-60c230472802\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"594605db2823c918c1e6f1e09bdd0345c70dbb457727b0140488f87335a2d2ad\",\"cipherparams\":{\"iv\":\"c28d25055c656fd5592d12e9ae8c05ef\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"eb1ac7b57f5f7a95aeef253ce179a72241bcab12577cdba6ac92b5f4895a69e0\"},\"mac\":\"95765f8b19be3498d446427f5331916062330d616be7e5f02259eefa816dd224\"}}",
                jacksonObjectMapper())
        credentials.address.`should be equal to`(Keys.toChecksumAddress("0xc1574afd83ad551280f692f448679a2e7633ed5d"))
    }
}