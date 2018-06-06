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

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * A generic implementation of the message header interface.
 */
public class TestMessageHeader implements MALMessageHeader, Composite
{
  protected URI URIFrom;
  protected Blob authenticationId;
  protected URI URITo;
  protected Time timestamp;
  protected QoSLevel QoSlevel;
  protected UInteger priority;
  protected IdentifierList domain;
  protected Identifier networkZone;
  protected SessionType session;
  protected Identifier sessionName;
  protected InteractionType interactionType;
  protected UOctet interactionStage;
  protected Long transactionId;
  protected UShort serviceArea;
  protected UShort service;
  protected UShort operation;
  protected UOctet areaVersion;
  protected Boolean isErrorMessage;
  protected static final long serialVersionUID = 111111111111111L;

  /**
   * Constructor.
   */
  public TestMessageHeader()
  {
  }

  /**
   * Constructor.
   *
   * @param uriFrom URI of the message source
   * @param authenticationId Authentication identifier of the message
   * @param uriTo URI of the message destination
   * @param timestamp Timestamp of the message
   * @param qosLevel QoS level of the message
   * @param priority Priority of the message
   * @param domain Domain of the service provider
   * @param networkZone Network zone of the service provider
   * @param session Session of the service provider
   * @param sessionName Session name of the service provider
   * @param interactionType Interaction type of the operation
   * @param interactionStage Interaction stage of the interaction
   * @param transactionId Transaction identifier of the interaction, may be null.
   * @param serviceArea Area number of the service
   * @param service Service number
   * @param operation Operation number
   * @param serviceVersion Service version number
   * @param isErrorMessage Flag indicating if the message conveys an error
   */
  public TestMessageHeader(final URI uriFrom,
                           final Blob authenticationId,
                           final URI uriTo,
                           final Time timestamp,
                           final QoSLevel qosLevel,
                           final UInteger priority,
                           final IdentifierList domain,
                           final Identifier networkZone,
                           final SessionType session,
                           final Identifier sessionName,
                           final InteractionType interactionType,
                           final UOctet interactionStage,
                           final Long transactionId,
                           final UShort serviceArea,
                           final UShort service,
                           final UShort operation,
                           final UOctet serviceVersion,
                           final Boolean isErrorMessage)
  {
    this.URIFrom = uriFrom;
    this.authenticationId = authenticationId;
    this.URITo = uriTo;
    this.timestamp = timestamp;
    this.QoSlevel = qosLevel;
    this.priority = priority;
    this.domain = domain;
    this.networkZone = networkZone;
    this.session = session;
    this.sessionName = sessionName;
    this.interactionType = interactionType;
    this.interactionStage = interactionStage;
    this.transactionId = transactionId;
    this.serviceArea = serviceArea;
    this.service = service;
    this.operation = operation;
    this.areaVersion = serviceVersion;
    this.isErrorMessage = isErrorMessage;
  }

  @Override
  public URI getURIFrom()
  {
    return URIFrom;
  }

  @Override
  public void setURIFrom(final URI urIFrom)
  {
    this.URIFrom = urIFrom;
  }

  @Override
  public Blob getAuthenticationId()
  {
    return authenticationId;
  }

  @Override
  public void setAuthenticationId(final Blob authenticationId)
  {
    this.authenticationId = authenticationId;
  }

  @Override
  public IdentifierList getDomain()
  {
    return domain;
  }

  @Override
  public void setDomain(final IdentifierList domain)
  {
    this.domain = domain;
  }

  @Override
  public UOctet getInteractionStage()
  {
    return interactionStage;
  }

  @Override
  public void setInteractionStage(final UOctet interactionStage)
  {
    this.interactionStage = interactionStage;
  }

  @Override
  public InteractionType getInteractionType()
  {
    return interactionType;
  }

  @Override
  public void setInteractionType(final InteractionType interactionType)
  {
    this.interactionType = interactionType;
  }

  @Override
  public Boolean getIsErrorMessage()
  {
    return isErrorMessage;
  }

  @Override
  public void setIsErrorMessage(final Boolean isErrorMessage)
  {
    this.isErrorMessage = isErrorMessage;
  }

  @Override
  public Identifier getNetworkZone()
  {
    return networkZone;
  }

  @Override
  public void setNetworkZone(final Identifier networkZone)
  {
    this.networkZone = networkZone;
  }

  @Override
  public UShort getOperation()
  {
    return operation;
  }

  @Override
  public void setOperation(final UShort operation)
  {
    this.operation = operation;
  }

  @Override
  public UInteger getPriority()
  {
    return priority;
  }

  @Override
  public void setPriority(final UInteger priority)
  {
    this.priority = priority;
  }

  @Override
  public QoSLevel getQoSlevel()
  {
    return QoSlevel;
  }

  @Override
  public void setQoSlevel(final QoSLevel qoSLevel)
  {
    this.QoSlevel = qoSLevel;
  }

  @Override
  public UShort getService()
  {
    return service;
  }

  @Override
  public void setService(final UShort service)
  {
    this.service = service;
  }

  @Override
  public UShort getServiceArea()
  {
    return serviceArea;
  }

