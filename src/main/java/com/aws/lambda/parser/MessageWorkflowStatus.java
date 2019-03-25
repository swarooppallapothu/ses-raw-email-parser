package com.aws.lambda.parser;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Swaroop Pallapothu on Mar, 2019
 */
public enum MessageWorkflowStatus {
    @JsonProperty("PENDING")
    PENDING("Pending"),
    @JsonProperty("RECEIVED")
    RECEIVED("Received"),
    @JsonProperty("SENT")
    SENT("Sent"),
    @JsonProperty("MESSAGE_NOT_FOUND")
    MESSAGE_NOT_FOUND("Message NoT Found");

    MessageWorkflowStatus(String name) {
        this.name = name;
    }

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
