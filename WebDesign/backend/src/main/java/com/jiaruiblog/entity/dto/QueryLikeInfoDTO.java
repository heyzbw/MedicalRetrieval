package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryLikeInfoDTO {
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String entityId;
    private String userId;
}
