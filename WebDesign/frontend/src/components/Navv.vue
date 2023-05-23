<template>
    <div class="nav-container">
        <div class="nav-logo" @click="$router.push('/')">
            <img :src="logoSrc" width="30%" height="30%" alt="" />
        </div>
        <div class="text-box">
            <div class="circle-cover"></div>
            MeidcalRetrieval
        </div>



      <div class="sd">
            <div @click="$router.push('/')">首页</div>
            <div> / </div>
            <div @click="logout()">登录</div>
        </div>

        <div class="user-zone" v-if="true">
            <Dropdown>
                <a class="user-tag" href="javascript:void(0)" style="text-align: center; width: 36px;">
                    <img :src="userAvatar" alt="">
                </a>
                <template #list>
                    <DropdownMenu>
                        <DropdownItem @click.native="$router.push('/admin/allDocuments')">系统管理</DropdownItem>
<!--                        <DropdownItem @click.native="$router.push('/userPage')">个人主页</DropdownItem>-->
                        <DropdownItem @click.native="click_user_page()">个人主页</DropdownItem>
                        <DropdownItem @click.native="logout()" divided>退出登录</DropdownItem>
                    </DropdownMenu>
                </template>
            </Dropdown>
        </div>
        <div class="user-zone" v-else>
            <a class="user-tag" href="javascript:void(0)" style="text-align: center; width: 36px;"
                @click="$router.push('/login')">
                <img :src="defaultAvatar" alt="">
            </a>
        </div>
    </div>
</template>

<script>
const { BackendUrl } = require("@/api/request");

export default {
    name: "Navv",
    data() {
        return {
            buttonSize: "min",
            defaultAvatar: require("../assets/source/user.png"),
            userAvatar: require("../assets/source/user.png"),
            logoSrc: require("../assets/newlogo.png"),
        }
    },
    computed: {
        ad() {
            let item = localStorage.getItem("token");
            return (item === null || item === undefined || item === "");
        }
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
        logout() {
            localStorage.clear()
            this.$router.push({
                name: 'Login'
            })
        },
        click_user_page(){
            // 登录过了就跳转到个人主页
            let userId = localStorage.getItem("id")
            if(userId){
                this.$router.push({
                    path: '/userPage'
                })
            }
            // 否则，跳转到登录页面
            else{
                this.$router.push({
                    path: '/login'
                })
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.nav-container {
    position: fixed;
    z-index: 100;
    width: 100%;
    height: 70px;
    display: flex;
    align-items: center;
    font-size: 20px;
    color: #2c3e50;
    background: rgb(248, 248, 248);
    box-shadow: 0px 0px 8px 2px rgba(0, 0, 0, 0.3);
}

.sd div {
    display: inline-block;
}

.sd {
    right: 180px;
    cursor: pointer;
    font-size: 18px;
    position: absolute;
    display: flex;
}

.nav-logo {
    line-height: 50px;
    color: rgba(87, 77, 24, 100);
    font-size: 28px;
    text-align: left;
    /*width: 200px;*/
    float: left;
    cursor: pointer;
    padding: 20px 0 0 20px;
    font-family: Helvetica-Bold, Helvetica;
    font-weight: bold;
    color: #6c6025;
    /*color: red;*/

    padding-left: 10%;
}

.user-zone {
    position: absolute;
    right: 80px;
    top: 10px;
    display: flex;
    justify-content: flex-start;
    padding: 5px 5px 0px 5px;

    span {
        height: 36px;
        line-height: 36px;
        font-size: 14px;
        font-family: PingFangSC-Regular, PingFang SC, serif;
        font-weight: 400;
        color: #000000;
        padding-right: 10px;
    }

    .user-tag {

        border-radius: 36px;
        //background-color: #ffffff;
        box-sizing: content-box;

        img {
            border-radius: 38px;
            height: 36px;
            width: 36px;
            box-shadow: 0 0 4px #bbbbbb;
        }
    }

    &:hover {
        cursor: pointer;
        background-color: rgba(#fff, 0.2);
        border-radius: 8px;
    }
}

.nav-setting {
    float: right;
}

.text-box {
    width: 300px;
    height: 60px;
    color: black;

    line-height: 60px;
    font-size: 36px;
    font-style: italic;
    font-weight: bold;
    position: relative;
    top: 45%;
    right: 5%;
    transform: translate(-50%, -50%);

    overflow: hidden;
}

.circle-cover {
    width: 700px;
    height: 200px;
    background-color: rgb(240, 240, 240);

    position: absolute;
    left: 0;
    top: -70px;
    /*top=circle-cover.height/2+text-box.height/2*/

    box-shadow: 0 0 100px 10px rgb(218, 218, 218);

    animation: move-cover 3s infinite ease-in-out;

}

@keyframes move-cover {
    0% {
        left: 0;
    }

    50% {
        left: 50%;
    }

    100% {
        left: 100%;
    }
}
</style>