/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.encoder.Header;

import com.sun.istack.internal.Nullable;
import nasa.mo.mal.encoder.HttpBodyContentType;
import nasa.mo.mal.encoder.util.HeaderMappingHelper;
import nasa.mo.mal.encoder.util.ObjectFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import java.net.*;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Wai Phyo
 *         Created on 4/3/17.
 */
public class MalToHttpMapper implements HttpHeaderMapper {

    private static final String BAD_URI_ERR = "malformed URI";
    private static final String IP_PORT_SEPARATOR = ":";
    /**
     * As specified in WhiteBook, the value is hardcoded to 1.
     * From WhiteBook,
     * The version number ‘1’ shall be added to the SANA registry ‘MAL HTTP Binding Version Number’
     * and shall refer to the Mission Operations HTTP Transport and XML Encoding document ‘CCSDS 000.0-W-0’.
     */
    private static final String MAL_VERSION_NUMBER = "1";
    private static final String EMPTY_PORT = "-1";
    private static final String SCHEME_HOST_SEPARATOR = "://";

    private static class SingletonHolder {
        private static final MalToHttpMapper INSTANCE = new MalToHttpMapper();
    }
    public static MalToHttpMapper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private MalToHttpMapper() {

    }
    /**
     * Encoding URL From to Http Header format.
     * Whitebook [CCSDS 524.3-W-1] suggests encoding needs to follow rules on RFC3986
     *
     * Encoding used Spring's encoding method which follows the same RFC3986.
     *
     * http://docs.spring.io/spring/docs/4.3.x/javadoc-api/org/springframework/web/util/UriUtils.html#UriUtils()
     * From Spring Manual Page:
     * Utility class for URI encoding and decoding based on RFC 3986. Offers encoding methods for the various URI components.
     *
     * NOTE: encoding is hard coded to UTF-8.
     *
     * @param uriFrom Source URI
     * @throws MALException IO Exception
     */
    private String mapUrlFrom(URI uriFrom) throws MALException {
        HeaderMappingHelper.checkForNull(uriFrom);
        HeaderMappingHelper.checkForNull(uriFrom.getValue());
        return HeaderMappingHelper.encodeURI(uriFrom.getValue());
    }

    /**
     * Encoding Authentication ID.
     * Whitebook [CCSDS 524.3-W-1] suggests bytes to be converted to HEX String.
     *
     * @param authenticationId Authentication ID in blob form.
     * @throws MALException No Exception
     */
    private String mapAuthID(Blob authenticationId) throws MALException {
        HeaderMappingHelper.checkForNull(authenticationId);
        HeaderMappingHelper.checkForNull(authenticationId.getValue());
        return HeaderMappingHelper.convertByteArrayToHexString(authenticationId.getValue());

    }

    /**
     * This helper function will split URL into domain and paths.
     * 1. split url with '/' and limit to 2. Hence, [domain, path].
     * 2. validation for malformed url
     * 3. create new uniformed array with length 2.
     * 4. update values based on whitebook specification.
     * TODO need thorough testing
     * @param uri incoming URL
     * @return String array with 2 cells. [Domain, path]
     * @throws MALException malformed uri
     */
    private String[] uriPathSplitter(final String uri) throws MALException {
        String[] splitURI = new String[2];
        try {
            java.net.URI javaURI = new java.net.URI(uri);
            StringBuilder uriFirstPart = ObjectFactory.createStringBuilder();
            uriFirstPart.append(javaURI.getScheme())
                    .append(SCHEME_HOST_SEPARATOR)
                    .append(javaURI.getHost());
            if (!javaURI.getPath().equals(EMPTY_PORT)) {
                uriFirstPart.append(IP_PORT_SEPARATOR).append(javaURI.getPort());
            }
            splitURI[0] = uriFirstPart.toString();
            splitURI[1] = javaURI.getPath();
        } catch (URISyntaxException exp) {
            throw new MALException("URISyntaxException in splitting URI " + exp.toString());
        }
        return splitURI;
    }

