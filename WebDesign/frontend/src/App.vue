<template>
  <div id="app">
    <!--    <div id="nav">-->
    <!--      <router-link to="/">Home</router-link> |-->
    <!--      <router-link to="/about">About</router-link>-->
    <!--    </div>-->
      <router-view />
      <ChatWindow/>

  </div>
</template>

<script>
import ChatWindow from './components/chat/chatAI.vue';

export default {
    components: {
        ChatWindow,
        // ChatWindowTest,
    },
    // ...
};
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
  position: relative;

  .ChatWindow >>> .chat-button {
    position: absolute;
    top: 20%;
    right: 20%;
    z-index: 9999; // 确保在页面的最上层
  }

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
