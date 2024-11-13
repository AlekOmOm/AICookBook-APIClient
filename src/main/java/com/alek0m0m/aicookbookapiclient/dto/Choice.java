
package com.alek0m0m.aicookbookapiclient.dto;

import com.fasterxml.jackson.annotation.*;
import org.apache.logging.log4j.message.Message;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "index",
        "message",
        "logprobs",
        "finish_reason"
})

public class Choice {

    @JsonProperty("index")
    private Integer index;
    @JsonProperty("message")
    private Message message;
    @JsonProperty("logprobs")
    private Object logprobs;
    @JsonProperty("finish_reason")
    private String finishReason;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("index")
    public Integer getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(Integer index) {
        this.index = index;
    }

    @JsonProperty("message")
    public Message getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Message message) {
        this.message = message;
    }

    @JsonProperty("logprobs")
    public Object getLogprobs() {
        return logprobs;
    }

    @JsonProperty("logprobs")
    public void setLogprobs(Object logprobs) {
        this.logprobs = logprobs;
    }

    @JsonProperty("finish_reason")
    public String getFinishReason() {
        return finishReason;
    }

    @JsonProperty("finish_reason")
    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}