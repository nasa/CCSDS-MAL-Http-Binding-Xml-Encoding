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
 * This is a replica of Functional Interface Function
 * This will execute a method that has 1 parameter and return something.
 * Default Function Interface cannot be used as it does not throw exception
 * @param <T> parameter object type
 * @param <R> return type
 */
@FunctionalInterface
public interface FunctionWithMALException<T, R> {
    /**
     * Any function that has 1 parameter and return something.
     * @param parameter any parameter
     * @return any object
     * @throws MALException any possible MAL Exception
     */
    R apply(T parameter) throws MALException;
}
