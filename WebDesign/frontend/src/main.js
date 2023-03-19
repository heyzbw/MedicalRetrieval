import Vue from 'vue';
import VueRouter from 'vue-router';
import App from './App.vue';
import router from './router/index.js';
import iView from 'view-design'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import Api from './api/index';
import '../theme/my-theme.less'
import axios from 'axios'
//配置后端的访问地址

Vue.use(VueRouter);
Vue.use(iView);
Vue.use(ElementUI);



Vue.prototype.$axios = Api;

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