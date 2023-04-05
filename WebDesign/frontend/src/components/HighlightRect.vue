<template>
  <div style="position: relative">
    <img :src="image | imgSrc" alt="docId" referrerpolicy="no-referrer" ref="image" />
    <canvas ref="canvas" style="position: absolute; top: 0; left: 0; z-index: 1;"></canvas>
  </div>
</template>

<script>
import { BackendUrl } from '@/api/request';

export default {
  props: {
    image: String,
    textResult: {
      type: Array,
      default: () => []
    }
  },
  filters: {
    imgSrc(value) {
      if (value === '' || value == null) {
        return require('@/assets/source/doc.png');
      } else {
        return BackendUrl() + '/files/image2/' + value;
      }
    }
  },
  mounted() {
    if (this.$refs.image.complete) {
      this.onImageLoad();
    } else {
      this.$refs.image.addEventListener('load', this.onImageLoad);
    }
  },
  beforeDestroy() {
    this.$refs.image.removeEventListener('load', this.onImageLoad);
  },
  watch: {
    image() {
      this.onImageLoad();
    }
  },
  methods: {
    onImageLoad() {
      const canvasElement = this.$refs.canvas;
      const imageElement = this.$refs.image;

      canvasElement.width = imageElement.offsetWidth;
      canvasElement.height = imageElement.offsetHeight;

      this.textResult.forEach(item => {
        this.drawHighlightRect(item);
      });
    },
    drawHighlightRect({ leftBottom, leftTop, rightBottom, rightTop }) {
      const canvasElement = this.$refs.canvas;
      const imageElement = this.$refs.image;

      const scaleX = imageElement.offsetWidth / imageElement.naturalWidth;
      const scaleY = imageElement.offsetHeight / imageElement.naturalHeight;

      const ctx = canvasElement.getContext('2d');

      const leftBottomObject = {
        x: Number(leftBottom.split(',')[0]) * scaleX,
        y: Number(leftBottom.split(',')[1]) * scaleY
      };
      const leftTopObject = {
        x: Number(leftTop.split(',')[0]) * scaleX,
        y: Number(leftTop.split(',')[1]) * scaleY
      };
      const rightBottomObject = {
        x: Number(rightBottom.split(',')[0]) * scaleX,
        y: Number(rightBottom.split(',')[1]) * scaleY
      };
      const rightTopObject = {
        x: Number(rightTop.split(',')[0]) * scaleX,
        y: Number(rightTop.split(',')[1]) * scaleY
      };

      const width = rightBottomObject.x - leftBottomObject.x;
      const height = leftTopObject.y - leftBottomObject.y;

      ctx.beginPath();
      ctx.rect(leftBottomObject.x, leftBottomObject.y, width, height);
      ctx.closePath();
      ctx.lineWidth = 2;
      ctx.fillStyle = 'rgba(255, 255, 0, 0.3)';
      ctx.fill();
      ctx.strokeStyle = 'yellow';
      ctx.stroke();
    }
  }
};
</script>


<!--<template>-->
<!--  <div style="position: relative">-->
<!--    <img :src="image | imgSrc" alt="docId" referrerpolicy="no-referrer" ref="image" />-->
<!--    <canvas ref="canvas" style="position: absolute; top: 0; left: 0; z-index: 1;"></canvas>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import { BackendUrl } from '@/api/request';-->

<!--export default {-->
<!--  props: {-->
<!--    image: String,-->
<!--    leftBottom: String,-->
<!--    leftTop: String,-->
<!--    rightBottom: String,-->
<!--    rightTop: String-->
<!--  },-->
<!--  filters: {-->
<!--    imgSrc(value) {-->
<!--      if (value === '' || value == null) {-->
<!--        return require('@/assets/source/doc.png');-->
<!--      } else {-->
<!--        return BackendUrl() + '/files/image2/' + value;-->
<!--      }-->
<!--    }-->
<!--  },-->
<!--  computed: {-->
<!--    leftBottomObject() {-->
<!--      const [x, y] = this.leftBottom.split(',');-->
<!--      return { x: Number(x), y: Number(y) };-->
<!--    },-->
<!--    leftTopObject() {-->
<!--      const [x, y] = this.leftTop.split(',');-->
<!--      return { x: Number(x), y: Number(y) };-->
<!--    },-->
<!--    rightBottomObject() {-->
<!--      const [x, y] = this.rightBottom.split(',');-->
<!--      return { x: Number(x), y: Number(y) };-->
<!--    },-->
<!--    rightTopObject() {-->
<!--      const [x, y] = this.rightTop.split(',');-->
<!--      return { x: Number(x), y: Number(y) };-->
<!--    }-->
<!--  },-->
<!--  mounted() {-->
<!--    // this.$nextTick(() => {-->
<!--      this.drawHighlightRect();-->
<!--    // });-->
<!--  },-->
<!--  methods: {-->
<!--    drawHighlightRect() {-->
<!--      const canvasElement = this.$refs.canvas;-->
<!--      const imageElement = this.$refs.image;-->

<!--      canvasElement.width = imageElement.offsetWidth;-->
<!--      canvasElement.height = imageElement.offsetHeight;-->
<!--      canvasElement.style.zIndex = 1;-->


<!--      const ctx = canvasElement.getContext('2d');-->

<!--      const { leftBottomObject, leftTopObject, rightBottomObject, rightTopObject } = this;-->

<!--      const width = rightBottomObject.x - leftBottomObject.x;-->
<!--      const height = leftTopObject.y - leftBottomObject.y;-->

<!--      ctx.beginPath();-->
<!--      ctx.rect(leftBottomObject.x, leftBottomObject.y, width, height);-->
<!--      ctx.closePath();-->
<!--      ctx.lineWidth = 2;-->
<!--      ctx.fillStyle = 'rgba(255, 255, 0, 0.3)'; // 使用半透明的黄色作为填充颜色-->
<!--      ctx.fill(); // 填充矩形区域-->
<!--      ctx.strokeStyle = 'yellow';-->
<!--      ctx.stroke();-->
<!--    }-->
<!--  }-->
<!--};-->
<!--</script>-->
