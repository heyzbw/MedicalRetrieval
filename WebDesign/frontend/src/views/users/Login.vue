<template>
    <div class="container">
        <div class="title2">MedicalRetrieval</div>

        <div class="title1">麻雀</div>

        <img class="icon" src="../../assets/icon.png" />

        <div class="login-wrapper">
            <div class="header">登 录</div>
            <div class="form-wrapper">
                <input type="text" v-model="username" name="username" placeholder="用户名" class="input-item">
                <input type="password" v-model="pwd" name="password" placeholder="密码" class="input-item">
                <div style="font-size: 12px; color: #2c3e50">
                    试用账号：admin123， 密码：admin123
                    <Icon type="md-copy" style="cursor: pointer" @click="tian" />
                </div>
                <div class="btn" @click="login">登录</div>
            </div>
            <div class="msg">
                还没有账号？
                <!--                <a href="#/registry">现在注册</a>-->
                <router-link to="/registry">现在注册</router-link>
            </div>
        </div>
    </div>
</template>

<script>

import UserRequest from '@/api/user'

export default {
    name: "login",
    data() {
        return {
            username: "",
            pwd: "",
        }
    },
    methods: {
        tian() {
            this.username = 'admin123';
            this.pwd = 'admin123';
        },
        login() {
            let params = {
                "username": this.username,
                "password": this.pwd
            }
            UserRequest.postUserLogin(params).then(
                response => {
                    if (response.data == null) {
                        this.$Message.error('登录失败，请重试！');
                    } else {
                        console.log(response.data)
                        localStorage.setItem("token", response.data.token)
                        localStorage.setItem("id", response.data.userId)
                        localStorage.setItem("username", response.data.username)
                        localStorage.setItem("avatar", response.data.avatar)
                        localStorage.setItem("type", response.data['type'] || '普通用户');
                        this.$router.push({
                            path: '/',
                            query: {
                                userName: this.userName
                            }
                        })
                    }
                }
            )
        }
    }
}

</script>

<style scoped>
.container {
    height: 100vh;
    width: 100%;
    background-image: url("../../assets/source/background.png");
    background-size: 100%;
}

.login-wrapper {
    background-color: #fff;
    width: 358px;
    height: 588px;
    border-radius: 15px;
    padding: 0 50px;
    position: relative;
    left: 80%;
    top: 50%;
    transform: translate(-50%, -50%);
}

.header {
    font-size: 38px;
    font-weight: bold;
    text-align: center;
    line-height: 200px;
}

.input-item {
    display: block;
    width: 100%;
    margin-bottom: 20px;
    border: 0;
    padding: 10px;
    border-bottom: 1px solid rgb(128, 125, 125);
    font-size: 15px;
    outline: none;
}

.input-item::placeholder {
    text-transform: uppercase;
}

.btn {
    text-align: center;
    padding: 10px;
    width: 100%;
    margin-top: 40px;
    background-image: linear-gradient(to right, #eaeea6, #c2fbce);
    color: #fff;
    cursor: pointer;
}

.msg {
    text-align: center;
    line-height: 88px;
}

a {
    text-decoration-line: none;
    color: #abc1ee;
}

.title2 {
    color: #000;
    text-align: left;
    font: 400 64px/80px "Vast Shadow", sans-serif;
    position: absolute;
    width: 791px;
    height: 103px;
    left: 10%;
    top: 50%;
    display: flex;
    align-items: center;
    justify-content: flex-start;
}

.title1 {
    color: #000;
    text-align: center;
    font: 700 222px/289px "Rounded Mplus 1c Bold", sans-serif;
    position: absolute;
    left: 8%;
    top: 20%;
    width: 518px;
    height: 308px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.icon {
    width: 315px;
    height: 301px;
    position: absolute;
    left: 36%;
    top: 20%;
}
</style>