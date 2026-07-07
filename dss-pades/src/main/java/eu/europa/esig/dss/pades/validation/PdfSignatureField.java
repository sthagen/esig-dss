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
package eu.europa.esig.dss.pades.validation;

import eu.europa.esig.dss.pades.PAdESUtils;
import eu.europa.esig.dss.pdf.PAdESConstants;
import eu.europa.esig.dss.pdf.PdfDict;
import eu.europa.esig.dss.pdf.SigFieldPermissions;

import java.io.Serializable;
import java.util.Objects;

/**
 * Object of this interface represents a PDF Signature field
 *
 */
public class PdfSignatureField implements Serializable {

    private static final long serialVersionUID = 1391102373661984177L;

    /** Name of the  signature field */
    private final String fieldName;

    /** Fully qualified name of the signature field */
    private final String fullyQualifiedName;

    /** The lock dictionary */
    private final SigFieldPermissions lockDictionary;

    /**
     * Default constructor
     *
     * @param sigFieldDict {@link PdfDict}
     */
    public PdfSignatureField(final PdfDict sigFieldDict) {
        Objects.requireNonNull(sigFieldDict, "sigFieldDict cannot be null!");
        this.fieldName = extractFieldName(sigFieldDict);
        this.fullyQualifiedName = extractFullyQualifiedName(sigFieldDict);
        this.lockDictionary = extractLockDictionary(sigFieldDict);
    }

    private static String extractFieldName(PdfDict sigFieldDict) {
        return sigFieldDict.getStringValue(PAdESConstants.FIELD_NAME_NAME);
    }

    private static String extractFullyQualifiedName(PdfDict sigFieldDict) {
        StringBuilder fullyQualifiedName = new StringBuilder();
        PdfDict currentDict = sigFieldDict;
        while (currentDict != null) {
            String partialName = currentDict.getStringValue(PAdESConstants.FIELD_NAME_NAME);
            if (partialName != null) {
                if (fullyQualifiedName.length() > 0) {
                    fullyQualifiedName.insert(0, ".");
                }
                fullyQualifiedName.insert(0, partialName);
            }
            currentDict = currentDict.getAsDict(PAdESConstants.PARENT_NAME);
        }
        return fullyQualifiedName.length() > 0 ? fullyQualifiedName.toString() : null;
    }

    private static SigFieldPermissions extractLockDictionary(PdfDict sigFieldDict) {
        PdfDict lock = sigFieldDict.getAsDict(PAdESConstants.LOCK_NAME);
        if (lock != null) {
            return PAdESUtils.extractPermissionsDictionary(lock);
        }
        return null;
    }

    /**
     * This method returns a signature field's name
     *
     * @return {@link String} name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * This method returns a signature field's fully qualified name.
     *
     * @return {@link String} fully qualified name
     */
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    /**
     * Returns a /Lock dictionary content, when present
     *
     * @return {@link SigFieldPermissions}
     */
    public SigFieldPermissions getLockDictionary() {
        return lockDictionary;
    }

    @Override
    public String toString() {
        return "PdfSignatureField {" +"name=" + getFieldName() + '}';
    }

}
