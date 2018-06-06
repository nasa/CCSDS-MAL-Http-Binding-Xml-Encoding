/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http.util;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.ccsds.moims.mo.mal.*;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wphyo
 *         Created on 6/20/17.
 * Storing stages where
 * a default reply should be sent upon receiving messages AND
 * a message is a response, NOT a new message when sending messages.
 */
public class HttpTransportHelper {
    /**
     * UriRegex REGEX for 1 to 65535
     * 1 - 9
     * 10 - 9999
     * 10000 - 59999
     * 60000 - 64999
     * 65000 - 65499
     * 65500 - 65529
     * 65530 - 65535
     */
    private static final String PORT = "([1-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])";
    /*
     * The old pattern accepts malhttp, http, https, and malhttps
     * Red Book specifies that URIs must start with malhttp schema.
     * In Shadow Test, schema for both https and http test cases are malhttp
     */
    //    private static final String BASE_URI = "^(mal)?" + Constants.PLAIN_HTTP + "(s)?://(.+):" + PORT + ".*$";
    private static final String BASE_URI = "^malhttp://(.+):" + PORT + ".*$";

    private static final Pattern BASE_URI_PATTERN = Pattern.compile(BASE_URI);
    private static final int POTENTIAL_IP_GROUP = 1;
    private static final  String[] SCHEMES = new String[] { Constants.PLAIN_HTTP, Constants.SECURE_HTTP };
    private static final  UrlValidator URL_VALIDATOR = new UrlValidator(SCHEMES, UrlValidator.ALLOW_LOCAL_URLS);

    private static final InteractionTypeAndStage[] DEFAULT_REPLY_STAGES = new InteractionTypeAndStage[] {
            new InteractionTypeAndStage(InteractionType.SEND, (byte)0),
            new InteractionTypeAndStage(InteractionType.INVOKE, MALInvokeOperation._INVOKE_RESPONSE_STAGE),
            new InteractionTypeAndStage(InteractionType.PROGRESS, MALProgressOperation._PROGRESS_UPDATE_STAGE),
            new InteractionTypeAndStage(InteractionType.PROGRESS, MALProgressOperation._PROGRESS_RESPONSE_STAGE),
            new InteractionTypeAndStage(InteractionType.PUBSUB, MALPubSubOperation._PUBLISH_STAGE),
            new InteractionTypeAndStage(InteractionType.PUBSUB, MALPubSubOperation._NOTIFY_STAGE)
    };

    private static final InteractionTypeAndStage[] RESPONSE_REPLY_STAGES = new InteractionTypeAndStage[] {
            new InteractionTypeAndStage(InteractionType.SUBMIT, MALSubmitOperation._SUBMIT_ACK_STAGE),
            new InteractionTypeAndStage(InteractionType.REQUEST, MALRequestOperation._REQUEST_RESPONSE_STAGE),
            new InteractionTypeAndStage(InteractionType.INVOKE, MALInvokeOperation._INVOKE_ACK_STAGE),
            new InteractionTypeAndStage(InteractionType.PROGRESS, MALProgressOperation._PROGRESS_ACK_STAGE),
            new InteractionTypeAndStage(InteractionType.PUBSUB, MALPubSubOperation._REGISTER_ACK_STAGE),
            new InteractionTypeAndStage(InteractionType.PUBSUB, MALPubSubOperation._DEREGISTER_ACK_STAGE),
            new InteractionTypeAndStage(InteractionType.PUBSUB, MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE),
            new InteractionTypeAndStage(InteractionType.PUBSUB, MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE),
    };

    /**
     * Validating URI from MAL Header based on Red-Book Section 3.4
     *
     * Format:
     * 1.   It starts with scheme http TODO it starts with malhttp
     * 2.   followed by "://"
     * 3.   followed by IP or hostname
     * 3.1      IP4
     * 3.2      IP6
     * 3.3      HostName
     * 4.   followed by ":" & port
     * 5.   optional "/{anything}"
     *
     * Steps:
     * 1.   Check if it fits general format: http://{IP}:{port}{/anything}
     * 2.   if it fits, remove "[]" if it is ip-6
     *          if it is, use Apache to check validity of ip-6
     * 3.   if not, Apache to check validity of URL
     *
     * @param url URL from MAL Message Header
     * @return flag if it is valid.
     */
    public static boolean isValidURI(String url) {
        Matcher baseURIMatcher = BASE_URI_PATTERN.matcher(url);
        if (baseURIMatcher.matches()) {
            String potentialIP = baseURIMatcher.group(POTENTIAL_IP_GROUP);
            url = url.replaceAll("^malhttp", "http");
            if (potentialIP.startsWith("[") && potentialIP.endsWith("]")) {
                potentialIP = potentialIP.substring(1, potentialIP.length() - 1);
                return InetAddressValidator.getInstance().isValidInet6Address(potentialIP);
            } else {
                return URL_VALIDATOR.isValid(url);
            }
        }
        return false;
    }

