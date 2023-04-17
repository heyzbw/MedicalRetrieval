<template>
    <div>
        <div class="nav">
            <Nav></Nav>
        </div>
        <div class="main-container">
            <div class="title-info">
                <div class="doc-trim" style="text-align: center">
                    <img :src="thumbId | imgSrc" alt="thumb"
                        style="width: 36px;max-height: 48px;border: 1px solid #dcdee2; border-radius: 2px">
                </div>
                <div class="doc-info">
                    <div class="doc-info-title">
                        {{ title }}
                    </div>
                    <div class="doc-info-tag">
                        <Tag :color="item.color" v-for="item in tags" :index="item.index">{{ item.name }}</Tag>
                    </div>
                    <div class="doc-info-detail">
                        <Icon type="md-person" />
                        {{ userName }}
                        <Icon type="md-time" />
                        {{ createTime }}
                        <Icon type="md-thumbs-up" />
                        {{ likeCount }}
                        <Icon type="md-heart" />
                        {{ collectCount }}
                    </div>

                </div>
            </div>
            <div class="doc-preview">
                <component :is="component" v-if="component" :previewId="previewId" :keyword="keyword" :pageNum="pageNum" />
            </div>
            <!-- <FloatBall @click="changeshow"></FloatBall> -->
            <div>
                <div class="doc-operation-body">
                    <doc-operation :likeStatus="likeStatus" :collectStatus="collectStatus"
                                   @addLike="addLike"  @addCollect="addCollect" />
                </div>
                <div class="doc-comment">
                    <comment-page />
                </div>
            </div>
        </div>

    </div>
</template>

<script>

// import PdfView from "./PngView"
import { BackendUrl } from '@/api/request'
import md5 from 'md5';
import Nav from "@/components/Nav"
import DocRequest from "@/api/document"
import { parseTime } from "@/utils/index"
import fileTool from "@/utils/fileUtil"
import DocOperation from "./docOperation"
import FloatBall from '@/components/FloatBall';

import CommentPage from "./CommentPage"
import DocumentRequest from "@/api/document"
import axios from 'axios';

