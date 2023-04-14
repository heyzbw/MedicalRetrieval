<template>
    <div class="homepage">
        <Navv></Navv>
        <div class="hotdots">
            <!--            <div id="tagbox">-->
            <!--              <a v-for="item in tagsData" :href="item.link" :class="item.class" :style="item.style">{{ item.text }}</a>-->
            <!--            </div>-->
            <div id="tagbox" style="width: 30%; height: 100%;">
                <a href="http://localhost:8080/#/searchResult?keyWord=癌症" class="blue" zycate="jb" zyid="yssh0405"
                    style="left: 109.69px; top: 127.802px; font-size: 21px; opacity: 2.55039; z-index: 0;">癌症</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=强迫症" class="blue" zycate="jb" zyid="ysj00525"
                    style="left: 54.9179px; top: 189.706px; font-size: 19px; opacity: 1.84858; z-index: 1;">强迫症</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=肾病" class="fivecolor" zycate="hxyw" zyid="hxy10474"
                    style="left: 113.834px; top: 241.575px; font-size: 23px; opacity: 2.94355; z-index: 2;">肾病</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=失眠症" class="fivecolor" zycate="hxyw" zyid="hxy10674"
                    style="left: 148.46px; top: 197.349px; font-size: 29px; opacity: 4.77725; z-index: 3;">失眠症</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=糖尿病" class="green" zycate="zcy" zyid="62"
                    style="left: 199.314px; top: 111.577px; font-size: 26px; opacity: 3.7817; z-index: 4;">糖尿病</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=cancer" class="yellow" zycate="jc" zyid="EX000008"
                    style="left: 156.373px; top: 57.6685px; font-size: 19px; opacity: 1.97793; z-index: 5;">cancer</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=中风" class="red" zycate="zz" zyid="SY000020"
                    style="left: 75.6025px; top: 70.4171px; font-size: 16px; opacity: 1.1174; z-index: 6;">中风</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Arthritis" class="fivecolor" zycate="hxyw"
                    zyid="hxy10005"
                    style="left: 1.70483px; top: 139.649px; font-size: 15px; opacity: 0.825342; z-index: 7;">Arthritis</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=哮喘" class="yellow" zycate="jc" zyid="EX000051"
                    style="left: 36.002px; top: 228.445px; font-size: 15px; opacity: 0.872314; z-index: 8;">哮喘</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Diabetes" class="green" zycate="zcy" zyid="713"
                    style="left: 101.956px; top: 296.276px; font-size: 17px; opacity: 1.23708; z-index: 9;">Diabetes</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=肝炎" class="red" zycate="zz" zyid="SY000007"
                    style="left: 153.154px; top: 316.233px; font-size: 19px; opacity: 2.02285; z-index: 10;">肝炎</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Hypertension" class="fivecolor" zycate="hxyw"
                    zyid="hxy10419"
                    style="left: 199.778px; top: 282.533px; font-size: 24px; opacity: 3.23574; z-index: 11;">Hypertension</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=抑郁症" class="green" zycate="zcy" zyid="1568"
                    style="left: 274.387px; top: 209.036px; font-size: 26px; opacity: 3.97511; z-index: 12;">抑郁症</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Depression" class="green" zycate="zcy" zyid="727"
                    style="left: 228.62px; top: 121.796px; font-size: 23px; opacity: 3.15698; z-index: 13;">Depression</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=贫血" class="yellow" zycate="jc" zyid="EX000151"
                    style="left: 276.388px; top: 49.3408px; font-size: 19px; opacity: 1.89941; z-index: 14;">贫血</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Asthma" class="fivecolor" zycate="hxyw"
                    zyid="hxy11018"
                    style="left: 197.682px; top: 13.856px; font-size: 16px; opacity: 1.06154; z-index: 15;">Asthma</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=近视" class="yellow" zycate="jc" zyid="EX000165"
                    style="left: 109.201px; top: 25.4562px; font-size: 14px; opacity: 0.604361; z-index: 16;">近视</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Acne" class="fivecolor" zycate="hxyw" zyid="hxy12102"
                    style="left: 87.4754px; top: 80.4446px; font-size: 13px; opacity: 0.380792; z-index: 17;">Acne</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=脱发" class="fivecolor" zycate="hxyw" zyid="hxy10088"
                    style="left: 58.2006px; top: 163.379px; font-size: 13px; opacity: 0.308486; z-index: 18;">脱发</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Cardiovascular Disease" class="yellow" zycate="jc"
                    zyid="EX000248"
                    style="left: 37.1065px; top: 251.918px; font-size: 13px; opacity: 0.356974; z-index: 19;">Cardiovascular
                    Disease</a><a href="http://localhost:8080/#/searchResult?keyWord=结肠炎" class="green" zycate="zcy"
                    zyid="1473"
                    style="left: 100.174px; top: 322.954px; font-size: 14px; opacity: 0.529744; z-index: 20;">结肠炎</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Epilepsy" class="green" zycate="zcy" zyid="14"
                    style="left: 167.364px; top: 358.495px; font-size: 15px; opacity: 0.851694; z-index: 21;">Epilepsy</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=白内障" class="green" zycate="zcy" zyid="628"
                    style="left: 243.478px; top: 349.931px; font-size: 17px; opacity: 1.33301; z-index: 22;">白内障</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Influenza" class="fivecolor" zycate="hxyw"
                    zyid="hxy12101"
                    style="left: 226.343px; top: 299.865px; font-size: 19px; opacity: 1.84871; z-index: 23;">Influenza</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=静脉曲张" class="red" zycate="zz" zyid="SY000067"
                    style="left: 352.298px; top: 221.21px; font-size: 19px; opacity: 2.02993; z-index: 24;">静脉曲张</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Gastroenteritis" class="yellow" zycate="jc"
                    zyid="EX000020"
                    style="left: 359.976px; top: 133.885px; font-size: 18px; opacity: 1.67218; z-index: 25;">Gastroenteritis</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=结石症" class="fivecolor" zycate="hxyw" zyid="hxy11029"
                    style="left: 332.527px; top: 59.9115px; font-size: 16px; opacity: 1.10089; z-index: 26;">结石症</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Jaundice" class="red" zycate="zz" zyid="SY000025"
                    style="left: 284.831px; top: 18.045px; font-size: 14px; opacity: 0.630247; z-index: 27;">Jaundice</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=失眠症" class="red" zycate="zz" zyid="SY000099"
                    style="left: 210.628px; top: 19.1481px; font-size: 13px; opacity: 0.323662; z-index: 28;">失眠症</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Lupus" class="yellow" zycate="jc" zyid="EX000179"
                    style="left: 148.988px; top: 63.3786px; font-size: 13px; opacity: 0.149836; z-index: 29;">Lupus</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=脂肪肝" class="red" zycate="zz" zyid="SY000068"
                    style="left: 116.859px; top: 139.892px; font-size: 12px; opacity: 0.0751812; z-index: 30;">脂肪肝</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Kidney Stones" class="blue" zycate="jb"
                    zyid="yssh0503"
                    style="left: 97.7218px; top: 229.243px; font-size: 12px; opacity: 0.0821707; z-index: 31;">Kidney
                    Stones</a><a href="http://localhost:8080/#/searchResult?keyWord=恶性肿瘤" class="blue" zycate="jb"
                    zyid="yssh0002"
                    style="left: 129.387px; top: 308.067px; font-size: 13px; opacity: 0.167813; z-index: 32;">恶性肿瘤</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Carcinoma" class="red" zycate="zz" zyid="SY000029"
                    style="left: 204.805px; top: 355.086px; font-size: 13px; opacity: 0.33644; z-index: 33;">Carcinoma</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=癌性疾病" class="blue" zycate="jb" zyid="ysj01218"
                    style="left: 266.389px; top: 357.036px; font-size: 14px; opacity: 0.578687; z-index: 34;">癌性疾病</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Psoriasis" class="red" zycate="zz" zyid="SY000048"
                    style="left: 335.785px; top: 312.978px; font-size: 15px; opacity: 0.822143; z-index: 35;">Psoriasis</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=肿瘤" class="yellow" zycate="jc" zyid="EX000208"
                    style="left: 386.416px; top: 235.608px; font-size: 15px; opacity: 0.907424; z-index: 36;">肿瘤</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Pimples" class="blue" zycate="jb" zyid="zlzn0214"
                    style="left: 384.518px; top: 148.714px; font-size: 15px; opacity: 0.745505; z-index: 37;">Pimples</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=乙型肝炎" class="fivecolor" zycate="hxyw" zyid="hxy11017"
                    style="left: 349.983px; top: 80.8252px; font-size: 14px; opacity: 0.460122; z-index: 38;">乙型肝炎</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Obesity" class="yellow" zycate="jc" zyid="EX000217"
                    style="left: 274.232px; top: 56.1897px; font-size: 13px; opacity: 0.207987; z-index: 39;">Obesity</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=气管炎" class="green" zycate="zcy" zyid="1"
                    style="left: 229.762px; top: 85.4326px; font-size: 12px; opacity: 0.0469843; z-index: 40;">气管炎</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Heart Disease" class="blue" zycate="jb"
                    zyid="yssh0005"
                    style="left: 201.979px; top: 159.114px; font-size: 12px; opacity: -0.0210021; z-index: 41;">Heart
                    Disease</a><a href="http://localhost:8080/#/searchResult?keyWord=高血压" class="red" zycate="zz"
                    zyid="SY000141"
                    style="left: 183.47px; top: 247.694px; font-size: 12px; opacity: -0.00205437; z-index: 42;">高血压</a><a
                    href="http://localhost:8080/#/searchResult?keyWor心脏病消丸" class="green" zycate="zcy" zyid="9"
                    style="left: 253.904px; top: 310.496px; font-size: 12px; opacity: 0.100102; z-index: 43;">心脏病</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Osteoarthritis" class="green" zycate="zcy"
                    zyid="1440"
                    style="left: 321.685px; top: 313.697px; font-size: 13px; opacity: 0.257684; z-index: 44;">Osteoarthritis</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=Bone Loss" class="blue" zycate="jb" zyid="yssh0003"
                    style="left: 366.678px; top: 252.71px; font-size: 13px; opacity: 0.358355; z-index: 45;">Bone Loss</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=宫颈肿瘤" class="blue" zycate="jb" zyid="zlzn0145"
                    style="left: 384.038px; top: 167.511px; font-size: 13px; opacity: 0.27025; z-index: 46;">宫颈肿瘤</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=阿尔茨海默症" class="red" zycate="zz" zyid="SY000073"
                    style="left: 321.627px; top: 131.551px; font-size: 12px; opacity: 0.0828413; z-index: 47;">阿尔茨海默症</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=SLE" class="yellow" zycate="jc" zyid="EX000269"
                    style="left: 285.566px; top: 190.467px; font-size: 12px; opacity: -0.0107012; z-index: 48;">SLE</a><a
                    href="http://localhost:8080/#/searchResult?keyWord=风湿症" class="blue" zycate="jb" zyid="ysj01426"
                    style="left: 330.413px; top: 246.596px; font-size: 12px; opacity: 0.0766935; z-index: 49;"></a>
            </div>
        </div>
        <img :src="imgSrc" width="100%" height="100%" alt="" />
        <div class="top-group" style="text-align: center; ">

            <SearchGroup></SearchGroup>
        </div>

        <el-drawer v-drawer title="" :visible.sync="drawer" :direction="direction" class="drawer" :size="size">
            <div class="bottom-group">
                <div class="right-panel">
                    <div class="top-container">
                        <div class="panel-title"><span>热门推荐🔥</span></div>
                    </div>
                    <div class="hot-trend">
                        <HotTrend></HotTrend>
                    </div>
                </div>
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

            </div>

        </el-drawer>

        <footer>
            <img :src="logoSrc" alt="" width="3%" height="3%" @click="showsomething" class="imgg" style="">
        </footer>
    </div>
