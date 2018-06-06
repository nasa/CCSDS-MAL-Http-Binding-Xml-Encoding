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

/**
 * @author Wai Phyo
 *         Created on 6/9/17.
 * This is a replica of Functional Interface Consumer
 * For this project, almost all methods are throwing MALExceptions
 * Hence, this is recreated with a thrown exception
 */
@FunctionalInterface
public interface ConsumerWithMALException<T> {
    /**
     * A method which will execute a method with one parameter and return nothing
     * @param object any object but mostly MAL elements
     * @throws MALException any possible MAL Exception
     */
    void accept(T object) throws MALException;
}
