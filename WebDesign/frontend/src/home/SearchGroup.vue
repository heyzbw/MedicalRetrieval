<template>
    <div class="search-group">
        <div class="ulist">
            <!-- <div class="text-box">
                <div class="circle-cover"></div>
                医学检索

            </div>
            <div class="text-box1">
                <div class="circle-cover1"></div>
                一触即得
            </div> -->
            <div class="box">
                <div class="sc">
                    <p>医学文献</p>
                </div>
                <div class="sc1">
                    <p>一触即得</p>
                </div>
            </div>

            <!-- </ul> -->
        </div>
        <div class="search-zone">
            <!-- <div class="logo">
                <img :src="logoSrc" width="100%" height="100%" alt="" />
            </div> -->

            <div class="search-input">
                <div class="search-input-top">
                    <div class="search-selection" @click="routeTo">
                        <span>文库</span>
                    </div>
                    <input :placeholder="placeholder" v-model="searchValue" @focus="userInputFlag = true"
                        @blur="whenInputBlur" @keyup.enter="clickToSearch(searchValue)" @input="changeItem" ref="input">

                    <el-button type="primary" size="mini" style="margin-left: 10px" plain @click="search()">高级搜索</el-button>

                    <!-- 弹窗, 高级搜索 -->
                    <el-dialog v-dialogDrag title="高级搜索" :visible.sync="dialogFormVisible" class="dialog">

                        <el-input placeholder="请输入标题" v-model="title" class="input-with-select" style="width: ">
                            <el-select slot="prepend" placeholder="标题" style="width:100px" :disabled="true">
                                <el-option label="关键字" value="1"></el-option>
                                <el-option label="时间" value="2"></el-option>
                                <el-option label="标题" value="3"></el-option>
                            </el-select>

                            <el-button class="butt" slot="append" icon="el-icon-plus" style="margin:0 10px"
                                @click="addnum(index - 1)"></el-button>
                            <el-button class="butt" slot="append" icon="el-icon-minus" style=" "
                                :disabled="true"></el-button>
                        </el-input>


                        <el-input placeholder="请输入时间" v-model="time" class="input-with-select" style="">
                            <el-select slot="prepend" placeholder="时间" style="width:100px" :disabled="true">
                                <el-option label="关键字" value="1"></el-option>
                                <el-option label="时间" value="2"></el-option>
                                <el-option label="标题" value="3"></el-option>
                            </el-select>
                            <el-button class="butt" slot="append" icon="el-icon-plus" style="margin:0 10px"
                                @click="addnum(index - 1)"></el-button>
                            <el-button class="butt" slot="append" icon="el-icon-minus" style=" "
                                :disabled="true"></el-button>
                        </el-input>

                        <div style="margin-top: 15px;" v-for=" index in numbb">
                            <el-input placeholder="请内容" v-model="items[index - 1]" class="input-with-select" style="">
                                <el-select v-model="select[index - 1]" slot="prepend" placeholder="关键字" style="width:100px"
                                    :disabled="true">
                                    <el-option label="关键字" value="1"></el-option>
                                    <el-option label="时间" value="2"></el-option>
                                    <el-option label="标题" value="3"></el-option>

                                </el-select>
                                <el-select v-model="logi[index - 1]" slot="append" placeholder="and or"
                                    style="width:60px;margin:0 0px ;position:relative">
                                    <el-option label="与" value="1"></el-option>
                                    <el-option label="或" value="0"></el-option>
                                </el-select>
                                <el-button class="butt" slot="append" icon="el-icon-plus" style="margin:0 0px"
                                    @click="addnum(index - 1)"></el-button>
                                <el-button class="butt" slot="append" icon="el-icon-minus" style=" "
                                    @click="deletenum(index)"></el-button>

                            </el-input>
                        </div>
                        <div slot="footer" class="dialog-footer">
                            <el-button plain @click="dialogFormVisible = false">取 消</el-button>
                            <el-button type="danger" @click="savebtn">搜 索</el-button>
                        </div>
                    </el-dialog>


                    <div class="search-button"
                        style="width: 100px; line-height: 45px; display: flex; align-content: center; flex-wrap: wrap; justify-content: center;"
                        @click="clickToSearch(searchValue)">
                        <img :src="searchSrc" width="16px" height="16px" alt="" style="display: inline-block;" />
                    </div>
                </div>
                <div class="search-input-bottom" v-show="hotSearch.length">
                    <span class="title" style="font-width: 500;">搜索记录：</span>
                    <span class="search-tag" style="margin-left: 20px;" v-for="item in hotSearch"
                        @click="clickToSearch(item)">{{ item }}</span>
                </div>
                <div class="user-search-result" v-show="userInputFlag && userSearch.length > 0">
                    <div class="user-search-item" v-for="item in userSearch" @mousedown="preventBlur($event)">
                        <p @click="clickToUserSearch(item)">{{ item }}</p>
                        <div class="user-search-remove" @click="removeUserSearch(item)">删除</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'

