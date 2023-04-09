<template>
    <div class="search-doc">
        <div class="doc-title-group">
            <!-- <div class="doc-pic" style="text-align: center;">
                <img :src="thumbId | imgSrc" alt="thumb"
                    style="width: 36px;max-height: 48px;border: 1px solid #dcdee2; border-radius: 2px">
            </div> -->
            <div class="title-group">
                <div class="doc-title-info" type="file" @click="getDocView()">
                    {{ Title }}
                </div>
                <div class="description">
                    <div class="description-item">
                        Author:{{ Author[0] }}
                    </div>
                    <div class="description-item1">
                        Journal:{{ Journal }}
                    </div>

                </div>
            </div>
        </div>
        <div class="doc-abstract">

            <p v-html="Abstract"></p>

        </div>
        <ul class="ivu-list-item-action">
            <li>
                <i class="ivu-icon ivu-icon-ios-star-outline"></i>
                {{ collectNum }}
            </li>
            <li>
                <i class="ivu-icon ivu-icon-ios-thumbs-up-outline"></i>0
            </li>
            <li>
                <i class="ivu-icon ivu-icon-ios-chatbubbles-outline"></i>
                {{ commentNum }}
            </li>
        </ul>
        <!--        </div>-->
    </div>
</template>

<script>
import { parseTime } from "@/utils/index"
import { BackendUrl } from '@/api/request'
import axios from "axios";

export default {
    name: "PubmedItem",
    data() {
        return {
            actionUrl: BackendUrl() + "/files/upload",
            uploadParam: {},
            uploadProcess: 0.00,
        }
    },
    props: {
        Journal: { type: String, requires: true },
        ISSN: { type: String, requires: true },
        Title: { type: String, requires: true },
        Abstract: { type: String, requires: true },
        Author: { type: Array, requires: true, default: [] },
        collectNum: { type: Number, requires: false, default: 0 },
        commentNum: { type: Number, requires: false, default: 0 },
        Source: { type: String, requires: true },
        doi: { type: String, requires: true }
    },
    // 将 prop 数据转换为本地数据
    methods: {
        getDocView() {
            this.$axios({
                method: "post",
                url: "http://127.0.0.1:8083/PDFdownload",
                responseType: 'arraybuffer',
                data: {
                    'doi': this.doi,
                    'Title': this.Title
                }
            }).then(response => {
                console.log(typeof (response.data))
                this.info(true)
                // const json = JSON.parse(response.data);
                // console.log(typeof (json))
                console.log(response)
                let inputFile = response.data
                let bblob = new Blob([inputFile], { type: 'application/pdf;charset=utf-8' })
                let blob = new File([bblob], this.Title + '.pdf', { type: 'application/pdf;charset=utf-8' });
                //window.URL.createObjectURL(blob)
                window.URL.createObjectURL(bblob)
                //window.URL.createObjectURL(filee)
                const link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = this.Title + '.pdf';
                link.click();
                window.URL.revokeObjectURL(link.href);
                this.uploadParam = {
                    fileId: this.Title,
                    file: blob
                };
                console.log(blob)
                let param = this.uploadParam
                let formData = new FormData();
                formData.set("fileName", param.fileId);
                formData.set("file", param.file);
                const config = {
                    onUploadProgress: (progressEvent) => {
                        // progressEvent.loaded:已上传文件大小
                        // progressEvent.total:被上传文件的总大小
                        this.uploadProcess = Number(
                            ((progressEvent.loaded / progressEvent.total) * 0.9).toFixed(2)
                        );
                    },
                };
                console.log(formData)
                axios.post(this.actionUrl, formData, config).then(res => {
                    let { data } = res
                    console.log(res)
                    if (data['code'] === 200 || data['code'] === 'success') {
                        this.uploadProcess = 1;
                        this.$Message.success("成功！")
                    } else {
                        this.$Message.error("出错：" + data['message'])
                        this.uploadProcess = 0.00
                    }
                    setTimeout(() => {
                    }, 1000)
                }).catch(err => {
                    this.$Message.error("上传出错！")
                    this.uploadProcess = 0.0
                })
                // 无论是否成功都过滤掉
                this.uploadParam = {}
            }).catch(error => {
                //console.log(error.response, "error");
                this.info(false)
            });


        },
        info(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '正在保存远程数据库请耐心等待' : 'pubmed错了点问题，换篇文章或者稍后再试'
            });
        },

    }
}
    // filters: {
    //     imgSrc(value) {
    //         if (value === "" || value === undefined || value == null) {
    //             return BackendUrl() + "/files/image2/d2d9933cf295443990b2bed036a534ec";
    //         } else {
    //             return BackendUrl() + "/files/image2/" + value;
    //         }
    //     }
    // }

</script>

<style scoped>
.search-doc {
    padding: 12px 12px;
    border-bottom: 1px solid #e8eaec;
}

.search-doc:hover {
    background-color: rgba(245, 245, 245, 100);
    cursor: pointer;
}


.doc-title-group {
    height: 48px;
    margin-bottom: 16px;
    /*display: flex;*/
    /*flex: 1;*/
    /*align-items: flex-start;*/
    display: block;
}

.doc-pic {
    height: 48px;
    width: 48px;
    float: left;
    line-height: 48px;
    margin: auto;
}

.title-group {
    height: 48px;
    float: left;
    width: calc(100% - 60px);
}

.doc-title-info {
    height: 22px;
    /*line-height: 36px;*/
    /*margin-bottom: 12px;*/
    color: rgba(208, 164, 1, 100);
    font-size: 16px;
    font-weight: 700;
}

.doc-title-info {
    height: 22px;
    /*line-height: 36px;*/
    /*margin-bottom: 12px;*/
    color: rgb(18, 135, 243);
    font-size: 16px;
    font-weight: 700;
}

.description {
    height: 26px;
    line-height: 26px;
    display: block;
    color: rgba(0, 0, 0, .45);
    font-size: 14px;
    float: left;
}

.description-item {
    width: 200px;
    line-height: 24px;
    padding-top: 2px;
    float: left;
}

.description-item1 {
    width: 800px;
    line-height: 24px;
    padding-top: 2px;
    float: left;
}

ul {
    margin: 16px 0 0;
}

li {
    padding: 0 20px;
}

.doc-abstract>>>em {
    background-color: yellow;
}

.sl-abstract {
    margin: 2px 0 0 0;
}
</style>