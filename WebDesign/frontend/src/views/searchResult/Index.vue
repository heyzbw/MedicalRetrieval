<template>
    <div style="background-color: #fff">
        <div class="nav">
            <Nav></Nav>
        </div>
        <div class="doc-group" style="display: inline-block">
            <div style="background-color: #fff">
                <SearchInput ref="searchInput" @on-search="getListData"></SearchInput>
                <button @click="getListDatap()">Pubmed搜索</button>
            </div>
            <SearchItem v-if="searchifag" v-for="item in data.slice((currentPage - 1) * pageSize, (currentPage) * pageSize)"
                :id="item.id" :thumbId="item.thumbId" :title="item.title" :description="item.description"
                :time="item.createTime" :user-name="item.userName" :category="item.categoryVO" :tags="item.tagVOList"
                :stringList="item.stringList" :collect-num="item.collectNum" :comment-num="item.commentNum"
                :keyword="keyword">
            </SearchItem>
            <PubmedItem v-if="searchpubmed"
                v-for="item in datapubmed.slice((currentPage - 1) * pageSize, (currentPage) * pageSize)" :Title="item.Title"
                :Abstract="item.Abstract" :ISSN="item.ISSN" :Journal="item.Journal" :Source="item.Source"
                :Author="item.Author" :doi="item.doi">
            </PubmedItem>
            <div class="page-container" v-show="datapubmed.length > 0">
                <Page :model-value="currentPage" :total="20" :page-size="pageSize" @on-change="pageChange" />
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
            let keyword = this.$route.query.keyWord
            this.keyword = keyword
            console.log(this.keyword)
            if (keyword === "") return;
            this.$axios({
                method: "post",
                url: "http://127.0.0.1:8083/getpubmed",
                data: { 'keyword': this.keyword, }
            }).then(response => {
                this.loading = false;
                console.log(response.data);
                this.datapubmed = response.data.Papers;
            }).catch(error => {
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
        pageChange(page) {
            this.currentPage = page
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
                "type": "FILTER"
            }
            DocumentRequest.getListData(params).then(res => {
                this.loading = false;
                if (res.code === 200) {
                    this.totalItems = res.data.totalNum;
                    this.data = res.data.documents;
                    console.log("关键字查询成功，返回内容为：");
                    console.log("data:",res.data)
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

<style scoped>
.nav {
    background-color: #ffcc4f;
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

.page-container {
    /*background-color: yellow;*/
    text-align: left;
    padding: 25px;
}
</style>