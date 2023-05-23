<template>
    <el-popover
        placement="bottom-start"
        width="500"
        trigger="click"
        v-model="visible"
        popper-class="chat-window"
        transition="el-zoom-in-center"
    >
        <div class="chat-body">
            <el-scrollbar
                class="chat-messages"
                ref="chatMessages"
                v-loading="loading"
                horizontal="false"
            >
                <div
                    v-for="(msg, index) in chatHistory"
                    :key="index"
                    class="chat-message"
                    :class="{ 'chat-message-user': msg.color === 'blue', 'chat-message-gpt': msg.color === 'green' }"
                >

                    <div class="chat-content" v-html="msg.content"></div>
                </div>
            </el-scrollbar>
            <div class="chat-input-wrapper">
                <el-input
                    type="textarea"
                    autosize="{ minRows: 1, maxRows: 4}"
                    placeholder="请输入内容"
                    v-model="userInput"
                    class="chat-input"
                    @keyup.enter.native="sendMessage"
                ></el-input>
                <el-button type="primary" class="send-button" @click="sendMessage">发送</el-button>
            </div>
        </div>
        <el-button
            slot="reference"
            class="chat-button"
            icon="el-icon-chat-line-square"
            circle
            type="success"
        ></el-button>
    </el-popover>
</template>

<script>
// ChatBot.vue
import { EventBus } from '@/utils/event-bus';
import ChatGptRequest from '@/api/askGpt';

export default {
    data() {
        return {
            visible: false,
            userInput: '',
            chatHistory: [
                { content: "有什么我可以帮您", color: 'green' }
            ],
            loading: false,
        };
    },

    created() {
        EventBus.$on('message', this.receiveMessage);

    },
    beforeDestroy() {
        EventBus.$off('message', this.receiveMessage);
    },

    methods: {
        receiveMessage(message) {
            message = message.replace(/<em>(.*?)<\/em>/g, '<a href="#" class="keyword">$1</a>');
            this.visible = true;  // 展开聊天窗口
            this.chatHistory.push({ content: message, color: 'green' });
            this.updateKeywordLinks();
            // this.scrollToBottom();
        },

        async sendMessage() {

            let msg = this.userInput.trim();
            if (msg === '') {
                return;
            }
            this.userInput = ""
            this.chatHistory.push({ content: msg, color: 'blue' });

            this.loading = true;
            // Simulate API response
            const params = {
                question: msg
            };
            ChatGptRequest.askGpt(params).then((res) => {
                // var answer = res.data;

                this.loading = false;
                this.chatHistory.push({ content: res.data, color: 'green' });
                this.updateKeywordLinks();
                // this.scrollToBottom();
            }).catch((err) => {
                this.loading = false;
                this.chatHistory.push({ content: '网络错误，请稍后再试', color: 'green' });
                this.updateKeywordLinks();
                // this.scrollToBottom();
            });
        },

        updateKeywordLinks(){

            this.$nextTick(() => {
                const chatMessages = this.$refs.chatMessages.$refs.wrap;  // 获取到正确的滚动元素
                chatMessages.scrollTop = chatMessages.scrollHeight;

                // 为新生成的关键词链接绑定点击事件
                const keywordLinks = chatMessages.getElementsByClassName('keyword');
                Array.from(keywordLinks).forEach(link => {
                    link.addEventListener('click', e => {
                        e.preventDefault();  // 阻止默认的跳转事件
                        this.handleKeywordClick(link.textContent);  // 传递关键词给处理函数
                    });
                });
            });

        },

        searchValue(value) {

            if (value !== "") {
                this.$router.push({
                    path: '/searchResult',
                    query: {
                        keyWord: value
                    }
                })
                this.$eventBus.$emit('search_correct', value);
            }

        },

        handleKeywordClick(keyword) {
            console.log("keyword", keyword)
            // 处理点击关键词链接的逻辑
            this.searchValue(keyword);
        }
    },
};
</script>

<style scoped>
.chat-window {
    height: 40vh;  /* 40% of the viewport height */
    width: 25vw;   /* 25% of the viewport width */
    display: flex;
    flex-direction: column;
}

.chat-body {
    flex: 1 1 auto;
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 100%;
}

.chat-input-wrapper {
    flex: 0 0 auto;
    display: flex;
    align-items: center;
    padding: 10px;
    border-top: 1px solid #ebeef5;
}

.chat-input {
    flex-grow: 1;
    margin-right: 10px;
}

.chat-messages {
    flex: 1 1 auto;
    padding: 10px;

    height: 600px;

}

.chat-message {
    margin-bottom: 10px;
    display: flex;
    align-items: center;
}

.chat-message-user {
    flex-direction: row-reverse;
}

.chat-message-gpt {
    flex-direction: row;
}

.chat-content {
    padding: 10px;
    border-radius: 10px;
    background-color: #e6f7ff;
}

.chat-message-user .chat-content {
    background-color: #90ee90;
}

.send-button {
    align-self: flex-end;
    height: 30px;
    padding: 0 10px;
}

.keyword {
    color: yellow;
    cursor: pointer;
}

.chat-button {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 9999;
}
</style>
