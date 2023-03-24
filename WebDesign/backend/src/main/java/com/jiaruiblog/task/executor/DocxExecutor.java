package com.jiaruiblog.task.executor;

import com.jiaruiblog.entity.ContentEachPage;
import com.jiaruiblog.task.data.TaskData;
import com.jiaruiblog.util.MsExcelParse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/24 11:42
 * @Version 1.0
 */
public class DocxExecutor extends TaskExecutor{

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {
        MsExcelParse.readPdfText(is, textFilePath);
    }

    @Override
    protected List<ContentEachPage> readTextEachPage(InputStream is) throws IOException {
        return null;
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        // TODO document why this method is empty

    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {

    }
}