export default {
    data() {
        return {
            title: "",
            userName: "",
            docId: "",
            tags: [],
            descriptions: "",
            sieze: "",
            docState: "",
            createTime: new Date(),
            thumbId: "",
            component: null,
            txtID: "",
            tagColor: ['orange', 'gold', 'lime', 'cyan', 'blue', 'geekblue', 'magenta'],
            selectText: "",
            collectCount: 0,
            likeCount: 0,
            likeStatus: 0,
            collectStatus: 0,
            previewId: null,
            keyword: "",
            infoVisible: false,
            showtab: true,
            translateResult: '',
            pageNum: '',
            left: 0,
            top: 40,
            bg: 1,
            menu: false,
            isLoading: false,
            flags: false, //控制使用
            position: {
                x: 0,
                y: 0,
            },
            nx: "",
            ny: "",
            dx: "",
            dy: "",
            xPum: "",
            yPum: "",
            movb: 1,
            num: 1,
        }
    },
    components: {
        Nav, DocOperation, CommentPage, FloatBall
    },
    mounted() {
        this.getSelectText();
        this.getMessage();
        document.addEventListener('mouseup', () => {
            const selection = window.getSelection();

            let iframe = document.getElementById('myIframe');


            iframe.onload = function () {
                setTimeout(() => {
                    console.log(iframe.contentWindow.getSelection().toString())
                    let iframe = document.getElementById('myIframe');
                    this.selectText = iframe.contentWindow.getSelection().toString();
                    console.log(iframe.contentWindow)
                }, 100);
            }
            if (selection.toString().length > 0) {
                this.selectedText = selection.toString();
            }
        });
        this.left = this.$refs.fu.offsetLeft - 150;
        this.top = this.$refs.fu.offsetTop
    },
    filters: {
        imgSrc(value) {
            if (value === "" || value == null) {
                return require('@/assets/source/doc.png');
            } else {
                return BackendUrl() + "/files/image2/" + value;
            }
        }
    },
    created() {
        this.init()
        this.getLikeInfo();
    },
    methods: {
        init() {
            this.docId = this.$route.query.docId;
            this.keyword = this.$route.query.keyword
            this.pageNum = this.$route.query.pageNum
            // let
            var params = {
                docId: this.docId,
                userId: localStorage.getItem("id")
            }
            DocRequest.getData(params).then(response => {
                if (response.code === 200) {
                    this.title = response.data.title;
                    this.userName = response.data.userName;
                    console.log(response.data);
                    this.size = fileTool.bytesToSize(response.data.size)
                    this.thumbId = response.data.thumbId;
                    var docTime = response.data.createTime;
                    this.descriptions = response.data.description
                    this.docState = response.data.docState
                    this.txtID = response.data.txtId
                    this.createTime = parseTime(new Date(docTime), '{y}年{m}月{d}日 {h}:{i}:{s}');

                    let tagList = response.data['tagVOList'];
                    this.tags = this.renderTags(tagList);

                    let title = response.data.title

                    this.previewId = response.data.previewFileId
                    // console.log("keyword" + { keyword })
                    let suffix = title.split(".")[title.split('.').length - 1];
                    switch (suffix) {
                        case 'pdf':
                            this.component = () => import('@/views/preview/PdfView')
                            break
                        case 'png':
                        case 'jpg':
                        case 'jpeg':
                            this.component = () => import('@/views/preview/PngView')
                            break
                        case 'html':
                        case 'txt':
                            this.component = () => import('@/views/preview/HtmlView')
                            break
                        case 'docx':
                        case 'doc':
                            this.component = () => import('@/views/preview/WordView3')
                            break
                        case 'pptx':
                            // this.component = () => import('@/views/preview/PPTxView')
                            this.component = () => import('@/views/preview/PptxView2')
                            break
                        case 'xlsx':
                            this.component = () => import('@/views/preview/excel2')
                            break
                        case 'md':
                            this.component = () => import('@/views/preview/mdView')
                            break
                        default:
                            this.component = () => import('@/views/preview/ErrorView')
                            break
                    }
                }
            })
        },

        renderTags(tags) {
            tags.forEach((item, index) => {
                item['index'] = index;
                item['color'] = this.tagColor[parseInt(Math.random() * this.tagColor.length)];
            })
            return tags;
        },

        async getLikeInfo() {
            let param = {
                entityId: this.docId
            }
            await DocRequest.getLikeInfo(param).then(res => {
                if (res.code == 200) {
                    let result = res.data;
                    this.collectCount = result.collectCount || 0;
                    this.likeCount = result.likeCount || 0;
                    this.likeStatus = result.likeStatus || 0;
                    this.collectStatus = result.collectStatus || 0
                } else {
                    // this.$Message.info("error")
                }
            }).catch(err => {
                // this.$Message.info("error")
            })
        },
        async addLike(entityType) {
            if (entityType !== 1 && entityType !== 2) {
                return
            }
            console.log("entityType" + entityType)
            let params = {
                entityType: entityType,
                entityId: this.docId
            }
            await DocRequest.addLike({ params }).then(res => {
                if (res.code == 200) {
                    let result = res.data;
                    if (entityType === 1) {
                        this.likeCount = result.likeCount || 0;
                        this.likeStatus = result.likeStatus || 0;
                        if (this.likeStatus === 0) {
                            this.$Message.info("取消点赞！")
                        } else {
                            this.$Message.success("点赞成功！")
                        }
                    } else {
                        this.collectCount = result.likeCount || 0;
                        this.collectStatus = result.likeStatus || 0;
                        if (this.collectStatus === 0) {
                            this.$Message.info("取消收藏！")
                        } else {
                            this.$Message.success("收藏成功！")
                        }
                    }
                } else {
                    this.$Message.info("error")
                }
            }).catch(err => {
                this.$Message.info("error")
            })
        },

        async addCollect(entityType) {
          console.log("发出收藏事件")
          if (entityType !== 1) {
            return
          }
          let params = {
            docId: this.docId
          }
          await DocRequest.addCollect({ params }).then(res => {
            if (res.code == 200) {
              let result = res.data;
              console.log("res data:", res.data)
              if (result === "SUCCESS_REMOVE_COLLECT") {
                this.$Message.info("取消收藏！")
                this.collectStatus = false
              } else {
                this.$Message.success("收藏成功！")
                this.collectStatus = true
              }
            } else {
              this.$Message.info("error")
            }
          }).catch(err => {
            this.$Message.info("error")
          })
        },

        downloadTxt(doc) {
            let fileId = this.txtID
            if (fileId === null || fileId === '') {
                return;
            }
            DocumentRequest.getTxtFile(fileId).then(res => {
                const dom = document.createElement('a');
                dom.href = URL.createObjectURL(res);
                dom.download = this.title + '.txt';
                dom.click();
            }).catch(error => {
                console.log(error)
            })
        },
        /**
         * 对文档进行重新建立索引
         * @param doc
         */
        rebuildIndex(doc) {
            let docId = doc['id']
            if (docId === null || docId === '') {
                return;
            }
            DocumentRequest.getRebuildIndex({ docId: docId }).then(res => {
                if (res.code === 200) {
                    this.action_modal = false
                } else {
                    this.$Message.error("重建失败，请检查！")
                }
                this.getListData(this.cateId, this.filterWord)
            })
        },
        getSelectText() {
            let _this = this;
            let iframe = document.getElementById('myIframe');
            let x = '';
            let y = '';
            let _x = '';
            let _y = '';
            // iframe 加载完成后执行并将双击事件过滤掉，因为双击事件可能也触发滑选，所以为了不误操作，将其剔除
            iframe.onload = function () {
                // 鼠标点击监听
                setTimeout(() => {
                    console.log("qwe" + iframe)
                    iframe.contentDocument.addEventListener('mousedown', function (e) {
                        x = e.pageX;
                        y = e.pageY;
                    }, true);
                    // 鼠标抬起监听
                    iframe.contentDocument.addEventListener('mouseup', function (e) {
                        _x = e.pageX;
                        _y = e.pageY;
                        if (x == _x && y == _y) return; //判断点击和抬起的位置，如果相同，则视为没有滑选，不支持双击选中
                        var choose = iframe.contentWindow.getSelection().toString();
                        _this.selectText = choose;
                    }, true);
                }, 100);
            };
        },
        sendMessage() {
            let vm = this;
            //获取iframe
            let iframe = document.getElementById('myIframe');
            //将滑选数据传入到iframe中
            iframe.contentWindow.postMessage(vm.selectText, '*');
        },
        changeshow() {
            console.log(value)
            this.showtab = !this.showtab
        },
        translateText() {
            const salt = Date.now();
            const appid = '20230312001596883';
            const key = '75c2j7YXfCFdiwiZO_0b';
            const from = 'auto';
            const to = 'zh';
            const sign = md5(`${appid}${this.selectText}${salt}${key}`);
            // setTimeout(
            //     () => {
            axios.get('/api/trans/vip/translate', {
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                params: {
                    q: this.selectText,
                    from: from,
                    to: to,
                    appid: appid,
                    salt: salt,
                    sign: sign
                }

            }).then(response => {
                // 处理响应
                console.log(response.data.trans_result[0].dst);
                this.translateResult = response.data.trans_result[0].dst;
            }).catch(error => {
                // 处理错误
                console.log(error);
                console.log(sign);
                console.log(this.selectText);
                // console.log(params);
            }, 100)

            // });
        },
        out2() {
            this.menu = false;
        },
        over2() { },
        out() {
            this.bg = 2;
        },
        over() {
            this.menu = true;
            this.num = 2;
            this.bg = 1;
        },
        callback() {
            this.$router.go(-1);
        },
        onRefresh() {
            // window.location.reload();
            setTimeout((res) => {
                console.log(res);
                this.isLoading = false;
            }, 1000);
        },
        down() {
            this.flags = true;
            var touch;
            if (event.touches) {
                touch = event.touches[0];
            } else {
                touch = event;
            }
            this.position.x = touch.clientX;
            this.position.y = touch.clientY;
            this.dx = this.$refs.fu.offsetLeft;
            this.dy = this.$refs.fu.offsetTop;
        },
        move() {
            if (this.flags) {
                this.movb = 2;
                this.menu = false;
                var touch;
                if (event.touches) {
                    touch = event.touches[0];
                } else {
                    touch = event;
                }
                this.nx = touch.clientX - this.position.x;
                this.ny = touch.clientY - this.position.y;
                this.xPum = this.dx + this.nx;
                this.yPum = this.dy + this.ny;
                let width = window.innerWidth - this.$refs.fu.offsetWidth; //屏幕宽度减去自身控件宽度
                let height = window.innerHeight - this.$refs.fu.offsetHeight; //屏幕高度减去自身控件高度
                this.xPum < 0 && (this.xPum = 0);
                this.yPum < 0 && (this.yPum = 0);
                this.xPum > width && (this.xPum = width);
                this.yPum > height && (this.yPum = height);
                // if (this.xPum >= 0 && this.yPum >= 0 && this.xPum<= width &&this.yPum<= height) {
                this.$refs.fu.style.left = this.xPum + "px";
                this.$refs.fu.style.top = this.yPum + "px";
                this.left = this.xPum - 280;
                this.top = this.yPum + 20;
                // }
                //阻止页面的滑动默认事件
                document.addEventListener(
                    "touchmove",
                    function () {
                        event.preventDefault();
                    },
                    false
                );
            }
        },
        //鼠标释放时候的函数
        end() {
            this.flags = false;
        },
        onClick() {
            if (this.movb == 2) {
                this.movb = 1;
            } else {
                this.menu = !this.menu;
            }
            // this.$emit("click");
        },
    }

}
</script>

