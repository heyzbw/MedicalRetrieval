<template>
  <div>
    <img ref="image" :src="imageSrc" />
  </div>
</template>

<script>
export default {
  data() {
    return {
      imageSrc: 'path/to/image.jpg',
      highlightRect: null
    }
  },
  mounted() {
    // 在mounted钩子函数中调用绘制高亮框的方法
    this.drawHighlightRect()
  },
  methods: {
    drawHighlightRect() {
      // 获取图片DOM元素
      const imageElement = this.$refs.image

      // 创建canvas元素
      const canvasElement = document.createElement('canvas')

      // 设置canvas元素的尺寸和位置
      canvasElement.width = imageElement.offsetWidth
      canvasElement.height = imageElement.offsetHeight
      canvasElement.style.position = 'absolute'
      canvasElement.style.top = imageElement.offsetTop + 'px'
      canvasElement.style.left = imageElement.offsetLeft + 'px'

      // 获取canvas的绘图上下文
      const ctx = canvasElement.getContext('2d')

      // 绘制矩形
      const { leftBottom, leftTop, rightBottom, rightTop } = this
      ctx.beginPath()
      ctx.moveTo(leftBottom.x, leftBottom.y)
      ctx.lineTo(leftTop.x, leftTop.y)
      ctx.lineTo(rightTop.x, rightTop.y)
      ctx.lineTo(rightBottom.x, rightBottom.y)
      ctx.closePath()
      ctx.lineWidth = 2
      ctx.strokeStyle = 'yellow'
      ctx.stroke()

      // 将canvas元素添加到DOM中
      document.body.appendChild(canvasElement)

      // 保存canvas元素的引用，以便在需要时删除它
      this.highlightRect = canvasElement
    }
  },
  beforeDestroy() {
    // 在组件销毁前删除canvas元素
    if (this.highlightRect) {
      this.highlightRect.remove()
      this.highlightRect = null
    }
  }
}
</script>
