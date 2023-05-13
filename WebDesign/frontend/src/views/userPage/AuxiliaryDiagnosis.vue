<template>
  <div class="container">
    <h2>选择你的病例，目前可以支持通过上传你的病例图片</h2>
    <p>我们将会帮你通过人工智能的方式分析，并进行检索得到相关性好的文献。</p>
    <el-upload
        class="upload-demo"
        ref="upload"
        action="#"
        list-type="picture-card"
        accept="image/png"
        :on-preview="handlePictureCardPreview"
        :on-remove="handleRemove2"
        :http-request="uploadAvatar"
        :before-upload="beforeAvatarUpload"
        :file-list="fileList"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
    </el-upload>

    <div class="input-container">
      <el-input
          v-model="imagefile"
          placeholder="请输入文件名"
          style="width:100%;"
      ></el-input>
    </div>

    <div class="input-container">
      <el-input
          v-model="auxiliaryDiagnosis"
          placeholder="请输入辅助诊断疾病类型（可选）"
          style="width:100%;"
      ></el-input>
    </div>


    <div class="button-container">
      <el-button
          type="primary"
          class="submit-btn"
          @click="submitUpload"
          :disabled="isUploading"
      >{{ isUploading ? '正在生成...' : '生成' }}</el-button>

    </div>
  </div>
</template>

<script>
import DocumentRequest from "@/api/document"

export default {
  name: "DocUpload",
  data() {
    return {
      fileList: [],
      imagefile: '',
      imageUrl: "",
      dialogImageUrl: "",
      form: [],
      dialogVisible: '',
      auxiliaryDiagnosis: "", // 辅助诊断疾病类型
      isUploading: false, // 是否正在上传的标识
    }
  },

  // clickToSearch(value) {
  //   if (value !== "") {
  //     this.$router.push({
  //       path: '/searchResult',
  //       query: {
  //         keyWord: value
  //       }
  //     })
  //   } else {
  //     this.routeTo()
  //   }
  // },

  methods: {
    submitUpload() {
      this.isUploading = true;

      // 显示等待提示
      this.$message({ type: 'info', message: '正在生成，请稍等...' });

      let formData = new FormData();
      if (this.imagefile != "") {
        formData.set("filename", this.imagefile);
        formData.set("diagnosisDiseaseTypes", this.auxiliaryDiagnosis);
        for (let i = 0; i < this.form.length; i++) {
          formData.append('imageList', this.form[i]);
        }

        DocumentRequest.getCasePredictionData(formData)
            .then(res => {
              console.log("res:", res)
              if (res.code === 200) {
                console.log("data:", res.data)
                this.$refs.upload.clearFiles();
                this.$message.success("检索成功");
                console.log("检索成功，即将跳转")
                this.$router.push({
                  path: '/PredictionCase',
                  query: {
                    entityData: JSON.stringify(res.data.predictCaseOutcome),
                    documentData: JSON.stringify(res.data.documentVos),
                  }
                });
              } else {
                this.$message.success("检索失败");
              }
              this.isUploading = false;
            })
            .catch(err => {
              console.error('Error occurred:', err);
              this.isUploading = false;
            });
      } else {
        this.$message.error("请输入文件名");
        this.isUploading = false;
      }
    },

    uploadAvatar(item) {
      console.log(item.file)
      this.form.push(item.file)
    },
    beforeAvatarUpload(file) {
      const isPng = file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isPng) {
        this.$message.error('上传图片只能是 JPG或png 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!')
      }
      return (isPng) && isLt2M
    },
    handleRemove2(file, fileList) {
      console.log(this.form.length)
      console.log(this.form)
      console.log(file)
      for (let i = 0; i < this.form.length; i++) {
        if (this.form[i].uid === file.uid) {
          console.log(i, "tt")
          this.form.splice(i, 1)
        }
        console.log(this.form, "434")
      }
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
  }
}
</script>


<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 50px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  background-color: #ffffff;
}

h2 {
  margin-bottom: 15px;
}

p {
  margin-bottom: 25px;
}

.input-container {
  margin-top: 20px;
}

.button-container {
  margin-top: 20px;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.submit-btn {
  width: 100%;
}
</style>
