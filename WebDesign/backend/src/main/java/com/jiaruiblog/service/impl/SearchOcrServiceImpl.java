package com.jiaruiblog.service.impl;

import com.google.common.collect.Lists;
import com.jiaruiblog.entity.OcrPosition;
import com.jiaruiblog.entity.OcrResult;
import com.jiaruiblog.entity.vo.PositionVO;
import com.jiaruiblog.entity.vo.SearchOcrResultVO;
import com.jiaruiblog.service.SearchOcrService;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SearchOcrServiceImpl implements SearchOcrService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public BaseApiResult searchOcrByKeyword(String keyword) throws IOException {

        List<SearchOcrResultVO> resultVOS = Lists.newArrayList();
        Query query = new Query();
        query.addCriteria(Criteria.where("ocrText").regex(Pattern.compile(keyword)));
        List<OcrResult> ocrResults = mongoTemplate.find(query, OcrResult.class,"ocr_result");
        for(int i=0;i<ocrResults.size();i++)
        {
            SearchOcrResultVO searchOcrResultVO = new SearchOcrResultVO();
//            将图片转化为bytes类型的数据并存入VO中
            byte[] imageBytes = Base64.getDecoder().decode(ocrResults.get(i).getImage());
//            可以加一步对图片进行处理
            searchOcrResultVO.setImage(imageBytes);

//            将包含keyword的文字保存到List中
            List<PositionVO> positionVOS = Lists.newArrayList();
            for(OcrPosition ocrPosition:ocrResults.get(i).getTextResult())
            {
                if(ocrPosition.getText().contains(keyword))
                {
                    PositionVO positionVO = new PositionVO(ocrPosition);
                    positionVOS.add(positionVO);
                }
            }
            searchOcrResultVO.setPositionVOS(positionVOS);

            searchOcrResultVO.setOcrText(ocrResults.get(i).getOcrText());
            searchOcrResultVO.setPdfPage(ocrResults.get(i).getPdfPage());
            searchOcrResultVO.setPdfURL(ocrResults.get(i).getPdfURL());

//            将结果保存到一个list中
            resultVOS.add(searchOcrResultVO);

//            FileOutputStream fos = new FileOutputStream("D:/test/image"+i+".png");
//            fos.write(imageBytes);
//            fos.close();
        }

      return BaseApiResult.success(resultVOS);
    }
}
