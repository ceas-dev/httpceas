package com.http.ceas.core;


public class HttpStatus{

    private final int code;
    private final String message;
    private final Type type;

    public static final HttpStatus
    CONTINUE = valueOf(100, "Continue"),
    SWITCHING_PROTOCOLS = valueOf(101, "Switching Protocols"),
    PROCESSING = valueOf(102, "Processing"),
    CHECKPOINT = valueOf(103, "Checkpoint"),
    OK = valueOf(200, "OK"),
    CREATED = valueOf(201, "Created"),
    ACCEPTED = valueOf(202, "Accepted"),
    NO_CONTENT = valueOf(204, "No Content"),
    RESET_CONTENT = valueOf(205, "Reset Content"),
    PARTIAL_CONTENT = valueOf(206, "Partial Content"),
    ALREADY_REPORTED = valueOf(208, "Already Reported"),
    IM_USED = valueOf(226, "IM Used"),
    MULTIPLE_CHOICES = valueOf(300, "Multiple Choices"),
    MOVED_PERMANENTLY = valueOf(301, "Moved Permanently"),
    FOUND = valueOf(302, "Found"),
    MOVED_TEMPORARILY = valueOf(302, "Moved Temporarily"),
    SEE_OTHER = valueOf(303, "See Other"),
    NOT_MODIFIED = valueOf(304,  "Not Modified"),
    USE_PROXY = valueOf(305, "Use Proxy"),
    TEMPORARY_REDIRECT = valueOf(307,  "Temporary Redirect"),
    PERMANENT_REDIRECT = valueOf(308, "Permanent Redirect"),
    BAD_REQUEST = valueOf(400, "Bad Request"),
    UNAUTHORIZED = valueOf(401,  "Unauthorized"),
    PAYMENT_REQUIRED = valueOf(402, "Payment Required"),
    FORBIDDEN = valueOf(403, "Forbidden"),
    NOT_FOUND = valueOf(404, "Not Found"),
    METHOD_NOT_ALLOWED = valueOf(405, "Method Not Allowed"),
    NOT_ACCEPTABLE = valueOf(406,  "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED = valueOf(407,  "Proxy Authentication Required"),
    REQUEST_TIMEOUT = valueOf(408, "Request Timeout"),
    CONFLICT = valueOf(409, "Conflict"),
    GONE = valueOf(410, "Gone"),
    LENGTH_REQUIRED = valueOf(411, "Length Required"),
    PRECONDITION_FAILED = valueOf(412, "Precondition Failed"),
    PAYLOAD_TOO_LARGE = valueOf(413, "Payload Too Large"),
    REQUEST_ENTITY_TOO_LARGE = valueOf(413,  "Request Entity Too Large"),
    URI_TOO_LONG = valueOf(414, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE = valueOf(415,  "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE = valueOf(416, "Requested range not satisfiable"),
    EXPECTATION_FAILED = valueOf(417, "Expectation Failed"),
    INSUFFICIENT_SPACE_ON_RESOURCE = valueOf(419, "Insufficient Space On Resource"),
    METHOD_FAILURE = valueOf(420, "Method Failure"),
    DESTINATION_LOCKED = valueOf(421, "Destination Locked"),
    UNPROCESSABLE_ENTITY = valueOf(422, "Unprocessable Entity"),
    LOCKED = valueOf(423, "Locked"),
    FAILED_DEPENDENCY= valueOf(424,  "Failed Dependency"),
    TOO_EARLY = valueOf(425, "Too Early"),
    UPGRADE_REQUIRED = valueOf(426, "Upgrade Required"),
    PRECONDITION_REQUIRED = valueOf(428, "Precondition Required"),
    TOO_MANY_REQUESTS = valueOf(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE = valueOf(431, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS = valueOf(451, "Unavailable For Legal Reasons"),
    INTERNAL_SERVER_ERROR = valueOf(500, "Internal Server Error"),
    NOT_IMPLEMENTED = valueOf(501, "Not Implemented"),
    BAD_GATEWAY = valueOf(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE= valueOf(503, "Service Unavailable"),
    GATEWAY_TIMEOUT = valueOf(504,  "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED= valueOf(505, "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES = valueOf(506, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE = valueOf(507, "Insufficient Storage"),
    LOOP_DETECTED = valueOf(508,  "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED = valueOf(509, "Bandwidth Limit Exceeded"),
    NOT_EXTENDED = valueOf(510,  "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED = valueOf(511, "Network Authentication Required");

    private HttpStatus(int code, String message){
        this.code = code;
        this.message = message;
        this.type = Type.valueOf(code);
    }

    public static HttpStatus valueOf(int code, String message){
        return new HttpStatus(code, message);
    }

    public int code(){
        return code;
    }

    public String message(){
        return message;
    }

    public Type type(){
        return type;
    }
    
    public boolean isSuccess(){
        switch(type){
            case INFORMATIONAL:
            case SUCCESSFUL:
                return true;
            default:
                return false;
        }
    }


    @Override
    public String toString(){
        return code + ", " + message;
    }

    @Override
    public boolean equals(Object obj){
        if(obj != null && obj instanceof HttpStatus){
            return ((HttpStatus)obj).code() == code;
        }
        return false;
    }


    public enum Type{

        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int code;

        Type(int code) {
            this.code = code;
        }

        public int value() {
            return this.code;
        }

        public static Type valueOf(int statusCode) {
            int statusCodeResolved = (statusCode / 100);
            for (Type type : values()) {
                if (type.code == statusCodeResolved) return type;
            }
            throw new IllegalArgumentException("The status code " + "statusCode" + " was not recognized");
        }
    }

}

