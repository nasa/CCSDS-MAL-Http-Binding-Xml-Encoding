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
public class EntityKeyBuilder {
    private Identifier firstSubKey;
    private Long secondSubKey;
    private Long thirdSubKey;
    private Long fourthSubKey;

    private EntityKeyBuilder() {
    }

    public static EntityKeyBuilder create() {
        return new EntityKeyBuilder();
    }

    public EntityKey build() {
        return new EntityKey(firstSubKey, secondSubKey, thirdSubKey, fourthSubKey);
    }

    public EntityKeyBuilder firstSubKey(final Identifier firstSubKey) {
        this.firstSubKey = firstSubKey;
        return this;
    }

    public EntityKeyBuilder secondSubKey(final Long secondSubKey) {
        this.secondSubKey = secondSubKey;
        return this;
    }

    public EntityKeyBuilder thirdSubKey(final Long thirdSubKey) {
        this.thirdSubKey = thirdSubKey;
        return this;
    }

    public EntityKeyBuilder fourthSubKey(final Long fourthSubKey) {
        this.fourthSubKey = fourthSubKey;
        return this;
    }
}
