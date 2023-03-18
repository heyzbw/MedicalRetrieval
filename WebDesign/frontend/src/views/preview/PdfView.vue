<template>
    <div class="home_wrap">
        <div>
            <p>{{ selectedText }}</p>
            <button @click="translateText">翻译</button>
        </div>
        <div v-show="view_flag" style="padding: 400px; color: #ffcc4f;">
            <div class="demo-spin-icon-load">
                <Icon type="md-refresh" style="font-size: 48px;"/>
            </div>
            <div style='font-size:16px'>加载中...</div>
        </div>
        <iframe :src="`pdf/web/viewer.html?file=${pdfURL}`"
                class="pdf-window"
                width="100%"
                height="100%"
                frameborder="no">

        </iframe>

    </div>
</template>

<script>

    import { BackendUrl } from '@/api/request'
    import md5 from 'md5';
    import axios from 'axios';

    export default {
        view_flag:true,
        name: "PdfView",
        data() {
            return {
                selectedText: ''
            }
        },
        data() {
            return {
                pdfURL: BackendUrl() + '/files/view/' + this.$route.query.docId,
                view: false
            };
        },
        created() {
            this.getPdfText();
        },
        methods: {
            getPdfText() {
                let docId = this.$route.query.docId;
                this.pdfURL = BackendUrl() + '/files/view/' + docId
            },
            translateText() {
                const salt = Date.now();
                const appid = '20230312001596883';
                const key = '75c2j7YXfCFdiwiZO_0b';
                const from = 'auto';
                const to = 'zh';
                const sign = md5(`${appid}${this.selectedText}${salt}${key}`);

                axios.get('http://api.fanyi.baidu.com/api/trans/vip/translate', {
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
                }).catch(error => {
                    // 处理错误
                    console.log(error);
                });
            }
        },
        mounted() {
            document.addEventListener('mouseup', () => {
                const selection = window.getSelection();
                if (selection.toString().length > 0) {
                    this.selectedText = selection.toString();
                }
            });
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