<style lang="scss" scoped>
.nav {
    background-color: #f1f0f0;
    width: 100%;
    height: 50px;
    //position: absolute;
    //left: 0;
    //top: 0;
}


.main-container {
    width: 90%;
    //height: 100vh;

    padding: 25px;
    margin: auto;
    box-sizing: content-box;

    .title-info {
        height: 125px;
        width: 100%;
        box-shadow: 0px 0px 5px 0px rgba(64, 64, 64, 0.3000);
        border-radius: 8px;
        background-color: #fffeff;
        padding: 12px;
        display: block;

        .doc-trim {
            float: left;
            width: 40px;
            //background-color: lightblue;
            height: 20px;
            line-height: 40px;
            display: block;
        }

        .doc-info {
            display: block;
            width: calc(100% - 40px);
            float: left;
            padding: 0 0px;
            text-align: left;

            .doc-info-title {
                font-size: 14px;
                font-family: PingFangSC-Semibold, PingFang SC;
                font-weight: 200;
                color: #000000;
                line-height: 20px;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
            }

            .doc-info-tag {
                height: 30px;
                line-height: 40px;
                display: flex;
                align-items: flex-start;
                padding-top: 3px;
            }

            .doc-info-detail {
                height: 40px;
                font-size: 14px;
                font-family: PingFangSC-Regular, PingFang SC;
                font-weight: 400;
                color: #000000;
                line-height: 20px;
            }

        }
    }

    .doc-preview {
        margin: 15px 0;
        //overflow-y: auto;
        height: 70%;
        padding: 5px 0;
        box-shadow: 0px 0px 5px 0px rgba(64, 64, 64, 0.3000);
        border-radius: 8px;
        background-color: #fffeff;
    }

    .doc-operation-body {
        height: 150px;
        //line-height: 200px;
        text-align: center;
        box-shadow: 0px 0px 5px 0px rgba(64, 64, 64, 0.3000);
        border-radius: 8px;
        background-color: #fffeff;
    }

    .doc-comment {

        margin: 20px 0;

        background-color: #69bef3;
        min-height: 120px;

        box-shadow: 0px 0px 5px 0px rgba(64, 64, 64, 0.3000);
        border-radius: 8px;
        background-color: #fffeff;

        padding: 36px 40px;
    }
}


