<template>
    <div class="nav-container">
        <div class="nav-logo" @click="$router.push('/')">
            <img :src="logoSrc" width="80%" height="80%" alt="" />
        </div>

        <div class="nav-setting" v-show="true">
            <Button type="text" @click="$router.push('/admin/newDocument1')">
                + 添加文档
            </Button>
            <Button type="text" @click="$router.push('/admin/allDocuments')">
                <Icon type="ios-keypad" />
            </Button>
        </div>

    </div>
</template>

<script>
export default {
    name: "Nav",
    data() {
        return {
            buttonSize: "min",
            logoSrc: require("../assets/sparrow2.png"),
        }
    }
}
</script>

<style scoped>
.nav-container {
    width: 100%;
    height: 50px;
    line-height: 50px;
    background-color: #ffffff;
    color: rgba(208, 164, 1, 100);
    font-family: Roboto, serif;
    float: left;
    box-shadow: 0px 0px 8px 2px rgba(0, 0, 0, 0.2);
    top: 0;
    left: 0;
    position: fixed;
    z-index: 998;
}

.nav-logo {
    line-height: 50px;
    color: rgba(87, 77, 24, 100);
    font-size: 28px;
    text-align: left;
    /*width: 200px;*/
    float: left;
    cursor: pointer;

    font-family: Helvetica-Bold, Helvetica;
    font-weight: bold;
    color: #6c6025;
    /*color: red;*/

    padding-left: 2%;
}

.nav-setting {
    float: right;
}

p {
    font-size: 2em;
    top: 0;
    left: 12%;
    font-style: oblique;
    color: rgba(255, 255, 255, 0.3);
    background-image: url(../assets/source/greneenpuls.png);

    letter-spacing: 8px;
    /* 注意添加私有前缀 */
    -webkit-background-clip: text;
    font-weight: 50;
    animation: wave 30s linear infinite;
    position: fixed;
}

@keyframes wave {
    from {
        background-position: 0 0;
    }

    to {
        background-position: 1000px 0;
    }
}
</style>