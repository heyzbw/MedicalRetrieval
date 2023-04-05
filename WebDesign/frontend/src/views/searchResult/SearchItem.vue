<template>
    <div class="search-doc">
        <div class="doc-title-group">
            <div class="doc-pic" style="text-align: center;">
                <img :src="thumbId | imgSrc" alt="thumb"
                    style="width: 36px;max-height: 48px;border: 1px solid #dcdee2; border-radius: 2px">
            </div>
            <div class="title-group">
                <div class="doc-title-info">
                    {{ title }}

                </div>

                <div class="description">
                    <div class="description-item">
                        {{ timeIn }}
                    </div>
                    <div class="description-item">
                        {{ userName }}
                    </div>
                    <div class="description-item" v-show="categoryIn">
                        {{ categoryIn }}
                    </div>
                    <Tag color="blue" v-for="item in tagsIn">{{ item }}</Tag>
                </div>
            </div>
            <el-tooltip class="item" effect="dark" placement="bottom">
                <div slot="content">{{ this.tooltips }} </div>
                <div class="defen">得分:{{ this.score }}</div>
            </el-tooltip>

        </div>
        <div class="doc-abstract" v-show="ocrResultListin">
            <div style="padding:0 0 0 30px">
                <!-- <div @click="getDocView()" v-if="contentResultSize>=1">【第{{ esSearchContentList[0].pageNum }}页】 <div style="color:blue">来源于文本</div>
                    <p v-html="esSearchContentList[0].contentHighLight[0]"></p>
                    <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                </div>

                <div @click="getDocView1()" v-if="contentResultSize>=2">【第{{ esSearchContentList[1].pageNum }}页】 <div style="color:blue">来源于文本</div>
                    <p v-html="esSearchContentList[1].contentHighLight[0]"></p>
                    <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                </div>
                <div v-if="this.contentshow &&  contentResultSize>=3" @click="getDocView2()" >【第{{ esSearchContentList[2].pageNum }}页】 <div style="color:blue">来源于文本</div>
                    <p v-html="esSearchContentList[2].contentHighLight[0]"></p>
                    <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                </div>
                <div v-if="this.ocrshow" @click="getDocView3()">【第{{ ocrResultList[0].pdfPage + 1 }}页】 <div
                        style="color:red">
                        来源于图片</div>
                    <p v-html="ocrResultList[0].ocrText"></p>
                    <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                </div> -->
                <el-tabs type="card" width="100%" style="height: 100%" v-model="activeTab">
                    <el-tab-pane label="来源于文本" name="first" width="100%">
                        <div @click="getDocView()" v-if="contentResultSize >= 1">【第{{ esSearchContentList[0].pageNum }}页】
                            <div style="color:blue">来源于文本</div>
                            <p v-html="esSearchContentList[0].contentHighLight[0]"></p>
                            <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                        </div>

                        <div @click="getDocView1()" v-if="contentResultSize >= 2">【第{{ esSearchContentList[1].pageNum }}页】
                            <div style="color:blue">来源于文本</div>
                            <p v-html="esSearchContentList[1].contentHighLight[0]"></p>
                            <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                        </div>
                        <div v-if="contentResultSize >= 3" @click="getDocView2()">【第{{ esSearchContentList[2].pageNum }}页】
                            <div style="color:blue">来源于文本</div>
                            <p v-html="esSearchContentList[2].contentHighLight[0]"></p>
                            <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                        </div>

                    </el-tab-pane>
                    <el-tab-pane label="来源于同义词" name="tab_second" width="100%" v-if="esSearchContentList_synoSize">
                        <div @click="getsynoView()" v-if="esSearchContentList_synoSize >= 1">【第{{
                            esSearchContentList_syno[0].pageNum }}页】 <div style="color:green">来源于同义词</div>
                            <p v-html="esSearchContentList_syno[0].contentHighLight[0]"></p>
                            <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                        </div>

                        <div @click="getsynoView1()" v-if="esSearchContentList_synoSize >= 2">【第{{
                            esSearchContentList_syno[1].pageNum }}页】
                            <div style="color:green">来源于同义词</div>
                            <p v-html="esSearchContentList_syno[1].contentHighLight[0]"></p>
                            <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                        </div>
                        <div v-if="esSearchContentList_synoSize >= 3" @click="getsynoView2()">【第{{
                            esSearchContentList_syno[2].pageNum }}页】
                            <div style="color:green">来源于同义词</div>
                            <p v-html="esSearchContentList_syno[2].contentHighLight[0]"></p>
                            <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="来源于图片" name="tab_third" width="100%" v-if="ocrResultListSize">
                        <div v-if="ocrResultListSize >= 1" @click="getDocView3()">【第{{ ocrResultList[0].pdfPage + 1 }}页】
                            <div style="color:red">
                                来源于图片</div>
                            <p v-html="ocrResultList[0].ocrText"></p>
                            <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                        </div>
                        <div v-if="ocrResultListSize >= 2" @click="getDocView3()">【第{{ ocrResultList[1].pdfPage + 1 }}页】
                            <div style="color:red">
                                来源于图片</div>
                            <p v-html="ocrResultList[1].ocrText"></p>
                            <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                        </div>
                        <div v-if="ocrResultListSize >= 3" @click="getDocView3()">【第{{ ocrResultList[2].pdfPage + 1 }}页】
                            <div style="color:red">
                                来源于图片</div>
                            <p v-html="ocrResultList[2].ocrText"></p>
                            <hr style="height:1px;border:none;border-top:1px solid lightgray;">
                        </div>

                    </el-tab-pane>
                </el-tabs>


            </div>
        </div>
        <ul class="ivu-list-item-action">
            <li>
                <i class="ivu-icon ivu-icon-ios-star-outline"></i>
                {{ collectNum }}
            </li>
            <li>
                <i class="ivu-icon ivu-icon-ios-thumbs-up-outline"></i>
                {{ likeNum }}
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

