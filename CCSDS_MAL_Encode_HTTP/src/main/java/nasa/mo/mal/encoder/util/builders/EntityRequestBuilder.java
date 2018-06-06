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

import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

/**
 * @author wphyo
 *         Created on 8/9/17.
 */
public class EntityRequestBuilder {
    private IdentifierList subDomain;
    private Boolean allAreas;
    private Boolean allServices;
    private Boolean allOperations;
    private Boolean onlyOnChange;
    private EntityKeyList entityKeys;

    private EntityRequestBuilder() {
    }

    public static EntityRequestBuilder create() {
        return new EntityRequestBuilder();
    }

    public EntityRequest build() {
        return new EntityRequest(subDomain, allAreas, allServices, allOperations, onlyOnChange, entityKeys);
    }

    public EntityRequestBuilder subDomain(final IdentifierList subDomain) {
        this.subDomain = subDomain;
        return this;
    }

    public EntityRequestBuilder allAreas(final Boolean allAreas) {
        this.allAreas = allAreas;
        return this;
    }

    public EntityRequestBuilder allServices(final Boolean allServices) {
        this.allServices = allServices;
        return this;
    }

    public EntityRequestBuilder allOperations(final Boolean allOperations) {
        this.allOperations = allOperations;
        return this;
    }

    public EntityRequestBuilder onlyOnChange(final Boolean onlyOnChange) {
        this.onlyOnChange = onlyOnChange;
        return this;
    }

    public EntityRequestBuilder entityKeys(final EntityKeyList entityKeys) {
        this.entityKeys = entityKeys;
        return this;
    }
}
