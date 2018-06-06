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

import nasa.mo.mal.encoder.util.HeaderMappingHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import java.util.Map;

/**
 * @author Wai Phyo
 *         Created on 4/24/17.
 * Singleton instance to convert Http Head values to MAL Header
 */
public class HttpToMalMapper {
    private static class SingletonHolder {
        private static final HttpToMalMapper INSTANCE = new HttpToMalMapper();
    }
    public static HttpToMalMapper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Constructor
     */
    private HttpToMalMapper() {

    }

    /**
     * Converting encoded URI String to decoded URI String
     * @param encodedString Http Header Value of String which is encoded URI
     * @return new URI MAL Object
     * @throws MALException possible exceptions from Helper method
     */
    
    private URI mapURIFrom(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new URI(HeaderMappingHelper.decodeURI(encodedString));
    }

    /**
     * Converting hexadecimal characters to Byte Array
     * @param encodedString  Http Header Value of String which are hexadecimal characters
     * @return new Blob MAL Object (byte array)
     * @throws MALException possible exceptions from Helper method
     */

    private Blob mapAuthenticationId(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new Blob(HeaderMappingHelper.convertHexStringToByteArray(encodedString));
    }

    /**
     * Converting encoded URI String to decoded URI String if the String exist
     * Based on session: 3.2.4.2.4 (intro) on Red-Book
     * @param uriTo Http Encoded String with encoded URI
     * @return new URI MAL Object
     * @throws MALException possible exceptions from Helper method
     */

    private URI mapURITo(String uriTo, String host, String requestTarget) throws MALException {
        if (uriTo == null || uriTo.equals(HeaderMappingHelper.EMPTY_STRING)) {
            if (host != null && requestTarget != null) {
                return new URI(HeaderMappingHelper.decodeURI(host) + HeaderMappingHelper.decodeURI(requestTarget));
            } else {
                return null;
            }
        } else {
            return new URI(HeaderMappingHelper.decodeURI(uriTo));
        }
    }

    /**
     * Converting time in String to Millisecond in Long
     * @param encodedString Http Header Value of String in "yyyy-DDDTHH:mm:ss.ddd"
     * @return new QoS Level MAL object or NULL for invalid values
     * @throws MALException null exception
     */

    private Time mapTimestamp(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new Time(HeaderMappingHelper.decodeTime(encodedString));
    }

    /**
     * Converting Enum Value of String to Enum Object
     * @param encodedString Http Header Value of String
     * @return new QoS Level MAL object or NULL for invalid values
     * @throws MALException null exception
     */

    private QoSLevel mapQoSLevel(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return QoSLevel.fromString(encodedString);
    }

    /**
     * Converting String to Long
     * @param encodedString Http Header Value of Long
     * @return new Unsigned Integer MAL object
     * @throws MALException null exception
     */

    private UInteger mapPriority(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new UInteger(Long.valueOf(encodedString));
    }

    /**
     * Converting "." separated encoded String to decoded List
     * @param encodedString Http Header Value of encoded Strings separated by "."
     * @return new Identifier List MAL object
     * @throws MALException null exceptions & possible exceptions from helper
     */

    private IdentifierList mapDomain(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        IdentifierList identifiers = new IdentifierList();
        if (!encodedString.isEmpty()) {
            for (String each : encodedString.trim().split(HeaderMappingHelper.DEC_MILLISECOND_SEPARATOR)) {
                identifiers.add(new Identifier(HeaderMappingHelper.decodeString(each)));
            }
        }
        return identifiers;
    }

    /**
     * Converting Encoded String to decoded String
     * using {@link HeaderMappingHelper#decodeString(String)}
     * @param encodedString Http Header Value of String
     * @return new Identifier MAL object
     * @throws MALException null exception
     */

    private Identifier mapNetworkZone(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new Identifier(HeaderMappingHelper.decodeString(encodedString));
    }

    /**
     * Converting Enum Value of String to Enum Object
     * @param encodedString Http Header Value of String
     * @return new Session Type MAL object or NULL for invalid values
     * @throws MALException null exception
     */

    private SessionType mapSession(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return SessionType.fromString(encodedString);
    }
    /**
     * Converting Encoded String to decoded String
     * using {@link HeaderMappingHelper#decodeString(String)}
     * @param encodedString Http Header Value of String
     * @return new Identifier MAL object
     * @throws MALException null exception
     */

    private Identifier mapSessionName(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new Identifier(HeaderMappingHelper.decodeString(encodedString));
    }

    /**
     * Converting Enum Value of String to Enum Object
     * @param encodedString Http Header Value of String
     * @return new Interaction Type MAL object or NULL for invalid values
     * @throws MALException null exception
     */

    private InteractionType mapInteractionType(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return InteractionType.fromString(encodedString);
    }

    /**
     * Converting String to Short
     * @param encodedString Http Header Value of Short
     * @return new Unsigned Octet MAL object
     * @throws MALException null exception
     */

