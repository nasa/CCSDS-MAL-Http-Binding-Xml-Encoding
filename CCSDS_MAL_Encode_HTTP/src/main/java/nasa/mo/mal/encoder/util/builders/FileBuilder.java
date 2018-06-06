/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.encoder.util.builders;

import org.ccsds.moims.mo.mal.structures.*;

/**
 * @author wphyo
 *         Created on 8/9/17.
 */
public class FileBuilder {
    private Identifier name;
    private String mimeType;
    private Time creationDate;
    private Time modificationDate;
    private ULong size;
    private Blob content;
    private NamedValueList metaData;

    private FileBuilder() {
    }

    public static FileBuilder create() {
        return new FileBuilder();
    }

    public File build() {
        return new File(name, mimeType, creationDate, modificationDate, size, content, metaData);
    }

    public FileBuilder name(final Identifier name) {
        this.name = name;
        return this;
    }

    public FileBuilder mimeType(final String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public FileBuilder creationDate(final Time creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public FileBuilder modificationDate(final Time modificationDate) {
        this.modificationDate = modificationDate;
        return this;
    }

    public FileBuilder size(final ULong size) {
        this.size = size;
        return this;
    }

    public FileBuilder content(final Blob content) {
        this.content = content;
        return this;
    }

    public FileBuilder metaData(final NamedValueList metaData) {
        this.metaData = metaData;
        return this;
    }
}
