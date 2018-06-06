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
 * List class for BasicComposite.
 */
public final class BasicCompositeList extends java.util.ArrayList<BasicComposite> implements org.ccsds.moims.mo.mal.structures.CompositeList<BasicComposite>
{
  /**
   * Short form for type.
   */
  public static final Integer TYPE_SHORT_FORM = Integer.valueOf(-2);
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
  public static final Long SHORT_FORM = Long.valueOf(27866027022876670L);
  private static final long serialVersionUID = 27866027022876670L;
  /**
   * Default constructor for BasicCompositeList.
   */
  public BasicCompositeList()
  {
  }

  /**
   * Constructor that initialises the capacity of the list.
   * @param initialCapacity initialCapacity the required initial capacity.
   */
  public BasicCompositeList(int initialCapacity)
  {
    super(initialCapacity);
  }

  /**
   * Creates an instance of this type using the default constructor. It is a generic factory method.
   * @return A new instance of this type with default field values.
   */
  public org.ccsds.moims.mo.mal.structures.Element createElement()
  {
    return new BasicCompositeList();
  }

  /**
   * Encodes the value of this object using the provided MALEncoder.
   * @param encoder encoder - the encoder to use for encoding.
   * @throws org.ccsds.moims.mo.mal.MALException if any encoding errors are detected.
   */
  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException
  {
    org.ccsds.moims.mo.mal.MALListEncoder listEncoder = encoder.createListEncoder(this);
    for (int i = 0; i < size(); i++)
    {
      listEncoder.encodeNullableElement((BasicComposite) get(i));
    }
    listEncoder.close();
  }

  /**
   * Decodes the value of this object using the provided MALDecoder.
   * @param decoder decoder - the decoder to use for decoding.
   * @return Returns this object.
   * @throws org.ccsds.moims.mo.mal.MALException if any decoding errors are detected.
   */
  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException
  {
    org.ccsds.moims.mo.mal.MALListDecoder listDecoder = decoder.createListDecoder(this);
    int decodedSize = listDecoder.size();
    if (decodedSize > 0)
    {
      ensureCapacity(decodedSize);
    }
    while (listDecoder.hasNext())
    {
      add((BasicComposite) listDecoder.decodeNullableElement(new BasicComposite()));
    }
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