    /**
     * Check if the received message needs a default reply.
     * 1. if it is send, it is.
     * 2. if not, search in array
     *
     * @param interactionType SEND, SUBMIT, INVOKE, REQUEST, PROGRESS, PUBLISH-SUBSCRIBE
     * @param stage stage for each type
     * @return flag if it is correct message.
     */
    public static boolean isDefaultReply(InteractionType interactionType, UOctet stage) {
        return interactionType.equals(InteractionType.SEND) ||
                searchArray(DEFAULT_REPLY_STAGES, interactionType, Short.valueOf(stage.getValue()).byteValue());
    }

    /**
     * Check if the sending message is a new message or a response message
     * @param interactionType SEND, SUBMIT, INVOKE, REQUEST, PROGRESS, PUBLISH-SUBSCRIBE
     * @param stage stage for each type
     * @return flag if it is correct message.
     */
    public static boolean isResponseReply(InteractionType interactionType, UOctet stage) {
        return !interactionType.equals(InteractionType.SEND) && searchArray(RESPONSE_REPLY_STAGES, interactionType, Short.valueOf(stage.getValue()).byteValue());
    }

    /**
     * Find the type and stage match in the given array
     *
     * @param array the array to be searched.
     * @param type Interaction Type
     * @param stage Interaction Stage
     * @return if the array contains the type & stage.
     */
    private static boolean searchArray(InteractionTypeAndStage[] array, InteractionType type, byte stage) {
        return Arrays.stream(array)
                .anyMatch(each -> each.type.equals(type) && each.stage == stage);
    }

    /**
     * Finding respective MAL Error from Http Error code based on RedBook.
     * This method assumes that Http Status code is an error code i.e. >= 400
     *
     * @param statusCode Http Status Code
     * @return MAL Error Number
     */
    public static UInteger getMALErrorFromHttp(int statusCode) {
        if (statusCode < Constants.HTTP_LOWEST_ERROR_CODE) {
            throw new RuntimeException("Status code is not Error Code");
        }
        if (statusCode == ResponseCodes.BAD_REQUEST.getCode()) {
            return MALHelper.BAD_ENCODING_ERROR_NUMBER;
        } else if (statusCode == ResponseCodes.UNAUTHORIZED.getCode() ||
                statusCode == ResponseCodes.FORBIDDEN.getCode()) {
            return MALHelper.AUTHORISATION_FAIL_ERROR_NUMBER;
        } else if (statusCode == ResponseCodes.NOT_FOUND.getCode()) {
            return MALHelper.DESTINATION_UNKNOWN_ERROR_NUMBER;
        } else if (statusCode == ResponseCodes.METHOD_NOT_ALLOWED.getCode() ||
                statusCode == ResponseCodes.NOT_IMPLEMENTED.getCode()) {
            return MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER;
        } else if (statusCode == ResponseCodes.REQUEST_TIMEOUT.getCode() ||
                statusCode == ResponseCodes.GATEWAY_TIMEOUT.getCode()) {
            return MALHelper.DELIVERY_TIMEDOUT_ERROR_NUMBER;
        } else if (statusCode == ResponseCodes.GONE.getCode() ||
                statusCode == ResponseCodes.SERVICE_UNAVAILABLE.getCode()) {
            return MALHelper.DESTINATION_TRANSIENT_ERROR_NUMBER;
        } else if (statusCode == ResponseCodes.TOO_MANY_REQUEST.getCode()) {
            return MALHelper.TOO_MANY_ERROR_NUMBER;
        } else if (statusCode == ResponseCodes.INTERNAL_SERVER_ERROR.getCode()) {
            return MALHelper.INTERNAL_ERROR_NUMBER;
        } else if (statusCode == ResponseCodes.BAD_GATEWAY.getCode()) {
            return MALHelper.DELIVERY_FAILED_ERROR_NUMBER;
        } else if (statusCode == ResponseCodes.NETWORK_AUTHENTICATION_REQUIRED.getCode()) {
            return MALHelper.AUTHENTICATION_FAIL_ERROR_NUMBER;
        } else {
            return MALHelper.INTERNAL_ERROR_NUMBER;
        }
    }

