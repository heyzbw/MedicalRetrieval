<template>
    <div class="homepage">
        <Navv></Navv>
        <div class="top-group" style="text-align: center; ">
            <!-- <div class="ulist">
                <ul class="tabTagList">
                    <li id="tag01">检索</li>
                    <li id="tag02">浏览</li>
                </ul>
            </div> -->
            <SearchGroup></SearchGroup>
        </div>

        <div class="bottom-group" v-if="1">
            <div class="left-panel">
                <div class="top-container">
                    <div class="panel-title left-pane-title" style="cursor: pointer" @click="routeTo">
                        <span>全部文档</span>
                    </div>


                    <div :class="item.clicked ? 'tag-info' : 'tag-info-unchecked'" v-for="item in data"
                        @click="changeToCurrentTag(item.name, item.tagId)">
                        <span>{{ item.name }}</span>
                    </div>
                </div>
                <div class="doc-thumb-1">
                    <DocThumb class="doc-thumb" :flag="false" :title="doc.name" :docId="doc.thumbId"
                        v-for="doc in currentData.slice(0, 6)" :key="doc.id" @click.native="getDocView(doc.id)">
                    </DocThumb>
                </div>
                <div class="doc-thumb-1 second-group">
                    <DocThumb class="doc-thumb" :flag="false" :title="doc.name" :docId="doc.thumbId"
                        v-for="doc in currentData.slice(6, 12)" :key="doc.id" @click.native="getDocView(doc.id)">
                    </DocThumb>
                </div>
            </div>
            <div class="right-panel">
                <div class="top-container">
                    <div class="panel-title"><span>热门文档排行 🔥</span></div>
                </div>
                <div class="hot-trend">
                    <HotTrend></HotTrend>
                </div>
            </div>
        </div>
        <div>
            <h2>热点</h2>
            <div>
                <div id="tagbox"><a href="http://localhost:8080/#/searchResult?keyWord=新生儿黄疸" class="blue" zycate="jb"
                        zyid="yssh0405"
                        style="left: 109.69px; top: 127.802px; font-size: 21px; opacity: 2.55039; z-index: 0;">新生儿黄疸</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=Reye综合症" class="blue" zycate="jb" zyid="ysj00525"
                        style="left: 54.9179px; top: 189.706px; font-size: 19px; opacity: 1.84858; z-index: 1;">Reye综合症</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=奥美拉唑" class="fivecolor" zycate="hxyw"
                        zyid="hxy10474"
                        style="left: 113.834px; top: 241.575px; font-size: 23px; opacity: 2.94355; z-index: 2;">奥美拉唑</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=磷酸氯喹" class="fivecolor" zycate="hxyw"
                        zyid="hxy10674"
                        style="left: 148.46px; top: 197.349px; font-size: 29px; opacity: 4.77725; z-index: 3;">磷酸氯喹</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=金花消痤丸" class="green" zycate="zcy" zyid="62"
                        style="left: 199.314px; top: 111.577px; font-size: 26px; opacity: 3.7817; z-index: 4;">金花消痤丸</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=癌胚抗原(CEA)检测" class="yellow" zycate="jc"
                        zyid="EX000008"
                        style="left: 156.373px; top: 57.6685px; font-size: 19px; opacity: 1.97793; z-index: 5;">癌胚抗原(CEA)检测</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=丘疹" class="red" zycate="zz" zyid="SY000020"
                        style="left: 75.6025px; top: 70.4171px; font-size: 16px; opacity: 1.1174; z-index: 6;">丘疹</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=尼尔雌醇" class="fivecolor" zycate="hxyw"
                        zyid="hxy10005"
                        style="left: 1.70483px; top: 139.649px; font-size: 15px; opacity: 0.825342; z-index: 7;">尼尔雌醇</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=氧分压" class="yellow" zycate="jc" zyid="EX000051"
                        style="left: 36.002px; top: 228.445px; font-size: 15px; opacity: 0.872314; z-index: 8;">氧分压</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=肝舒乐颗粒" class="green" zycate="zcy" zyid="713"
                        style="left: 101.956px; top: 296.276px; font-size: 17px; opacity: 1.23708; z-index: 9;">肝舒乐颗粒</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=呼吸急促" class="red" zycate="zz" zyid="SY000007"
                        style="left: 153.154px; top: 316.233px; font-size: 19px; opacity: 2.02285; z-index: 10;">呼吸急促</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=重组人粒细胞巨噬细胞集落刺激因子" class="fivecolor" zycate="hxyw"
                        zyid="hxy10419"
                        style="left: 199.778px; top: 282.533px; font-size: 24px; opacity: 3.23574; z-index: 11;">重组人粒细胞巨噬细胞集落刺激因子</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=小儿肠胃康颗粒" class="green" zycate="zcy" zyid="1568"
                        style="left: 274.387px; top: 209.036px; font-size: 26px; opacity: 3.97511; z-index: 12;">小儿肠胃康颗粒</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=茵山莲颗粒" class="green" zycate="zcy" zyid="727"
                        style="left: 228.62px; top: 121.796px; font-size: 23px; opacity: 3.15698; z-index: 13;">茵山莲颗粒</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=血常规检查" class="yellow" zycate="jc" zyid="EX000151"
                        style="left: 276.388px; top: 49.3408px; font-size: 19px; opacity: 1.89941; z-index: 14;">血常规检查</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=拉米夫定" class="fivecolor" zycate="hxyw"
                        zyid="hxy11018"
                        style="left: 197.682px; top: 13.856px; font-size: 16px; opacity: 1.06154; z-index: 15;">拉米夫定</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=尿常规检查" class="yellow" zycate="jc" zyid="EX000165"
                        style="left: 109.201px; top: 25.4562px; font-size: 14px; opacity: 0.604361; z-index: 16;">尿常规检查</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=白消安" class="fivecolor" zycate="hxyw"
                        zyid="hxy12102"
                        style="left: 87.4754px; top: 80.4446px; font-size: 13px; opacity: 0.380792; z-index: 17;">白消安</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=地塞米松" class="fivecolor" zycate="hxyw"
                        zyid="hxy10088"
                        style="left: 58.2006px; top: 163.379px; font-size: 13px; opacity: 0.308486; z-index: 18;">地塞米松</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=自身免疫性肝病相关抗体检测" class="yellow" zycate="jc"
                        zyid="EX000248"
                        style="left: 37.1065px; top: 251.918px; font-size: 13px; opacity: 0.356974; z-index: 19;">自身免疫性肝病相关抗体检测</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=养血荣筋丸" class="green" zycate="zcy" zyid="1473"
                        style="left: 100.174px; top: 322.954px; font-size: 14px; opacity: 0.529744; z-index: 20;">养血荣筋丸</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=京万红" class="green" zycate="zcy" zyid="14"
                        style="left: 167.364px; top: 358.495px; font-size: 15px; opacity: 0.851694; z-index: 21;">京万红</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=参莲胶囊" class="green" zycate="zcy" zyid="628"
                        style="left: 243.478px; top: 349.931px; font-size: 17px; opacity: 1.33301; z-index: 22;">参莲胶囊</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=环磷酰胺" class="fivecolor" zycate="hxyw"
                        zyid="hxy12101"
                        style="left: 226.343px; top: 299.865px; font-size: 19px; opacity: 1.84871; z-index: 23;">环磷酰胺</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=反跳痛" class="red" zycate="zz" zyid="SY000067"
                        style="left: 352.298px; top: 221.21px; font-size: 19px; opacity: 2.02993; z-index: 24;">反跳痛</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=葡萄糖耐量试验" class="yellow" zycate="jc"
                        zyid="EX000020"
                        style="left: 359.976px; top: 133.885px; font-size: 18px; opacity: 1.67218; z-index: 25;">葡萄糖耐量试验</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=阿巴卡韦" class="fivecolor" zycate="hxyw"
                        zyid="hxy11029"
                        style="left: 332.527px; top: 59.9115px; font-size: 16px; opacity: 1.10089; z-index: 26;">阿巴卡韦</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=肥胖" class="red" zycate="zz" zyid="SY000025"
                        style="left: 284.831px; top: 18.045px; font-size: 14px; opacity: 0.630247; z-index: 27;">肥胖</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=鳞屑" class="red" zycate="zz" zyid="SY000099"
                        style="left: 210.628px; top: 19.1481px; font-size: 13px; opacity: 0.323662; z-index: 28;">鳞屑</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=结晶" class="yellow" zycate="jc" zyid="EX000179"
                        style="left: 148.988px; top: 63.3786px; font-size: 13px; opacity: 0.149836; z-index: 29;">结晶</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=扑翼样震颤" class="red" zycate="zz" zyid="SY000068"
                        style="left: 116.859px; top: 139.892px; font-size: 12px; opacity: 0.0751812; z-index: 30;">扑翼样震颤</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=不安腿综合征" class="blue" zycate="jb" zyid="yssh0503"
                        style="left: 97.7218px; top: 229.243px; font-size: 12px; opacity: 0.0821707; z-index: 31;">不安腿综合征</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=隐睾" class="blue" zycate="jb" zyid="yssh0002"
                        style="left: 129.387px; top: 308.067px; font-size: 13px; opacity: 0.167813; z-index: 32;">隐睾</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=囟膨隆" class="red" zycate="zz" zyid="SY000029"
                        style="left: 204.805px; top: 355.086px; font-size: 13px; opacity: 0.33644; z-index: 33;">囟膨隆</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=1型糖尿病" class="blue" zycate="jb" zyid="ysj01218"
                        style="left: 266.389px; top: 357.036px; font-size: 14px; opacity: 0.578687; z-index: 34;">1型糖尿病</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=斜视" class="red" zycate="zz" zyid="SY000048"
                        style="left: 335.785px; top: 312.978px; font-size: 15px; opacity: 0.822143; z-index: 35;">斜视</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=肿瘤标志物" class="yellow" zycate="jc" zyid="EX000208"
                        style="left: 386.416px; top: 235.608px; font-size: 15px; opacity: 0.907424; z-index: 36;">肿瘤标志物</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=获得性免疫缺陷综合征" class="blue" zycate="jb"
                        zyid="zlzn0214"
                        style="left: 384.518px; top: 148.714px; font-size: 15px; opacity: 0.745505; z-index: 37;">获得性免疫缺陷综合征</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=奈韦拉平" class="fivecolor" zycate="hxyw"
                        zyid="hxy11017"
                        style="left: 349.983px; top: 80.8252px; font-size: 14px; opacity: 0.460122; z-index: 38;">奈韦拉平</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=降钙素" class="yellow" zycate="jc" zyid="EX000217"
                        style="left: 274.232px; top: 56.1897px; font-size: 13px; opacity: 0.207987; z-index: 39;">降钙素</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=活血消炎丸" class="green" zycate="zcy" zyid="1"
                        style="left: 229.762px; top: 85.4326px; font-size: 12px; opacity: 0.0469843; z-index: 40;">活血消炎丸</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=先天性白血病" class="blue" zycate="jb" zyid="yssh0005"
                        style="left: 201.979px; top: 159.114px; font-size: 12px; opacity: -0.0210021; z-index: 41;">先天性白血病</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=颈静脉怒张" class="red" zycate="zz" zyid="SY000141"
                        style="left: 183.47px; top: 247.694px; font-size: 12px; opacity: -0.00205437; z-index: 42;">颈静脉怒张</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=醒消丸" class="green" zycate="zcy" zyid="9"
                        style="left: 253.904px; top: 310.496px; font-size: 12px; opacity: 0.100102; z-index: 43;">醒消丸</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=安神健脑液" class="green" zycate="zcy" zyid="1440"
                        style="left: 321.685px; top: 313.697px; font-size: 13px; opacity: 0.257684; z-index: 44;">安神健脑液</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=手足口病" class="blue" zycate="jb" zyid="yssh0003"
                        style="left: 366.678px; top: 252.71px; font-size: 13px; opacity: 0.358355; z-index: 45;">手足口病</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=宫颈肿瘤" class="blue" zycate="jb" zyid="zlzn0145"
                        style="left: 384.038px; top: 167.511px; font-size: 13px; opacity: 0.27025; z-index: 46;">宫颈肿瘤</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=食欲亢进" class="red" zycate="zz" zyid="SY000073"
                        style="left: 321.627px; top: 131.551px; font-size: 12px; opacity: 0.0828413; z-index: 47;">食欲亢进</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=前列腺液常规检查" class="yellow" zycate="jc"
                        zyid="EX000269"
                        style="left: 285.566px; top: 190.467px; font-size: 12px; opacity: -0.0107012; z-index: 48;">前列腺液常规检查</a><a
                        href="http://localhost:8080/#/searchResult?keyWord=胼胝体发育不全或缺如" class="blue" zycate="jb"
                        zyid="ysj01426"
                        style="left: 330.413px; top: 246.596px; font-size: 12px; opacity: 0.0766935; z-index: 49;">胼胝体发育不全或缺如</a>
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
            imgSrc: require("../assets/source/banner.png"),
            defaultAvatar: require("../assets/source/user_avater.png"),
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

    .top-group {
        // height: 340px;
        //width: 100%;
        padding: 400px 0px 0 0;
        //z-index: -1;

    }

    .bottom-group {
        margin: auto;
        width: 1200px;
        height: 619px;
        box-shadow: 0 0 5px 0 rgba(64, 64, 64, 0.3);
        border-radius: 8px;

        .left-panel {
            height: 100%;
            width: 900px;
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
            height: 110px;
            width: 100%;
            display: flex;
            justify-content: flex-start;
            align-items: center;

            .panel-title {
                span {
                    font-size: 20px;
                    font-family: PingFangSC-Semibold, PingFang SC, serif;
                    font-weight: 600;
                    color: #464646;
                    line-height: 28px;
                }
            }

            .left-pane-title {
                margin-left: 24px;
            }

            .tag-info {
                height: 30px;
                background: #00c026;
                border-radius: 15px;
                //border: 1px solid #000000;
                padding: 0 10px;
                margin-left: 20px;
                line-height: 32px;
                color: #fff;

                span {
                    font-size: 14px;
                    font-family: PingFangSC-Regular, PingFang SC, serif;
                    font-weight: 400;
                    line-height: 20px;
                }

                &:hover {
                    cursor: pointer;
                    background: #b9ff83;
                }
            }

            .tag-info-unchecked {
                height: 30px;
                border-radius: 15px;
                border: 1px solid #AAAAAA;
                padding: 0 10px;
                margin-left: 20px;
                line-height: 32px;
                color: #AAAAAA;

                &:hover {
                    cursor: pointer;
                    background: #00c026;
                    //border: 1px solid #000000;
                    color: #AAAAAA;
                }

                span {
                    height: 20px;
                    font-size: 14px;
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