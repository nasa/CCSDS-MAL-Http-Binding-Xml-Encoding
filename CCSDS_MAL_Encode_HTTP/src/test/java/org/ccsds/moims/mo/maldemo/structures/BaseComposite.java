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
 * This data structure defines a base abstract composite.
 */
public abstract class BaseComposite implements org.ccsds.moims.mo.mal.structures.Composite
{
  private org.ccsds.moims.mo.mal.structures.URI fieldBA;
  private Boolean fieldBB;
  private Float fieldBC;
  /**
   * Default constructor for BaseComposite.
   */
  public BaseComposite()
  {
  }

  /**
   * Constructor that initialises the values of the structure.
   * @param fieldBA null.
   * @param fieldBB null.
   * @param fieldBC null.
   */
  public BaseComposite(org.ccsds.moims.mo.mal.structures.URI fieldBA, Boolean fieldBB, Float fieldBC)
  {
    this.fieldBA = fieldBA;
    this.fieldBB = fieldBB;
    this.fieldBC = fieldBC;
  }

  /**
   * Returns the field fieldBA.
   * @return The field fieldBA.
   */
  public org.ccsds.moims.mo.mal.structures.URI getFieldBA()
  {
    return fieldBA;
  }

  /**
   * Sets the field fieldBA.
   * @param __newValue __newValue The new value.
   */
  public void setFieldBA(org.ccsds.moims.mo.mal.structures.URI __newValue)
  {
    fieldBA = __newValue;
  }

  /**
   * Returns the field fieldBB.
   * @return The field fieldBB.
   */
  public Boolean getFieldBB()
  {
    return fieldBB;
  }

  /**
   * Sets the field fieldBB.
   * @param __newValue __newValue The new value.
   */
  public void setFieldBB(Boolean __newValue)
  {
    fieldBB = __newValue;
  }

  /**
   * Returns the field fieldBC.
   * @return The field fieldBC.
   */
  public Float getFieldBC()
  {
    return fieldBC;
  }

  /**
   * Sets the field fieldBC.
   * @param __newValue __newValue The new value.
   */
  public void setFieldBC(Float __newValue)
  {
    fieldBC = __newValue;
  }

  /**
   * Compares this object to the specified object. The result is true if and only if the argument is not null and is the same type that contains the same value as this object.
   * @param obj obj the object to compare with.
   * @return true if the objects are the same; false otherwise.
   */
  public boolean equals(Object obj)
  {
    if (obj instanceof BaseComposite)
    {
      BaseComposite other = (BaseComposite) obj;
      if (fieldBA == null)
      {
        if (other.fieldBA != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldBA.equals(other.fieldBA))
        {
          return false;
        }
      }
      if (fieldBB == null)
      {
        if (other.fieldBB != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldBB.equals(other.fieldBB))
        {
          return false;
        }
      }
      if (fieldBC == null)
      {
        if (other.fieldBC != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldBC.equals(other.fieldBC))
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
    hash = 83 * hash + (fieldBA != null ? fieldBA.hashCode() : 0);
    hash = 83 * hash + (fieldBB != null ? fieldBB.hashCode() : 0);
    hash = 83 * hash + (fieldBC != null ? fieldBC.hashCode() : 0);
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
    buf.append("fieldBA=");
    buf.append(fieldBA);
    buf.append(", fieldBB=");
    buf.append(fieldBB);
    buf.append(", fieldBC=");
    buf.append(fieldBC);
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
    encoder.encodeNullableURI(fieldBA);
    encoder.encodeNullableBoolean(fieldBB);
    encoder.encodeNullableFloat(fieldBC);
  }

  /**
   * Decodes the value of this object using the provided MALDecoder.
   * @param decoder decoder - the decoder to use for decoding.
   * @return Returns this object.
   * @throws org.ccsds.moims.mo.mal.MALException if any decoding errors are detected.
   */
  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException
  {
    fieldBA = decoder.decodeNullableURI();
    fieldBB = decoder.decodeNullableBoolean();
    fieldBC = decoder.decodeNullableFloat();
    return this;
  }

}
