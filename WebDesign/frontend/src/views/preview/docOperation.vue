<template>
    <div class="operation-container">

        <div class="item" v-for="item in data" @click="operate(item)">
            <div class="item-logo">
                <img :src="item.src" :alt="item.src" style="height: 75%;width:75%">
            </div>
            <div class="operation-title">
                {{ item.name }}
            </div>
        </div>

    </div>
</template>

<script>
import CollectRequest from '@/api/collect'
import { BackendUrl } from '@/api/request'

export default {
    name: "docOperation",
    data() {
        return {
            data: [
                {
                    name: "马上收藏",
                    src: this.collectStatus == false ? require("@/assets/source/cancelcollect.png") : require("@/assets/source/collect.png"),
                    index: "1"
                },
                {
                    name: "竖个大拇指",
                    src: this.collectStatus == false ? require("@/assets/source/like-empt.png") : require("@/assets/source/like-fill.png"),
                    index: "2"
                },
                {
                    name: "立马下载",
                    src: require("@/assets/source/download.png"),
                    index: "3"
                },
            ],
            docId: this.$route.query.docId
        }
    },
    props: {
        collectStatus: { type: Boolean, request: true, default: false },
        likeStatus: { type: Boolean, request: true, default: false }
    },
    mounted() {

    },

    methods: {
        operate(item) {
            if (item.index === "3") {
                window.open(BackendUrl() + "/files/view/" + this.docId, "_blank");
            }
            else if (item.index === "1") {
                console.log("发出收藏事件")
                this.$emit("addCollect", Number(item.index))
                // this.collectStatus = !this.collectStatus
                // console.log("collectStatus:" + this.collectStatus)
                if (this.collectStatus == true) {
                    this.data[0].src = require("@/assets/source/collect.png")
                }
                else {
                    this.data[0].src = require("@/assets/source/cancelcollect.png")

                }

            }
            else if (item.index === "2") {
                this.$emit("addLike", Number(item.index))
                console.log("发出点赞事件")
                // this.likeStatus = !this.likeStatus
                // console.log("likeStatus:" + this.likeStatus)

                if (this.likeStatus == true) {
                    this.data[1].src = require("@/assets/source/like-fill.png")
                }
                else {
                    this.data[1].src = require("@/assets/source/like-empt.png")

                }
            }
        },
    }
}
</script>

<style scoped>
.operation-container {
    width: 50%;
    min-width: 400px;
    display: flex;
    justify-content: space-between;
    margin: auto;
    height: 200px;
}

.item {
    width: 120px;
    height: 120px;
    border-radius: 120px;
    margin: auto;
    position: relative;

}

.item:hover {
    cursor: pointer;

}

.operation-title {
    font-size: 14px;
    font-family: PingFangSC-Medium, PingFang SC, serif;
    font-weight: 500;
    color: #000000;
    line-height: 20px;
}

.item-logo {
    height: 80px;
    line-height: 80px;
    width: 120px;
}

.item-logo img {
    position: absolute;
    top: 40%;
    left: 50%;
    transform: translate(-50%, -50%);
    -webkit-transform: translate(-50%, -50%);
    -moz-transform: translate(-50%, -50%);
}

img {
    /*width: 48px;*/
    /*height: 48px;*/
}
</style>