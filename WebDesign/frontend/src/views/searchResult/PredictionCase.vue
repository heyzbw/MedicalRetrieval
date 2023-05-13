<template>
  <div id="app">
    <el-container>
      <el-header>
        <el-row>
          <el-col :span="24">
            <h1>实体分类与推荐文献</h1>
          </el-col>
        </el-row>
      </el-header>
      <el-main>
        <el-row>
          <el-col :span="24">
            <el-card>
              <h2 slot="header">实体分类</h2>
              <el-row v-for="(entities, category) in entityData" :key="category" class="entity-row">
                <el-col :span="24" class="text-left">
                  <h3 style="display: inline-block; margin-right: 10px;">{{ category }}：</h3>
                  <el-tag
                      v-for="(entity, index) in entities"
                      :key="index"
                      :type="getTagType(category)"
                      @click="handleTagClick(entity)"
                      style="margin-right: 5px; margin-bottom: 5px;"
                  >
                    {{ entity }}
                  </el-tag>
                </el-col>
              </el-row>
            </el-card>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-card>
              <h2 slot="header">推荐文献</h2>

              <PredictCCaseItem v-for="item in documentData"
                          :id="item.id" :thumbId="item.thumbId" :title="item.title" :esSearchContentList="item.esSearchContentList"
                          :time="item.createTime" :user-name="item.userName" :category="item.categoryVO" :tags="item.tagVOList"
                          :collect-num="item.collectNum" :like-num="item.likeNum" :comment-num="item.commentNum" :click-num="item.click_num"
                          :ocrResultList="item.ocrResultList" :keyword="keyword" :click_score="item.click_score"
                          :content_score="item.content_score" :esSearchContentList_syno="item.esSearchContentList_syno"
                          :like_score="item.like_score" :medicalOpinions="item.medicalOpinions">
              </PredictCCaseItem>

            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>



<script>

import PredictCCaseItem from "./PredictCCaseItem";

export default {
  components: {PredictCCaseItem},
  data() {
    return {
      selectedItem: -1,
      entityData: {
        身体: ["血"],
        医疗程序: [
          "维持性血液透析",
          "血液透析",
          "血透检测",
          "注射",
          "静脉推注",
          "小换药",
        ],
        疾病: ["尿毒症", "糖尿病", "慢性肾脏病5期"],
        症状: ["神清", "慢性病容"],
        药物: ["低分子量肝素钙"],
      },
      documentData:{},

      categoryMap: {
        disease: '疾病',
        diagnosisDiseaseTypes: "辅助诊断疾病类型",
        body: '身体',
        symptom: '症状',
        medicalProcedure: '医疗程序',
        medicine: '药物',
        department : "科室",
        microorganism : "微生物类",
        medicalExamination:"医学检验项目",
        medicalEquipment:"医疗设备"
      }
    };
  },
  created() {
    if (this.$route.query.entityData) {
      const rawData = JSON.parse(this.$route.query.entityData);
      this.entityData = this.mapAndFilter(rawData);
      // console.log("entityData", this.entityData);
    }
    if (this.$route.query.documentData) {
      this.documentData = JSON.parse(this.$route.query.documentData);

    }
  },
  methods: {
    mapAndFilter(rawData) {
      const result = {};
      for (const key in rawData) {
        if (rawData[key].length > 0) {  // 过滤空数组
          result[this.categoryMap[key] || key] = rawData[key];  // 使用映射，如果没有映射就使用原始的key
        }
      }
      return result;
    },

    getTagType(key) {
      switch (key) {
        case "疾病":
        case "辅助诊断疾病类型":
          return "danger";
        case "症状":
          return "warning";
        case "医疗程序":
          return "success";
        case "药物":
          return "info";
        default:
          return "";
      }
    },
  },
};
</script>

<style scoped>
#app {
  font-family: Arial, sans-serif;
}

.el-container {
  padding: 20px;
}

.el-header h1,
.el-main h2 {
  margin-bottom: 20px;
}

.title-group {
  margin-bottom: 10px;
}

.doc-title-info {
  font-size: 1.2em;
  font-weight: bold;
}

.description {
  color: #666;
}

.description-item,
.el-tag {
  margin-right: 10px;
  display: inline-block;
}

.pubmed-item {
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.selected-item {
  background-color: #eee;
}

.entity-row {
  align-items: flex-start;
}

</style>
