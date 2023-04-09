<template>
    <div>
        <div class="stats-panel">
            <stats-card v-for="item in titles" :number="data[item.key]" :title="item.title"></stats-card>
        </div>
        <el-container>
            <el-aside width="40%" style="padding: 20px">
                <div class="echarts">
                    <div id="mmo" ref="myEcharts1" style="height: 450px;"></div>
                </div>
            </el-aside>
            <el-main width="60%">
                <div>
                    <div id="main" style="height: 1250px;"></div>
                </div>
            </el-main>
        </el-container>

    </div>
</template>

<script>
import StatsCard from "@/views/stats/StatsCard";
import StatsRequest from "@/api/stats";
import CategoryRequest from "@/api/category";
import graph from '../../assets/source/MeSH.json'

export default {
    name: "Index.vue",
    data() {
        return {
            data: {},
            titles: [{
                key: "docNum",
                title: '文档总数'
            }, {
                key: 'commentNum',
                title: '评论总数',
            }, {
                key: "tagNum",
                title: '标签总数'
            }, {
                key: 'categoryNum',
                title: '分类总数',
            },
            ],
            catelist: [],
            categoryType: 'CATEGORY',
            categoryOption: [],
        }
    },
    components: {
        StatsCard
    },
    created() {
        this.getTrendData()
        this.getcate()
        // this.getmain()
    },
    watch: {
        catelist: function () {
            this.meomeryEcharts()
        }
    },
    methods: {
        getTrendData() {
            StatsRequest.postStatsData().then(response => {
                // console.log(response.data)
                this.data = response.data
            })
        },
        getcate() {
            var catelist = [];
            const params = {
                type: this.categoryType
            };

            CategoryRequest.getListData(params).then(response => {
                // console.log(response.data);
                if (response.code !== 200) {
                    return;
                }
                if (response.data.length > 0) {
                    response.data.forEach(item => {

                        item['seeName'] = item.name

                        this.categoryOption.push(item)

                        let param = {
                            cateId: item['id'],
                            tagId: '',
                            keyword: '',
                            page: 1,
                            rows: 24,
                        }
                        // console.log(item['seeName'])
                        // setTimeout(function () {
                        //     // 填入代码
                        // }, 1000);
                        CategoryRequest.getDocList(param).then(res => {
                            // console.log(res)
                            if (res.code === 200) {
                                // console.log(res.data)
                                res.data = JSON.parse(JSON.stringify(res.data))
                                let result = res.data;
                                catelist.push({
                                    'value': result.total,
                                    'name': item['seeName']
                                })
                            }
                        }).catch(err => {
                            console.log(err)
                        })
                    })
                }
            })

            setTimeout(function () {
                //     // 填入代码
                this.catelist = catelist;
                // console.log(this.catelist)
            }, 100);

        },
        meomeryEcharts() {
            // console.log(1)
            var myChart = this.$echarts.init(document.getElementById('mmo'));
            window.addEventListener("resize", myChart.resize);
            // console.log(this.catelist)
            // 配置图表
            setTimeout(function () {
                var option = {
                    title: {
                        text: '文献类型数据统计',
                        left: 'center'
                    },
                    tooltip: {
                        trigger: 'item',
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'left'
                    },
                    series: [{
                        name: '文献类型数据',
                        type: 'pie',
                        radius: '50%',

                        data: this.catelist,
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }]
                };
                myChart.setOption(option, true);
            }, 600);

        },
        getmain() {
            var chartDom = document.getElementById('main');
            var myChart = this.$echarts.init(chartDom);
            var option;
            myChart.showLoading();

            myChart.hideLoading();
            graph.nodes.forEach(function (node) {
                node.label = {
                    show: true
                };
            });
            option = {
                title: {
                    text: 'Les Miserables',
                    subtext: 'Default layout',
                    top: 'bottom',
                    left: 'right'
                },
                tooltip: {},
                legend: [
                    {
                        // selectedMode: 'single',
                        data: graph.categories.map(function (a) {
                            return a.name;
                        })
                    }
                ],

                animationEasingUpdate: 'quinticInOut',
                series: [
                    {
                        type: 'graph',
                        layout: 'none',
                        data: graph.nodes,
                        links: graph.links,
                        categories: graph.categories,
                        roam: true,
                        label: {
                            position: 'right',
                            formatter: '{b}'
                        },
                        lineStyle: {
                            color: 'source',
                            curveness: 0.3
                        },
                        emphasis: {
                            focus: 'adjacency',
                            lineStyle: {
                                width: 10
                            }
                        }
                    }
                ]
            };
            myChart.setOption(option);
        }

    },
    mounted() {
        // setTimeout(function () {
        //     // 填入代码
        // }, 1000);
        this.$nextTick(() => {
            this.meomeryEcharts();
            this.getmain();
            // option && myChart.setOption(option);
        })
    },
}
</script>

<style scoped>
.stats-panel {
    padding: 10px;
    display: flex;
    justify-content: space-between;
}
</style>