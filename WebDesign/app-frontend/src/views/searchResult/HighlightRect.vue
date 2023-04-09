<template>
  <div>
    <img :src="image | imgSrc" alt="docId" referrerpolicy="no-referrer">
  </div>
</template>

<script>
import {BackendUrl} from "@/api/request";

export default {
  props: {
    image: String,
    leftBottom: String,
    leftTop: String,
    rightBottom: String,
    rightTop: String
  },
  filters:{
    imgSrc(value) {
      if (value === "" || value == null) {
        return require('@/assets/source/doc.png')
      } else {
        return BackendUrl() + "/files/image2/" + value;
      }
    }
  },
  computed: {
    leftBottomObject() {
      const [x, y] = this.leftBottom.split(',')
      return { x: Number(x), y: Number(y) }
    },
    leftTopObject() {
      const [x, y] = this.leftTop.split(',')
      return { x: Number(x), y: Number(y) }
    },
    rightBottomObject() {
      const [x, y] = this.rightBottom.split(',')
      return { x: Number(x), y: Number(y) }
    },
    rightTopObject() {
      const [x, y] = this.rightTop.split(',')
      return { x: Number(x), y: Number(y) }
    }
  },
  mounted() {
    this.drawHighlightRect()
  },
  methods: {
    drawHighlightRect() {
      const imageElement = this.$refs.image

      const canvasElement = document.createElement('canvas')

      canvasElement.width = imageElement.offsetWidth
      canvasElement.height = imageElement.offsetHeight
      canvasElement.style.position = 'absolute'
      canvasElement.style.top = imageElement.offsetTop + 'px'
      canvasElement.style.left = imageElement.offsetLeft + 'px'

      const ctx = canvasElement.getContext('2d')

      const { leftBottomObject, leftTopObject, rightBottomObject, rightTopObject } = this
      ctx.beginPath()
      ctx.moveTo(leftBottomObject.x, leftBottomObject.y)
      ctx.lineTo(leftTopObject.x, leftTopObject.y)
      ctx.lineTo(rightTopObject.x, rightTopObject.y)
      ctx.lineTo(rightBottomObject.x, rightBottomObject.y)
      ctx.closePath()
      ctx.lineWidth = 2
      ctx.strokeStyle = 'yellow'
      ctx.stroke()

      document.body.appendChild(canvasElement)

      this.highlightRect = canvasElement
    }
  },
  beforeDestroy() {
    if (this.highlightRect) {
      this.highlightRect.remove()
      this.highlightRect = null
    }
  }
}
</script>
