/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package org.ccsds.moims.mo.maldemo.structures;

/**
 * Enumeration class for BasicEnum.
 */
public final class BasicEnum extends org.ccsds.moims.mo.mal.structures.Enumeration
{
  /**
   * Short form for type.
   */
  public static final Integer TYPE_SHORT_FORM = Integer.valueOf(4);
  /**
   * Short form for area.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort AREA_SHORT_FORM = new org.ccsds.moims.mo.mal.structures.UShort(99);
  /**
   * Version for area.
   */
  public static final org.ccsds.moims.mo.mal.structures.UOctet AREA_VERSION = new org.ccsds.moims.mo.mal.structures.UOctet((short)1);
  /**
   * Short form for service.
   */
  public static final org.ccsds.moims.mo.mal.structures.UShort SERVICE_SHORT_FORM = new org.ccsds.moims.mo.mal.structures.UShort(1);
  /**
   * Absolute short form for type.
   */
  public static final Long SHORT_FORM = Long.valueOf(27866027006099460L);
  private static final long serialVersionUID = 27866027006099460L;
  /**
   * Enumeration ordinal index for value FIRST.
   */
  public static final int _FIRST_INDEX = 0;
  /**
   * Enumeration numeric value for value FIRST.
   */
  public static final org.ccsds.moims.mo.mal.structures.UInteger FIRST_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(1);
  /**
   * Enumeration singleton for value FIRST.
   */
  public static final BasicEnum FIRST = new BasicEnum(BasicEnum._FIRST_INDEX);
  /**
   * Enumeration ordinal index for value SECOND.
   */
  public static final int _SECOND_INDEX = 1;
  /**
   * Enumeration numeric value for value SECOND.
   */
  public static final org.ccsds.moims.mo.mal.structures.UInteger SECOND_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(2);
  /**
   * Enumeration singleton for value SECOND.
   */
  public static final BasicEnum SECOND = new BasicEnum(BasicEnum._SECOND_INDEX);
  /**
   * Enumeration ordinal index for value THIRD.
   */
  public static final int _THIRD_INDEX = 2;
  /**
   * Enumeration numeric value for value THIRD.
   */
  public static final org.ccsds.moims.mo.mal.structures.UInteger THIRD_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(3);
  /**
   * Enumeration singleton for value THIRD.
   */
  public static final BasicEnum THIRD = new BasicEnum(BasicEnum._THIRD_INDEX);
  /**
   * Enumeration ordinal index for value FOURTH.
   */
  public static final int _FOURTH_INDEX = 3;
  /**
   * Enumeration numeric value for value FOURTH.
   */
  public static final org.ccsds.moims.mo.mal.structures.UInteger FOURTH_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(4);
  /**
   * Enumeration singleton for value FOURTH.
   */
  public static final BasicEnum FOURTH = new BasicEnum(BasicEnum._FOURTH_INDEX);
  /**
   * Set of enumeration instances.
   */
  private static final BasicEnum[] _ENUMERATIONS = {FIRST, SECOND, THIRD, FOURTH};
  /**
   * Set of enumeration string values.
   */
  private static final String[] _ENUMERATION_NAMES = {"FIRST", "SECOND", "THIRD", "FOURTH"};
  /**
   * Set of enumeration values.
   */
  private static final org.ccsds.moims.mo.mal.structures.UInteger[] _ENUMERATION_NUMERIC_VALUES = {FIRST_NUM_VALUE, SECOND_NUM_VALUE, THIRD_NUM_VALUE, FOURTH_NUM_VALUE};
  /**
   * @param ordinal null.
   */
  private BasicEnum(int ordinal)
  {
    super(ordinal);
  }

  /**
   * Returns a String object representing this type's value.
   * @return a string representation of the value of this object.
   */
  public String toString()
  {
    switch (getOrdinal())
    {
      case _FIRST_INDEX:
        return "FIRST";
      case _SECOND_INDEX:
        return "SECOND";
      case _THIRD_INDEX:
        return "THIRD";
      case _FOURTH_INDEX:
        return "FOURTH";
      default:
        throw new RuntimeException("Unknown ordinal!");
    }
  }