  @Override
  public void setServiceArea(final UShort serviceArea)
  {
    this.serviceArea = serviceArea;
  }

  @Override
  public UOctet getAreaVersion()
  {
    return areaVersion;
  }

  @Override
  public void setAreaVersion(final UOctet areaVersion)
  {
    this.areaVersion = areaVersion;
  }

  @Override
  public SessionType getSession()
  {
    return session;
  }

  @Override
  public void setSession(final SessionType session)
  {
    this.session = session;
  }

  @Override
  public Identifier getSessionName()
  {
    return sessionName;
  }

  @Override
  public void setSessionName(final Identifier sessionName)
  {
    this.sessionName = sessionName;
  }

  @Override
  public Time getTimestamp()
  {
    return timestamp;
  }

  @Override
  public void setTimestamp(final Time timestamp)
  {
    this.timestamp = timestamp;
  }

  @Override
  public Long getTransactionId()
  {
    return transactionId;
  }

  @Override
  public void setTransactionId(final Long transactionId)
  {
    this.transactionId = transactionId;
  }

  @Override
  public URI getURITo()
  {
    return URITo;
  }

  @Override
  public void setURITo(final URI urITo)
  {
    this.URITo = urITo;
  }

  @Override
  public Element createElement()
  {
    return new TestMessageHeader();
  }

  @Override
  public void encode(final MALEncoder encoder) throws MALException
  {
    encoder.encodeNullableURI(URIFrom);
    encoder.encodeNullableBlob(authenticationId);
    encoder.encodeNullableURI(URITo);
    encoder.encodeNullableTime(timestamp);
    encoder.encodeNullableElement(QoSlevel);
    encoder.encodeNullableUInteger(priority);
    encoder.encodeNullableElement(domain);
    encoder.encodeNullableIdentifier(networkZone);
    encoder.encodeNullableElement(session);
    encoder.encodeNullableIdentifier(sessionName);
    encoder.encodeNullableElement(interactionType);
    encoder.encodeNullableUOctet(interactionStage);
    encoder.encodeNullableLong(transactionId);
    encoder.encodeNullableUShort(serviceArea);
    encoder.encodeNullableUShort(service);
    encoder.encodeNullableUShort(operation);
    encoder.encodeNullableUOctet(areaVersion);
    encoder.encodeNullableBoolean(isErrorMessage);
  }

  @Override
  public Element decode(final MALDecoder decoder) throws MALException
  {
    URIFrom = decoder.decodeNullableURI();
    authenticationId = decoder.decodeNullableBlob();
    URITo = decoder.decodeNullableURI();
    timestamp = decoder.decodeNullableTime();
    QoSlevel = (QoSLevel) decoder.decodeNullableElement(QoSLevel.BESTEFFORT);
    priority = decoder.decodeNullableUInteger();
    domain = (IdentifierList) decoder.decodeNullableElement(new IdentifierList());
    networkZone = decoder.decodeNullableIdentifier();
    session = (SessionType) decoder.decodeNullableElement(SessionType.LIVE);
    sessionName = decoder.decodeNullableIdentifier();
    interactionType = (InteractionType) decoder.decodeNullableElement(InteractionType.SEND);
    interactionStage = decoder.decodeNullableUOctet();
    transactionId = decoder.decodeNullableLong();
    serviceArea = decoder.decodeNullableUShort();
    service = decoder.decodeNullableUShort();
    operation = decoder.decodeNullableUShort();
    areaVersion = decoder.decodeNullableUOctet();
    isErrorMessage = decoder.decodeNullableBoolean();

    return this;
  }

  @Override
  public UShort getAreaNumber()
  {
    return new UShort(0);
  }

  @Override
  public UShort getServiceNumber()
  {
    return new UShort(0);
  }

  @Override
  public Long getShortForm()
  {
    return 0L;
  }

  @Override
  public Integer getTypeShortForm()
  {
    return 0;
  }

  @Override
  public String toString()
  {
    final StringBuilder str = new StringBuilder("GENMessageHeader{");
    str.append("URIFrom=");
    str.append(URIFrom);
    str.append(", authenticationId=");
    str.append(authenticationId);
    str.append(", URITo=");
    str.append(URITo);
    str.append(", timestamp=");
    str.append(timestamp);
    str.append(", QoSlevel=");
    str.append(QoSlevel);
    str.append(", priority=");
    str.append(priority);
    str.append(", domain=");
    str.append(domain);
    str.append(", networkZone=");
    str.append(networkZone);
    str.append(", session=");
    str.append(session);
    str.append(", sessionName=");
    str.append(sessionName);
    str.append(", interactionType=");
    str.append(interactionType);
    str.append(", interactionStage=");
    str.append(interactionStage);
    str.append(", transactionId=");
    str.append(transactionId);
    str.append(", serviceArea=");
    str.append(serviceArea);
    str.append(", service=");
    str.append(service);
    str.append(", operation=");
    str.append(operation);
    str.append(", serviceVersion=");
    str.append(areaVersion);
    str.append(", isErrorMessage=");
    str.append(isErrorMessage);
    str.append('}');

    return str.toString();
  }
}
