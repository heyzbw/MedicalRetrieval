<template>
    <div class="search-doc">
        <div class="doc-title-group">
            <!-- <div class="doc-pic" style="text-align: center;">
                <img :src="thumbId | imgSrc" alt="thumb"
                    style="width: 36px;max-height: 48px;border: 1px solid #dcdee2; border-radius: 2px">
            </div> -->
            <div class="title-group">
                <div class="doc-title-info" @click="getDocView()">
                    {{ Title }}
                </div>
                <div class="description">
                    <div class="description-item">
                        {{ Journal }}
                    </div>
                    <div class="description-item">
                        {{ Author }}
                    </div>
                    <div class="description-item">
                        {{ ISSN }}
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
                <i class="ivu-icon ivu-icon-ios-thumbs-up-outline"></i>889
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
    name: "PubmedItem",
    data() {
        return {

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
                data: {
                    'doi': this.doi,
                    'Title': this.Title
                }
            }).then(response => {
                console.log(response.data)
            }).catch(error => {
                console.log(error.response, "error");
                this.$message({
                    message: error.response.data.errMsg,
                    type: 'error'
                });
            });
        }

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

.sl-abstract {
    margin: 2px 0 0 0;
}
</style>