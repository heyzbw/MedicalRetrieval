<template>
    <div>
        <div class="sssss">
            <div class="callback float" @mousedown="down" @touchstart="down" @mousemove="move" @touchmove="move"
                @mouseover="over" @mouseout="out" @mouseup="end" @touchend="end" @click="onClick" ref="fu"
                :class="bg == 1 ? 'bg1' : 'bg2'">
                <!-- <p @click="callback">返回</p> :style="{ 'background-image': img }"
                <img @click="callback" src="../assets/logocopy.png" alt /> -->
            </div>
            <div :style="{ left: left + 'px', top: top + 'px' }" v-if="menu" @mouseleave="out2" @mouseover.capture="over2"
                class="menuclass">
                <div class="titlea">
                    快捷导航 <i style="color: #409eff" class="el-icon-setting"></i>
                </div>
                <div class="boxa">
                    <div class="item" style="
                                                          background: linear-gradient(150deg, #accaff 0%, #3b88ec 100%);
                                                        ">
                        基础表统计
                    </div>
                    <div class="item" style="
                                                          background: linear-gradient(150deg, #e8d6ff 0%, #9f55ff 100%);
                                                        ">
                        分产业统计
                    </div>
                    <div class="item" style="
                                                          background: linear-gradient(150deg, #fdda45 0%, #fe6b62 100%);
                                                        ">
                        周报
                    </div>
                    <div class="item" style="
                                                          background: linear-gradient(150deg, #cefbc8 0%, #00aec5 100%);
                                                        ">
                        自营投资项目台账
                    </div>
                    <div class="item" style="
                                                          margin-top: 7px;
                                                          background: linear-gradient(150deg, #c5f8e6 0%, #10a465 100%);
                                                        ">
                        项目进度台账
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            left: 0,
            top: 40,
            bg: 1,
            menu: false,
            isLoading: false,
            flags: false, //控制使用
            position: {
                x: 0,
                y: 0,
            },
            nx: "",
            ny: "",
            dx: "",
            dy: "",
            xPum: "",
            yPum: "",
            movb: 1,
            num: 1,
        };
    },
    created() { },
    mounted() {
        this.left = this.$refs.fu.offsetLeft - 150;
        this.top = this.$refs.fu.offsetTop
    },
    methods: {
        out2() {
            this.menu = false;
        },
        over2() { },
        out() {
            this.bg = 2;
        },
        over() {
            this.menu = true;
            this.num = 2;
            this.bg = 1;
        },
        callback() {
            this.$router.go(-1);
        },
        onRefresh() {
            // window.location.reload();
            setTimeout((res) => {
                console.log(res);
                this.isLoading = false;
            }, 1000);
        },
        down() {
            this.flags = true;
            var touch;
            if (event.touches) {
                touch = event.touches[0];
            } else {
                touch = event;
            }
            this.position.x = touch.clientX;
            this.position.y = touch.clientY;
            this.dx = this.$refs.fu.offsetLeft;
            this.dy = this.$refs.fu.offsetTop;
        },
        move() {
            if (this.flags) {
                this.movb = 2;
                this.menu = false;
                var touch;
                if (event.touches) {
                    touch = event.touches[0];
                } else {
                    touch = event;
                }
                this.nx = touch.clientX - this.position.x;
                this.ny = touch.clientY - this.position.y;
                this.xPum = this.dx + this.nx;
                this.yPum = this.dy + this.ny;
                let width = window.innerWidth - this.$refs.fu.offsetWidth; //屏幕宽度减去自身控件宽度
                let height = window.innerHeight - this.$refs.fu.offsetHeight; //屏幕高度减去自身控件高度
                this.xPum < 0 && (this.xPum = 0);
                this.yPum < 0 && (this.yPum = 0);
                this.xPum > width && (this.xPum = width);
                this.yPum > height && (this.yPum = height);
                // if (this.xPum >= 0 && this.yPum >= 0 && this.xPum<= width &&this.yPum<= height) {
                this.$refs.fu.style.left = this.xPum + "px";
                this.$refs.fu.style.top = this.yPum + "px";
                this.left = this.xPum - 750;
                this.top = this.yPum;
                // }
                //阻止页面的滑动默认事件
                document.addEventListener(
                    "touchmove",
                    function () {
                        event.preventDefault();
                    },
                    false
                );
            }
        },
        //鼠标释放时候的函数
        end() {
            this.flags = false;
        },
        onClick() {
            if (this.movb == 2) {
                this.movb = 1;
            } else {
                this.menu = !this.menu;
            }
            // this.$emit("click");
        },
    },
};
</script>
<style scoped>
.bg1 {
    background-image: url("../assets/logocopy.png");
}

.bg2 {
    background-image: url("../assets/logocopy.png");
}

.callback p {
    font-size: 16px;
    color: #fff;
    background: rgba(56, 57, 58, 0.5);
    border-radius: 50%;
    text-align: center;
    width: 50px;
    height: 50px;
    line-height: 50px;
    font-family: PingFang SC;
    font-weight: 600;
    box-shadow: 0 0 10px #fff;
}

.callback img {
    display: block;
    width: 50px;
    height: 50px;
    box-shadow: 0 0 10px rgb(133, 129, 129);
    border-radius: 50%;
    background: #fff;
}

.callback {
    position: fixed;
    width: 80px;
    height: 80px;
    background-repeat: no-repeat;
    background-size: 100% 100%;
    top: 40px;
    left: 94%;
    z-index: 99999;
}

.float {
    position: fixed;
    touch-action: none;
    text-align: center;
    border-radius: 24px;
    line-height: 48px;
    color: white;
}

.menuclass {
    text-align: left;
    position: absolute;
    color: #000;
    width: 764px;
    background: #ffffff;
    box-shadow: 0px 6px 26px 1px rgba(51, 51, 51, 0.16);
    padding: 20px;
}

.sssss {
    position: relative;
    background-color: #000;
    right: 0;
    z-index: 99999;
}

.titlea {
    font-size: 18px;
    font-family: Microsoft YaHei-Bold, Microsoft YaHei;
    font-weight: bold;
    color: #333333;
}

.boxa {
    display: flex;
    flex-wrap: wrap;
    margin-top: 20px;
    z-index: 999999;
}

.item {
    width: 168px;
    height: 75px;
    border-radius: 4px 4px 4px 4px;
    font-size: 16px;
    font-family: Microsoft YaHei-Bold, Microsoft YaHei;
    font-weight: bold;
    color: #ffffff;
    text-align: center;
    margin-left: 7px;
    line-height: 75px;
}
</style>

