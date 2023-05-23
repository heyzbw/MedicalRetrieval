import Vue from 'vue';
import VueRouter from 'vue-router';
import App from './App.vue';
import router from './router/index.js';
import iView from 'view-design'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import jquery from "jquery";
import '../theme/my-theme.less'
import axios from 'axios'
import 'amfe-flexible';
//配置后端的访问地址

import * as echarts from 'echarts';
Vue.prototype.$ = jquery;
Vue.prototype.$echarts = echarts
Vue.use(VueRouter);
Vue.use(iView);
Vue.use(ElementUI);
Vue.prototype.$axios = axios

Vue.directive('dialogDrag', {
    bind(el, binding, vnode) {

    }
})

// Create a new Vue instance as an event bus
const EventBus = new Vue();
Vue.prototype.$eventBus = EventBus; // Mount the event bus to the Vue prototype chain

new Vue({
    el: '#app',
    router,
    render: h => h(App)
});

// import Vue from 'vue';

export const bus = new Vue();
