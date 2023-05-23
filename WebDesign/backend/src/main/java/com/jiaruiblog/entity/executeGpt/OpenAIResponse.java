package com.jiaruiblog.entity.executeGpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIResponse {
    private String id;
    private String object;
    private long created;
    private List<Choice> choices;
    // getters and setters omitted for brevity
//    help me to write the get and set method of all the attributes





}



