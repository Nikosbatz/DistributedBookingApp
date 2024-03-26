package com.example;

import org.json.JSONObject;

public class ManagerOperationTask implements Task {
    public enum OperationType {
        ADD_LISTING, SHOW_LISTINGS
    }

    private OperationType operationType;
    private JSONObject payload; // For ADD_LISTING, this would be listing details. For SHOW_LISTINGS, this might be unused.

    public ManagerOperationTask(OperationType operationType, JSONObject payload) {
        this.operationType = operationType;
        this.payload = payload;
    }

    // Getters
    public OperationType getOperationType() {
        return operationType;
    }

    public JSONObject getPayload() {
        return payload;
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
}

