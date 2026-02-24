<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <section class="hero-carousel">
      <el-carousel height="100vh" :interval="5000" arrow="always">
        <el-carousel-item v-for="(slide, index) in slides" :key="index">
          <div class="carousel-slide">
            <img :src="slide.img" :alt="slide.title" />
            <div class="carousel-overlay"></div>
            <div class="carousel-caption">
              <h1>{{ slide.title }}</h1>
              <p>{{ slide.desc }}</p>
              <button class="btn-custom">{{ slide.btn }}</button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 服务项目 -->
    <section class="section bg-light">
      <div class="container">
        <div class="section-title">
          <h2>我们的服务</h2>
          <p>提供全方位的婚纱摄影服务，满足你对完美婚礼的所有想象</p>
        </div>
        <div class="row">
          <div class="col-lg-4 mb-4" v-for="(svc, i) in services" :key="i">
            <div class="service-card">
              <div class="service-img"><img :src="svc.img" :alt="svc.title" /></div>
              <div class="card-body">
                <h5>{{ svc.title }}</h5>
                <p>{{ svc.desc }}</p>
                <router-link to="/services" class="btn-custom">了解详情</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 作品展示 -->
    <section class="section">
      <div class="container">
        <div class="section-title">
          <h2>精选作品</h2>
          <p>每一张照片都是一个故事，每一个故事都值得被珍藏</p>
        </div>
        <div class="row">
          <div class="col-lg-4 mb-4" v-for="(work, i) in works" :key="i">
            <div class="portfolio-item">
              <img :src="work.img" :alt="work.title" />
              <div class="portfolio-overlay">
                <h4>{{ work.title }}</h4>
                <p>{{ work.desc }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="text-center mt-5">
          <router-link to="/portfolio" class="btn-custom">查看更多作品</router-link>
        </div>
      </div>
    </section>

    <!-- 套餐价格 -->
    <section class="section bg-light">
      <div class="container">
        <div class="section-title">
          <h2>套餐价格</h2>
          <p>透明定价，无隐形消费，多种套餐满足不同需求</p>
        </div>
        <div class="row">
          <div class="col-lg-4 mb-4" v-for="(pkg, i) in packages" :key="i">
            <div class="price-card" :class="{ popular: pkg.popular }">
              <h3>{{ pkg.name }}</h3>
              <div class="price">{{ pkg.price }}</div>
              <ul>
                <li v-for="(item, j) in pkg.features" :key="j">
                  <el-icon color="var(--secondary-color)"><Select /></el-icon> {{ item }}
                </li>
              </ul>
              <a href="javascript:void(0)" class="btn-custom w-100" style="display:block;text-align:center;" @click="userStore.openBooking()">立即预约</a>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 客户评价 -->
    <section class="section">
      <div class="container">
        <div class="section-title">
          <h2>客户评价</h2>
          <p>听听他们怎么说，每一个好评都是对我们的肯定</p>
        </div>
        <div class="row">
          <div class="col-lg-4 mb-4" v-for="(t, i) in testimonials" :key="i">
            <div class="testimonial-item">
              <div class="testimonial-img"><img :src="t.img" alt="客户头像" /></div>
              <p>"{{ t.text }}"</p>
              <h5>{{ t.name }}</h5>
              <div class="stars">
                <el-icon v-for="s in t.stars" :key="s" color="#f8c145"><StarFilled /></el-icon>
                <el-icon v-if="t.half" color="#f8c145"><Star /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { Select, StarFilled, Star, Location, Phone, Message, Clock } from '@element-plus/icons-vue'
import { userStore } from '../stores/user'

const slides = [
  { img: 'https://picsum.photos/id/1011/1920/1080', title: '定格永恒的爱', desc: '用镜头捕捉每一个浪漫瞬间，让幸福成为永恒的记忆', btn: '了解更多' },
  { img: 'https://picsum.photos/id/1019/1920/1080', title: '专属你的浪漫', desc: '个性化定制拍摄方案，打造独一无二的婚纱照', btn: '查看套餐' },
  { img: 'https://picsum.photos/id/1039/1920/1080', title: '见证幸福时刻', desc: '专业团队全程服务，让你的婚纱照拍摄体验更加完美', btn: '立即预约' }
]

const services = [
  { img: 'https://picsum.photos/id/1027/600/400', title: '婚纱拍摄', desc: '专业的摄影团队，多种风格可选，室内外景结合，打造专属你的婚纱大片。' },
  { img: 'https://picsum.photos/id/1040/600/400', title: '婚礼跟拍', desc: '记录婚礼当天的每一个珍贵瞬间，从接亲到婚宴，全程高清拍摄。' },
  { img: 'https://picsum.photos/id/1062/600/400', title: '婚纱租赁', desc: '上千款婚纱礼服任选，高端定制，专业试纱师一对一服务，完美贴合身形。' },
  { img: 'https://picsum.photos/id/1070/600/400', title: '化妆造型', desc: '资深化妆师团队，根据你的五官和风格定制专属妆容，全程跟妆补妆。' },
  { img: 'https://picsum.photos/id/1080/600/400', title: '相册制作', desc: '进口材质，多种款式相册可选，高清冲印，色彩还原度高，长久保存不褪色。' },
  { img: 'https://picsum.photos/id/1082/600/400', title: '旅拍服务', desc: '全国热门旅拍城市可选，一站式服务，包含交通住宿，边旅行边拍婚纱照。' }
]

const works = [
  { img: 'https://picsum.photos/id/1015/600/400', title: '森系婚纱照', desc: '自然清新 · 浪漫唯美' },
  { img: 'https://picsum.photos/id/1018/600/400', title: '复古婚纱照', desc: '经典永恒 · 优雅大气' },
  { img: 'https://picsum.photos/id/1025/600/400', title: '海景婚纱照', desc: '浪漫海边 · 唯美大气' },
  { img: 'https://picsum.photos/id/1035/600/400', title: '城市婚纱照', desc: '时尚都市 · 个性十足' },
  { img: 'https://picsum.photos/id/1045/600/400', title: '中式婚纱照', desc: '传统韵味 · 喜庆吉祥' },
  { img: 'https://picsum.photos/id/1065/600/400', title: '轻奢婚纱照', desc: '高端定制 · 奢华体验' }
]

const packages = [
  { name: '浪漫体验套餐', price: '¥3999', popular: false, features: ['室内拍摄场景3个', '服装造型各3套', '精修照片20张', '10寸水晶相册1本', '24寸放大相框1个', '底片全送'] },
  { name: '经典挚爱套餐', price: '¥6999', popular: true, features: ['室内+外景拍摄', '服装造型各5套', '精修照片40张', '16寸水晶相册2本', '36寸放大相框1个', '底片全送+视频花絮'] },
  { name: '高端定制套餐', price: '¥12999', popular: false, features: ['多场景定制拍摄', '服装造型不限', '精修照片80张', '高端定制相册3本', '全屋相框组合', '微电影+底片全送'] }
]

const testimonials = [
  { img: 'https://picsum.photos/id/1001/200/200', text: '拍摄体验非常好，摄影师很专业，化妆师的技术也超棒，照片出来的效果比想象中还要好，非常满意！', name: '张先生 & 李女士', stars: 5, half: false },
  { img: 'https://picsum.photos/id/1002/200/200', text: '从前期沟通到后期选片，整个过程都非常顺畅，工作人员都很有耐心，性价比超高，推荐给准备拍婚纱照的朋友们！', name: '王先生 & 刘女士', stars: 5, half: false },
  { img: 'https://picsum.photos/id/1003/200/200', text: '旅拍服务真的太赞了，全程不用操心，摄影师会指导动作，拍出来的照片自然又好看，是一次非常完美的体验。', name: '赵先生 & 陈女士', stars: 4, half: true }
]

const form = reactive({ name: '', phone: '', date: '', pkg: '', message: '' })
</script>

<style scoped>
.hero-carousel { margin-top: -70px; }
.hero-carousel :deep(.el-carousel__container) { height: 100vh !important; }
.carousel-slide { position: relative; height: 100%; }
.carousel-slide img { width: 100%; height: 100%; object-fit: cover; }
.carousel-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.45); }
.carousel-caption { position: absolute; bottom: 20%; left: 50%; transform: translateX(-50%); text-align: center; color: #fff; z-index: 2; }
.carousel-caption h1 { font-size: 4rem; font-weight: 700; margin-bottom: 20px; text-shadow: 2px 2px 4px rgba(0,0,0,0.5); }
.carousel-caption p { font-size: 1.2rem; margin-bottom: 30px; text-shadow: 1px 1px 2px rgba(0,0,0,0.5); }

.service-card { border-radius: 10px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.08); transition: all 0.3s ease; height: 100%; background: #fff; }
.service-card:hover { transform: translateY(-10px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }
.service-img { height: 200px; overflow: hidden; }
.service-img img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.service-card:hover .service-img img { transform: scale(1.1); }
.card-body { padding: 25px; text-align: center; }
.card-body h5 { font-weight: 700; margin-bottom: 15px; color: var(--dark-color); }
.card-body p { color: #666; margin-bottom: 15px; }

.portfolio-item { position: relative; overflow: hidden; border-radius: 10px; }
.portfolio-item img { width: 100%; height: 300px; object-fit: cover; transition: transform 0.5s; }
.portfolio-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(241,174,181,0.8); display: flex; flex-direction: column; justify-content: center; align-items: center; opacity: 0; transition: opacity 0.5s; }
.portfolio-item:hover .portfolio-overlay { opacity: 1; }
.portfolio-item:hover img { transform: scale(1.1); }
.portfolio-overlay h4 { color: #fff; font-weight: 700; margin-bottom: 10px; }
.portfolio-overlay p { color: #fff; }

.price-card { border: 1px solid #eee; border-radius: 10px; padding: 40px 30px; text-align: center; transition: all 0.3s ease; height: 100%; background: #fff; position: relative; }
.price-card:hover { transform: translateY(-10px); box-shadow: 0 15px 30px rgba(241,174,181,0.2); border-color: var(--secondary-color); }
.price-card.popular { background: var(--primary-color); }
.price-card.popular::before { content: '热门推荐'; position: absolute; top: -15px; left: 50%; transform: translateX(-50%); background: var(--secondary-color); color: #fff; padding: 5px 20px; border-radius: 20px; font-size: 0.9rem; font-weight: 600; }
.price-card h3 { font-weight: 700; margin-bottom: 20px; }
.price-card .price { font-size: 2.5rem; font-weight: 700; color: var(--secondary-color); margin-bottom: 20px; }
.price-card ul { list-style: none; padding: 0; margin-bottom: 30px; text-align: left; }
.price-card ul li { padding: 10px 0; border-bottom: 1px solid #eee; display: flex; align-items: center; gap: 10px; }

.testimonial-item { padding: 30px; border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.08); text-align: center; background: #fff; }
.testimonial-img { width: 100px; height: 100px; border-radius: 50%; overflow: hidden; margin: 0 auto 20px; border: 5px solid var(--primary-color); }
.testimonial-img img { width: 100%; height: 100%; object-fit: cover; }
.testimonial-item p { font-style: italic; margin-bottom: 20px; color: #666; }
.testimonial-item h5 { font-weight: 700; margin-bottom: 5px; }
.stars { display: flex; justify-content: center; gap: 2px; }

.contact-info { background: var(--primary-color); padding: 40px; border-radius: 10px; }
.contact-info h3 { font-weight: 700; margin-bottom: 30px; }
.contact-info-item { display: flex; align-items: center; margin-bottom: 20px; gap: 15px; }
.contact-info-item h5 { margin: 0 0 5px; font-weight: 600; }
.contact-info-item p { margin: 0; color: #666; }
.contact-form { padding: 40px; border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.08); background: #fff; }
.contact-form h3 { font-weight: 700; margin-bottom: 30px; }

@media (max-width: 768px) {
  .carousel-caption h1 { font-size: 2.5rem; }
  .carousel-caption p { font-size: 1rem; }
}
</style>
