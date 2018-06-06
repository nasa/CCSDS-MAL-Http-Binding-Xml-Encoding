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

import org.ccsds.moims.mo.mal.structures.QoSLevel;

/**
 * This data structure defines a more complex composite.
 */
public final class ComplexComposite extends BaseComposite
{
  /**
   * Short form for type.
   */
  public static final Integer TYPE_SHORT_FORM = Integer.valueOf(3);
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
  public static final Long SHORT_FORM = Long.valueOf(27866027006099459L);
  private static final long serialVersionUID = 27866027006099459L;
  private BasicComposite fieldD;
  private BasicEnum fieldE;
  private QoSLevel fieldF;
  private org.ccsds.moims.mo.mal.structures.IntegerList fieldG;
  private BasicEnumList fieldH;
  private org.ccsds.moims.mo.mal.structures.EntityKey fieldI;
  /**
   * Default constructor for ComplexComposite.
   */
  public ComplexComposite()
  {
  }

  /**
   * Constructor that initialises the values of the structure.
   * @param fieldBA null.
   * @param fieldBB null.
   * @param fieldBC null.
   * @param fieldD null.
   * @param fieldE null.
   * @param fieldF null.
   * @param fieldG null.
   * @param fieldH null.
   * @param fieldI null.
   */
  public ComplexComposite(org.ccsds.moims.mo.mal.structures.URI fieldBA, Boolean fieldBB, Float fieldBC, BasicComposite fieldD, BasicEnum fieldE, QoSLevel fieldF, org.ccsds.moims.mo.mal.structures.IntegerList fieldG, BasicEnumList fieldH, org.ccsds.moims.mo.mal.structures.EntityKey fieldI)
  {
    super(fieldBA, fieldBB, fieldBC);
    this.fieldD = fieldD;
    this.fieldE = fieldE;
    this.fieldF = fieldF;
    this.fieldG = fieldG;
    this.fieldH = fieldH;
    this.fieldI = fieldI;
  }

  /**
   * Creates an instance of this type using the default constructor. It is a generic factory method.
   * @return A new instance of this type with default field values.
   */
  public org.ccsds.moims.mo.mal.structures.Element createElement()
  {
    return new ComplexComposite();
  }

  /**
   * Returns the field fieldD.
   * @return The field fieldD.
   */
  public BasicComposite getFieldD()
  {
    return fieldD;
  }

  /**
   * Sets the field fieldD.
   * @param __newValue __newValue The new value.
   */
  public void setFieldD(BasicComposite __newValue)
  {
    fieldD = __newValue;
  }

  /**
   * Returns the field fieldE.
   * @return The field fieldE.
   */
  public BasicEnum getFieldE()
  {
    return fieldE;
  }

  /**
   * Sets the field fieldE.
   * @param __newValue __newValue The new value.
   */
  public void setFieldE(BasicEnum __newValue)
  {
    fieldE = __newValue;
  }

  /**
   * Returns the field fieldF.
   * @return The field fieldF.
   */
  public QoSLevel getFieldF()
  {
    return fieldF;
  }

  /**
   * Sets the field fieldF.
   * @param __newValue __newValue The new value.
   */
  public void setFieldF(QoSLevel __newValue)
  {
    fieldF = __newValue;
  }

  /**
   * Returns the field fieldG.
   * @return The field fieldG.
   */
  public org.ccsds.moims.mo.mal.structures.IntegerList getFieldG()
  {
    return fieldG;
  }

  /**
   * Sets the field fieldG.
   * @param __newValue __newValue The new value.
   */
  public void setFieldG(org.ccsds.moims.mo.mal.structures.IntegerList __newValue)
  {
    fieldG = __newValue;
  }

  /**
   * Returns the field fieldH.
   * @return The field fieldH.
   */
  public BasicEnumList getFieldH()
  {
    return fieldH;
  }

  /**
   * Sets the field fieldH.
   * @param __newValue __newValue The new value.
   */
  public void setFieldH(BasicEnumList __newValue)
  {
    fieldH = __newValue;
  }

