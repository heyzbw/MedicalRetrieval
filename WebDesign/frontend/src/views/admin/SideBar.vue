<template>
    <Menu mode="vertical"
          :theme="theme"
          active-name="1"
          width="200px"
          @on-select="turnUrl"
          :active-name="$route.name"
    >
        <MenuItem name="newDocument1">
            <Icon type="md-arrow-round-up" />
            添加文档
        </MenuItem>
        <MenuItem name="docReview" v-if="permission === 'ADMIN'" >
            <Icon type="md-checkmark-circle-outline" />
            文档审核
        </MenuItem>
        <MenuItem name="allDocuments">
            <Icon type="md-folder-open"/>
            全部文档
        </MenuItem>
        <MenuItem name="category">
            <Icon type="md-archive"/>
            文档分类
        </MenuItem>
        <MenuItem name="tags">
            <Icon type="md-pricetag"/>
            文档标签
        </MenuItem>
        <MenuItem name="commentManage">
            <Icon type="md-chatboxes" />
            评论管理
        </MenuItem>
        <MenuItem name="users" v-if="permission === 'ADMIN'" >
            <Icon type="md-contact"/>
            用户管理
        </MenuItem>
        <MenuItem name="stats" v-if="permission === 'ADMIN'" >
            <Icon type="ios-podium"/>
            文档统计
        </MenuItem>
        <MenuItem name="systemConfig" v-if="permission === 'ADMIN'" >
            <Icon type="ios-hammer" />
            系统设置
        </MenuItem>
    </Menu>
</template>
<script>
import UserRequest from '@/api/user'

export default {
    data() {
        return {
            theme: 'light',
            permission: 'ADMIN'
        }
    },
    methods: {
        turnUrl(name) {

            this.$router.push(name);
        }
    },
    mounted() {
          console.log("userId", localStorage.getItem("id"))
          let params = {
            id:localStorage.getItem("id")
          }


        UserRequest.getUser(params).then(response => {
          if (response.code === 200) {
            console.log("data:",response.data)
            this.permission = response.data.permissionEnum
          }
        })
    }
}
</script>

<style scoped>
/deep/ .ivu-menu-light {
    height: 100% !important;
}

.ivu-menu {
    height: 100%;
}
</style>
