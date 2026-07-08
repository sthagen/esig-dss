package eu.europa.esig.dss.pades.validation.suite;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.jaxb.object.Message;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.utils.Utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// See DSS-3914
class PAdESAnnotModifiedDocMDPTest extends AbstractPAdESTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new InMemoryDocument(getClass().getResourceAsStream("/validation/dss-3914/annot-modified-docmdp.pdf"));
    }

    @Override
    protected void checkPdfRevision(DiagnosticData diagnosticData) {
        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        assertTrue(Utils.isCollectionEmpty(signature.getPdfExtensionChanges()));
        assertTrue(Utils.isCollectionEmpty(signature.getPdfSignatureOrFormFillChanges()));
        assertFalse(Utils.isCollectionEmpty(signature.getPdfAnnotationChanges()));
        assertTrue(Utils.isCollectionEmpty(signature.getPdfUndefinedChanges()));
    }

    @Override
    protected void verifySimpleReport(SimpleReport simpleReport) {
        super.verifySimpleReport(simpleReport);

        List<Message> warnings = simpleReport.getAdESValidationWarnings(simpleReport.getFirstSignatureId());
        assertTrue(warnings.stream().anyMatch(m -> MessageTag.BBB_FC_ISVADMDPD_ANS.getId().equals(m.getKey())));
    }

}
