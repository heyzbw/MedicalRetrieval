package com.jiaruiblog.entity.executeGpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Choice {
    private int index;
    private Message message;
    // getters and setters omitted for brevity

}
