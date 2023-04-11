<template>
    <div class="nav-container">
        <div class="nav-logo" @click="$router.push('/')">
            <img :src="logoSrc" width="70%" height="70%" alt="" />
        </div>
        <div class="sd">
            <div @onclick="$router.push('/')">首页</div>
            <div> / </div>
            <div @click="logout()">登陆</div>
        </div>
        <div class="user-zone" v-if="true">
            <Dropdown>
                <a class="user-tag" href="javascript:void(0)" style="text-align: center; width: 36px;">
                    <img :src="userAvatar" alt="">
                </a>
                <template #list>
                    <DropdownMenu>
                        <DropdownItem @click.native="$router.push('/admin/allDocuments')">系统管理</DropdownItem>
                        <DropdownItem @click.native="$router.push('/userPage')">个人主页</DropdownItem>
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
                this.defaultAvatar = BackendUrl() + "/files/image2/" + value;
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
        }
    }
}
</script>

<style lang="scss" scoped>
.nav-container {
    position: fixed;
    z-index: 100;
    width: 100%;
    height: 60px;
    display: flex;
    align-items: center;
    font-size: 20px;
    color: #2c3e50;
    background: #ffff;
    box-shadow: 0px 0px 8px 2px rgba(0, 0, 0, 0.3);
}

.sd div {
    display: inline-block;
}

.sd {
    right: 15%;
    cursor: pointer;
    font-size: 26px;
    position: absolute;
    display: flex;
}

.nav-logo {
    //line-height: 80px;
    //color: rgba(87, 77, 24, 100);
    //font-size: 28px;
    //text-align: left;
    width: 200px;
    float: left;
    cursor: pointer;
    //padding: 2px 0 0 0px;


}

.user-zone {
    position: absolute;
    right: 10px;
    top: 7px;
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
</style>