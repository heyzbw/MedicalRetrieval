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

              <div class="button-group">
                <el-button type="primary" @click="dialogVisible = true">诊断和治疗意见</el-button>
                <el-button type="primary" @click="getPicView()">阅读文献</el-button>
              </div>
            </div>

<!--            <div slot="content" style="font-size: 15px;">{{ this.tooltips }} </div>-->
            <div class="defen">置信度为（最高100）:{{ this.score.toFixed(3) }}</div>

        </div>

      <!-- 医疗意见弹窗 -->
      <el-dialog title="诊断和治疗意见" :visible.sync="dialogVisible">
        <ul>
          <li v-for="(opinion, index) in medicalOpinions" :key="index">{{ opinion }}</li>
        </ul>
        <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">关闭</el-button>
            </span>
      </el-dialog>

    </div>
</template>

<script>

import { parseTime } from "@/utils/index"
import { BackendUrl } from '@/api/request'

import HighlightRect from '@/components/HighlightRect.vue'

export default {
    name: "PredictCCaseItem",
    components: {
        HighlightRect
    },
    data() {
        return {

            dialogVisible: false,  // 控制医疗意见弹窗的显示和隐藏
            // medicalOpinions: [
            //   '血糖监测：在血液透析过程中，由于采用无糖透析液，患者容易出现低血糖现象。护理人员应密切监测患者的血糖水平，并在透析过程中观察患者是否出现低血糖症状，如有异常应及时报告医生处理。',
            //   '心理护理：长期血液透析治疗可能导致患者产生焦虑、恐惧和沮丧等不良情绪。护理人员应主动与患者交流，帮助患者了解血液透析的目的、注意事项和疗效，以提高患者战胜疾病的信心。',
            //   '血管通路护理：保护好血管通路对透析治疗十分重要。护理人员应熟练掌握血管走行、弹性、深浅，并注意观察患者血管通路的状况。对于动静脉内瘘护理和静脉留置导管护理，护理人员应严格执行无菌操作，并密切监测患者情况。'],  // 医疗意见列表，你可以根据实际情况修改
            pageNum: '',
            ocrshow: '',
            contentshow: '',
            ocrNum: '',
            score: this.like_score + this.content_score + this.click_score,
            contentResultSize: 0,
            ocrResultListSize: 0,
            esSearchContentList_synoSize: 0,
            activeTab: 'first',
            tooltips: '不知道',
            filterWordLen:"",

        }
    },
    methods: {
        getDocView(item) {
            console.log(this.id)
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: item.pageNum,
                    score: this.score,
                    tooltips: this.tooltips

                }
            });
        },
        // ...其他方法...
        getSynoView(item) {
            console.log(this.id)
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: this.keyword,
                    pageNum: item.pageNum,
                    score: this.score,
                    tooltips: this.tooltips

                }
            });
        },
        getPicView() {
            console.log(this.id)
            this.$router.push({
                path: '/preview',
                query: {
                    docId: this.id,
                    keyword: "",
                    pageNum: 1,
                    score: this.score,
                    tooltips: this.tooltips
                }
            });
        },
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
        clickNum: {type: Number,requires: false,default: 0},
        keyword: { type: String, requires: true },
        esSearchContentList: { type: Array, requires: false, default: [] },
        esSearchContentList_syno: { type: Array, requires: false, default: [] },
        ocrResultList: { type: Array, requires: false, default: [] },
        like_score: { type: Number, requires: false },
        content_score: { type: Number, requires: false },
        click_score: { type: Number, requires: false },
        medicalOpinions:{type:Array,requires:false}

    },
    // 将 prop 数据转换为本地数据
    created() {
        this.score = this.like_score + this.content_score + this.click_score
        let str = Number(this.content_score);
        str = str.toFixed(2)
        this.tooltips = "血糖监测：在血液透析过程中，由于采用无糖透析液，患者容易出现低血糖现象。护理人员应密切监测患者的血糖水平，并在透析过程中观察患者是否出现低血糖症状，如有异常应及时报告医生处理。\n" +
            "\n" +
            "心理护理：长期血液透析治疗可能导致患者产生焦虑、恐惧和沮丧等不良情绪。护理人员应主动与患者交流，帮助患者了解血液透析的目的、注意事项和疗效，以提高患者战胜疾病的信心。\n" +
            "\n" +
            "饮食护理：糖尿病肾病患者的饮食控制对预后非常重要。护理人员应制定合理的饮食方案，并详细记录患者每日的饮食情况以及血压、血糖、体质量等情况。同时，应与患者家属沟通，确保患者膳食平衡、合理、营养。"
        if (this.esSearchContentList !== [] && this.esSearchContentList !== null) {
            this.contentResultSize = this.esSearchContentList.length
        }
        if (this.ocrResultList !== [] && this.ocrResultList !== null) {
            this.ocrResultListSize = this.ocrResultList.length
        }
        if (this.esSearchContentList_syno !== [] && this.esSearchContentList_syno !== null) {
            this.esSearchContentList_synoSize = this.esSearchContentList_syno.length
        }
        console.log(this.contentResultSize)
        console.log(this.ocrResultListSize)

        if (this.contentResultSize == 0) {
            if (this.ocrResultListSize != 0) {
                this.activeTab = 'tab_third'
            }
            else {
                this.activeTab = 'tab_second'
            }
        }
        console.log("esSearchContentList:", this.esSearchContentList)

        // 统计字符个数
        if (this.keyword) {
          let chineseChars = this.keyword.match(/[\u4e00-\u9fa5]/g);
          if (chineseChars && chineseChars.length > 0) {
            // 如果关键词包含中文字符，按字符个数统计
            this.filterWordLen = chineseChars.length;
            // console.log("中文的个数为：",this.filterWordLen)
            // 将非中文字符转换为数组，再计算数组长度
            let nonChineseChars = this.keyword.replace(/[\u4e00-\u9fa5]/g, ' ').trim();
            // console.log("nonChineseChars为",nonChineseChars)
            let temp = nonChineseChars.split(/\s+/);

            this.filterWordLen += temp.length;
          } else {
            // 如果关键词不包含中文字符，按单词个数统计
            this.filterWordLen = this.keyword.trim().split(/\s+/).length;
          }
        } else {
          this.filterWordLen = 0;
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
        }
    },
    filters: {
        imgSrc(value) {
            if (value === "" || value == null) {
                return require('@/assets/source/doc.png')
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
    font-style: italic;
    font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;
    font-size: 20px;
    float: right;
}

.doc-title-info {
    height: 22px;
    /*line-height: 36px;*/
    /*margin-bottom: 12px;*/
    color: rgb(29, 27, 27);
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
    width: 300px;
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
    background-color: rgb(127, 200, 255);
}

.doc-abstract>>>bm {
    background-color: rgb(49, 246, 49);
}

.doc-abstract {
    height: 330px;
}

.sl-abstract {
    margin: 2px 0 0 0;
}


</style>