import StatsRequest from "@/api/stats";

import maintainCondition from "../api/maintainCondition";
import { title } from 'process';



export default {
    name: "SearchGroup",
    data() {
        return {
            logoSrc: require("../assets/svg/newLogo.svg"),
            searchSrc: require("../assets/svg/search.svg"),
            placeholder: '请输入您想要查找的文档',
            hotSearch: [],
            searchValue: '',
            originUserSearch: [],
            userSearch: [],
            userInputFlag: false,
            hotshow: true,
            yuncishow: false,
            conditions: maintainCondition.conditions,
            dataList: [],
            items: [],
            numbb: 1,
            select: [],
            logi: [],
            time: null,
            advancedsearch: '',
            title: '',
            // 公共数据
            commonData: {
                id: "",
                invstscde: "", // 处理状态编码,
                invstsnam: "", // 处理状态名称
            },

            conditions: maintainCondition.conditions, //搜索条件

            dialogFormVisible: false, //高级搜索弹框显示隐藏
            sortDialogFormVisible: false, //排序搜索弹框显示隐藏
            invimtmcdeInputShow: true, //处理状态编码维护编码控制
            nums: "", //控制新增按钮只能新增一个
        };

    },
    created() {
        this.init();
    },
    mounted() {

    },
    methods: {
        routeTo() {
            this.$router.push('/doc')
        },
        init() {
            StatsRequest.getSearchHistory().then(response => {
                if (response.code === 200) {
                    this.hotSearch = response.data.hotSearch;
                    console.log(response.data)
                    this.originUserSearch = response.data.userSearch;
                    this.userSearch = this.originUserSearch.slice(0, 10);
                }
            }).catch(err => {
                console.log(err)
            })
        },

        whenInputBlur() {
            setTimeout(() => {
                this.userInputFlag = false
            }, 100)
        },
        clickToSearch(value) {
            if (value !== "") {
                this.$router.push({
                    path: '/searchResult',
                    query: {
                        keyWord: value
                    }
                })
            } else {
                this.routeTo()
            }
        },
        /**
         * 根据用户输入内容动态筛选可以呈现的备选列表
         */
        changeItem() {
            if (this.searchValue === null || this.searchValue === '') {
                this.userSearch = this.originUserSearch.slice(0, 10);
            }
            this.userSearch = this.originUserSearch.filter((el) => el.toLowerCase().includes(this.searchValue)).slice(0, 10);
        },

        preventBlur(e) {
            if (e && e.preventDefault) {
                e.preventDefault(); //阻止默认浏览器动作(W3C)
            }
        },

        clickToUserSearch(item) {
            this.searchValue = item
        },
        removeUserSearch(item) {
            let i = this.userSearch.indexOf(item)
            if (i > -1) {
                this.userSearch.splice(i, 1)
            }
            let j = this.originUserSearch.indexOf(item)
            if (j > -1) {
                this.originUserSearch.splice(j, 1)
                this.removeSearchHistory(item)
            }
        },
        async removeSearchHistory(searchWord) {
            let param = {
                userId: localStorage.getItem("id"),
                searchWord: searchWord
            }
            if (param.userId === null || param.searchWord === null) {
                return
            }
            await StatsRequest.removeSearchHistory(param).then().catch(err => this.$Message.error(err))
        },
        search() {
            this.dialogFormVisible = true;
        },
        // 高级搜索-新增
        //高级搜索-删除
        addnum() {
            this.numbb = this.numbb + 1;

        },
        deletenum(index) {
            if (this.numbb == 1) {
                this.info(false)
            }
            else {
                this.items.splice(index, 1);
                this.numbb = this.numbb - 1
            }

        },
        info(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : '请填写好内容'
            });
        },
        info1(nodesc) {
            this.$Notice.info({
                title: '通知信息',
                desc: nodesc ? '' : '时间和标题不能超过一个'
            });
        },
        // 高级搜索-保存
        savebtn() {
            console.log(this.items);
            console.log(this.logi);
            //console.log(this.select);
            // let items = []
            // let luoji = []
            // let mn = 0
            // let tm = 0
            // for (let i = 0; i < this.numbb; i++) {
            //     if (this.select[i] == '1') {
            //         items.push(this.items[i])
            //         luoji.push(this.logi[i])
            //     }
            //     else if (this.select[i] == '2') {
            //         this.time = this.items[i];
            //         mn = mn + 1
            //     } else {
            //         this.title = this.items[i]
            //         tm = tm + 1
            //     }

            // }
            // console.log(items);
            // console.log(mn);
            // if (mn > 1 || tm > 1) {
            //     this.info1(false)
            // }
            // else {
            if (this.items.length == 0) {
                this.info(false)
            }
            else {
                if (this.items.length != 1) {
                    for (let i = 0; i < this.items.length; i++) {
                        if (i == this.items.length - 1) {
                            if (this.logi[i] == 1 || this.logi[i - 1] == 1) {
                                console.log(this.items[i])
                                this.advancedsearch = this.advancedsearch + ' | ' + this.items[i];
                            }
                            else {
                                console.log(this.items[i])
                                this.advancedsearch = this.advancedsearch + this.items[i];
                            }
                        }
                        else {
                            if (this.logi[i] == 0 || this.logi[i + 1] == 0) {
                                this.advancedsearch = this.advancedsearch + this.items[i] + ' | ';
                                console.log(this.items[i])
                            }
                            else {
                                let m = 0
                                let h = 1
                                this.advancedsearch = this.advancedsearch + '( '
                                for (let n = i; n < this.items.length; n++) {
                                    if (parseInt(this.logi[n + 1]) * h != 0 && (n + 1) != this.items.length) {
                                        console.log(parseInt(this.logi[n + 1]))
                                        h = this.logi[n] * h
                                        m = m + 1
                                        this.advancedsearch = this.advancedsearch + this.items[n] + ' & '
                                    }
                                    else {
                                        this.advancedsearch = this.advancedsearch + this.items[n] + ' ) ';
                                        if ((n + 1) != this.items.length) {
                                            this.advancedsearch = this.advancedsearch + '| '
                                        }
                                        break
                                    }
                                }
                                console.log(this.items[i])
                                i = i + m;
                            }
                        }
                    }
                } else {
                    this.advancedsearch = this.items[0]
                }
                console.log(this.advancedsearch);
                if (this.time != null) {
                    this.time = parseInt(this.time);
                }

                // console.log(typeof (this.time));
                // console.log(this.time)
                this.$router.push({
                    path: '/superSearch',
                    query: {
                        keyWord: this.advancedsearch,
                        time: this.time,
                        title: this.title,
                    }
                })
            }

            // }
            this.advancedsearch = '';
        },



    }
}
</script>

