package com.securityx.modelfeature.utils;

import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

public class CefExtension implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CefExtension.class);

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("extensionFields", extensionFields)
                .toString();
    }

    /**
     * Serial version
     */
    private static final long serialVersionUID = 1L;

    /**
     * Holds the field mapping
     */
    private final Map<String, String> extensionFields;

    public CefExtension(final Map<String, String> extensionFields) {
        this.extensionFields = extensionFields;
    }


    public Map<String, String> getExtensionFields() {
        return extensionFields;
    }
}