    /**
     * Encoding URL-To to several fields.
     *
     * 1. Null validations
     * 2. URL-TO = Http endpoint
     * 2.1. IP & Port from URL-TO mapped to Host
     * 2.2. Source ID mapped to request-target.
     *
     * 3. URL-TO != Http endpoint
     * 3.1. URL-To mapped to X-MAL-URL-TO
     * 3.2 IP & Host from Http endpoint mapped to host
     * —
     * NOTE: encoding is hard coded to UTF-8.
     *
     * @param uriTo Target URL
     * @param httpEndPoint Http Endpoint URL
     * @throws MALException malformed uris
     */
    public void mapUrlTo(URI uriTo, URI httpEndPoint, StringBuffer host, StringBuffer requestTarget, StringBuffer malUriTo) throws MALException {
        HeaderMappingHelper.checkForNull(uriTo);
        HeaderMappingHelper.checkForNull(uriTo.getValue());
        HeaderMappingHelper.checkForNull(httpEndPoint);
        HeaderMappingHelper.checkForNull(httpEndPoint.getValue());

        if (uriTo.getValue().equals(httpEndPoint.getValue())) {
            String[] splitURI = uriPathSplitter(httpEndPoint.getValue());
            if (splitURI.length != 2) {
                throw new MALException(BAD_URI_ERR);
            }
            host.append(HeaderMappingHelper.encodeURI(splitURI[0]));
            requestTarget.append(HeaderMappingHelper.encodeURI(splitURI[1].length() == 0 ? "/" : splitURI[1]));
        } else {
            malUriTo.append(HeaderMappingHelper.encodeURI(uriTo.getValue()));
            String[] splitURI = uriPathSplitter(httpEndPoint.getValue());
            if (splitURI.length != 2) {
                throw new MALException(BAD_URI_ERR);
            }
            host.append(HeaderMappingHelper.encodeURI(splitURI[0]));
        }

    }

    /**
     * Mapping Timestamp based on WhiteBook.
     * Encoding details in the encoding method.
     * Based on BlueBook, Time is accurate up to millisecond.
     *
     * @param timestamp from MAL Message
     * @throws MALException no exceptions
     */
    private String mapTimestamp(Time timestamp) throws MALException {
        HeaderMappingHelper.checkForNull(timestamp);
        HeaderMappingHelper.checkForNull(timestamp.getValue());
        return HeaderMappingHelper.encodeTime(timestamp.getValue());
    }

    /**
     * Mapping QOS Level.
     * Specifications from WhiteBook is the same as overridden toString() method from QoSLevel
     *
     * @param qoSLevel from MAL message
     * @throws MALException no exception
     */
    private String mapQosLevel(QoSLevel qoSLevel) throws MALException {
        HeaderMappingHelper.checkForNull(qoSLevel);
        return qoSLevel.toString();
    }

    /**
     * Mapping priority level.
     *
     * UInteger has long value.
     * Getting long value, and converting to String.
     *
     * @param priority priority level from MAL Message
     * @throws MALException no exceptions
     */
    private String mapPriority(UInteger priority) throws MALException {
        HeaderMappingHelper.checkForNull(priority);
        HeaderMappingHelper.checkForNull(priority.getValue());
        return Long.toString(priority.getValue());
    }

    /**
     * Loop each Identifier
     *
     * @param domain Domain from MAL Message Header
     * @return encoded String
     * @throws MALException based on identifier encoder
     */
    private String mapDomain(IdentifierList domain) throws MALException {
        StringBuilder result = ObjectFactory.createStringBuilder();
        Iterator iterator = domain.iterator();
        if (domain.isEmpty()) {
            return HeaderMappingHelper.EMPTY_STRING;
        }
        result.append(encodedIdentifier((Identifier) iterator.next()));
        while (iterator.hasNext()) {
            result.append(HeaderMappingHelper.DOT_SEPARATOR).append(encodedIdentifier((Identifier) iterator.next()));
        }
        return result.toString();
    }

    /**
     * TODO: clarify MIME encoding logic
     * @param identifier Idenfier Structure from MAL
     * @return encoded String
     */
    private String encodedIdentifier(Identifier identifier) throws MALException {
        return HeaderMappingHelper.encodeString(identifier.getValue());
    }

    /**
     * Mapping NetworkZone
     * @param networkZone NetWork Zone from MAL Message Header
     * @return encoded String
     * @throws MALException based on identifier encoder
     */
    private String mapNetworkZone(Identifier networkZone) throws MALException {
        return encodedIdentifier(networkZone);
    }

    /**
     * Mapping Session Type
     * Specifications from WhiteBook is the same as overridden toString() method from SessionType
     *
     * @param session session type from MAL message
     * @throws MALException no exception
     */
    private String mapSession(SessionType session) throws MALException {
        HeaderMappingHelper.checkForNull(session);
        return session.toString();
    }

