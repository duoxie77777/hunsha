<template>
  <div class="pricing-page">
    <section class="breadcrumb-section">
      <div class="container">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><router-link to="/">首页</router-link></li>
          <li class="breadcrumb-item active">套餐价格</li>
        </ol>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <div class="section-title">
          <h2>套餐价格</h2>
          <p>透明定价，无隐形消费，多种套餐满足不同需求，总有一款适合您</p>
        </div>

        <!-- 优惠横幅 -->
        <div class="promotion-banner">
          <h3>限时优惠 · 春季特惠</h3>
          <p>活动期间预订任意套餐，立减800元，再赠送价值1500元的婚嫁大礼包</p>
          <div class="promotion-price">活动截止：2026年6月30日</div>
          <button class="btn-custom" @click="userStore.openBooking()">立即抢购</button>
        </div>

        <!-- 套餐卡片 -->
        <div class="row">
          <div class="col-lg-4 mb-4" v-for="(pkg, i) in packages" :key="i">
            <div class="package-card" :class="{ popular: pkg.popular }">
              <div v-if="pkg.popular" class="package-badge">热门推荐</div>
              <div class="package-header">
                <h3>{{ pkg.name }}</h3>
                <div class="package-price">{{ pkg.price }}<span>起</span></div>
                <div class="package-period">拍摄时长：{{ pkg.duration }}</div>
              </div>
              <div class="package-body">
                <ul class="package-features">
                  <li v-for="(f, j) in pkg.features" :key="j">
                    <el-icon color="var(--success-color)"><Select /></el-icon> {{ f }}
                  </li>
                </ul>
                <el-button :type="pkg.popular ? 'primary' : ''" class="w-100" size="large"
                  :style="pkg.popular ? 'background:var(--secondary-color);border-color:var(--secondary-color);' : 'color:var(--secondary-color);border-color:var(--secondary-color);'"
                  @click="userStore.openBooking()">
                  立即预约
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 套餐对比 -->
        <div class="section-title" style="margin-top:80px;">
          <h2>套餐详细对比</h2>
          <p>清晰了解各套餐的详细差异，选择最适合您的拍摄方案</p>
        </div>
        <el-table :data="comparisonData" border stripe style="width:100%;" :header-cell-style="{ background: 'var(--primary-color)', fontWeight: 600 }">
          <el-table-column prop="feature" label="服务内容" width="180" />
          <el-table-column prop="basic" label="浪漫体验套餐 (¥3999)" align="center" />
          <el-table-column prop="classic" label="经典挚爱套餐 (¥6999)" align="center" />
          <el-table-column prop="premium" label="高端定制套餐 (¥12999)" align="center" />
        </el-table>

        <!-- 增值服务 -->
        <div class="section-title" style="margin-top:80px;">
          <h2>增值服务</h2>
          <p>除基础套餐外，我们还提供多种增值服务，满足您的个性化需求</p>
        </div>
        <div class="row">
          <div class="col-lg-4 mb-4" v-for="(addon, i) in addons" :key="i">
            <div class="addon-item">
              <h4><el-icon :size="20" color="var(--secondary-color)"><component :is="addon.icon" /></el-icon> {{ addon.title }}</h4>
              <div class="addon-price">{{ addon.price }}</div>
              <p>{{ addon.desc }}</p>
            </div>
          </div>
        </div>

        <!-- 套餐说明 -->
        <div class="section-title" style="margin-top:80px;">
          <h2>套餐说明</h2>
          <p>了解套餐使用规则，保障您的权益</p>
        </div>
        <ul class="terms-list">
          <li v-for="(term, i) in terms" :key="i">
            <el-icon color="var(--secondary-color)"><InfoFilled /></el-icon>
            <span v-html="term"></span>
          </li>
        </ul>

        <!-- 预约表单 -->
        <div class="row" style="margin-top:60px;">
          <div class="col-lg-8" style="margin:0 auto;">
            <div class="booking-form-container">
              <h3>在线预约套餐</h3>
              <el-form :model="form" label-width="0">
                <div class="row">
                  <div class="col-lg-6 mb-4">
                    <el-input v-model="form.name" placeholder="您的姓名" size="large" />
                  </div>
                  <div class="col-lg-6 mb-4">
                    <el-input v-model="form.phone" placeholder="联系电话" size="large" />
                  </div>
                  <div class="col-lg-6 mb-4">
                    <el-date-picker v-model="form.date" placeholder="预约拍摄日期" size="large" style="width:100%;" />
                  </div>
                  <div class="col-lg-6 mb-4">
                    <el-select v-model="form.pkg" placeholder="选择套餐类型" size="large" style="width:100%;">
                      <el-option label="浪漫体验套餐 (¥3999)" value="1" />
                      <el-option label="经典挚爱套餐 (¥6999)" value="2" />
                      <el-option label="高端定制套餐 (¥12999)" value="3" />
                    </el-select>
                  </div>
                  <div class="col-lg-12 mb-4">
                    <el-input v-model="form.message" type="textarea" :rows="4" placeholder="您的特殊要求或疑问" />
                  </div>
                  <div class="col-lg-12">
                    <el-button type="primary" class="w-100" size="large" style="background:var(--secondary-color);border-color:var(--secondary-color);" @click="userStore.openBooking()">提交预约</el-button>
                  </div>
                </div>
              </el-form>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Select, Picture, Camera, Goods, Film, PictureFilled, Location, InfoFilled } from '@element-plus/icons-vue'
