import Vue from 'vue'

Vue.directive('drawer', {
    bind(el, binding, vnode, oldVnode) {
        const minWidth = 400
        const dragDom = el.querySelector('.el-drawer')
        dragDom.style.overflow = 'auto'
        dragDom.style.left = '15%'
        dragDom.style.width = '70%'
        dragDom.radius = '20px';

        console.log(dragDom)
        const resizeElL = document.createElement('div')
        const resizeElL1 = document.createElement('div')
        const img = new Image(38, 24)
        const img1 = new Image(24, 38)

        img.src = require('@/assets/source/strench1.png')
        img1.src = require('@/assets/source/strench.png')

        dragDom.appendChild(img)
        dragDom.appendChild(img1)

        dragDom.appendChild(resizeElL)
        dragDom.appendChild(resizeElL1)

        resizeElL.style.cursor = 'w-resize'
        resizeElL.style.position = 'absolute'
        resizeElL.style.height = '100%'
        resizeElL.style.width = '10px'
        resizeElL.style.left = '0px'
        resizeElL.style.top = '0px'
        img.style.position = 'absolute'
        img.style.left = '50%'
        img.style.top = '-10px'

        resizeElL1.style.cursor = 'ns-resize'
        resizeElL1.style.position = 'absolute'
        resizeElL1.style.height = '10px'
        resizeElL1.style.width = '100%'
        resizeElL1.style.left = '0px'
        resizeElL1.style.top = '0px'
        img1.style.position = 'absolute'
        img1.style.left = '-12px'
        img1.style.top = '50%'

        resizeElL.onmousedown = (e) => {
            const elW = dragDom.clientWidth
            const EloffsetLeft = dragDom.offsetLeft
            const clientX = e.clientX
            console.log(e)
            console.log(dragDom)
            document.onmousemove = function (e) {
                e.preventDefault()
                // 左侧鼠标拖拽位置
                if (clientX > EloffsetLeft && clientX < EloffsetLeft + 10) {
                    // 往左拖拽
                    if (clientX > e.clientX) {
                        dragDom.style.width = elW + (clientX - e.clientX) + 'px'
                    }
                    // 往右拖拽
                    if (clientX < e.clientX) {
                        if (dragDom.clientWidth >= minWidth) {
                            dragDom.style.width = elW - (e.clientX - clientX) + 'px'
                        }
                    }
                }
            }
            // 拉伸结束
            document.onmouseup = function (e) {
                document.onmousemove = null
                document.onmouseup = null
            }
        }
        resizeElL1.onmousedown = (e) => {
            const elW = dragDom.clientHeight
            const EloffsetTop = dragDom.offsetTop

            const clientY = e.clientY
            console.log(e)
            document.onmousemove = function (e) {
                e.preventDefault()
                // 左侧鼠标拖拽位置
                if (clientY > EloffsetTop && clientY < EloffsetTop + 10) {
                    // 往左拖拽
                    if (clientY > e.clientY) {
                        dragDom.style.height = elW + (clientY - e.clientY) + 'px'
                    }
                    // 往右拖拽
                    if (clientY < e.clientY) {
                        if (dragDom.clientHeight >= minHeight) {
                            dragDom.style.height = elW - (e.clientY - clientY) + 'px'
                        }
                    }
                }
                else {
                    dragDom.style.height = elW - (e.clientY - clientY) + 'px'
                }
            }
            // 拉伸结束
            document.onmouseup = function (e) {
                document.onmousemove = null
                document.onmouseup = null
            }
        }
    }
})
