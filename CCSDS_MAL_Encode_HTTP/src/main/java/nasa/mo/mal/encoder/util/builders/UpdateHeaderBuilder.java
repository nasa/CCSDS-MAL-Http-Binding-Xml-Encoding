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
public class UpdateHeaderBuilder {
    private Time timestamp;
    private URI sourceURI;
    private UpdateType updateType;
    private EntityKey key;

    private UpdateHeaderBuilder() {
    }

    public static UpdateHeaderBuilder create() {
        return new UpdateHeaderBuilder();
    }

    public UpdateHeader build() {
        return new UpdateHeader(timestamp, sourceURI, updateType, key);
    }

    public UpdateHeaderBuilder timestamp(final Time timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public UpdateHeaderBuilder sourceURI(final URI sourceURI) {
        this.sourceURI = sourceURI;
        return this;
    }

    public UpdateHeaderBuilder updateType(final UpdateType updateType) {
        this.updateType = updateType;
        return this;
    }

    public UpdateHeaderBuilder key(final EntityKey key) {
        this.key = key;
        return this;
    }
}
