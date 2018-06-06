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
 * This data structure defines a basic composite.
 */
public final class BasicComposite implements org.ccsds.moims.mo.mal.structures.Composite
{
  /**
   * Short form for type.
   */
  public static final Integer TYPE_SHORT_FORM = Integer.valueOf(2);
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
  public static final Long SHORT_FORM = Long.valueOf(27866027006099458L);
  private static final long serialVersionUID = 27866027006099458L;
  private Short fieldA;
  private String fieldB;
  private Boolean fieldC;
  /**
   * Default constructor for BasicComposite.
   */
  public BasicComposite()
  {
  }

  /**
   * Constructor that initialises the values of the structure.
   * @param fieldA null.
   * @param fieldB null.
   * @param fieldC null.
   */
  public BasicComposite(Short fieldA, String fieldB, Boolean fieldC)
  {
    this.fieldA = fieldA;
    this.fieldB = fieldB;
    this.fieldC = fieldC;
  }

  /**
   * Creates an instance of this type using the default constructor. It is a generic factory method.
   * @return A new instance of this type with default field values.
   */
  public org.ccsds.moims.mo.mal.structures.Element createElement()
  {
    return new BasicComposite();
  }

  /**
   * Returns the field fieldA.
   * @return The field fieldA.
   */
  public Short getFieldA()
  {
    return fieldA;
  }

  /**
   * Sets the field fieldA.
   * @param __newValue __newValue The new value.
   */
  public void setFieldA(Short __newValue)
  {
    fieldA = __newValue;
  }

  /**
   * Returns the field fieldB.
   * @return The field fieldB.
   */
  public String getFieldB()
  {
    return fieldB;
  }

  /**
   * Sets the field fieldB.
   * @param __newValue __newValue The new value.
   */
  public void setFieldB(String __newValue)
  {
    fieldB = __newValue;
  }

  /**
   * Returns the field fieldC.
   * @return The field fieldC.
   */
  public Boolean getFieldC()
  {
    return fieldC;
  }

  /**
   * Sets the field fieldC.
   * @param __newValue __newValue The new value.
   */
  public void setFieldC(Boolean __newValue)
  {
    fieldC = __newValue;
  }

  /**
   * Compares this object to the specified object. The result is true if and only if the argument is not null and is the same type that contains the same value as this object.
   * @param obj obj the object to compare with.
   * @return true if the objects are the same; false otherwise.
   */
  public boolean equals(Object obj)
  {
    if (obj instanceof BasicComposite)
    {
      BasicComposite other = (BasicComposite) obj;
      if (fieldA == null)
      {
        if (other.fieldA != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldA.equals(other.fieldA))
        {
          return false;
        }
      }
      if (fieldB == null)
      {
        if (other.fieldB != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldB.equals(other.fieldB))
        {
          return false;
        }
      }
      if (fieldC == null)
      {
        if (other.fieldC != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldC.equals(other.fieldC))
        {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Returns a hash code for this object.
   * @return a hash code value for this object.
   */
  public int hashCode()
  {
    int hash = 7;
    hash = 83 * hash + (fieldA != null ? fieldA.hashCode() : 0);
    hash = 83 * hash + (fieldB != null ? fieldB.hashCode() : 0);
    hash = 83 * hash + (fieldC != null ? fieldC.hashCode() : 0);
    return hash;
  }

  /**
   * Returns a String object representing this type's value.
   * @return a string representation of the value of this object.
   */
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append('(');
    buf.append("fieldA=");
    buf.append(fieldA);
    buf.append(", fieldB=");
    buf.append(fieldB);
    buf.append(", fieldC=");
    buf.append(fieldC);
    buf.append(')');
    return buf.toString();
  }

  /**
   * Encodes the value of this object using the provided MALEncoder.
   * @param encoder encoder - the encoder to use for encoding.
   * @throws org.ccsds.moims.mo.mal.MALException if any encoding errors are detected.
   */
  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException
  {
    encoder.encodeNullableShort(fieldA);
    encoder.encodeNullableString(fieldB);
    encoder.encodeNullableBoolean(fieldC);
  }

  /**
   * Decodes the value of this object using the provided MALDecoder.
   * @param decoder decoder - the decoder to use for decoding.
   * @return Returns this object.
   * @throws org.ccsds.moims.mo.mal.MALException if any decoding errors are detected.
   */
  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException
  {
    fieldA = decoder.decodeNullableShort();
    fieldB = decoder.decodeNullableString();
    fieldC = decoder.decodeNullableBoolean();
    return this;
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
