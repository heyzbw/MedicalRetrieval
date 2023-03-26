<template>
    <div class="search-group">
        <!-- <link href="../assets/source/lczs-index.css" rel="stylesheet" type="text/css"> -->

        <div class="search-zone">
            <div class="logo">
                <img :src="logoSrc" width="100%" height="100%" alt="" />
            </div>

            <div class="search-input">
                <div class="search-input-top">
                    <div class="search-selection" @click="routeTo">
                        <span>全部</span>
                    </div>
                    <input :placeholder="placeholder" v-model="searchValue" @focus="userInputFlag = true"
                        @blur="whenInputBlur" @keyup.enter="clickToSearch(searchValue)" @input="changeItem" ref="input">

                    <button class="subNav-btn btn2 currentDd-btn currentDt-btn"></button>
                    <div id="mb"></div>
                    <div class="showbox2 navContent-btn">
                        <div class="close"></div>
                        <div id="serachgaoji">
                            <h3>高级检索</h3>

                            <div class="s-condition fL w100"><select>
                                    <option value="name">疾病名</option>
                                    <option value="DIICD">ICD</option>
                                    <option value="DISpeciality">类别</option>
                                    <option value="DIConcurrent_Disease">并发症</option>
                                    <option value="DISimilar_Disease">鉴别诊断</option>
                                    <option value="DITreat_Drug">治疗药物</option>
                                    <option value="DI_Examination">检查</option>
                                    <option value="DIClinical_Symptoms">症状</option>
                                </select><input style="width:53%;margin:0 15px 0 9px ;"><select style="width:50px;">
                                    <option value="1">与</option>
                                    <option value="0">或</option>
                                </select><button onclick="addinput()"><img src="../assets/source/seach-gj03.png"></button>
                            </div>
                            <div class="s-condition fL w100"><select>
                                    <option value="name">疾病名</option>
                                    <option value="DIICD">ICD</option>
                                    <option value="DISpeciality">类别</option>
                                    <option value="DIConcurrent_Disease">并发症</option>
                                    <option value="DISimilar_Disease">鉴别诊断</option>
                                    <option value="DITreat_Drug">治疗药物</option>
                                    <option value="DI_Examination">检查</option>
                                    <option value="DIClinical_Symptoms">症状</option>
                                </select><input style="width:53%;margin:0 15px 0 9px ;"><select style="width:50px;">
                                    <option value="1">与</option>
                                    <option value="0">或</option>
                                </select><button onclick="addinput()"><img src="../assets/source/seach-gj03.png"></button>
                                <button onclick="remove(this)"><img src="../assets/source/seach-gj04.png"></button>
                            </div>
                        </div>
                        <div class="s-type fL w100">
                            <!-- <label>类型 : </label> -->
                            <!--  <a href="###"><input  name="aaaa" type="radio" checked="checked" /><span>全部</span></a> -->
                            <a><input name="aaaa" type="radio" checked="checked" value="jb"><span>疾病</span></a>
                            <a><input name="aaaa" type="radio" value="yw"><span>药物</span></a>
                            <a><input name="aaaa" type="radio" value="jc"><span>检查</span></a>
                            <a><input name="aaaa" type="radio" value="zz"><span>症状</span></a>
                            <a><input name="aaaa" type="radio" value="ss"><span>手术</span></a>
                            <a><input name="aaaa" type="radio" value="zlzn"><span>诊疗指南</span></a>
                            <a><input name="aaaa" type="radio" value="blbg"><span>病例报告</span></a>
                            <a><input name="aaaa" type="radio" value="hzxz"><span>患者教育</span></a>
                        </div>
                        <div class="btm-center">
                            <button><img src="../assets/source/search.png"></button>
                        </div>

                        <div class="rg-button fL">
                            <button><img src="../assets/source/search.png"></button>
                        </div>
                    </div>

                    <div class="search-button"
                        style="width: 100px; line-height: 45px; display: flex; align-content: center; flex-wrap: wrap; justify-content: center;"
                        @click="clickToSearch(searchValue)">
                        <img :src="searchSrc" width="16px" height="16px" alt="" style="display: inline-block;" />
                    </div>
                </div>
                <div class="search-input-bottom" v-show="hotSearch.length">
                    <span class="title" style="font-width: 500;">推荐搜索：</span>
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

import StatsRequest from "@/api/stats";
// import '@/api/sign-in'
import '@/api/jquery'
import '@/assets/source/lczs-index.css'
// import '@/api/lczs-index'


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
            userInputFlag: false
        }
    },
    created() {
        this.init();
    },
    mounted() {
        // this.$nextTick(() => {
        //     this.$refs.input.focus()
        // })
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
            console.log("待查询的关键词为：" + value)
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
        }
    }
}
</script>

<style lang="scss" scoped>
.search-group {
    width: 100%;
    height: 340px;
    position: absolute;
    left: 0;
    top: 0;
    padding-top: 100px;

    .search-zone {
        width: 745px;
        margin: auto;
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
                width: 560px;
                height: 45px;
                background-color: #fff;
                border-radius: 8px;
                border: 1px solid #000;
                display: flex;
                justify-content: flex-start;
                align-content: center;

                .search-selection {
                    width: 140px;
                    border-right: 1px solid #aaa;
                    margin-right: 5px;
                    height: 43px;
                    line-height: 45px;
                    display: inline-block;
                    vertical-align: middle;

                    &:hover {
                        cursor: pointer;
                        color: #8d7b25;
                    }

                    span {
                        font-size: 14px;
                        font-family: PingFangSC-Medium, PingFang SC, serif;
                        font-weight: 500;
                        color: #000000;
                        line-height: 20px;

                        &:hover {
                            color: #8d7b25;
                        }
                    }
                }

                input {
                    height: 43px;
                    width: 400px;
                    text-decoration: none;
                    outline: none;
                    border: none;
                }

                .search-button {
                    &:hover {
                        cursor: pointer;
                        background-color: #fceac8;
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
</style>