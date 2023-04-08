<template>
    <div class="search-zone">
        <button @click="searchValue">Pubmed搜索</button>
    </div>
</template>

<script>

export default {
    name: "SearchPubmed",
    data() {
        return {
            keyWord: this.$route.query.keyWord,
        }
    },
    methods: {
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
    /*background-color: #8ffaa6;*/
}

div /deep/ .ivu-input-group {
    box-shadow: rgba(0, 0, 0, 0.1) 0 1px 3px 0, rgba(0, 0, 0, 0.06) 0 1px 2px 0;
}

div /deep/ .ivu-input {
    border-top-left-radius: 60px;
    border-bottom-left-radius: 60px;
    border: none;
    /*outline：none;*/
    /*background-color: #8ffaa6;*/
}

div /deep/ .ivu-input:focus {
    border: none;
    outline: none;
}

div /deep/ .ivu-input-group-append {
    border-top-right-radius: 60px;
    border-bottom-right-radius: 60px;
    border: none !important;
}

div /deep/ .ivu-input-group-append::before {
    display: block;
    width: 1px;
    position: absolute;
    top: 5px;
    bottom: 5px;
    left: -1px;
    background: inherit;
    background-color: #8ffaa6;
}

div /deep/ .ivu-input-search {
    background-color: #ffffff !important;
    color: #8ffaa6 !important;
}
</style>