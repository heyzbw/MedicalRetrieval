<template>
    <div class="homepage">
        <Navv></Navv>
        <div class="top-group" style="text-align: center; ">
            <img :src="imgSrc" width="100%" height="99%" alt="" />
            <SearchGroup></SearchGroup>
        </div>

        <div class="bottom-group" v-if="1">
            <div class="left-panel">
                <div class="top-container">
                    <div class="panel-title left-pane-title" style="cursor: pointer" @click="routeTo">
                        <span>文库</span>
                    </div>


                    <div :class="item.clicked ? 'tag-info' : 'tag-info-unchecked'" v-for="item in data"
                        @click="changeToCurrentTag(item.name, item.tagId)">

                        <span>{{ item.name }}</span>
                    </div>
                </div>
                <div class="doc-thumb-1">
                    <DocThumb class="doc-thumb" :flag="false" :title="doc.name" :docId="doc.thumbId"
                        v-for="doc in currentData.slice(0, 3)" :key="doc.id" @click.native="getDocView(doc.id)">
                    </DocThumb>
                </div>
                <div class="doc-thumb-1 second-group">
                    <DocThumb class="doc-thumb" :flag="false" :title="doc.name" :docId="doc.thumbId"
                        v-for="doc in currentData.slice(3, 6)" :key="doc.id" @click.native="getDocView(doc.id)">
                    </DocThumb>
                </div>
                <div class="doc-thumb-1 second-group">
                    <DocThumb class="doc-thumb" :flag="false" :title="doc.name" :docId="doc.thumbId"
                        v-for="doc in currentData.slice(6, 9)" :key="doc.id" @click.native="getDocView(doc.id)">
                    </DocThumb>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import DocTag from '@/home/DocTag';
import DocThumb from '@/home/DocThumb'
import HotTrend from '@/home/HotTrend'
import SearchGroup from '@/home/SearchGroup'
import '@/api/script'
import Navv from '../components/Navv'

const { BackendUrl } = require("@/api/request");
import StatsRequest from "@/api/stats";

export default {
    name: "index.vue",
    components: {
        Navv,
        HotTrend,
        DocTag,
        DocThumb,
        SearchGroup,

    },
    data() {
        return {
            imgSrc: require("../assets/source/bg1.png"),
            defaultAvatar: require("../assets/source/user.png"),
            data: {},
            currentData: [],
            docshow: false,
            newshow: true,
        }
    },
    computed: {
        ad() {
            let item = localStorage.getItem("token");
            return (item === null || item === undefined || item === "");
        }
    },
    created() {
        this.init()
    },
    filters: {
        userAvatar(param) {
            let value = localStorage.getItem("avatar")
            if (value === "" || value == null || value === undefined) {
                return this.defaultAvatar;
            } else {
                return BackendUrl() + "/files/image2/" + value;
            }
        }
    },
    methods: {
        routeTo() {
            this.$router.push('/doc')
        },
        init() {
            StatsRequest.getRecentDoc().then(response => {
                if (response.code === 200) {
                    this.data = response.data;
                    this.changeToCurrentTag(this.data[0].name, this.data[0].tagId)
                }
            }).catch(err => {
                this.$Message.error("查询最近文档出错！")
            })
        },
        /**
         * 切换到某一个标签上
         * @param name
         * @param tagId
         */
        changeToCurrentTag(name, tagId) {
            this.currentData = []
            this.data.forEach(item => {
                if (item.name === name && item.tagId === tagId) {
                    item.clicked = true
                    this.currentData = item.docList
                } else {
                    item.clicked = false
                }
            })
        },
        getDocView(id) {
            this.$router.push({
                path: '/preview',
                query: {
                    docId: id
                }
            })

        },
        logout() {
            localStorage.clear()
            this.$router.push({
                name: 'Login'
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.homepage {
    width: 100%;
    // height: 100vh;
    //padding-bottom: 20px;
    //position: fixed;

    .top-group {
        padding: 60px 0 0 0;
        height: 240px;
        width: 100%;
        //position: fixed;
        z-index: -1;

    }

    .bottom-group {
        margin: auto;
        width: 100%;
        height: 1000px;
        box-shadow: 0 0 5px 0 rgba(64, 64, 64, 0.3);
        border-radius: 8px;

        .left-panel {
            height: 100%;
            width: 100%;
            float: left;
            padding: 0 0 0 0;
        }

        .right-panel {
            height: 100%;
            width: 300px;
            float: left;
            padding: 0 24px 0 40px;
        }

        .top-container {
            height: 100px;
            width: 100%;
            display: flex;
            justify-content: flex-start;
            align-items: center;

            .panel-title {
                span {
                    font-size: 16px;
                    font-family: PingFangSC-Semibold, PingFang SC, serif;
                    font-weight: 600;
                    color: #464646;
                    line-height: 20px;
                }
            }

            .left-pane-title {
                margin-left: 10px;
            }

            .tag-info {
                height: 30px;
                background: #41a0ff;
                border-radius: 15px;
                //border: 1px solid #000000;
                padding: 0 10px;
                margin-left: 20px;
                line-height: 20px;
                color: #fff;

                span {
                    font-size: 18px;
                    font-family: PingFangSC-Regular, PingFang SC, serif;
                    font-weight: 400;
                    line-height: 15px;
                }

                &:hover {
                    cursor: pointer;
                    background: #7abfff;
                }
            }

            .tag-info-unchecked {
                height: 30px;
                border-radius: 15px;
                border: 1px solid #AAAAAA;
                padding: 0 10px;
                margin-left: 15px;
                line-height: 22px;
                color: #AAAAAA;

                &:hover {
                    cursor: pointer;
                    background: #0050c0;
                    //border: 1px solid #000000;
                    color: #AAAAAA;
                }

                span {
                    height: 20px;
                    font-size: 18px;
                    font-family: PingFangSC-Regular, PingFang SC, serif;
                    font-weight: 400;
                    line-height: 20px;
                }
            }
        }

        .doc-thumb-1 {
            display: flex;
            flex-wrap: wrap;
            justify-content: flex-start;
            overflow: hidden;
            padding-left: 20px;

            .doc-thumb {
                margin-right: 38px;
            }
        }

        .second-group {
            margin-top: 40px;
        }

    }
}

.ulist {
    display: inline-block;
    //border: 2px solid #000;

    ul li {
        display: inline-block;
        font-size: 24px;
        font-family: PingFangSC-Medium, PingFang SC, serif;
        font-weight: 500;
        color: #000000;
        padding: 14px 16px 14px 16px;
    }
}

#tagbox {
    position: relative;
    margin: 20px auto 0px;
    width: 500px;
    height: 400px;
}

#tagbox a {
    position: absolute;
    padding: 3px 6px;
    font-family: Microsoft YaHei;
    color: #fff;
    TOP: 0px;
    font-weight: bold;
    text-decoration: none;
    left: 0px
}

#tagbox a:hover {
    border: #eee 1px solid;
    background: rgba(48, 48, 48, 0.42);
}

#tagbox .blue {
    color: #748EB9
}

#tagbox .red {
    color: #E68FCF
}

#tagbox .yellow {
    color: #55B0C1
}

#tagbox .fivecolor {
    color: #F4DCCB
}

#tagbox .green {
    color: #C2EBDE
}
</style>