    public static ResponseCodes getStatusCodeFromMALError(UInteger error) {
        long errorCode = error.getValue();
        if (errorCode == MALHelper._AUTHENTICATION_FAIL_ERROR_NUMBER) {
            return ResponseCodes.NETWORK_AUTHENTICATION_REQUIRED;
        } else if (errorCode == MALHelper._DELIVERY_TIMEDOUT_ERROR_NUMBER) {
            return ResponseCodes.GATEWAY_TIMEOUT;
        } else if (errorCode == MALHelper._DESTINATION_TRANSIENT_ERROR_NUMBER) {
            return ResponseCodes.SERVICE_UNAVAILABLE;
        } else if (errorCode == MALHelper._DELIVERY_FAILED_ERROR_NUMBER) {
            return ResponseCodes.BAD_GATEWAY;
        } else if (errorCode == MALHelper._UNSUPPORTED_OPERATION_ERROR_NUMBER) {
            return ResponseCodes.NOT_IMPLEMENTED;
        } else if (errorCode == MALHelper._INTERNAL_ERROR_NUMBER) {
            return ResponseCodes.INTERNAL_SERVER_ERROR;
        } else if (errorCode == MALHelper._TOO_MANY_ERROR_NUMBER) {
            return ResponseCodes.TOO_MANY_REQUEST;
        } else if (errorCode == MALHelper._DESTINATION_UNKNOWN_ERROR_NUMBER) {
            return ResponseCodes.NOT_FOUND;
        } else if (errorCode == MALHelper._AUTHORISATION_FAIL_ERROR_NUMBER) {
            return ResponseCodes.FORBIDDEN;
        } else if (errorCode == MALHelper._BAD_ENCODING_ERROR_NUMBER) {
            return ResponseCodes.BAD_REQUEST;
        } else {
            return ResponseCodes.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Get Http Response code based on the interaction type and stage.
     *
     * based on RedBook, response code is assigned.
     *
     * For an invalid type & stage, an error will be thrown
     *
     * @param type SEND, SUBMIT, INVOKE, REQUEST, PROGRESS, PUBLISH-SUBSCRIBE
     * @param stage stage for each type
     * @throws MALException Invalid Interaction Type or Stage
     */
    public static int getHttpResponseCode(InteractionType type, UOctet stage) throws MALException {
        ResponseCodes responseCode = null;
        if (type.equals(InteractionType.SEND)) {
            responseCode = ResponseCodes.OTHER;
        } else if (type.equals(InteractionType.SUBMIT)) {
            if (stage.equals(MALSubmitOperation.SUBMIT_ACK_STAGE)) {
                responseCode = ResponseCodes.OK;
            }
        } else if (type.equals(InteractionType.REQUEST)) {
            if (stage.equals(MALRequestOperation.REQUEST_RESPONSE_STAGE)) {
                responseCode = ResponseCodes.OK;
            }
        } else if (type.equals(InteractionType.INVOKE)) {
            if (stage.equals(MALInvokeOperation.INVOKE_RESPONSE_STAGE)) {
                responseCode = ResponseCodes.OK;
            } else if (stage.equals(MALInvokeOperation.INVOKE_ACK_STAGE)) {
                responseCode = ResponseCodes.ACCEPTED;
            }
        } else if (type.equals(InteractionType.PROGRESS)) {
            if (stage.equals(MALProgressOperation.PROGRESS_ACK_STAGE)) {
                responseCode = ResponseCodes.OK;
            } else if (stage.equals(MALProgressOperation.PROGRESS_UPDATE_STAGE) ||
                    stage.equals(MALProgressOperation.PROGRESS_RESPONSE_STAGE)) {
                responseCode = ResponseCodes.OTHER;
            }
        } else if (type.equals(InteractionType.PUBSUB)) {
            if (stage.equals(MALPubSubOperation.REGISTER_ACK_STAGE) ||
                    stage.equals(MALPubSubOperation.DEREGISTER_ACK_STAGE) ||
                    stage.equals(MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE) ||
                    stage.equals(MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE)) {
                responseCode = ResponseCodes.OK;
            } else if (stage.equals(MALPubSubOperation.NOTIFY_STAGE) ||
                    stage.equals(MALPubSubOperation.PUBLISH_STAGE)) {
                responseCode = ResponseCodes.OTHER;
            }
        }
        if (responseCode == null) {
            throw new MALException("Invalid Interaction Type or Stage");
        }
        return responseCode.getCode();
    }

    /**
     * returning simple responses
     * Steps:
     *
     * 1.   fill headers to Http Exchange Header
     * 2.   if response code is 204 (No Content), don't attach body.
     * 3.   for others, attach code & body
     *
     * @param exchange Http Exchange Object which contains response objects.
     * @param responseCode Http Response codes example: 200 = ok
     * @param body Body in Byte array
     * @param headers addition headers in a dictionary
     * @throws MALException IO
     */
    public synchronized static void fillResponse(HttpExchange exchange,
                                                 int responseCode,
                                                 byte[] body,
                                                 Map<String, String> headers) throws MALException {
        try {
            if (exchange == null) {
                throw new MALException("Null Http Exchange Object.");
            }
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(exchange.getResponseHeaders()::set);
            }
            if (responseCode == ResponseCodes.OTHER.getCode()) {
                exchange.sendResponseHeaders(responseCode, -1);
                exchange.getResponseBody().close();
            } else {
                if (body == null) {
                    body = new byte[0];
                }
                exchange.sendResponseHeaders(responseCode, body.length);
                OutputStream os = exchange.getResponseBody();
                os.write(body);
                os.close();
            }
        } catch (IOException exp) {
            throw new MALException("Error responding Http request -> " + exp, exp);
        }
    }

    /**
     * Container class to hold Interaction Type and Stage to check.
     */
    private static class InteractionTypeAndStage {
        InteractionType type;
        Byte stage;

        InteractionTypeAndStage(InteractionType type, Byte stage) {
            this.type = type;
            this.stage = stage;
        }
    }
}
