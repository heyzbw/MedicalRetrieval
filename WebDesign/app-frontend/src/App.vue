<template>
  <div id="app">

    <router-view />
  </div>
</template>



<script>
export default {
  created() {
    /*** 手机返回键监听 ***/
    mui.init({
      // 监听Android手机的back、menu按键
      keyEventBind: {
        backbutton: true, //Boolean(默认true)关闭back按键监听
      }
    })
    var first = null;
    // 给手机的back按键重新绑定监听(1s内，连续点击两次返回键，则退出应用)
    mui.back = () => {
      // 首次按返回键
      if (!first) {
        first = new Date().getTime();//记录第一次按返回键的时间
        if (this.$route.name == 'home') {//判断当前是否为首页(无后退页)
          mui.toast("再按一次退出应用");
        }
        history.go(-1); // 返回到上一页
        setTimeout(() => {
          // 1s后清除
          first = null;
        }, 1000);
      } else {
        if (new Date().getTime() - first < 1000) {
          // 连续按两次返回键且时间小于1s
          plus.runtime.quit(); //退出app
        }
      }
    }
  }
}



</script>

<style lang="scss">
.information {
  position: fixed;
  top: 0;
  left: calc(50% - 300px);
  height: 30px;
  width: 600px;
  background-color: rgba(#fff, 0.2);
  line-height: 30px;
  color: #333;
  z-index: 999;

  a {
    color: #408FFF;

    &:hover {
      cursor: pointer;
      color: blue;
    }
  }
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  background-color: #f8f6f9;
}

#nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
