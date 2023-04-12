<template>
    <div style="background-color: #fff">
        <div class="nav">
            <Nav></Nav>
        </div>
        <div class="doc-group" style="display: inline-block">
            <div style="background-color: #fff">
                <SearchInput ref="searchInput" @on-search="getListData" @on-change="sentkeyword"
                    style="display: inline-block"></SearchInput>
                <div class="pubutton">
                    <el-button @click="getListDatap()" class="bbb" @sentword="sentkeyword">Pubmed搜索</el-button>
                </div>
            </div>
            <SearchItem v-if="searchifag" v-for="item in data.slice((currentPage - 1) * pageSize, (currentPage) * pageSize)"
                :id="item.id" :thumbId="item.thumbId" :title="item.title" :esSearchContentList="item.esSearchContentList"
                :time="item.createTime" :user-name="item.userName" :category="item.categoryVO" :tags="item.tagVOList"
                :collect-num="item.collectNum" :like-num="item.likeNum" :comment-num="item.commentNum" :click-num="item.click_num"
                :ocrResultList="item.ocrResultList" :keyword="keyword" :click_score="item.click_score"
                :content_score="item.content_score" :esSearchContentList_syno="item.esSearchContentList_syno"
                :like_score="item.like_score">
            </SearchItem>
            <PubmedItem v-if="searchpubmed"
                v-for="item in datapubmed.slice((currentPage - 1) * pageSize, (currentPage) * pageSize)" :Title="item.Title"
                :Abstract="item.Abstract" :ISSN="item.ISSN" :Journal="item.Journal" :Source="item.Source"
                :Author="item.Author" :doi="item.doi">
            </PubmedItem>
            <div class="page-container" v-show="datapubmed.length > 0 || this.data.length > 0">
                <Page :model-value="currentPage" :total="totalItems" :page-size="pageSize" @on-change="pageChange" />
            </div>
            <div style="padding: 30px 10px; color: #555" v-show="data.length < 1">
                <span v-if="!loading">暂无内容，试试其他呢～</span>
                <span v-else>拼命查找中，请等待...</span>
            </div>
        </div>
        <Footer></Footer>
    </div>
</template>

<script>

import Nav from "@/components/Nav";
import SearchItem from "@/views/searchResult/SearchItem";
import DocItem from "@/views/searchResult/DocItem";
import Footer from "@/components/MyFooter";
import DocumentRequest from "@/api/document"
import SearchInput from "./SearchInput"
import PubmedItem from "./PubmedItem";

export default {
    name: "Index.vue",
    data() {
        return {
            data: [],
            currentPage: 1,
            totalItems: 4,
            pageSize: 6,
            loading: true,
            keyword: "",
            datapubmed: [],
            searchifag: true,
            searchpubmed: false,
            filterWordLen:0
        }
    },
    components: {
        Nav,
        Footer,
        DocItem,
        SearchItem,
        SearchInput,
        PubmedItem
    },
    mounted() {
        this.getListData()
    },
    methods: {
        getListDatap() {

            this.loading = true
            this.searchifag = false
            this.searchpubmed = true

            console.log(this.keyword)
            if (this.keyword === "") return;
            this.$axios({
                method: "post",
                url: "http://127.0.0.1:8083/getpubmed",
                data: { 'keyword': this.keyword, }
            }).then(response => {
                this.loading = false;
                console.log(response.data);
                this.datapubmed = response.data.Papers;
            }).catch(error => {
                if (this.data == undefined || this.data.length === 0) {
                    this.info2(false)
                }
                else {
                    this.info1(false)
                }
                console.log(error.response, "error");
                this.$message({
                    message: error.response.data.errMsg,
                    type: 'error'
                });
            });
        },
        info(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : '没有找到相关文档，试一试其他关键字'
            });
        },
        info1(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : 'pubmed只支持英文检索哟'
            });
        },
        info2(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : 'pubmed错了点问题，试一试其他关键字吧'
            });
        },
        pageChange(page) {
            this.currentPage = page
        },
        getListData() {


            // 统计字符个数
            if (this.keyword) {
              let chineseChars = this.keyword.match(/[\u4e00-\u9fa5]/g);
              if (chineseChars && chineseChars.length > 0) {
                // 如果关键词包含中文字符，按字符个数统计
                this.filterWordLen = chineseChars.length;
                console.log("中文的个数为：",this.filterWordLen)
                // 将非中文字符转换为数组，再计算数组长度
                let nonChineseChars = this.keyword.replace(/[\u4e00-\u9fa5]/g, ' ').trim();
                console.log("nonChineseChars为",nonChineseChars)
                let temp = nonChineseChars.split(/\s+/);

                this.filterWordLen += temp.length;
              } else {
                // 如果关键词不包含中文字符，按单词个数统计
                this.filterWordLen = this.keyword.trim().split(/\s+/).length;
              }
            } else {
              this.filterWordLen = 0;
            }


          console.log("搜索的字符个数为：",this.filterWordLen)


            this.loading = true
            this.searchifag = true
            this.searchpubmed = false
            let keyword = this.$route.query.keyWord
            this.keyword = keyword
            if (keyword === "") return;
            const params = {
                "categoryId": "",
                "filterWord": keyword,
                "page": this.currentPage - 1,
                "rows": this.pageSize,
                "tagId": "",
                "type": "FILTER",
                "userType": localStorage.getItem("type")
            }
            console.log(params)
            DocumentRequest.getListData(params).then(res => {
                this.loading = false;
                if (res.code === 200) {
                    this.totalItems = res.data.totalNum;
                    this.data = res.data.documents;
                    console.log("关键字查询成功，返回内容为：");
                    console.log("data:", res.data)
                    //console.log("desSearchContentList:", res.data[0].esSearchContentList)

                } else {
                    this.data = []
                }
                this.listLoading = false
                if (this.data == null || this.data.length === 0) {
                    this.info(false)
                }
            })
        },
        sentkeyword(data) {
            let keyword = data
            this.keyword = keyword
            console.log(this.keyword)
        },
    }
}
</script>

<style lang="scss" scoped>
.nav {
    background-color: #ffffff;
    width: 100%;
    height: 50px;
}

.doc-group {
    width: 1200px;
    /*position: absolute;*/
    margin: auto;
    /*background-color: #dcdee2;*/
    /*background-color: rgba(245, 245, 245, 100);*/
    color: rgba(16, 16, 16, 100);
    text-align: left;

    /*padding-left: 12px;*/
}

.pubutton {
    padding-right: 18%;
    padding-top: 5x;
    height: 50px;
    text-align: center;
    float: right;
}

.bbb {
    margin: 15px;
    font-family: Helvetica-Bold, Helvetica;
    font-weight: bold;
    font-size: 10px;
    padding-top: 0x;
    border: 1px solid #AAAAAA;
    width: 105px;
    text-align: center;
    height: 40px;
    border-radius: 10px;
    /*background-color: #7dffaa;*/


    &:hover {
        cursor: pointer;

    }
}

.page-container {
    /*background-color: yellow;*/
    text-align: left;
    padding: 25px;
}
</style>