export default {
    name: "SearchItem",
    data() {
        return {
            pageNum: '',
            ocrshow: '',
            contentshow: '',
            ocrNum: '',
            score: this.like_score + this.content_score + this.click_score,
            contentResultSize: 0,
            ocrResultListSize: 0,
            esSearchContentList_synoSize: 0,
            activeTab: 'first',
            tooltips: '',
        }
    },
    props: {
        id: { type: String, requires: true },
        thumbId: { type: String, requires: true },
        title: { type: String, requires: true },
        time: { type: Number, requires: true, default: "232" },
        userName: { type: String, requires: true, default: 'admin' },
        category: { type: Object, requires: false, default: '' },
        tags: { type: Array, requires: false, default: [] },
        collectNum: { type: Number, requires: false, default: 0 },
        commentNum: { type: Number, requires: false, default: 10 },
        likeNum: { type: Number, requires: false, default: 0 },
        keyword: { type: String, requires: true },
        esSearchContentList: { type: Array, requires: false, default: [] },
        esSearchContentList_syno: { type: Array, requires: false, default: [] },
        ocrResultList: { type: Array, requires: false, default: [] },
        like_score: { type: Number, requires: false },
        content_score: { type: Number, requires: false },
        click_score: { type: Number, requires: false },

    },
    // 将 prop 数据转换为本地数据
    created() {
        // console.log("like_score):", this.like_score)
        // console.log("content_score):", this.content_score)
        // console.log("click_score):", this.click_score)
        // console.log("esSearchContentList[0]):", this.esSearchContentList[0])
        // console.log("esSearchContentList[0].pageNum):", this.esSearchContentList[0].pageNum)
        this.score = this.like_score + this.content_score + this.click_score
        let str = Number(this.content_score);
        str = str.toFixed(2)
        this.tooltips = '内容得分' + str + ', ' + '点赞得分' + this.like_score + ', ' + '浏览得分' + this.click_score
        if (this.esSearchContentList !== [] && this.esSearchContentList !== null) {
            this.contentResultSize = this.esSearchContentList.length
        }
        if (this.ocrResultList !== [] && this.ocrResultList !== null) {
            this.ocrResultListSize = this.ocrResultList.length
        }
        if (this.esSearchContentList_syno !== [] && this.esSearchContentList_syno !== null) {
            this.esSearchContentList_synoSize = this.esSearchContentList_syno.length
        }

    },
    computed: {
        categoryIn: function () {
            if (this.category === null || this.category.name === null) {
                return null;
            } else {
                let temp = this.category.name
                if (temp.length > 6) {
                    temp = temp.substring(0, 6) + '...'
                }
                return temp;
            }
        },
        tagsIn: function () {
            if (this.tags === null || this.tags.length === 0) {
                return []
            } else {
                let temp = []
                // console.log(this.stringList)
                this.tags.forEach(item => {
                    let temp1 = item.name
                    if (temp1.length > 8) {
                        temp1 = temp1.substring(0, 8) + '...'
                    }
                    temp.push(temp1)
                })
                return temp
            }
        },
        timeIn: function () {
            return parseTime(new Date(this.time), '{y}年{m}月{d}日 {h}:{i}:{s}');
        },

        esSearchContentListin: function () {
            return this.esSearchContentList
        },
        ocrResultListin: function () {
            // console.log(this.esSearchContentList)
            if (this.ocrResultList === null || this.ocrResultList === 0) {
                this.ocrshow = false
                this.contentshow = true
            } else {
                this.contentshow = false
                this.ocrshow = true
            }
            return true
        },
        getDocView() {
            // console.log(x)
            console.log(typeof (this.esSearchContentList[0].pageNum))
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: this.esSearchContentList[0].pageNum

                }
            })

        },
        getDocView1() {
            console.log(this.esSearchContentList[1].pageNum)
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: this.esSearchContentList[1].pageNum
                }
            })

        },
        getDocView2() {
            // console.log(x)
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: this.esSearchContentList[2].pageNum
                }
            })

        },
        getsynoView() {
            // console.log(x)
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: this.esSearchContentList_syno[0].pageNum
                }
            })
        },
        getsynoView1() {
            // console.log(x)
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: this.esSearchContentList_syno[1].pageNum
                }
            })
        },
        getsynoView2() {
            // console.log(x)
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: this.esSearchContentList_syno[2].pageNum
                }
            })
        },
        getpicView() {
            // console.log(this.ocrNum)
            // console.log(typeof (this.ocrResultList[0].pdfPage))
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: this.ocrResultList[0].pdfPage + 1
                }
            })

        },
        getpicView1() {
            // console.log(this.ocrNum)
            // console.log(typeof (this.ocrResultList[0].pdfPage))
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: this.ocrResultList[1].pdfPage + 1
                }
            })

        },
        getpicView2() {
            // console.log(this.ocrNum)
            // console.log(typeof (this.ocrResultList[0].pdfPage))
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: this.ocrResultList[2].pdfPage + 1
                }
            })

        },
    },
    filters: {
        imgSrc(value) {
            if (value === "" || value === undefined || value == null) {
                return BackendUrl() + "/files/image2/d2d9933cf295443990b2bed036a534ec";
            } else {
                return BackendUrl() + "/files/image2/" + value;
            }
        }
    }
}
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

.defen {
    display: inline-block;
    height: 22px;
    float: right;
}

.doc-title-info:hover {
    text-decoration: underline;
    color: rgba(208, 164, 1, 100);
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

ul {
    margin: 16px 0 0;
}

li {
    padding: 0 20px;
}

.doc-abstract>>>em {
    background-color: yellow;
}

.doc-abstract>>>bm {
    background-color: rgb(49, 246, 49);
}

.sl-abstract {
    margin: 2px 0 0 0;
}
</style>