    /**
     * Mapping Session Name
     * @param sessionName Session Name from MAL Header
     * @return encoded String
     * @throws MALException based on Identifier encoder
     */
    private String mapSessionName(Identifier sessionName) throws MALException {
        return encodedIdentifier(sessionName);
    }

    /**
     * Mapping Interaction type
     * Specifications from WhiteBook is the same as overridden toString() method from Interaction Type
     *
     * @param interactionType from MAL message
     * @throws MALException no exception
     */
    private String mapInteractionType(InteractionType interactionType) throws MALException {
        HeaderMappingHelper.checkForNull(interactionType);
        return interactionType.toString();
    }

    /**
     * Mapping Interaction Stage
     *
     * @param interactionStage from MAL message
     * @throws MALException no exception
     */
    private String mapInteractionStage(UOctet interactionStage) throws MALException {
        if (interactionStage == null) {
            return "";
        }
        return Short.toString(interactionStage.getValue());
    }

    /**
     * Mapping Transaction ID
     *
     * @param transactionId Transaction ID from message header
     * @throws MALException no exception
     */
    private String mapTransactionId(Long transactionId) throws MALException {
        HeaderMappingHelper.checkForNull(transactionId);
        return Long.toString(transactionId);
    }

    /**
     * Mapping Service Area ID
     *
     * @param serviceArea Service Area ID from MAL message
     * @throws MALException no exception
     */
    private String mapServiceArea(UShort serviceArea) throws MALException {
        HeaderMappingHelper.checkForNull(serviceArea);
        HeaderMappingHelper.checkForNull(serviceArea.getValue());
        return Integer.toString(serviceArea.getValue());
    }

    /**
     * Mapping Service level
     *
     * @param service service level from MAL message
     * @throws MALException no exception
     */
    private String mapService(UShort service) throws MALException {
        HeaderMappingHelper.checkForNull(service);
        HeaderMappingHelper.checkForNull(service.getValue());
        return Integer.toString(service.getValue());
    }

    /**
     * Mapping operation ID.
     * UShort has an Integer value inside.
     * Converting Integer to String.
     *
     * @param op operation id from MAL message
     * @throws MALException no exception
     */
    private String mapOperation(UShort op) throws MALException {
        HeaderMappingHelper.checkForNull(op);
        HeaderMappingHelper.checkForNull(op.getValue());
        return Integer.toString(op.getValue());
    }

    /**
     * Mapping area version.
     * UOctet has short value.
     * Converting Short to String
     *
     * @param areaVersion from MAL message
     * @throws MALException no exception
     */
    private String mapAreaVersion(UOctet areaVersion) throws MALException {
        HeaderMappingHelper.checkForNull(areaVersion);
        HeaderMappingHelper.checkForNull(areaVersion.getValue());
        return Short.toString(areaVersion.getValue());
    }

    /**
     * mapping IsError flag
     *
     * @param isErrorMessage flag from MAL message
     * @return Encoded String
     * @throws MALException no exception
     */
    private String mapIsErrorMsg(Boolean isErrorMessage) throws MALException {
        HeaderMappingHelper.checkForNull(isErrorMessage);
        return isErrorMessage ? "True" : "False";
    }

    /**
     * Mapping Content Type
     *
     * Setting values from HttpBodyContentType enum based on the flag.
     * @param isXMLEncoded boolean
     * @return Encoded String
     * @throws MALException no exception
     */
    public String mapContentType(boolean isXMLEncoded) throws MALException {
        return isXMLEncoded ? HttpBodyContentType.XML_ENCODED.toString() : HttpBodyContentType.OTHER_ENCODED.toString();
    }

    /**
     * Mapping Body Byte length
     *
     * @param byteLength length in bytes of the encoded MAL HTTP message body
     * @return Encoded String
     * @throws MALException no exception
     */
    public String mapContentLength(long byteLength) throws MALException {
        return Long.toString(byteLength);
    }

    /**
     * Mapping Encoding ID
     *
     * @param encodingId Encoding ID
     * @return Encoded String
     * @throws MALException no exception
     */
    public String mapEncoding(int encodingId) throws MALException {
        return Integer.toString(encodingId);
    }

    /**
     * Mapping Version Number.
     *
     * Valueis hard coded as specified in WhiteBook.
     *
     * @return Encoded String
     * @throws MALException no exception
     */
    public String mapVersionNumber() throws MALException {
        return MAL_VERSION_NUMBER;
    }

