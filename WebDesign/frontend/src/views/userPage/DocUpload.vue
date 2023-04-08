<template>
    <div style="padding:20px 0 0 0">
        <el-tabs type="card" width="100%" style="height: 100%">
            <el-tab-pane label="上传文档" name="tab_first" width="100%">
                <div class="main-container" style="height: 900px">
                    <div class="upload-title">
                        <span>上传文档</span>
                    </div>
                    <Row>
                        <Col span="1" class="star-tag">
                        <span>*</span>
                        </Col>
                        <Col span="12" style="text-align: left">
                        <div class="upload-panel" @click="uploadDialogShow">
                            <div style="padding: 30px 0;">
                                <div style="padding: 5px; line-height: 45px;">
                                    <img :src="buttonSrc" width="68px" height="68px" alt="upload-pic" />
                                </div>
                                <p>支持Word/Excel/PPT/PDF，不超过100M</p>
                            </div>
                            <input type="file" ref="fileToUpload" id="fileToUpload" style="display: none" multiple
                                @change="changeFile">
                        </div>
                        </Col>
                    </Row>
                    <Row v-show="true">
                        <Col span="1" class="star-tag">
                        </Col>
                        <Col span="20" style="text-align: left">
                        <div class="file-title">
                            <span>{{ filename }}</span>
                        </div>
                        <div class="progress-wrapper" v-if="processFlag">
                            <div class="pro" :style="uploadProcess | processToStr"></div>
                        </div>
                        </Col>
                    </Row>

                    <Row style="padding: 5px 0; margin-top: 8px;" v-show="true">
                        <Col span="1" class="star-tag">
                        <span>*</span>
                        </Col>
                        <Col span="20">
                        <div class="search-input-top">
                            <Tag v-for="(item, index) in items" :key="index" :name="item" closable @on-close="handleClose2">
                                {{
                                    item
                                }}
                            </Tag>
                            <input @keyup.enter="handleAdd" v-model="item">
                            <Button icon="ios-add" type="dashed" size="small" @click="handleAdd">添加标签</Button>
                        </div>
                        </Col>
                    </Row>
                    <Row style="padding: 5px 0;" v-show="true">
                        <Col span="1" class="star-tag">
                        <span>*</span>
                        </Col>
                        <Col span="6">
                        <div class="cate-dropdown">
                            <Dropdown @on-click="switchCategory">
                                <a href="javascript:void(0)">
                                    {{ checkedCategory.name }}
                                    <Icon type="ios-arrow-down"></Icon>
                                </a>
                                <template #list>
                                    <DropdownMenu>
                                        <DropdownItem v-for="item in categoryOption" :name="item.id">
                                            {{ item.seeName }}
                                        </DropdownItem>
                                    </DropdownMenu>
                                </template>
                            </Dropdown>
                        </div>
                        </Col>
                    </Row>
                    <Row style="padding: 5px 0;" v-show="true">
                        <Col span="1" class="star-tag">
                        <span>*</span>
                        </Col>
                        <Col span="6">
                        <div class="booll">
                            <!-- 是否为扫描件？ -->
                            <el-radio v-model="radio" label="1">PDF文件</el-radio>
                            <el-radio v-model="radio" label="2">PDF扫描件</el-radio>
                        </div>
                        </Col>
                    </Row>
                    <!-- <Row style="padding: 5px 0;" v-show="true">
            <Col span="1" class="star-tag">
            <span>*</span>
            </Col>
            <Col span="20" class="description-area">

            <Input v-model="value1" maxlength="140" type="textarea" placeholder="请输入文档的描述信息"
                :autosize="{ minRows: 2, maxRows: 5 }" />
            </Col>
        </Row> -->

                    <Row style="margin-top: 30px;">
                        <Col span="1" class="star-tag">

                        </Col>
                        <Col>
                        <div class="upload-button"
                            style="width: 180px; height: 45px; border: 2px solid #000;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    background: #65c3f2;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    box-shadow: 0 0 4px 0 rgba(129,100,0,0.3);
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    border-radius: 8px;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    display: flex;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    justify-content: center;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    "
                            @click="uploadFile">
                            <div style="padding: 5px; line-height: 45px;">
                                <!-- <img :src="buttonSrc" width="24px" height="28px" alt="pic" /> -->
                            </div>

                            <span style="line-height: 45px; color: #000; font-size: 16px; font-weight: 600;">点我上传文档</span>
                        </div>
                        </Col>
                    </Row>
                </div>

            </el-tab-pane>
            <el-tab-pane label="批量导入文档" name="tab_second" width="100%">
                <div width="100%">

                    <el-upload class="upload-demo" drag action="#" multiple ref="upload" :file-list="files"
                        :on-progress="handleUpload" :http-request="handleUpload" :on-exceed='handExceed'
                        :on-remove="handleRemove1" :on-success='handFileSuccess' :before-remove="beforeRemove"
                        :auto-upload="true" :limit="5">
                        <i class="el-icon-upload"></i>
                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                        <div class="el-upload__tip" slot="tip">一次只能上传5个文件</div>
                    </el-upload>

                    <span slot="footer" class="dialog-footer">
                        <el-button @click="CancelUpload">取 消</el-button>
                        <el-button type="primary" @click="fileChange">立即上传</el-button>
                    </span>

                </div>

            </el-tab-pane>
            <el-tab-pane label="导入图片生成扫描件" name="tab_third" width="100%">
                <!-- <el-upload ref="upload" :action="actionUrl" :auto-upload="false" list-type="picture-card"
                    :http-request="uploadImage" accept="image/png" multiple :limit="9">
                    <i class="el-icon-plus"></i>
                </el-upload> -->
                <el-upload action="#" ref="upload" list-type="picture-card" accept="image/png"
                    :on-preview="handlePictureCardPreview" :on-remove="handleRemove2" :http-request="uploadAvatar"
                    :before-upload="beforeAvatarUpload" :file-list="fileList">
                    <!-- :action="baseUrl" -->
                    <img v-if="imageUrl" :src="imageUrl" class="avatar" />
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
                <!-- <el-dialog :visible.sync="dialogVisible" append-to-body>
                    <img width="100%" :src="dialogImageUrl" alt="">
                </el-dialog> -->

                <div>
                    <el-input v-model="imagefile" placeholder="请输入文件名" style="width:20%;"></el-input>
                </div>

                <el-button type="primary" class="btn btn-primary btn-sm submit-btn" @click="submitUpload">生成</el-button>
            </el-tab-pane>
        </el-tabs>

    </div>
