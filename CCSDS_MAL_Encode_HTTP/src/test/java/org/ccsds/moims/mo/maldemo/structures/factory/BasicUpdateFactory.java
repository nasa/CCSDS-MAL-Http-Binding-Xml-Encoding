/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package org.ccsds.moims.mo.maldemo.structures.factory;

import org.ccsds.moims.mo.maldemo.structures.BasicUpdate;

/**
 * Factory class for BasicUpdate.
 */
public final class BasicUpdateFactory implements org.ccsds.moims.mo.mal.MALElementFactory
{
  /**
   * Creates an instance of the source type using the default constructor. It is a generic factory method.
   * @return A new instance of the source type with default field values.
   */
  public org.ccsds.moims.mo.mal.structures.Element createElement()
  {
    return new BasicUpdate();
  }

}