import { userStore } from '../stores/user'
import { getPackagesApi } from '../api/package'

const packages = ref([
  { name: '浪漫体验套餐', price: '¥3999', duration: '1天', popular: false, features: ['室内拍摄场景3个', '服装造型各3套', '拍摄照片150张以上', '精修照片20张', '10寸水晶相册1本', '24寸放大相框1个', '底片全送', '资深摄影师拍摄'] },
  { name: '经典挚爱套餐', price: '¥6999', duration: '1天', popular: true, features: ['室内+外景共5个场景', '服装造型各5套', '拍摄照片300张以上', '精修照片40张', '16寸水晶相册2本', '36寸放大相框1个', '底片全送+视频花絮', '首席摄影师拍摄'] },
  { name: '高端定制套餐', price: '¥12999', duration: '2天', popular: false, features: ['多场景定制拍摄', '服装造型不限套数', '拍摄照片500张以上', '精修照片80张', '高端定制相册3本', '全屋相框组合', '微电影+底片全送', '总监级摄影师拍摄'] }
])

const packageOptions = ref([])

onMounted(async () => {
  try {
    const res = await getPackagesApi()
    if (res.data?.length) {
      packageOptions.value = res.data
      packages.value = res.data.map(p => ({
        name: p.name,
        price: '¥' + p.price,
        duration: '-',
        popular: p.isHot === 1,
        features: p.includes || []
      }))
    }
  } catch {}
})

const comparisonData = [
  { feature: '拍摄天数', basic: '1天', classic: '1天', premium: '2天' },
  { feature: '拍摄场景', basic: '室内3个场景', classic: '室内+外景共5个场景', premium: '定制化多场景' },
  { feature: '服装造型', basic: '各3套（基础区）', classic: '各5套（全馆任选）', premium: '不限套数（含高定区）' },
  { feature: '拍摄张数', basic: '150张以上', classic: '300张以上', premium: '500张以上' },
  { feature: '精修照片', basic: '20张', classic: '40张', premium: '80张' },
  { feature: '相册制作', basic: '10寸1本', classic: '16寸2本', premium: '高端定制3本' },
  { feature: '摄影师级别', basic: '资深摄影师', classic: '首席摄影师', premium: '总监级摄影师' },
  { feature: '视频花絮', basic: '无', classic: '30秒短视频', premium: '微电影3-5分钟' }
]

const addons = [
  { icon: Picture, title: '加修照片', price: '¥80/张', desc: '精修照片超出套餐数量，可单独加修。' },
  { icon: Camera, title: '加拍场景', price: '¥500/个', desc: '如需增加拍摄场景，每个场景收费500元。' },
  { icon: Goods, title: '加穿服装', price: '¥300/套', desc: '超出套餐服装数量，每套收费300元。' },
  { icon: Film, title: '视频制作', price: '¥1500起', desc: '专业视频剪辑制作，30秒-5分钟可选。' },
  { icon: PictureFilled, title: '相框升级', price: '¥300起', desc: '升级相框材质和尺寸，提升成品质感。' },
  { icon: Location, title: '外地拍摄', price: '¥2000起', desc: '如需到外地拍摄，收取交通和住宿费用。' }
]

