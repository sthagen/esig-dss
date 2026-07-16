package eu.europa.esig.dss.pades.extension.suite;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PAdESExtensionFullyQualifiedNameBToLTATest extends AbstractPAdESTestExtension {

    @Override
    protected FileDocument generateOriginalDocument() {
        DSSDocument fullyQualifiedNameDoc = new InMemoryDocument(getClass().getResourceAsStream("/doc-fully-qualified-name.pdf"), "doc.pdf", MimeTypeEnum.PDF);
        File originalDoc = new File("target/original-" + UUID.randomUUID().toString() + ".pdf");
        try (FileOutputStream fos = new FileOutputStream(originalDoc); InputStream is = fullyQualifiedNameDoc.openStream()) {
            Utils.copy(is, fos);
        } catch (IOException e) {
            throw new DSSException("Unable to create the original document", e);
        }
        return new FileDocument(originalDoc);
    }

    @Override
    protected PAdESSignatureParameters getSignatureParameters() {
        PAdESSignatureParameters signatureParameters = super.getSignatureParameters();

        SignatureImageParameters imageParameters = new SignatureImageParameters();
        signatureParameters.setImageParameters(imageParameters);
        SignatureFieldParameters fieldParameters = new SignatureFieldParameters();
        fieldParameters.setFieldId("level1.level2.field1");
        imageParameters.setFieldParameters(fieldParameters);
        SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
        textParameters.setText("Signature1");
        imageParameters.setTextParameters(textParameters);

        return signatureParameters;
    }

    @Override
    protected void checkPdfRevision(DiagnosticData diagnosticData) {
        super.checkPdfRevision(diagnosticData);

        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        assertEquals("level1.level2.field1", signature.getFirstFieldName());
    }

    @Override
    protected SignatureLevel getOriginalSignatureLevel() {
        return SignatureLevel.PAdES_BASELINE_B;
    }

    @Override
    protected SignatureLevel getFinalSignatureLevel() {
        return SignatureLevel.PAdES_BASELINE_LTA;
    }

}