<style lang="scss" scoped>
.search-group {
    width: 100%;
    height: 440px;
    position: absolute;
    left: 0;
    top: 0;
    padding-top: 25%;
    //border: 2px solid #000;


    //display: inline-block;
    //border: 2px solid #000;

    .tag {
        //display: inline-block;
        font-size: 34px;
        font-family: PingFangSC-Medium, PingFang SC, serif;
        font-weight: 500;
        color: #000000;
        padding: 14px 16px 14px 16px;
    }


    .search-zone {
        width: 745px;
        margin: auto;
        margin-right: 25%;
        display: flex;
        justify-content: flex-start;

        .logo {
            width: 300px;
            height: 45px;
            margin-right: 18px;
        }

        .search-input {
            position: relative;

            .search-input-top {
                width: 660px;
                height: 45px;
                background-color: #fff;
                border-radius: 8px;
                border: 2px solid #000;
                display: flex;
                justify-content: flex-start;
                align-content: center;

                .search-selection {
                    width: 140px;
                    border-right: 1.5px solid #000;
                    margin-right: 5px;
                    height: 43px;
                    line-height: 45px;
                    display: inline-block;
                    vertical-align: middle;

                    &:hover {
                        cursor: pointer;
                        color: #8bdbfb;
                    }

                    span {
                        font-size: 14px;
                        font-family: PingFangSC-Medium, PingFang SC, serif;
                        font-weight: 500;
                        color: #000000;
                        line-height: 20px;

                        &:hover {
                            color: #3a7bf2;
                        }
                    }
                }

                .dialog {
                    .input-with-select .el-input-group__prepend {
                        background-color: #fff;
                        width: 55%;
                    }

                }


                input {
                    height: 41px;
                    width: 400px;
                    text-decoration: none;

                    outline: none;
                    border: none;
                }

                .search-button {
                    &:hover {
                        cursor: pointer;
                        background-color: #72a3ec;
                        border-radius: 0 8px 8px 0;
                    }
                }
            }

            .search-input-bottom {
                margin-top: 20px;
                padding-left: 5px;
                text-align: left;
                font-size: 14px;
                line-height: 20px;

                .title {
                    font-family: PingFangSC-Medium, PingFang SC, serif;
                    font-weight: 500;
                    color: #464646;
                    line-height: 20px;
                }

                .search-tag {
                    &:hover {
                        cursor: pointer;
                    }
                }
            }

            .user-search-result {
                position: absolute;
                background-color: rgba(255, 255, 255, 0.8);
                width: 440px;
                border-radius: 12px;
                left: 120px;
                top: 50px;
                padding: 8px 0px;
                text-align: left;

                z-index: 100;

                .user-search-item {
                    line-height: 24px;
                    font-size: 14px;
                    padding: 2px 8px;
                    position: relative;

                    p {
                        overflow: hidden;
                        // 下面是显示几行写数字几就行
                        -webkit-line-clamp: 1;
                        display: -webkit-box;
                        -webkit-box-orient: vertical;
                        text-overflow: ellipsis;
                        width: calc(100% - 20px);
                    }

                    &:hover {
                        background-color: #fff;
                        cursor: pointer;
                    }

                    .user-search-remove {
                        position: absolute;
                        right: 0px;
                        top: 0px;
                        line-height: 28px;
                        font-size: 12px;
                        color: blue;
                        padding: 0px 8px;
                    }
                }
            }
        }
    }
}