  /**
   * Returns the field fieldI.
   * @return The field fieldI.
   */
  public org.ccsds.moims.mo.mal.structures.EntityKey getFieldI()
  {
    return fieldI;
  }

  /**
   * Sets the field fieldI.
   * @param __newValue __newValue The new value.
   */
  public void setFieldI(org.ccsds.moims.mo.mal.structures.EntityKey __newValue)
  {
    fieldI = __newValue;
  }

  /**
   * Compares this object to the specified object. The result is true if and only if the argument is not null and is the same type that contains the same value as this object.
   * @param obj obj the object to compare with.
   * @return true if the objects are the same; false otherwise.
   */
  public boolean equals(Object obj)
  {
    if (obj instanceof ComplexComposite)
    {
      if (! super.equals(obj))
      {
        return false;
      }
      ComplexComposite other = (ComplexComposite) obj;
      if (fieldD == null)
      {
        if (other.fieldD != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldD.equals(other.fieldD))
        {
          return false;
        }
      }
      if (fieldE == null)
      {
        if (other.fieldE != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldE.equals(other.fieldE))
        {
          return false;
        }
      }
      if (fieldF == null)
      {
        if (other.fieldF != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldF.equals(other.fieldF))
        {
          return false;
        }
      }
      if (fieldG == null)
      {
        if (other.fieldG != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldG.equals(other.fieldG))
        {
          return false;
        }
      }
      if (fieldH == null)
      {
        if (other.fieldH != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldH.equals(other.fieldH))
        {
          return false;
        }
      }
      if (fieldI == null)
      {
        if (other.fieldI != null)
        {
          return false;
        }
      }
      else
      {
        if (! fieldI.equals(other.fieldI))
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
    int hash = super.hashCode();
    hash = 83 * hash + (fieldD != null ? fieldD.hashCode() : 0);
    hash = 83 * hash + (fieldE != null ? fieldE.hashCode() : 0);
    hash = 83 * hash + (fieldF != null ? fieldF.hashCode() : 0);
    hash = 83 * hash + (fieldG != null ? fieldG.hashCode() : 0);
    hash = 83 * hash + (fieldH != null ? fieldH.hashCode() : 0);
    hash = 83 * hash + (fieldI != null ? fieldI.hashCode() : 0);
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
    buf.append(super.toString());
    buf.append(", fieldD=");
    buf.append(fieldD);
    buf.append(", fieldE=");
    buf.append(fieldE);
    buf.append(", fieldF=");
    buf.append(fieldF);
    buf.append(", fieldG=");
    buf.append(fieldG);
    buf.append(", fieldH=");
    buf.append(fieldH);
    buf.append(", fieldI=");
    buf.append(fieldI);
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
    super.encode(encoder);
    encoder.encodeNullableElement(fieldD);
    encoder.encodeNullableElement(fieldE);
    encoder.encodeNullableElement(fieldF);
    encoder.encodeNullableElement(fieldG);
    encoder.encodeNullableElement(fieldH);
    encoder.encodeNullableElement(fieldI);
  }

  /**
   * Decodes the value of this object using the provided MALDecoder.
   * @param decoder decoder - the decoder to use for decoding.
   * @return Returns this object.
   * @throws org.ccsds.moims.mo.mal.MALException if any decoding errors are detected.
   */
  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException
  {
    super.decode(decoder);
    fieldD = (BasicComposite) decoder.decodeNullableElement(new BasicComposite());
    fieldE = (BasicEnum) decoder.decodeNullableElement(BasicEnum.FIRST);
    fieldF = (QoSLevel) decoder.decodeNullableElement(QoSLevel.ASSURED);
    fieldG = (org.ccsds.moims.mo.mal.structures.IntegerList) decoder.decodeNullableElement(new org.ccsds.moims.mo.mal.structures.IntegerList());
    fieldH = (BasicEnumList) decoder.decodeNullableElement(new BasicEnumList());
    fieldI = (org.ccsds.moims.mo.mal.structures.EntityKey) decoder.decodeNullableElement(new org.ccsds.moims.mo.mal.structures.EntityKey());
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