const terms = [
  '<strong>预约有效期</strong>：预订后1年内有效，可免费改期1次。',
  '<strong>定金政策</strong>：预订需支付套餐总价30%的定金，定金支付后不可退。',
  '<strong>拍摄档期</strong>：周末和节假日档期紧张，建议提前2-3个月预约。',
  '<strong>天气因素</strong>：如遇恶劣天气无法拍摄，可免费改期。',
  '<strong>选片时间</strong>：拍摄完成后7-10个工作日可到店选片。',
  '<strong>成品周期</strong>：选片完成后30-45个工作日制作完成。',
  '<strong>售后服务</strong>：成品如有质量问题，免费重做。',
  '<strong>版权说明</strong>：照片版权归客户所有。'
]

const form = reactive({ name: '', phone: '', date: '', pkg: '', message: '' })
</script>

<style scoped>
.promotion-banner { background: linear-gradient(135deg, #fdf2f8 0%, var(--primary-color) 100%); border-radius: 15px; padding: 50px; text-align: center; margin-bottom: 60px; }
.promotion-banner h3 { font-weight: 700; margin-bottom: 20px; }
.promotion-banner p { color: #666; margin-bottom: 30px; max-width: 700px; margin-left: auto; margin-right: auto; }
.promotion-price { font-size: 2rem; font-weight: 700; color: var(--secondary-color); margin-bottom: 30px; }

.package-card { border-radius: 15px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.08); transition: all 0.5s ease; height: 100%; position: relative; }
.package-card:hover { transform: translateY(-15px); box-shadow: 0 20px 40px rgba(241,174,181,0.2); }
.package-card.popular { transform: translateY(-10px); box-shadow: 0 15px 35px rgba(241,174,181,0.15); }
.package-badge { position: absolute; top: -15px; left: 50%; transform: translateX(-50%); background: var(--secondary-color); color: #fff; padding: 8px 25px; border-radius: 25px; font-weight: 600; z-index: 2; box-shadow: 0 5px 15px rgba(241,174,181,0.3); }
.package-header { background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%); color: #fff; padding: 40px 30px; text-align: center; }
.package-header h3 { font-weight: 700; margin-bottom: 15px; font-size: 1.8rem; }
.package-price { font-size: 3rem; font-weight: 700; margin-bottom: 10px; }
.package-price span { font-size: 1.2rem; font-weight: 400; }
.package-period { font-size: 0.9rem; opacity: 0.9; }
.package-body { padding: 40px 30px; background: #fff; }
.package-features { list-style: none; padding: 0; margin-bottom: 30px; }
.package-features li { padding: 12px 0; border-bottom: 1px solid #f1f1f1; display: flex; align-items: center; gap: 10px; }
.package-features li:last-child { border-bottom: none; }

.addon-item { border-radius: 10px; padding: 30px; background: #fff; box-shadow: 0 5px 15px rgba(0,0,0,0.05); transition: all 0.3s ease; height: 100%; }
.addon-item:hover { transform: translateY(-5px); box-shadow: 0 10px 25px rgba(241,174,181,0.1); }
.addon-item h4 { font-weight: 600; margin-bottom: 15px; display: flex; align-items: center; gap: 10px; }
.addon-price { color: var(--secondary-color); font-weight: 700; font-size: 1.2rem; margin-bottom: 15px; }
.addon-item p { color: #666; }

.terms-list { max-width: 800px; margin: 0 auto; padding: 0; list-style: none; }
.terms-list li { padding: 15px 0; border-bottom: 1px solid #eee; display: flex; align-items: flex-start; gap: 15px; }
.terms-list li:last-child { border-bottom: none; }

.booking-form-container { background: #fff; border-radius: 15px; padding: 40px; box-shadow: 0 10px 30px rgba(0,0,0,0.08); }
.booking-form-container h3 { font-weight: 700; margin-bottom: 30px; text-align: center; }
</style>