</template>

<script>
import DocTag from '@/home/DocTag';
import DocThumb from '@/home/DocThumb'
import HotTrend from '@/home/HotTrend'
import SearchGroup from '@/home/SearchGroup'
import '@/api/script'
import Navv from '../components/Navv'
import '@/api/drawer'

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
            defaultAvatar: require("../assets/source/user_avater.png"),
            data: {},
            currentData: [],
            docshow: false,
            newshow: true,
            drawer: false,
            direction: 'btt',
            size: '80%',
            logoSrc: require("../assets/source/up.png"),
            tagsData: [
                { name: '新生儿黄疸', url: 'http://localhost:8080/#/searchResult?keyWord=新生儿黄疸', color: 'blue' },
                { name: 'Reye综合症', url: 'http://localhost:8080/#/searchResult?keyWord=Reye综合症', color: 'blue' },
                { name: '奥美拉唑', url: 'http://localhost:8080/#/searchResult?keyWord=奥美拉唑', color: 'fivecolor' },
                { name: '磷酸氯喹', url: 'http://localhost:8080/#/searchResult?keyWord=磷酸氯喹', color: 'fivecolor' },
                { name: '金花消痤丸', url: 'http://localhost:8080/#/searchResult?keyWord=金花消痤丸', color: 'green' },
                { name: '癌胚抗原(CEA)检测', url: 'http://localhost:8080/#/searchResult?keyWord=癌胚抗原(CEA)检测', color: 'yellow' },
                { name: '丘疹', url: 'http://localhost:8080/#/searchResult?keyWord=丘疹', color: 'red' }
            ]
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
            console.log("item.name:" + name)
            console.log("item.tagid:" + tagId)
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
        },
        showsomething() {
            this.drawer = true
        },
    }
}
</script>

<style lang="scss" scoped>
.homepage {
    width: 100%;
    // height: 100vh;
    //padding-bottom: 20px;
    //position: fixed;
    position: relative;
    background-image: url("../assets/source/bg1.png");
    //height: 100%;

    .top-group {
        //height: 340px;
        width: 100%;

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
                background: #69d0f2;
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
                    background: #a2f3f7;
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
                    background: #2faafc;
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

img {
    padding-top: 50px;
}

footer {
    position: fixed;
    bottom: 0;
    float: left;
    /* 4. 设置页脚position为absolute之后，再将其bottom值设为0，即可使其处于页面的底部 */
    width: 100%;
    cursor: pointer;

    .imgg {
        padding-left: -20%;
    }
}

.drawer .el-drawer__body {
    width: 400px;
    /* 设置宽度 */
    height: 300px;
}


.hotdots {
    width: 100%;
    height: 10%;
    position: absolute;
    left: 0%;
    top: 53%;
    justify-content: space-between;
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