.box {
    width: 20%;
    height: 10%;
    margin: -10% 0 -4% 15%;
    //display: flex;
    position: relative;
    font-style: italic;

}

.sc {
    width: 300px;
    height: 200px;
    //text-align: center;
    //position: absolute;
    //display: flex;
    top: 3%;
    left: 40%;
    position: relative;
    margin-top: -5%;
    margin-left: 15%;
    margin-bottom: 0%
}

.sc1 {
    width: 300px;
    height: 200px;
    //text-align: center;
    position: relative;
    //position: absolute;
    top: 3%;
    left: 65%;
    margin-top: -30%;
    margin-left: 23%
}

.sc p {
    font-size: 48px;
    line-height: 50px;
    letter-spacing: .1em;
    transform: translate(0);
}

.sc1 p {
    font-size: 48px;
    line-height: 50px;
    letter-spacing: .1em;
    transform: translate(0);
}

.sc p:nth-child(1) {
    top: 75%;
    left: 35%;
    animation: example1 2s ease-out 1s backwards;
}

.sc1 p:nth-child(1) {
    animation: example2 2s ease-in-out 4s backwards;
}

@keyframes example1 {
    0% {
        transform: translate(-60px);
        opacity: 0;
    }

    50% {
        transform: translate(-30px);
        opacity: .5;
    }

    100% {
        transform: translate(0);
        opacity: 1;
    }
}


@keyframes example2 {
    0% {
        opacity: 0;
    }

    40% {
        opacity: .8;
    }

    45% {
        opacity: .3;
    }

    50% {
        opacity: .8;
    }

    55% {
        opacity: .3;
    }

    60% {
        opacity: .8;
    }

    100% {
        opacity: 1;
    }
}
</style>