</template>

<script>
import { BackendUrl } from '@/api/request'
import CategoryRequest from "@/api/category";
import DocumentRequest from "@/api/document"

import axios from "axios";

export default {
    name: "DocUpload",
    data() {
        return {
            placeholder: "输入一些内容",
            buttonSrc: require("@/assets/source/fileupload.png"),
            actionUrl: BackendUrl() + "/files/upload",
            actionUrl_multi: BackendUrl() + "/files/uploadMultiFile",
            filename: '',
            uploadProcess: 0.00,
            count: [],
            num: 0,
            processFlag: false,
            uploadParam: {},
            categoryOption: [],
            checkedCategory: { id: "ALL", name: "全部分类" },
            categoryType: 'CATEGORY',
            items: [],
            item: '',
            radio: '',
            //回显附件列表
            fileList: [],
            //上传附件列表
            files: [],
            files_new: [],
            formData: {},
            imagefile: '',
            imageUrl: "",
            dialogImageUrl: "",
            form: [],
            dialogVisible: '',
        }

    },
    mounted() {
        this.getAllItems()
    },
    filters: {
        processToStr(uploadProcess) {
            let width = "" + uploadProcess * 100 + "%";
            return { 'width': width }
        }
    },
    methods: {
        switchCategory(param) {
            this.checkedCategory = this.categoryOption.find(item => item.id === param)
            if (param === 'ALL') {
                param = ''
            }
            this.$emit("changeCate", param)
        },
        uploadDialogShow() {
            this.$refs.fileToUpload.dispatchEvent(new MouseEvent("click"));
        },
        changeFile() {
            const inputFile = this.$refs.fileToUpload.files[0];
            console.log(typeof inputFile)
            let filename = inputFile.name;
            // 此处应向后台请求 后台保存上传文件名称返回fileId作为文件标识
            this.uploadParam = {
                fileId: filename,
                file: inputFile,
                file_choice: this.radio
            };
            this.filename = filename
            this.processFlag = true
            this.uploadProcess = 0
        },
        // 最后上传
        uploadFile() {
            let param = this.uploadParam

            if (param === {} || param.file === undefined || param.fileId === undefined) {
                this.info(false)
                return;
            };
            let formData = new FormData();
            formData.set("fileName", param.fileId);
            formData.set("file", param.file);
            formData.set("fileChoice", param.file_choice)
            formData.set("labels", this.items)
            formData.set("userid", localStorage.getItem("id"))
            formData.set("username", localStorage.getItem("username"))
            const config = {
                // headers: {},
                onUploadProgress: (progressEvent) => {
                    // progressEvent.loaded:已上传文件大小
                    // progressEvent.total:被上传文件的总大小
                    this.uploadProcess = Number(
                        ((progressEvent.loaded / progressEvent.total) * 0.9).toFixed(2)
                    );
                },
            };

            // 添加用户信息
            //   config.headers.authorization = localStorage.getItem("token");
            //   config.headers.id = localStorage.getItem("id")
            //   config.headers.username = localStorage.getItem("username")
            //   console.log("config.headers.id",config.headers.id)
            // console.log("localStorage.getItem(id)",localStorage.getItem("id"))

            this.progressFlag = true;
            console.log(formData)
            axios.post(this.actionUrl, formData, config).then(res => {
                let { data } = res
                if (data['code'] === 200 || data['code'] === 'success') {
                    this.uploadProcess = 1;
                    this.$Message.success("成功！")
                    console.log(res)
                } else {
                    this.$Message.error("出错：" + data['message'])
                    this.uploadProcess = 0.00
                }

                setTimeout(() => {
                    this.processFlag = false;
                    this.filename = ''
                }, 1000)
            }).catch(err => {
                this.$Message.error("上传出错！")
                this.processFlag = false
                this.uploadProcess = 0.0
            })
            // 无论是否成功都过滤掉
            this.uploadParam = {}
        },

        handleAdd() {
            this.items[this.num] = this.item
            this.num = this.num + 1;
            this.item = ''
            console.log(this.items)
            console.log(this.num)
        },
        handleClose2(event, name) {
            const index = this.items.indexOf(name);
            this.items.splice(index, 1);
            this.num = this.num - 1
        },
        getAllItems() {
            this.loading = true
            const params = {
                type: this.categoryType
            };
            CategoryRequest.getListData(params).then(response => {
                this.loading = false;
                if (response.code !== 200) {
                    return;
                }
                this.listLoading = false
                this.categoryOption = [{ id: "ALL", name: "全部分类", seeName: '全部分类', createDate: '', updateDate: '' }]
                if (response.data.length > 0) {
                    response.data.forEach(item => {
                        if (item.name.length > 8) {
                            item['seeName'] = item.name.slice(0, 8) + "..."
                        } else {
                            item['seeName'] = item.name
                        }
                        this.categoryOption.push(item)
                    })
                }
            })
        },
        info(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : '请上传文件'
            });
        },
        info1(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : '请选择分类'
            });
        },
        info2(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : '请选择pdf的格式'
            });
        },
        info3(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : '上传失败'
            });
        },

        handleUpload(fileObject) {
            let fd = new FormData();
            this.files.push(fileObject.file)
            console.log("files:", this.files);
        },
        async fileChange() {
            if (this.files.length > 5) {
                this.$message.warning(`当前限制只能上传选择 1~5 个文件`);
                return
            } else {
                const loading = this.$loading({
                    lock: true,
                    text: '上传中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                this.$refs.upload.submit() // 这里是执行文件上传的函数，其实也就是获取我们要上传的文件
                let random = Math.random();
                let formData = new FormData();
                for (let i = 0; i < this.files.length; i++) {
                    formData.append("files", this.files[i])
                }
                formData.set("userid", localStorage.getItem("id"))
                formData.set("username", localStorage.getItem("username"))

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
                console.log(this.files)
                axios.post(this.actionUrl_multi, formData, config).then(res => {
                    let { data } = res
                    if (data['code'] === 200 || data['code'] === 'success') {
                        this.uploadProcess = 1;
                        this.$Message.success("成功！")
                        console.log(res)
                        this.$refs.upload.clearFiles();
                        this.files = []
                        loading.close();

                    } else {
                        this.$Message.error("出错：" + data['message'])
                        this.uploadProcess = 0.00
                        loading.close();
                        console.log(res)
                        this.$message.error("上传文件失败" + res.data.msg);
                    }
                    setTimeout(() => {
                        this.filename = ''
                    }, 1000)
                }).catch(err => {
                    this.$Message.error("上传出错！")
                    this.processFlag = false
                    this.uploadProcess = 0.0
                })
                //console.log(res);
                // JSON.parse(res)
            }

        },
        handExceed(files, fileList) {
            this.$message.warning(`当前限制选择 5 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
        },
        CancelUpload() {
            this.files = []
            this.$refs.upload.clearFiles();
        },
        handFileSuccess(file) {
            console.log(file);
            this.files = []
            this.$refs.upload.clearFiles();
        },
        handleRemove1(file, fileList) {
            console.log(file, fileList);
            this.files = fileList
        },
        beforeRemove(file, fileList) {
            // console.log(file, fileList);
        },
        uploadImage(file) {
            this.formData.append("blogimg", file.file);
            console.log(file);
        },
        // 点击按钮触发
        async submitUpload() {



            let formData = new FormData();
            if (this.imagefile != "") {

                formData.set("filename", this.imagefile);

                for (let i = 0; i < this.form.length; i++) {
                    formData.append('imageList', this.form[i]);
                }

                DocumentRequest.getImageData(formData).then(res => {
                    if (res.code === 200) {
                        console.log("data:", res.data)
                        this.$refs.upload.clearFiles();
                        this.$message.success("发布成功！");
                    } else {
                        this.info3(false)
                    }
                })
            }
            else {
                this.$message.error("情输入文件名");
            }

        },
        // 图片上传功能
        uploadAvatar(item) {
            console.log(item.file)
            this.form.push(item.file)
        },
        beforeAvatarUpload(file) {
            const isPng = file.type === 'image/png'
            const isLt2M = file.size / 1024 / 1024 < 2

            if (!isPng) {
                this.$message.error('上传图片只能是 JPG或png 格式!')
            }
            if (!isLt2M) {
                this.$message.error('上传图片大小不能超过 2MB!')
            }
            return (isPng) && isLt2M
        },
        handleRemove2(file, fileList) {
            console.log(this.form.length)
            console.log(this.form)
            console.log(file)
            for (let i = 0; i < this.form.length; i++) {
                if (this.form[i].uid === file.uid) {
                    console.log(i, "tt")
                    this.form.splice(i, 1)
                }
                console.log(this.form, "434")
            }
        },
        handlePictureCardPreview(file) {
            this.dialogImageUrl = file.url
            this.dialogVisible = true
        },
        /**
         * 清空上传组件
         */
        emptyUpload() {
            const mainImg = this.$refs.upload
            if (mainImg) {
                if (mainImg.length) {
                    mainImg.forEach(item => {
                        item.clearFiles()
                    })
                } else {
                    this.$refs.upload.clearFiles()
                }
            }
        },


    }
}
</script>

<style scoped lang="scss">
.main-container {
    padding: 30px;

    .upload-title {
        width: 96px;
        height: 33px;
        font-size: 24px;
        font-family: PingFangSC-Medium, PingFang SC, serif;
        font-weight: 500;
        color: #000000;
        line-height: 33px;

        margin-bottom: 30px;
    }

    .upload-panel {
        width: 300px;
        height: 190px;
        background: #FFFFFF;
        border-radius: 8px;
        border: 2px solid #000000;
        padding: 4px 0;
        position: relative;
        overflow: hidden;
        display: inline-block;
        text-align: center;

        &:hover {
            cursor: pointer;
            border: 2px dashed #000000;
        }

    }

    .file-title {
        width: 100%;
        min-height: 21px;
        font-size: 14px;

        &:hover {
            cursor: pointer;
            background-color: #FFFAE4;
        }

    }

    .progress-wrapper {
        width: 100%;
        height: 3px;
        //position: relative;
        background-color: #eeeeee;
        position: absolute;

        .pro {
            width: 40%;
            height: 100%;
            background-image: linear-gradient(45deg, rgba(255, 255, 255, 0.15) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, 0.15) 50%, rgba(255, 255, 255, 0.15) 75%, transparent 75%, transparent);
            background-color: #00B83F;
            background-size: 40px 40px;
            animation: progressbar 2s linear infinite;
        }

        @keyframes progressbar {
            0% {
                background-position: 40px 0
            }

            100% {
                background-position: 0 0
            }
        }
    }

    .cate-dropdown {
        text-align: center;
        width: 35%;
        height: 45px;
        background: #FFFFFF;
        border-radius: 8px;
        border: 1px solid #000000;
        //margin-left: 10px;

        font-size: 14px;
        font-family: PingFangSC-Medium, PingFang SC, serif;
        font-weight: 500;
        color: #000000;
        line-height: 45px;
    }

    .booll {
        text-align: center;
        width: 60%;
        height: 45px;
        background: #FFFFFF;
        border-radius: 8px;
        //border: 1px solid #000000;
        //margin-left: 10px;

        font-size: 14px;
        font-family: PingFangSC-Medium, PingFang SC, serif;
        font-weight: 500;
        color: #000000;
        line-height: 45px;
    }

    .search-input-top {

        width: 60%;
        height: 45px;
        background: #FFFFFF;
        border-radius: 8px;
        border: 1px solid #000000;
        padding: 8px;
        //margin: 10px 0;

        display: flex;
        justify-content: flex-start;
        align-content: center;

        input {
            height: 30px;
            width: 10%;
            text-decoration: none;
            outline: none;
            border: none;
            border-radius: 8px;
            //border: 1px solid #000000;
        }
    }

    .description-area {
        /deep/ .ivu-input {
            border: 1px solid #000000;
        }
    }

    .star-tag {
        padding-right: 10px;
        text-align: right;
        color: red;
    }
}

/deep/ .ivu-upload-drag {
    border: none;
}

.upload-button {
    &:hover {
        cursor: pointer;
        padding: 1px 0 0 1px;
    }
}
</style>