.bg1 {
    background-image: url("../../assets/logocopy.png");
}

.bg2 {
    background-image: url("../../assets/logocopy.png");
}

.callback p {
    font-size: 16px;
    color: #fff;
    background: rgba(56, 57, 58, 0.5);
    border-radius: 50%;
    text-align: center;
    width: 50px;
    height: 50px;
    line-height: 50px;
    font-family: PingFang SC;
    font-weight: 600;
    box-shadow: 0 0 10px #fff;
}

.callback img {
    display: block;
    width: 50px;
    height: 50px;
    box-shadow: 0 0 10px rgb(133, 129, 129);
    border-radius: 50%;
    background: #fff;
}

.callback {
    position: fixed;
    width: 80px;
    height: 80px;
    background-repeat: no-repeat;
    background-size: 100% 100%;
    top: 40px;
    left: 94%;
    z-index: 99999;
}

.float {
    position: fixed;
    touch-action: none;
    text-align: center;
    border-radius: 24px;
    line-height: 48px;
    color: white;
}

.menuclass {
    position: fixed;
    width: 80px;
    height: 80px;
    background-repeat: no-repeat;
    background-size: 100% 100%;
    top: 40px;
    right: 94%;
    z-index: 99999;
}

.sssss {
    position: relative;
    background-color: #000;
    right: 0;
    z-index: 99999;
}
</style>