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

class PAdESFormModifiedFieldMDPInvalidTest extends AbstractPAdESTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new InMemoryDocument(getClass().getResourceAsStream("/validation/fieldmdp-modified-invalid.pdf"));
    }

    @Override
    protected void checkPdfRevision(DiagnosticData diagnosticData) {
        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        assertFalse(Utils.isCollectionEmpty(signature.getPdfExtensionChanges()));
        assertFalse(Utils.isCollectionEmpty(signature.getPdfSignatureOrFormFillChanges()));
        assertTrue(Utils.isCollectionEmpty(signature.getPdfAnnotationChanges()));
        assertTrue(Utils.isCollectionEmpty(signature.getPdfUndefinedChanges()));
    }

    @Override
    protected void verifySimpleReport(SimpleReport simpleReport) {
        super.verifySimpleReport(simpleReport);

        List<Message> warnings = simpleReport.getAdESValidationWarnings(simpleReport.getFirstSignatureId());
        assertTrue(warnings.stream().anyMatch(m -> MessageTag.BBB_FC_ISVAFMDPD_ANS.getId().equals(m.getKey())));
        assertTrue(warnings.stream().anyMatch(m -> MessageTag.BBB_FC_ISVASFLD_ANS.getId().equals(m.getKey())));
    }

}
