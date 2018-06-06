/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.encoder.util;

import org.ccsds.moims.mo.mal.MALException;

import java.util.Objects;

/**
 * @author Wai Phyo
 *         Created on 6/9/17.
 * Functional Interface to add individual elements of the list to the xml document.
 * This is a replica to BiConsumer Interface.
 * BiConsumer is not used because it does not handle MALException
 *
 * @param <T> name of the current MAL element which will become name of parent xml element
 * @param <U> current MAL element
 */
@FunctionalInterface
public interface BiConsumerWithMALException<T, U> {
    /**
     *
     * @param elementName name of the current MAL element which will become name of parent xml element
     * @param value current MAL element
     * @throws MALException null or other possible exceptions
     */
    void accept(T elementName, U value) throws MALException;
}
