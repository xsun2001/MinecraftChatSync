package io.xsun.minecraft.chatsync.common.communication.insideprotocol;

public class InvalidMessageTypeException extends RuntimeException {

    private final String invalidTypeName;

    public InvalidMessageTypeException(String invalidTypeName) {
        super("Invalid type name: " + invalidTypeName);
        this.invalidTypeName = invalidTypeName;
    }

    public String getInvalidTypeName() {
        return invalidTypeName;
    }
}
