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
            id='myIframe' @mousemove="handleMouseSelect" style="">
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
      pageNum: this.$route.query.pageNum,
      high_light_content: this.$route.query.content
    };
  },
  watch() {
    this.getSelectText();
  },
  mounted() {
     //这是滑选事件

    let vm = this;

    // this.getSelectText();
    this.getMessage();

    let iframe = document.getElementById('myIframe');

    iframe.onload = function() {
       // 在这里调用 getSelectText 方法
      vm.getSelectText();

      // document.body.addEventListener("click", getWord, false);
      let x, y;
      document.getElementById("myIframe").contentWindow.onmousedown = function (event) {
        x = event.pageX;
        y = event.pagey;
      }
      document.getElementById("myIframe").contentWindow.onmouseup = function (event) {
        let new_x = event.pageX;
        let new_y = event.pagey;
        if (x == new_x && y == new_y) {
          //执行点击事件操作
        } else {
          // 选中操作
          getWord()
        }
      }
      // console.log("要跳转到的页面是：",vm.pageNum)
      // document.getElementById("myIframe").contentWindow.PDFViewerApplication.page = vm.pageNum

      // this.translateText();
      document.addEventListener('mouseup', () => {
        const selection = window.getSelection();
        let iframe = document.getElementById('myIframe');
        iframe.onload = function () {
          setTimeout(() => {
            console.log(iframe.contentWindow.getSelection().toString())
            let iframe = document.getElementById('myIframe');
            this.selectedText = iframe.contentWindow.getSelection().toString();
            // iframe.contentWindow.PDFViewerApplication.page = vm.pageNum

            // console.log(iframe.contentWindow)
          }, 100);
        }
        if (selection.toString().length > 0) {
          this.selectedText = selection.toString();
        }
      });
      vm.sendMessage();
    };


    //这是滑选事件
    // vm.getSelectText();
    function getWord() {

      // iframe中获取选中文字的方法
      var word = document.getElementById("myIframe").contentWindow.getSelection().toString();

      console.log(word)
      vm.$emit('func', word)
    }

    // 搜索时 接收数据
  },
  created() {
    //   this.sendMessage();
    //   // this.sendMessage1();
    //
    //   // this.getMessage1();
    // this.getMessage();
    // this.getSelectText()
    // this.getPdfText();

    //this.getSelectText();
  },
  methods: {
    getDateTime() {
      return parseInt(new Date().getTime());
    },
    getPdfText() {
      console.log("method pdfText")
      let docId = this.$route.query.docId;
      this.keyword = this.$route.query.keyword;
      this.pageNum = this.$route.query.pageNum;
      this.high_light_content = this.$route.query.content,
          this.pdfURL = BackendUrl() + '/files/view/' + docId;

    },

    sendMessage() {
      //获取iframe
      let vm = this;
      //获取iframe
      let iframe = document.getElementById('myIframe');

      this.pdfURL = BackendUrl() + '/files/view/' + this.$route.query.docId

      //将滑选数据传入到iframe中
      // console.log('vm' + vm.keyword);
      var arr = [];
      arr[0] = vm.keyword
      arr[1] = vm.pageNum
      arr[2] = vm.high_light_content
      console.log(arr)
      iframe.contentWindow.postMessage(arr, '*');
    },

    // 接受数据
    getMessage() {
      let vm = this;
      //获取iframe
      let iframe = document.getElementById('myIframe');
      // iframe监听是否有数据传入，将传入的数据作为参数传递给pdf.js的find接口
      this.pdfURL = BackendUrl() + '/files/view/' + this.$route.query.docId

      iframe.contentWindow.addEventListener('message', function (e) {
        //这里打印一下，看是否拿到了传入的数据


        setTimeout(() => {
          let iframe = document.getElementById('myIframe');
          console.log(iframe.contentWindow.PDFViewerApplication);

          // 输入查询数据
          // iframe.contentWindow.PDFViewerApplication.page = parseInt(e.data[1])
          let keyword1 = e.data[0]
          let highLight_content = e.data[2]

          // let keyword2 = "分子靶向"
          // const regex = new RegExp(`${keyword1}|${keyword2}`, 'gi');
          // console.log(iframe.contentWindow.PDFViewerApplication.page)
          // iframe.contentWindow.PDFViewerApplication.findBar.findField.value = regex;
          iframe.contentWindow.PDFViewerApplication.findBar.findField.value = keyword1;

          // 要求查询结果全体高亮
          iframe.contentWindow.PDFViewerApplication.findBar.highlightAll.checked = true;
          // 上面两部已经OK，触发highlightallchange方法。
          iframe.contentWindow.PDFViewerApplication.findBar.dispatchEvent('highlightallchange');
          var number = Number(e.data[1])
          console.log("length of e",e.data.length)
          if(e.data.length > 2)
          {
            highLight_content = highLight_content.replaceAll("<em>","").replaceAll("</em>","")
            console.log("要高亮的内容为：",highLight_content)
            // iframe.contentWindow.PDFViewerApplication.findBar.findField.value = highLight_content;
            // iframe.contentWindow.PDFViewerApplication.findBar.findField.value = "癌症";
            // iframe.contentWindow.PDFViewerApplication.findBar.highlightAll.checked = true;
          }

          iframe.contentWindow.PDFViewerApplication.pdfViewer.currentPageNumber = number;
          iframe.contentWindow.PDFViewerApplication.page = number

        }, 500);

      }, false);

    },
    handleMouseSelect() {
      let text = window.getSelection().toString()
      console.log(text)
    },

    WaitForIFrame() {
      if (iframe.readyState != "complete") {
        setTimeout("WaitForIFrame();", 200);
      } else {
        iframe.contentDocument.addEventListener('mousedown', function (e) {
          x = e.pageX;
          y = e.pageY;
        }, true);
        // 鼠标抬起监听
        iframe.contentDocument.addEventListener('mouseup', function (e) {
          _x = e.pageX;
          _y = e.pageY;
          // if (x == _x && y == _y) return; //判断点击和抬起的位置，如果相同，则视为没有滑选，不支持双击选中
          var choose = iframe.contentWindow.getSelection().toString();
          this.selectedText = choose;
        }, true);
        console.log("test" + this.selectedText)
      }
    },

    // 滑选事件注册： 获取鼠标选中的文本
    getSelectText() {
      let _this = this;
      console.log("test" + this.selectedText)

      let iframe = document.getElementById('myIframe');

      this.pdfURL = BackendUrl() + '/files/view/' + this.$route.query.docId
      let x = '';
      let y = '';
      let _x = '';
      let _y = '';
      // console.log("test" + this.selectedText)
      // iframe 加载完成后执行并将双击事件过滤掉，因为双击事件可能也触发滑选，所以为了不误操作，将其剔除
      // while (1) {
      // WaitForIFrame();
      // 鼠标点击监听
      // setTimeout(() => {
      iframe.onload = function () {
        iframe.contentDocument.addEventListener('mousedown', function (e) {
          x = e.pageX;
          y = e.pageY;
        }, true);
        // 鼠标抬起监听
        iframe.contentDocument.addEventListener('mouseup', function (e) {
          _x = e.pageX;
          _y = e.pageY;
          if (x == _x && y == _y) return; //判断点击和抬起的位置，如果相同，则视为没有滑选，不支持双击选中
          var choose = iframe.contentWindow.getSelection().toString();
          this.selectedText = choose;
        }, true);
        console.log("test" + this.selectedText)
        // }, 100);

      }

    },
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