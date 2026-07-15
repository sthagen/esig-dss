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
package eu.europa.esig.dss.pades.validation.suite;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.PDFRevisionWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.enumerations.SignatureScopeType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DSS3913Test extends AbstractPAdESTestValidation {

	@Override
	protected DSSDocument getSignedDocument() {
		return new InMemoryDocument(getClass().getResourceAsStream("/validation/DSS-3913.pdf"));
	}

	@Override
	protected void checkPdfRevision(DiagnosticData diagnosticData) {
		super.checkPdfRevision(diagnosticData);

		boolean firstSigFound = false;
		boolean secondSigFound = false;
		for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
			PDFRevisionWrapper pdfRevision = signatureWrapper.getPDFRevision();
			assertNotNull(pdfRevision);

			List<String> signatureFieldNames = pdfRevision.getSignatureFieldNames();
			assertEquals(1, signatureFieldNames.size());

			if ("level1.level2.field1".equals(signatureFieldNames.get(0))) {
				List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
				assertEquals(1, signatureScopes.size());
				assertEquals(SignatureScopeType.PARTIAL, signatureScopes.get(0).getScope());

				firstSigFound = true;

			} else if ("altLevel1.altLevel2.field1".equals(signatureFieldNames.get(0))) {
				List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
				assertEquals(1, signatureScopes.size());
				assertEquals(SignatureScopeType.FULL, signatureScopes.get(0).getScope());

				secondSigFound = true;
			}

			assertTrue(pdfRevision.isPdfSignatureDictionaryConsistent());
		}
		assertTrue(firstSigFound);
		assertTrue(secondSigFound);
	}

}
