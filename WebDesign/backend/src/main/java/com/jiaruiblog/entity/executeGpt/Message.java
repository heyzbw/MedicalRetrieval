package com.jiaruiblog.entity.executeGpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    private String role;
    private String content;
    // getters and setters omitted for brevity
}
