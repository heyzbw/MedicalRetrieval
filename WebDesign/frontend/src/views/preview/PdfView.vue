<template>
    <div class="home_wrap">
        <div>
            <!-- <p>{{ selectedText }}</p> -->
            <!-- <button @click="translateText">翻译</button> -->
        </div>
        <div v-show="view_flag" style="padding: 400px; color: #ffcc4f;">
            <div class="demo-spin-icon-load">
                <Icon type="md-refresh" style="font-size: 48px;" />
            </div>
            <div style="font-size:16px">加载中...</div>
        </div>
        <iframe :src="`pdf/web/viewer.html?file=${pdfURL}`" class="pdf-window" width="100%" height="100%" frameborder="no"
            id="myIframe">
        </iframe>

    </div>
</template>

<script>

import { BackendUrl } from '@/api/request'
import md5 from 'md5';
import axios from 'axios';


export default {
    name: "PdfView",

    data() {
        return {
            pdfURL: BackendUrl() + '/files/view/' + this.$route.query.docId,
            keyword: this.$route.query.keyword,
            selectedText: '',
            view_flag: false,
            translateResult: '',
            pageNum: this.$route.query.pageNum
        };
    },
    mounted() {
        //这是滑选事件
        this.getMessage();
        this.sendMessage();
        // this.getMessage1();
        // this.sendMessage1();
        // this.getSelectText();
        // this.translateText();
        // document.addEventListener('mouseup', () => {
        //     const selection = window.getSelection();

        //     let iframe = document.getElementById('myIframe');


        //     iframe.onload = function () {
        //         setTimeout(() => {
        //             console.log(iframe.contentWindow.getSelection().toString())
        //             let iframe = document.getElementById('myIframe');
        //             this.selectedText = iframe.contentWindow.getSelection().toString();
        //             console.log(iframe.contentWindow)
        //         }, 100);
        //     }
        //     if (selection.toString().length > 0) {
        //         this.selectedText = selection.toString();
        //     }
        // });
        // 搜索时 接收数据
    },
    created() {
        this.sendMessage();
        // this.sendMessage1();
        this.getPdfText();
        // this.getMessage1();
        this.getMessage();



        // this.getSelectText();
    },
    methods: {
        getPdfText() {
            let docId = this.$route.query.docId;
            this.keyword = this.$route.query.keyword;
            this.pageNum = this.$route.query.pageNum;
            this.pdfURL = BackendUrl() + '/files/view/' + docId;
        },

        sendMessage() {
            //获取iframe
            let vm = this;
            //获取iframe
            let iframe = document.getElementById('myIframe');
            //将滑选数据传入到iframe中
            // console.log('vm' + vm.keyword);
            var arr = [];
            arr[0] = vm.keyword
            arr[1] = vm.pageNum
            console.log(arr[1])
            iframe.contentWindow.postMessage(arr, '*');
        },

        // 接受数据
        getMessage() {
            //获取iframe
            let iframe = document.getElementById('myIframe');
            // iframe监听是否有数据传入，将传入的数据作为参数传递给pdf.js的find接口
            iframe.contentWindow.addEventListener('message', function (e) {
                //这里打印一下，看是否拿到了传入的数据
                // console.log("e" + e.data);
                console.log(iframe.contentWindow)
                // 这里打印的是pdf.js暴露出来的find接口

                iframe.onload = function () {
                    setTimeout(() => {
                        let iframe = document.getElementById('myIframe');
                        console.log(iframe.contentWindow.PDFViewerApplication);
                        // console.log(e.data[1])
                        // 输入查询数据
                        iframe.contentWindow.PDFViewerApplication.page = parseInt(e.data[1])

                        console.log(iframe.contentWindow.PDFViewerApplication.page)
                        iframe.contentWindow.PDFViewerApplication.findBar.findField.value = e.data[0];
                        // 要求查询结果全体高亮
                        iframe.contentWindow.PDFViewerApplication.findBar.highlightAll.checked = true;
                        // 上面两部已经OK，触发highlightallchange方法。
                        iframe.contentWindow.PDFViewerApplication.findBar.dispatchEvent('highlightallchange');
                        console.log(typeof (e.data[1]))
                        iframe.contentWindow.PDFViewerApplication.page = parseInt(e.data[1])
                        console.log(iframe.contentWindow.PDFViewerApplication.page)

                    }, 1000);
                    // setTimeout(() => {
                    //     let iframe = document.getElementById('myIframe');
                    //     console.log(iframe.contentWindow.PDFViewerApplication.page)
                    //     iframe.contentWindow.PDFViewerApplication.page = parseInt(e.data[1])
                    //     console.log(iframe.contentWindow.PDFViewerApplication.page)

                    // }, 1000);

                }
            }, false);

        },

        // sendMessage1() {
        //     //获取iframe
        //     let vm = this;
        //     //获取iframe
        //     let iframe = document.getElementById('myIframe');
        //     //将滑选数据传入到iframe中
        //     // console.log('vm' + vm.keyword);
        //     iframe.contentWindow.postMessage(vm.pageNum, '*');

        // },

        // // 接受数据
        // getMessage1() {
        //     //获取iframe
        //     let iframe = document.getElementById('myIframe');
        //     // iframe监听是否有数据传入，将传入的数据作为参数传递给pdf.js的find接口
        //     iframe.contentWindow.addEventListener('message', function (e) {
        //         //这里打印一下，看是否拿到了传入的数据
        //         // console.log("e" + e.data);
        //         // console.log(iframe.contentWindow)
        //         // 这里打印的是pdf.js暴露出来的find接口

        //         iframe.onload = function () {
        //             setTimeout(() => {
        //                 let iframe = document.getElementById('myIframe');
        //                 console.log(iframe.contentWindow.PDFViewerApplication);
        //                 // 输入查询数据
        //                 iframe.contentWindow.PDFViewerApplication.page = e.data
        //                 // iframe.contentWindow.PDFViewerApplication.findBar.findField.value = e.data;
        //                 // // 要求查询结果全体高亮
        //                 // iframe.contentWindow.PDFViewerApplication.findBar.highlightAll.checked = true;
        //                 // // 上面两部已经OK，触发highlightallchange方法。OK。全部完成，效果如文章开头，因为项目保密，所以就不这么着吧。
        //                 // iframe.contentWindow.PDFViewerApplication.findBar.dispatchEvent('highlightallchange');
        //             }, 200);

        //         }
        //     }, false);

        // },
        // 滑选事件注册： 获取鼠标选中的文本
        // getSelectText() {
        //     console.log("test" + this.selectedText)

        //     let iframe = document.getElementById('myIframe');
        //     let x = '';
        //     let y = '';
        //     let _x = '';
        //     let _y = '';
        //     // console.log("test" + this.selectedText)
        //     // iframe 加载完成后执行并将双击事件过滤掉，因为双击事件可能也触发滑选，所以为了不误操作，将其剔除
        //     // iframe.onload = function () {
        //     // 鼠标点击监听
        //     // setTimeout(() => {

        //     iframe.contentDocument.addEventListener('mousedown', function (e) {
        //         x = e.pageX;
        //         y = e.pageY;
        //     }, true);
        //     // 鼠标抬起监听
        //     iframe.contentDocument.addEventListener('mouseup', function (e) {
        //         _x = e.pageX;
        //         _y = e.pageY;
        //         // if (x == _x && y == _y) return; //判断点击和抬起的位置，如果相同，则视为没有滑选，不支持双击选中
        //         var choose = iframe.contentWindow.getSelection().toString();
        //         this.selectedText = choose;
        //     }, true);
        //     console.log("test" + this.selectedText)
        //     // }, 100);
        //     // };
        // },
        translateText() {
            const salt = Date.now();
            const appid = '20230312001596883';
            const key = '75c2j7YXfCFdiwiZO_0b';
            const from = 'auto';
            const to = 'zh';
            const sign = md5(`${appid}${this.selectedText}${salt}${key}`);
            // setTimeout(
            //     () => {
            axios.get('/api/trans/vip/translate', {
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                params: {
                    q: this.selectedText,
                    from: from,
                    to: to,
                    appid: appid,
                    salt: salt,
                    sign: sign
                }

            }).then(response => {
                // 处理响应
                console.log(response.data.trans_result[0].dst);
                this.translateResult = response.data.trans_result[0].dst;
            }).catch(error => {
                // 处理错误
                console.log(error);
                console.log(sign);
                console.log(this.selectedText);
                // console.log(params);
            }, 100)

            // });
        },

    }
}
</script>

<style lang="scss" scoped>
.home_wrap {
    margin-top: -10px;

    //padding: 15px;
    img {
        width: 100%;
    }

    height: 98.7vh;
}
</style>