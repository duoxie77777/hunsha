<template>
  <div class="services-page">
    <section class="breadcrumb-section">
      <div class="container">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><router-link to="/">首页</router-link></li>
          <li class="breadcrumb-item active">服务项目</li>
        </ol>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <div class="section-title">
          <h2>我们的服务项目</h2>
          <p>专业的服务团队，定制化的服务方案，为您打造独一无二的婚纱摄影体验</p>
        </div>

        <el-tabs v-model="activeTab" class="service-tabs">
          <el-tab-pane v-for="tab in tabs" :key="tab.key" :label="tab.label" :name="tab.key">
            <div class="row">
              <div class="col-lg-6 mb-4">
                <div class="service-detail-card">
                  <div class="service-img-container">
                    <span v-if="tab.tag" class="service-tag">{{ tab.tag }}</span>
                    <img :src="tab.img" :alt="tab.label" />
                  </div>
                </div>
              </div>
              <div class="col-lg-6 mb-4">
                <div class="service-content">
                  <h3>{{ tab.title }}</h3>
                  <p class="service-desc">{{ tab.desc }}</p>
                  <div class="service-features">
                    <h4>服务特色</h4>
                    <ul class="feature-list">
                      <li v-for="(f, i) in tab.features" :key="i">
                        <el-icon color="var(--secondary-color)"><Select /></el-icon> {{ f }}
                      </li>
                    </ul>
                  </div>
                  <div class="service-price">
                    <span class="price-value">{{ tab.price }}</span>
                    <span class="price-note">{{ tab.priceNote }}</span>
                  </div>
                  <button class="btn-custom" @click="userStore.openBooking()">立即预约</button>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>

        <!-- 服务流程 -->
        <div class="section-title" style="margin-top:60px;">
          <h2>服务流程</h2>
          <p>标准化的服务流程，让您的婚纱照拍摄体验更加顺畅</p>
        </div>
        <div class="row">
          <div class="col-lg-2 mb-4" v-for="(step, i) in steps" :key="i" style="width:16.666%;">
            <div class="process-step">
              <div class="step-number">{{ i + 1 }}</div>
              <h4>{{ step.title }}</h4>
              <p>{{ step.desc }}</p>
            </div>
          </div>
        </div>

        <!-- 常见问题 -->
        <div class="section-title" style="margin-top:60px;">
          <h2>常见问题</h2>
        </div>
        <el-collapse accordion>
          <el-collapse-item v-for="(faq, i) in faqs" :key="i" :title="faq.q" :name="i">
            <p>{{ faq.a }}</p>
          </el-collapse-item>
        </el-collapse>

        <!-- 预约咨询 -->
        <div class="booking-card">
          <h3>想要了解更多？立即咨询</h3>
          <p>专业客服为您解答所有疑问，量身定制拍摄方案</p>
          <span class="booking-phone">400-888-9999</span>
          <button class="btn-custom" @click="userStore.openBooking()">在线预约</button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Select } from '@element-plus/icons-vue'
import { userStore } from '../stores/user'

const activeTab = ref('wedding-photo')

const tabs = [
  { key: 'wedding-photo', label: '婚纱拍摄', tag: '热门服务', img: 'https://picsum.photos/id/1027/800/600', title: '婚纱拍摄服务', desc: '我们拥有专业的摄影团队和多元化的拍摄场景，为您打造专属于您的婚纱大片。从经典的室内拍摄到浪漫的外景取景，从森系小清新到复古宫廷风，我们都能满足您的个性化需求。', features: ['资深摄影师一对一全程拍摄', '上千款婚纱礼服任选，不分区不加价', '多场景拍摄，室内外景自由组合', '无隐形消费，底片全送', '专业修图团队，精修照片不满意免费重修', '免费提供拍摄所需道具和配饰'], price: '¥3999起', priceNote: '根据套餐不同价格有所调整' },
  { key: 'wedding-follow', label: '婚礼跟拍', tag: '', img: 'https://picsum.photos/id/1040/800/600', title: '婚礼跟拍服务', desc: '婚礼只有一次，珍贵的瞬间需要专业的记录。我们的婚礼跟拍团队经验丰富，熟悉婚礼流程的每一个环节，能够精准捕捉每一个感人的瞬间。', features: ['双机位拍摄，多角度记录婚礼全程', '专业高清摄像设备，画质清晰', '资深跟拍摄影师，熟悉各类婚礼流程', '快速出片，72小时内提供精修预告', '专业后期制作，含精美片头片尾', '提供多种格式视频文件，方便分享'], price: '¥4999起', priceNote: '根据机位和时长不同价格有所调整' },
  { key: 'dress-rental', label: '婚纱租赁', tag: '', img: 'https://picsum.photos/id/1062/800/600', title: '婚纱租赁服务', desc: '我们拥有上千款婚纱礼服，涵盖各种风格和尺寸，满足不同新娘的需求。所有婚纱均定期清洗保养，确保干净整洁。', features: ['上千款婚纱礼服任选，无分区限制', '免费试纱，专业试纱师一对一服务', '免费修改尺寸，确保完美贴合', '租赁期内免费保养，提供备用配饰', '新郎礼服、伴郎伴娘服配套租赁', '免费配送和回收，省时省力'], price: '¥1999起', priceNote: '根据婚纱款式和套系不同价格有所调整' },
  { key: 'makeup', label: '化妆造型', tag: '', img: 'https://picsum.photos/id/1070/800/600', title: '化妆造型服务', desc: '精致的妆容是完美婚纱照的基础。我们的化妆造型团队由资深化妆师组成，精通各种妆容风格。', features: ['资深化妆师一对一全程跟妆', '使用一线品牌化妆品，安全无刺激', '免费试妆，满意后确定造型方案', '提供新郎妆、妈妈妆、伴娘妆等', '全程补妆，确保妆容完美', '免费提供假睫毛、头饰等配饰'], price: '¥899起', priceNote: '根据化妆师级别和服务时长不同价格有所调整' },
  { key: 'travel-photo', label: '旅拍服务', tag: '特色服务', img: 'https://picsum.photos/id/1082/800/600', title: '旅拍服务', desc: '边旅行边拍婚纱照，让美景见证你们的爱情。我们提供全国热门旅拍城市的一站式服务。', features: ['全国多个热门旅拍城市可选', '一站式服务，包含交通住宿', '当地资深摄影师，熟悉最佳拍摄地点', '灵活的拍摄时间，自由安排行程', '提供当地特色服装和道具', '后期产品全国包邮到家'], price: '¥9999起', priceNote: '根据目的地和天数不同价格有所调整' }
]