    public synchronized void generateHeader(MALMessageHeader malMessageHeader,
                                                           Map<String, String> headerMap) throws MALException {
        //Map<String, String> headerMap = new HashMap<>();
        headerMap.put(HttpHeaderKeys.URI_FROM.toString(),
                mapUrlFrom(malMessageHeader.getURIFrom()));
        headerMap.put(HttpHeaderKeys.AUTH_ID.toString(),
                mapAuthID(malMessageHeader.getAuthenticationId()));
        headerMap.put(HttpHeaderKeys.TIMESTAMP.toString(),
                mapTimestamp(malMessageHeader.getTimestamp()));
        headerMap.put(HttpHeaderKeys.QOS_LEVEL.toString(),
                mapQosLevel(malMessageHeader.getQoSlevel()));
        headerMap.put(HttpHeaderKeys.PRIORITY.toString(),
                mapPriority(malMessageHeader.getPriority()));
        headerMap.put(HttpHeaderKeys.NETWORK_ZONE.toString(),
                mapNetworkZone(malMessageHeader.getNetworkZone()));
        headerMap.put(HttpHeaderKeys.SESSION.toString(),
                mapSession(malMessageHeader.getSession()));
        headerMap.put(HttpHeaderKeys.SESSION_NAME.toString(),
                mapSessionName(malMessageHeader.getSessionName()));
        headerMap.put(HttpHeaderKeys.INTERACTION_TYPE.toString(),
                mapInteractionType(malMessageHeader.getInteractionType()));
        headerMap.put(HttpHeaderKeys.INTERACTION_STAGE.toString(),
                mapInteractionStage(malMessageHeader.getInteractionStage()));
        headerMap.put(HttpHeaderKeys.TRANSACTION_ID.toString(),
                mapTransactionId(malMessageHeader.getTransactionId()));
        headerMap.put(HttpHeaderKeys.SERVICE.toString(),
                mapService(malMessageHeader.getService()));
        headerMap.put(HttpHeaderKeys.SERVICE_AREA.toString(),
                mapServiceArea(malMessageHeader.getServiceArea()));
        headerMap.put(HttpHeaderKeys.OPERATION.toString(),
                mapOperation(malMessageHeader.getOperation()));
        headerMap.put(HttpHeaderKeys.AREA_VERSION.toString(),
                mapAreaVersion(malMessageHeader.getAreaVersion()));
        headerMap.put(HttpHeaderKeys.IS_ERROR_MSG.toString(),
                mapIsErrorMsg(malMessageHeader.getIsErrorMessage()));
        headerMap.put(HttpHeaderKeys.VERSION.toString(),
                mapVersionNumber());
        headerMap.put(HttpHeaderKeys.DOMAIN.toString(),
                mapDomain(malMessageHeader.getDomain()));
        //return headerMap;
    }

    /**
     * Mapping all fields in MAL Message Header to respective Http Headers
     * @param malMessageHeader MAL Message Header with all properties
     * @return a map of http keys and values
     * @throws MALException null and other exceptions
     */
    public synchronized Map<String, String> generateHeader(MALMessageHeader malMessageHeader,
                                                           Map<String, String> headerMap,
                                                           URI httpEndPoint, boolean xmlEncoded,
                                                           @Nullable String encoderName) throws MALException {
        generateHeader(malMessageHeader, headerMap);
        try {
            StringBuffer host = ObjectFactory.createStringBuffer();
            StringBuffer requestTarget = ObjectFactory.createStringBuffer();
            StringBuffer malUriTo = ObjectFactory.createStringBuffer();
            mapUrlTo(malMessageHeader.getURITo(), httpEndPoint, host, requestTarget, malUriTo);
            if (host.length() > 0) {
                headerMap.put(HttpHeaderKeys.HOST.toString(), host.toString());
            }
            if (requestTarget.length() > 0) {
                headerMap.put(HttpHeaderKeys.REQUEST_TARGET.toString(), requestTarget.toString());
            }
            if (malUriTo.length() > 0) {
                headerMap.put(HttpHeaderKeys.URI_TO.toString(), malUriTo.toString());
            }
        } catch (MALException malException) {
            throw new MALException("Pattern not supported");
        }
        headerMap.put(HttpHeaderKeys.CONTENT_TYPE.toString(), mapContentType(xmlEncoded));
        if (!xmlEncoded && encoderName != null) {
            headerMap.put(HttpHeaderKeys.ENCODING.toString(), HeaderMappingHelper.encodeString(encoderName));
        }
        return headerMap;

    } 
}
