<template>
    <div style="background-color: #fff">
        <div class="nav">
            <Nav></Nav>
        </div>
        <div class="doc-group" style="display: inline-block">
            <div style="background-color: #fff">
                <SearchInput ref="searchInput" @on-search="getListData" style="display: inline-block"></SearchInput>
            </div>
            <SearchItem v-for="item in data.slice((currentPage - 1) * pageSize, (currentPage) * pageSize)" :id="item.id"
                :thumbId="item.thumbId" :title="item.title" :esSearchContentList="item.esSearchContentList"
                :time="item.createTime" :user-name="item.userName" :category="item.categoryVO" :tags="item.tagVOList"
                :collect-num="item.collectNum" :comment-num="item.commentNum" :ocrResultList="item.ocrResultList"
                :keyword="keyword" :click_score="item.click_score" :content_score="item.content_score"
                :like_score="item.like_score">
            </SearchItem>

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
import SearchInput from "@/views/searchResult/SearchInput"

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
            time: 2023,
        }
    },
    components: {
        Nav,
        Footer,
        DocItem,
        SearchItem,
        SearchInput,
    },
    mounted() {
        this.getSuperData()
    },
    methods: {
        info(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : '没有找到相关文档，试一试其他关键字'
            });
        },
        pageChange(page) {
            this.currentPage = page
        },
        getSuperData() {
            this.loading = true
            this.searchifag = true
            this.searchpubmed = false
            let keyword = this.$route.query.keyWord
            this.time = this.$route.query.time
            this.title = this.$route.query.title
            this.keyword = keyword
            if (keyword === "") return;
            const params = {
                "categoryId": "",
                "filterWord": keyword,
                "page": this.currentPage - 1,
                "rows": this.pageSize,
                "tagId": "",
                "type": "FILTER",
                "keyword": this.keyword,
                "time": this.time,
                "title": this.title
            }
            console.log("params",params)
            DocumentRequest.getSuperData(params).then(res => {
                this.loading = false;
                if (res.code === 200) {
                    console.log(res)
                    this.totalItems = res.data.totalNum;
                    this.data = res.data.documents;
                    console.log("关键字查询成功，返回内容为：");
                    console.log("data:", res.data)
                } else {
                    console.log(res)
                    this.data = []
                }
                this.listLoading = false
                if (this.data == null || this.data.length === 0) {
                    this.info(false)
                }
            })
        },
        getListData() {
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
                "userType":localStorage.getItem("type")
            }
            DocumentRequest.getListData(params).then(res => {
                this.loading = false;
                if (res.code === 200) {
                    this.totalItems = res.data.totalNum;
                    this.data = res.data.documents;
                    console.log("关键字查询成功，返回内容为：");
                    console.log("data:", res.data)
                } else {
                    this.data = []
                }
                this.listLoading = false
                if (this.data == null || this.data.length === 0) {
                    this.info(false)
                }
            })
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
    padding-right: 23%;
    padding-top: 5x;
    height: 50px;
    float: right;
}

.bbb {
    margin: 15px;
    font-family: Helvetica-Bold, Helvetica;
    font-weight: bold;
    font-size: 12px;
    padding-top: 5x;
    border: 1px solid #AAAAAA;
    width: 90px;
    height: 40px;
    border-radius: 18px;
    background-color: #7dffaa;
    color: #fff;

    &:hover {
        cursor: pointer;
        color: #1b933b;
    }
}

.page-container {
    /*background-color: yellow;*/
    text-align: left;
    padding: 25px;
}
</style>