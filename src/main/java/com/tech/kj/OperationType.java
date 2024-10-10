package com.tech.kj;

import java.util.Optional;
import java.util.stream.Stream;

public enum OperationType {
    c("-c"),
    d("-d");
    private String operationName;
    OperationType(String operationName){
        this.operationName = operationName;
    }
    public static Optional<OperationType> getOperationTypeByName(String operationName){
        return Stream.of(OperationType.values()).filter(type-> type.operationName.equals(operationName))
                .findFirst();
                //.orElseThrow(() -> new RuntimeException("operation not supported"));
    }
}
