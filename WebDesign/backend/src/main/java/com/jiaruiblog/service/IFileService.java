package com.jiaruiblog.service;

import com.itextpdf.text.DocumentException;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.MultiFilesUploadObj;
import com.jiaruiblog.entity.ResponseModel;
import com.jiaruiblog.entity.UploadFileObj;
import com.jiaruiblog.entity.dto.AdvanceDocumentDTO;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.DocumentDTO;
import com.jiaruiblog.enums.DocStateEnum;
import com.jiaruiblog.task.exception.TaskRunException;
import com.jiaruiblog.util.BaseApiResult;
import org.apache.http.auth.AuthenticationException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * @author jiarui.luo
 */
public interface IFileService {


    /**
     * 保存文件 - 表单
     *
     * @param md5
     * @param file
     * @return
     */
    FileDocument saveFile(String md5, MultipartFile file);


    void saveTagWhenSaveDoc(FileDocument fileDocument, List<String> tags);

    BaseApiResult documentUpload(MultipartFile file, String userId, String username) throws AuthenticationException;

    /**
     * 保存文件 - js文件流
     *
     * @param fileDocument FileDocument
     * @param inputStream InputStream
     * @return FileDocument
     */
    FileDocument saveFile(FileDocument fileDocument, InputStream inputStream);

    /**
     * update file
     * @Author luojiarui
     * @Description 重建索引和缩略图的时候专用的
     * @Date 18:05 2022/11/13
     * @Param fileDocument FileDocument
     **/
    void updateFile(FileDocument fileDocument);

    /**
     * @Author luojiarui
     * @Description // 更新文档状态
     * @Date 15:41 2022/11/13
     * @Param [fileDocument, state]
     * @return void
     **/
    void updateState(FileDocument fileDocument, DocStateEnum state, String errorMsg) throws TaskRunException;

    /**
     * @Author luojiarui
     * @Description // 删除GridFS系统中的文件
     * @Date 18:02 2022/11/13
     * @Param [id]
     * @return void
     **/
    void deleteGridFs(String ...id);

    /**
     * 删除文件
     *
     * @param id
     * @param isDeleteFile 是否删除文件
     * @return
     */
    void removeFile(String id, boolean isDeleteFile);

    /**
     * 根据id获取文件
     *
     * @param id
     * @return
     */
    Optional<FileDocument> getById(String id);

    /**
     * 根据id获取文件
     *
     * @param id
     * @return
     */
    Optional<FileDocument> getPreviewById(String id);

    /**
     * 根据md5获取文件对象
     *
     * @param md5
     * @return
     */
    FileDocument getByMd5(String md5);

    /**
     * queryById
     * @param docId String
     * @return result
     */
    FileDocument queryById(String docId);

    /**
     * 分页查询，按上传时间降序
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<FileDocument> listFilesByPage(int pageIndex, int pageSize);

    /**
     * listAndFilterByPage
     * @param pageIndex int
     * @param pageSize int
     * @param ids Collection
     * @return result
     */
    List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids);

    /**
     * listAndFilterByPageNotSort
     * @param pageIndex int
     * @param pageSize int
     * @param ids List
     * @return result
     */
    List<FileDocument> listAndFilterByPageNotSort(int pageIndex, int pageSize, List<String> ids);

    /**
     * 分页检索目前的文档信息
     * @param documentDTO DocumentDTO
     * @return result
     */
    BaseApiResult list(DocumentDTO documentDTO);

    BaseApiResult list_advance(AdvanceDocumentDTO advanceDocumentDTO) throws ParseException;

    /**
     *根据文档的详情，查询该文档的详细信息
     *
     * @param id ->Long
     * @return ApiResult
     */
    BaseApiResult detail(String id,String username) throws IOException;

    /**
     * 删除掉已经存在的文档
     *
     * @param id -> Long
     * @return ApiResult
     */
    BaseApiResult remove(String id);

    BaseApiResult listWithCategory(DocumentDTO documentDTO);

    /**
     * update file thumb
     * @param inputStream FileDocument
     * @param fileDocument InputStream
     * @throws FileNotFoundException file not found
     */
    void updateFileThumb(InputStream inputStream, FileDocument fileDocument) throws FileNotFoundException;

    /**
     * getFileThumb
     * @Author luojiarui
     * @Description // 查询缩略图信息
     * @Date 8:00 下午 2022/7/24
     * @param thumbId String
     * @return java.io.InputStream
     **/
    InputStream getFileThumb(String thumbId);

    byte[] getFileBytes(String thumbId);

    ResponseModel documentUpload_noAuth_multi(MultiFilesUploadObj multiFilesUploadObj) throws AuthenticationException;

    ResponseModel documentUpload_noAuth(UploadFileObj uploadFileObj) throws AuthenticationException;

    /**
     * 保存文件流到dfs系统中
     **/
    String uploadFileToGridFs(String prefix, InputStream in, String contentType);

    /**
     * @Author luojiarui
     * @Description 查询并删除某个文档
     * @Date 10:01 2022/12/10
     * @Param [docId]
     * @return com.jiaruiblog.entity.FileDocument
     **/
    List<FileDocument> queryAndRemove(String ...docId);

    /**
     * @Author luojiarui
     * @Description 文档查询并通过文档审批
     * @Date 10:27 2022/12/10
     * @Param [docId]
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     **/
    List<FileDocument> queryAndUpdate(String ...docId);

    /**
     * @Author luojiarui
     * @Description 查询是否在评审的文档
     * @Date 12:02 2022/12/10
     * @Param [pageDTO, reviewing]
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     **/
    List<FileDocument> queryFileDocument(BasePageDTO pageDTO, boolean reviewing);

    /**
     * @Author luojiarui
     * @Description 查询文档评审的列表, 实际是查询文档的信息
     * @Date 20:45 2022/11/30
     * @Param [page, user]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult queryFileDocumentResult(BasePageDTO pageDTO, boolean reviewing);


    ResponseModel createScanPDF(String filename, MultipartFile[] files, HttpServletRequest request) throws DocumentException, IOException, AuthenticationException;
}