    private UOctet mapInteractionStage(String encodedString) throws MALException {
        if (encodedString == null || encodedString.isEmpty()) {
            return null;
        }
        HeaderMappingHelper.checkForNull(encodedString);
        return new UOctet(Short.valueOf(encodedString));
    }

    /**
     * Converting String to Long
     * @param encodedString Http Header Value of Long
     * @return new Long Object
     * @throws MALException null exception
     */

    private Long mapTransactionId(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return Long.valueOf(encodedString);
    }

    /**
     * Converting String to Integer
     * @param encodedString Http Header Value of Integer
     * @return new Unsigned Short MAL object
     * @throws MALException null exception
     */

    private UShort mapServiceArea(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new UShort(Integer.valueOf(encodedString));
    }

    /**
     * Converting String to Integer
     * @param encodedString Http Header Value of Integer
     * @return new Unsigned Short MAL object
     * @throws MALException null exception
     */

    private UShort mapService(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new UShort(Integer.valueOf(encodedString));
    }



    private UShort mapOperation(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new UShort(Integer.valueOf(encodedString));
    }

    /**
     * Converting String to Short
     * @param encodedString Http Header Value of Short
     * @return new Unsigned Octet MAL Object
     * @throws MALException null exception
     */

    private UOctet mapAreaVersion(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return new UOctet(Short.valueOf(encodedString));
    }

    /**
     * Converting String to boolean.
     * NOTE: this method will return anything String other than "true" to false.
     *
     * @param encodedString Http Header Value of Is Error Message. String value is True / False
     * @return flag if it is error message
     * @throws MALException null exception
     */

    private Boolean mapErrorMessage(String encodedString) throws MALException {
        HeaderMappingHelper.checkForNull(encodedString);
        return Boolean.valueOf(encodedString);
    }

    /**
     * Fill all fields of header with values
     * @param malMessageHeader new MAL Message header to be updated with values
     * @throws MALException null exceptions
     */
    public synchronized MALMessageHeader fillMalMessageHeader(final Map<String, String> httpHeaderMap,
                                                              final MALMessageHeader malMessageHeader)
            throws MALException {
        malMessageHeader.setURIFrom(mapURIFrom(httpHeaderMap.get(HttpHeaderKeys.URI_FROM.toString())));
        malMessageHeader.setAuthenticationId(mapAuthenticationId(httpHeaderMap.get(HttpHeaderKeys.AUTH_ID.toString())));
        malMessageHeader.setURITo(mapURITo(httpHeaderMap.get(HttpHeaderKeys.URI_TO.toString()),
                httpHeaderMap.get(HttpHeaderKeys.HOST.toString()),
                httpHeaderMap.get(HttpHeaderKeys.REQUEST_TARGET.toString())));
        malMessageHeader.setTimestamp(mapTimestamp(httpHeaderMap.get(HttpHeaderKeys.TIMESTAMP.toString())));
        malMessageHeader.setQoSlevel(mapQoSLevel(httpHeaderMap.get(HttpHeaderKeys.QOS_LEVEL.toString())));
        malMessageHeader.setPriority(mapPriority(httpHeaderMap.get(HttpHeaderKeys.PRIORITY.toString())));
        malMessageHeader.setDomain(mapDomain(httpHeaderMap.get(HttpHeaderKeys.DOMAIN.toString())));
        malMessageHeader.setNetworkZone(mapNetworkZone(httpHeaderMap.get(HttpHeaderKeys.NETWORK_ZONE.toString())));
        malMessageHeader.setSession(mapSession(httpHeaderMap.get(HttpHeaderKeys.SESSION.toString())));
        malMessageHeader.setSessionName(mapSessionName(httpHeaderMap
                .get(HttpHeaderKeys.SESSION_NAME.toString())));
        malMessageHeader.setInteractionType(mapInteractionType(httpHeaderMap
                .get(HttpHeaderKeys.INTERACTION_TYPE.toString())));
        malMessageHeader.setInteractionStage(mapInteractionStage(httpHeaderMap
                .get(HttpHeaderKeys.INTERACTION_STAGE.toString())));
        malMessageHeader.setTransactionId(mapTransactionId(httpHeaderMap
                .get(HttpHeaderKeys.TRANSACTION_ID.toString())));
        malMessageHeader.setServiceArea(mapServiceArea(httpHeaderMap.get(HttpHeaderKeys.SERVICE_AREA.toString())));
        malMessageHeader.setService(mapService(httpHeaderMap.get(HttpHeaderKeys.SERVICE.toString())));
        malMessageHeader.setOperation(mapOperation(httpHeaderMap.get(HttpHeaderKeys.OPERATION.toString())));
        malMessageHeader.setAreaVersion(mapAreaVersion(httpHeaderMap.get(HttpHeaderKeys.AREA_VERSION.toString())));
        malMessageHeader.setIsErrorMessage(mapErrorMessage(httpHeaderMap.get(HttpHeaderKeys.IS_ERROR_MSG.toString())));
        return malMessageHeader;
    }
}