const steps = [
  { title: '预约咨询', desc: '线上或到店咨询，了解套餐详情' },
  { title: '确定方案', desc: '定制拍摄方案，选择拍摄场景' },
  { title: '预约档期', desc: '确定拍摄时间，签订拍摄合同' },
  { title: '拍摄当天', desc: '专业化妆造型，全程拍摄服务' },
  { title: '选片修图', desc: '选片确定精修，专业团队修图' },
  { title: '成品交付', desc: '相册相框制作完成，配送领取' }
]

const faqs = [
  { q: '拍摄前需要做哪些准备？', a: '拍摄前一周注意休息，保持良好状态；提前准备浅色内衣、隐形眼镜；男士准备黑白色袜子、黑色皮鞋，提前修剪头发；女士可提前做美甲，拍摄当天素颜到店。' },
  { q: '拍摄当天需要多长时间？', a: '一般情况下，室内拍摄需要6-8小时；如果包含外景拍摄，根据外景地距离不同，需要8-10小时。' },
  { q: '可以自带服装拍摄吗？', a: '当然可以。自带服装不计入套餐规定的服装套数内，您可以放心准备。' },
  { q: '精修照片不满意可以修改吗？', a: '可以的。我们承诺精修照片不满意免费重修，直到您满意为止。' }
]
</script>

<style scoped>
.service-tabs :deep(.el-tabs__item) { font-size: 1rem; font-weight: 600; padding: 0 25px; }
.service-tabs :deep(.el-tabs__active-bar) { background-color: var(--secondary-color); }
.service-tabs :deep(.el-tabs__item.is-active) { color: var(--secondary-color); }

.service-detail-card { border-radius: 10px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.08); }
.service-img-container { position: relative; height: 400px; }
.service-img-container img { width: 100%; height: 100%; object-fit: cover; }
.service-tag { position: absolute; top: 20px; left: 20px; background: var(--secondary-color); color: #fff; padding: 5px 15px; border-radius: 20px; font-weight: 600; }
.service-content h3 { font-weight: 700; margin-bottom: 20px; color: var(--dark-color); }
.service-desc { color: #666; margin-bottom: 25px; line-height: 1.8; }
.service-features h4 { font-weight: 600; margin-bottom: 15px; }
.feature-list { list-style: none; padding: 0; }
.feature-list li { padding: 8px 0; display: flex; align-items: center; gap: 10px; }
.service-price { display: flex; align-items: center; margin-bottom: 25px; gap: 15px; }
.price-value { font-size: 2rem; font-weight: 700; color: var(--secondary-color); }
.price-note { color: #666; font-size: 0.9rem; }

.process-step { text-align: center; padding: 0 15px; }
.step-number { width: 80px; height: 80px; border-radius: 50%; background: var(--primary-color); color: var(--secondary-color); display: flex; align-items: center; justify-content: center; font-size: 2rem; font-weight: 700; margin: 0 auto 20px; }
.process-step h4 { font-weight: 600; margin-bottom: 15px; }
.process-step p { color: #666; }

.booking-card { background: var(--primary-color); border-radius: 10px; padding: 40px; text-align: center; margin-top: 60px; }
.booking-card h3 { font-weight: 700; margin-bottom: 20px; }
.booking-card p { color: #666; margin-bottom: 30px; }
.booking-phone { font-size: 2rem; font-weight: 700; color: var(--secondary-color); margin-bottom: 25px; display: block; }

@media (max-width: 992px) {
  .service-img-container { height: 300px; }
  .col-lg-2 { width: 33.333% !important; }
}
@media (max-width: 768px) {
  .col-lg-2 { width: 50% !important; }
}
</style>
