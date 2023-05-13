<template>
  <div>
    <el-table :data="diagnosisData" style="width: 100%">
      <el-table-column prop="userId" label="用户名"></el-table-column>
      <el-table-column prop="createTime" label="诊断时间">
        <template slot-scope="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="uuid" label="病例图片" width="180">
        <template slot-scope="scope">
          <div
              class="thumb-group"
              v-for="(imgId, index) in scope.row.uuid"
              :key="index"
              style="display: inline-block; margin-right: 10px;"
          >
            <div class="thumb-com">
              <div class="thumb-pic">
                <el-image
                    class="image-hover"
                    :src="imgId | imgSrc(50, 50)"
                    :preview-src-list="scope.row.uuid.map(id => id | imgSrc)"
                    fit="cover"
                    style="width: 50px; height: 50px;"
                    @error="handleImageError"
                ></el-image>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="ills" label="预测疾病">
        <template slot-scope="scope">
          <div v-for="(ill, index) in scope.row.ills" :key="index" style="margin-bottom: 4px;">
            <el-tag>{{ ill }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="illType" label="辅助诊断疾病类型"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <span>
            <el-button type="primary" @click="navigateToRecord(scope.row)" style="margin-right: 10px;">浏览</el-button>
          </span>
                <span>
            <el-button type="danger" @click="handleDelete(scope.$index, scope.row)" style="margin-right: 10px;">删除</el-button>
          </span>
        </template>
      </el-table-column>


    </el-table>
  </div>
</template>

<script>
import axios from 'axios';
import DocumentRequest from "@/api/document";
import { BackendUrl } from "@/api/request";
export default {
  data() {
    return {
      diagnosisData: [],
    };
  },
  filters: {

    imgSrc: function (value, width, height) {
      if (value === "" || value == null) {
        return require('@/assets/source/doc.png')
      } else {
        let url = BackendUrl() + "/files/image2/" + value;
        if (width && height) {
          url += `?width=${width}&height=${height}`;
        }
        return url;
      }
    }
  },
  mounted() {
    this.getDiagnosesRecord()
    console.log("diagnosisData:",this.diagnosisData)
  },
  methods: {

    navigateToRecord(row) {
      this.$router.push({
        path: '/PredictionCase',
        query: {
          entityData: JSON.stringify(row.predictCaseOutcome),
          documentData: JSON.stringify(row.documentVos),
        }
      });
    },

    handleImageError(err) {
      console.error('Image load error:', err);
    },
    getDiagnosesRecord(){
      const params = {}

      DocumentRequest.getDiagnosesRecord(params).then((res) => {
      if(res.code === 200){
        console.log("res",res)
        this.diagnosisData = res.data;
        console.log("this.diagnosisData:",this.diagnosisData)
      }
      });
    },
    formatDate(timestamp) {
      const date = new Date(timestamp);
      return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`;
    },
    handleDelete(index, row) {
      this.$confirm('确定要删除这条记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
          .then(() => {
            axios.delete(`/api/diagnosis/${row.id}`)
                .then(() => {
                  this.diagnosisData.splice(index, 1);
                  this.$message({
                    type: 'success',
                    message: '删除成功',
                  });
                })
                .catch(() => {
                  this.$message({
                    type: 'error',
                    message: '删除失败',
                  });
                });
          })
          .catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除',
            });
          });
    },
  },
};
</script>
<style scoped>
.image-hover {
  transition: transform 0.3s;
}

.image-hover:hover {
  transform: scale(1.2);
}
</style>

