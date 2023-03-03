<template>
    <div class="home_wrap">
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

    export default {
        name: "PdfView",
        data() {
            return {
                pdfURL: BackendUrl() + '/files/view/' + this.$route.query.docId
            };
        },
        created() {
            this.getPdfText();
        },
        methods: {
            getPdfText() {
                let docId = this.$route.query.docId;
                this.pdfURL = BackendUrl() + '/files/view/' + docId
            }
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