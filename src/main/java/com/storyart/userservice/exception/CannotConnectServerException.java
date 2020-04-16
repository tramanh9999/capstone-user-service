package com.storyart.userservice.exception;
import com.netflix.discovery.shared.transport.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CannotConnectServerException extends TransportException {

    private static final Logger logger= LoggerFactory.getLogger(CannotConnectServerException.class);
    public CannotConnectServerException(String message) {
        super(message);
        logger.error(message);
    }

    public CannotConnectServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
