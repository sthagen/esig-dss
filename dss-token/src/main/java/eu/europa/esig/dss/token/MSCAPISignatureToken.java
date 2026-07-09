/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * <p>
 * This file is part of the "DSS - Digital Signature Services" project.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.token;

import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.SignatureValue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

/**
 * Class holding all MS CAPI API access logic.
 *
 */
public class MSCAPISignatureToken extends AbstractKeyStoreTokenConnection {

	/** The SunMSCAPI provider name */
	private static final String SUN_MSCAPI = "SunMSCAPI";

	/**
	 * Default constructor
	 */
	public MSCAPISignatureToken() {
		// empty
	}

	@Override
	protected KeyStore getKeyStore() throws DSSException {
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("Windows-MY");
			keyStore.load(null, null);
		} catch (IOException | GeneralSecurityException e) {
			throw new DSSException("Unable to load MS CAPI keystore", e);
		}
		return keyStore;
	}

	@Override
	protected PasswordProtection getKeyProtectionParameter() {
		return new PasswordProtection("nimp".toCharArray());
	}

	@Override
	protected Signature getSignatureInstance(final String javaSignatureAlgorithm) throws NoSuchAlgorithmException {
		try {
			if (javaSignatureAlgorithm.contains("RSAandMGF1")) {
                // See https://github.com/bcgit/bc-java/issues/2280
				return Signature.getInstance(EncryptionAlgorithm.RSASSA_PSS.getName(), SUN_MSCAPI);
			}
		} catch (NoSuchProviderException e) {
            throw new DSSException("Unable to load signature instance with provider " + SUN_MSCAPI, e);
        }

        return super.getSignatureInstance(javaSignatureAlgorithm);
    }

    @Override
    public SignatureValue signDigest(final Digest digest, final SignatureAlgorithm signatureAlgorithm, final DSSPrivateKeyEntry keyEntry) throws DSSException {
        verifyIfSignDigestPossible(digest, signatureAlgorithm, keyEntry);
        return super.signDigest(digest, signatureAlgorithm, keyEntry);
    }

    @Override
	public void close() {
		// nothing to close
	}

    /**
     * Method that verifies if the digest signature is possible.
     *
     * @param digest The digested data that need to be signed
     * @param signatureAlgorithm The signature algorithm
     * @param keyEntry The private key to be used
     */
    protected void verifyIfSignDigestPossible(final Digest digest, final SignatureAlgorithm signatureAlgorithm, final DSSPrivateKeyEntry keyEntry) {
        if (signatureAlgorithm.getEncryptionAlgorithm().equals(EncryptionAlgorithm.RSASSA_PSS)) {
            throw new DSSException("Not possible to sign digest with security provider " + SUN_MSCAPI + " using RSASSA-PSS encryption algorithm.");
        }
    }

}
