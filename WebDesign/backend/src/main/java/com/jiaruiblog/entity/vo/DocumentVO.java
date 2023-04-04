package com.jiaruiblog.entity.vo;

import com.jiaruiblog.entity.ocrResult.EsSearchContent;
import com.jiaruiblog.entity.ocrResult.OcrResult;
import com.jiaruiblog.enums.DocStateEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DocumentVO
 * @Description DocumentVO
 * @Author luojiarui
 * @Date 2022/6/21 9:03 下午
 * @Version 1.0
 **/
@Data
public class
DocumentVO {

    private String id;

    private String title;

    private String description;

    private List<String> stringList;

    private Long size;

    private Long collectNum;
//    点赞数量
    private Long likeNum;
    private Long commentNum;

    private CategoryVO categoryVO;

    private List<TagVO> tagVOList;

    private String userName;

    private String thumbId;

    private DocStateEnum docState;

    private String errorMsg;

    private String txtId;

    private String previewFileId;

    private Date createTime;

//    ocr结果list
    private List<OcrResult>ocrResultList;

    private List<EsSearchContent> esSearchContentList;

    private List<EsSearchContent> esSearchContentList_syno;

//    得分
    private double content_score;

    private double click_score;

    private double like_score;

    private boolean hasCollect;
    private boolean hasLike;
}
