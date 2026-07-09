package eu.europa.esig.dss.token;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.spi.DSSSecurityProvider;
import eu.europa.esig.dss.spi.DSSUtils;

/**
 * For manual testing
 */
@Disabled
class MSCAPIWithRSASSAPSSTest {

    private static final Logger LOG = LoggerFactory.getLogger(MSCAPIWithRSASSAPSSTest.class);

    static {
        DSSSecurityProvider.initSystemProviders();
    }

    @Test
    void testSigning() {

        try (MSCAPISignatureToken signatureToken = new MSCAPISignatureToken()) {
            List<DSSPrivateKeyEntry> keys = signatureToken.getKeys();
            KSPrivateKeyEntry entry = (KSPrivateKeyEntry) keys.get(0);

            ToBeSigned toBeSigned = new ToBeSigned("Hello world".getBytes(StandardCharsets.UTF_8));

            SignatureValue signValue = signatureToken.sign(toBeSigned, SignatureAlgorithm.RSA_SSA_PSS_SHA256_MGF1, entry);
            assertNotNull(signValue.getAlgorithm());
            LOG.info("Sig value : {}", Base64.getEncoder().encodeToString(signValue.getValue()));

            assertDoesNotThrow(() -> {
                Signature sig = Signature.getInstance(signValue.getAlgorithm().getJCEId());
                sig.initVerify(entry.getCertificate().getPublicKey());
                sig.update(toBeSigned.getBytes());
                assertTrue(sig.verify(signValue.getValue()));
            });

            assertDoesNotThrow(() -> {
                Cipher cipher = Cipher.getInstance(entry.getEncryptionAlgorithm().getName(),
                        DSSSecurityProvider.getSecurityProviderName());
                cipher.init(Cipher.DECRYPT_MODE, entry.getCertificate().getPublicKey());
                byte[] decrypted = cipher.doFinal(signValue.getValue());
                LOG.info("Decrypted : {}", Base64.getEncoder().encodeToString(decrypted));
            });
        }
    }

    @Test
    void testDigestSigning() {

        try (MSCAPISignatureToken signatureToken = new MSCAPISignatureToken()) {
            List<DSSPrivateKeyEntry> keys = signatureToken.getKeys();
            KSPrivateKeyEntry entry = (KSPrivateKeyEntry) keys.get(0);

            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RSA_SSA_PSS_SHA256_MGF1;
            ToBeSigned toBeSigned = new ToBeSigned("Hello world".getBytes(StandardCharsets.UTF_8));
            final DigestAlgorithm digestAlgorithm = signatureAlgorithm.getDigestAlgorithm();
            final byte[] digestBinaries = DSSUtils.digest(digestAlgorithm, toBeSigned.getBytes());
            Digest digest = new Digest(digestAlgorithm, digestBinaries);

            assertThrows(DSSException.class, () -> signatureToken.signDigest(digest, signatureAlgorithm, entry));
        }
    }
}
