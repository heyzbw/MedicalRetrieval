package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;

import java.io.IOException;
import java.util.List;

/**
 * @author jiarui.luo
 */
public interface ElasticService {


    /**
     * search
     * @param keyword String
     * @return result
     * @throws IOException
     */
    List<FileDocument> search(String keyword) throws IOException;

    /**
     * RemoveCollect
     * @param docId String
     * @return boolean
     */
    public boolean RemoveCollect(String docId) throws IOException;

    /**
     * addCollect
     * @param docId String
     * @return boolean
     */
    public boolean addCollect(String docId);

}
