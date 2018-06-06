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

import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Subscription;

/**
 * @author wphyo
 *         Created on 8/9/17.
 */
public class SubscriptionBuilder {
    private Identifier subscriptionId;
    private EntityRequestList entities;

    private SubscriptionBuilder() {
        subscriptionId = null;
        entities = null;
    }

    public static SubscriptionBuilder create() {
        return new SubscriptionBuilder();
    }

    public Subscription build() {
        return new Subscription(subscriptionId, entities);
    }

    public SubscriptionBuilder subscriptionId(final Identifier subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public SubscriptionBuilder entities(final EntityRequestList entities) {
        this.entities = entities;
        return this;
    }


}
