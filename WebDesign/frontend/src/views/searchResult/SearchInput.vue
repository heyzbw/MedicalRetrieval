<template>
    <div class="search-zone">
        <Input search enter-button="搜索" v-model="keyWord" placeholder="请输入文档关键字" @on-search="searchValue"
            @on-change="sentword" />
    </div>
</template>

<script>

export default {
    name: "SearchInput",
    data() {
        return {
            keyWord: this.$route.query.keyWord,
        }
    },

    created() {
        this.$eventBus.$on('search_correct', this.handleSearch);
    },

    beforeDestroy() {
        this.$eventBus.$off('search_correct', this.handleSearch);  // 不要忘了在组件销毁前取消事件监听
    },

    methods: {
        handleSearch(value){
            this.keyWord = value;
            this.searchValue(value);
        },

        searchValue(value) {
            if (value !== "") {
                this.$router.push({
                    path: '/searchResult',
                    query: {
                        keyWord: value
                    }
                })
                this.$emit("on-search", true)
            }
        },
        sentword() {
            this.$emit("on-change", this.keyWord)
            console.log(this.keyWord)
        }
    }
}
</script>

<style scoped>
.search-zone {
    width: 800px;
    line-height: 80px;
    height: 80px;
    /*margin: auto;*/
    padding-top: 20px;
    color: #637cfd;
    /*background-color: #8ffaa6;*/
}

/deep/ .ivu-input-group {
    box-shadow: rgba(0, 0, 0, 0.1) 0 1px 3px 0, rgba(0, 0, 0, 0.06) 0 1px 2px 0;
}

/deep/ .ivu-input {
    border-top-left-radius: 60px;
    border-bottom-left-radius: 60px;
    border: none;
    /*outline：none;*/
    /*background-color: #8ffaa6;*/
}

/deep/ .ivu-input:focus {
    border: none;
    outline: none;
}

/deep/ .ivu-input-group-append {
    border-top-right-radius: 60px;
    border-bottom-right-radius: 60px;
    border: none !important;
}

/deep/ .ivu-input-group-append::before {
    display: block;
    width: 1px;
    position: absolute;
    top: 5px;
    bottom: 5px;
    left: -1px;
    background: inherit;
    background-color: #637cfd;
}

/deep/ .ivu-input-search {
    background-color: #ffffff !important;
    color: #8fb6fa !important;
}
</style>