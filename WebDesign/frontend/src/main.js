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
//配置后端的访问地址


import * as echarts from 'echarts';
Vue.prototype.$ = jquery;
Vue.prototype.$echarts = echarts
Vue.use(VueRouter);
Vue.use(iView);
Vue.use(ElementUI);
Vue.prototype.$axios = axios


// Vue.prototype.$axios = Api;

// The routing configuration
// const RouterConfig = {
//     routes: Routers
// };
// const router = new VueRouter(RouterConfig);

new Vue({
    el: '#app',
    router,
    render: h => h(App)
});