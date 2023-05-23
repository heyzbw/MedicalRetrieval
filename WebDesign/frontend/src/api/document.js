import {
    DOC_DETAIL_URL,
    DOC_LIST_URL,
    DOC_REMOVE_URL,
    DOC_WITH_CHECK_URL,

    DOC_UPLOAD_URL,
    DOC_VIEW_URL,
    DOC_TXT_URL,
    DOC_REBUILD_URL,

    ADD_LIKE_URL,
    GET_LIKE_URL,

    COLLECT_ADD_URL,
    COLLECT_REMOVE_URL,
    DOC_IMAGE_URL,
    DOC_SUPER_URL,

    CASE_IMAGE_URL,
    diagnosis_GET_URL

} from './url'

import { Get, Post, Delete, Download } from "@/api/request";

export default {

    getData: (params) => {
        return Get(DOC_DETAIL_URL, params);
    },

    getListData: (params) => {
        return Post(DOC_LIST_URL, params);
    },

    getImageData: (params) => {
        return Post(DOC_IMAGE_URL, params);
    },

    getCasePredictionData: (params) => {
        return Post(CASE_IMAGE_URL, params);
    },

    getSuperData: (params) => {
        return Post(DOC_SUPER_URL, params);
    },
    deleteData: (params) => {
        return Delete(DOC_REMOVE_URL, params);
    },

    getDataWithCheck: (params) => {
        return Get(DOC_WITH_CHECK_URL, params);
    },

    // 必须登录以后进行上传
    docUpload: (param, config) => {
        return Post(DOC_UPLOAD_URL, param, config)
    },

    getView: (param) => {
        return Get(DOC_VIEW_URL + param, null)
    },

    getTxtFile: (param) => {
        return Download(DOC_TXT_URL + param, { params: {}, responseType: 'blob' })
    },

    getRebuildIndex: (param) => {
        return Get(DOC_REBUILD_URL, param);
    },

    addLike: (param) => {
        return Post(ADD_LIKE_URL, null, param)
    },

    getLikeInfo: (param) => {
        return Get(GET_LIKE_URL, param)
    },

    addCollect: (param) => {
        return Post(COLLECT_ADD_URL, null, param)
    },
    getDiagnosesRecord:(param) => {
        return Post(diagnosis_GET_URL,null,param)
    }

}