  /**
   * Returns the enumeration element represented by the supplied string, or null if not matched.
   * @param s s The string to search for.
   * @return The matched enumeration element, or null if not matched.
   */
  public static BasicEnum fromString(String s)
  {
    for (int i = 0; i < _ENUMERATION_NAMES.length; i++)
    {
      if (_ENUMERATION_NAMES[i].equals(s))
      {
        return _ENUMERATIONS[i];
      }
    }
    return null;
  }

  /**
   * Returns the nth element of the enumeration.
   * @param ordinal ordinal The index of the enumeration element to return.
   * @return The matched enumeration element.
   */
  public static BasicEnum fromOrdinal(int ordinal)
  {
    return _ENUMERATIONS[ordinal];
  }

  /**
   * Returns the enumeration element represented by the supplied numeric value, or null if not matched.
   * @param value value The numeric value to search for.
   * @return The matched enumeration value, or null if not matched.
   */
  public static BasicEnum fromNumericValue(org.ccsds.moims.mo.mal.structures.UInteger value)
  {
    for (int i = 0; i < _ENUMERATION_NUMERIC_VALUES.length; i++)
    {
      if (_ENUMERATION_NUMERIC_VALUES[i].equals(value))
      {
        return _ENUMERATIONS[i];
      }
    }
    return null;
  }

  /**
   * Returns the numeric value of the enumeration element.
   * @return The numeric value.
   */
  public org.ccsds.moims.mo.mal.structures.UInteger getNumericValue()
  {
    return _ENUMERATION_NUMERIC_VALUES[ordinal];
  }

  /**
   * Returns an instance of this type using the first element of the enumeration. It is a generic factory method but just returns an existing element of the enumeration as new values of enumerations cannot be created at runtime.
   * @return The first element of the enumeration.
   */
  public org.ccsds.moims.mo.mal.structures.Element createElement()
  {
    return _ENUMERATIONS[0];
  }

  /**
   * Encodes the value of this object using the provided MALEncoder.
   * @param encoder encoder - the encoder to use for encoding.
   * @throws org.ccsds.moims.mo.mal.MALException if any encoding errors are detected.
   */
  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException
  {
    encoder.encodeUOctet(new org.ccsds.moims.mo.mal.structures.UOctet(ordinal.shortValue()));
  }

  /**
   * Decodes the value of this object using the provided MALDecoder.
   * @param decoder decoder - the decoder to use for decoding.
   * @return Returns this object.
   * @throws org.ccsds.moims.mo.mal.MALException if any decoding errors are detected.
   */
  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException
  {
    return fromOrdinal(decoder.decodeUOctet().getValue());
  }

  /**
   * Returns the absolute short form of this type.
   * @return The absolute short form of this type.
   */
  public Long getShortForm()
  {
    return SHORT_FORM;
  }

  /**
   * Returns the type short form of this type which is unique to the area/service it is defined in but not unique across all types.
   * @return The type short form of this type.
   */
  public Integer getTypeShortForm()
  {
    return TYPE_SHORT_FORM;
  }

  /**
   * Returns the area number of this type.
   * @return The area number of this type.
   */
  public org.ccsds.moims.mo.mal.structures.UShort getAreaNumber()
  {
    return AREA_SHORT_FORM;
  }

  /**
   * Returns the area version of this type.
   * @return The area number of this type.
   */
  public org.ccsds.moims.mo.mal.structures.UOctet getAreaVersion()
  {
    return AREA_VERSION;
  }

  /**
   * Returns the service number of this type.
   * @return The service number of this type.
   */
  public org.ccsds.moims.mo.mal.structures.UShort getServiceNumber()
  {
    return SERVICE_SHORT_FORM;
  }

}
