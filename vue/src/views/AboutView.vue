<template>
  <div class="about-page">
    <!-- 面包屑 -->
    <section class="page-banner">
      <div class="container">
        <h1>关于我们</h1>
        <div class="breadcrumb-nav">
          <router-link to="/">首页</router-link>
          <span> / </span>
          <span class="active">关于我们</span>
        </div>
      </div>
    </section>

    <!-- 品牌故事 -->
    <section class="brand-story">
      <div class="container">
        <div class="section-title">
          <h2>品牌故事</h2>
          <p>十五年匠心坚守，只为记录每一对新人的幸福瞬间</p>
        </div>
        <div class="story-content">
          <div class="story-text">
            <h3>唯爱，因爱而生</h3>
            <p>唯爱婚纱摄影创立于2009年，由知名摄影师李明先生与婚纱设计师王丽女士共同创立。最初只是一间不足百平米的小小摄影工作室，怀揣着对摄影的热爱和对爱情的敬畏，我们用心记录每一对新人的幸福时刻。</p>
            <p class="highlight">我们相信，每一张婚纱照都不只是简单的照片，而是爱情的见证，是时光的珍藏，是永恒的记忆。</p>
            <p>十五年来，我们从最初的3人团队发展到如今的百余人专业团队，从1家门店扩展到全国8家直营分店，服务了超过10万对新人，但始终不变的是我们对品质的追求和对每一对新人的用心。</p>
            <p>我们坚持"用镜头捕捉幸福，用光影定格爱情"的理念，不断创新拍摄风格，提升服务品质，只为让每一对选择唯爱的新人，都能拥有独一无二的婚纱照，留下一生难忘的美好回忆。</p>
            <el-button type="primary" class="btn-custom">了解更多品牌故事</el-button>
          </div>
          <div class="story-image">
            <img src="https://picsum.photos/id/1083/800/600" alt="唯爱婚纱摄影品牌故事">
            <div class="story-quote">
              <el-icon><ChatLineSquare /></el-icon>
              <p>最好的爱情，值得最好的记录；最美的瞬间，值得最用心的珍藏。</p>
              <div class="quote-author">—— 唯爱创始人 · 李明</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 发展历程 -->
    <section class="section">
      <div class="container">
        <div class="section-title">
          <h2>发展历程</h2>
          <p>十五年风雨兼程，见证无数幸福，也成就了更好的我们</p>
        </div>
        <el-timeline>
          <el-timeline-item
            v-for="item in timeline"
            :key="item.year"
            :timestamp="item.year"
            placement="top"
            color="var(--primary-color)"
          >
            <el-card shadow="hover">
              <h4>{{ item.title }}</h4>
              <p>{{ item.desc }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </section>

    <!-- 核心优势 -->
    <section class="advantage-section section">
      <div class="container">
        <div class="section-title">
          <h2>核心优势</h2>
          <p>六大核心优势，打造极致拍摄体验，成就完美婚纱照</p>
        </div>
        <div class="row">
          <div class="col-lg-4 col-md-6 mb-4" v-for="item in advantages" :key="item.title">
            <div class="advantage-card">
              <div class="advantage-icon">
                <el-icon :size="36"><component :is="item.icon" /></el-icon>
              </div>
              <h4>{{ item.title }}</h4>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 荣誉资质 -->
    <section class="honor-section section">
      <div class="container">
        <div class="section-title">
          <h2>荣誉资质</h2>
          <p>十五年深耕，获得行业和客户的双重认可</p>
        </div>
        <div class="honor-gallery">
          <div class="honor-item" v-for="h in honors" :key="h.title">
            <div class="honor-icon">
              <el-icon :size="32"><component :is="h.icon" /></el-icon>
            </div>
            <div class="honor-title">{{ h.title }}</div>
            <div class="honor-year">{{ h.year }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 企业文化 -->
    <section class="culture-section section">
      <div class="container">
        <div class="section-title">
          <h2>企业文化</h2>
          <p>以爱为名，用心服务，成就每一对新人的完美时刻</p>
        </div>
        <div class="row">
          <div class="col-lg-4 col-md-6 mb-4" v-for="c in culture" :key="c.title">
            <div class="culture-card">
              <div class="culture-icon">
                <el-icon :size="36"><component :is="c.icon" /></el-icon>
              </div>
              <h4>{{ c.title }}</h4>
              <p>{{ c.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import {
  ChatLineSquare, Camera, ChatDotRound, Share,
  Location, Phone, Clock, Message,
  Trophy, Medal, Star, StarFilled,
  Sunny, OfficeBuilding, Aim, View, Headset
} from '@element-plus/icons-vue'
import { userStore } from '../stores/user'

const timeline = [
  { year: '2009', title: '品牌创立', desc: '唯爱婚纱摄影工作室在北京市朝阳区正式成立，创始团队仅3人，以独特的拍摄风格和贴心的服务迅速获得市场认可。' },
  { year: '2012', title: '首次扩张', desc: '开设第二家门店，团队规模扩大至20人，引入专业化妆团队和后期制作团队，服务体系日趋完善。' },
  { year: '2015', title: '全国布局', desc: '上海、广州分店相继开业，正式开启全国化布局，推出旅拍服务，成为行业内旅拍服务的先行者。' },
  { year: '2018', title: '品牌升级', desc: '完成品牌全面升级，打造全新VI系统，建立标准化服务流程，年服务新人突破1万对。' },
  { year: '2021', title: '技术革新', desc: '引入3D虚拟场景拍摄技术，建立专业影像后期制作中心，荣获"中国婚纱摄影行业创新奖"。' },
  { year: '2024', title: '全新征程', desc: '全国直营门店达8家，团队规模超100人，累计服务新人超10万对，开启品牌第二个十五年新篇章。' }
]

const advantages = [
  { icon: 'Camera', title: '专业摄影团队', desc: '所有摄影师均拥有8年以上婚纱摄影经验，定期接受国际前沿拍摄技术培训，总监级摄影师全程掌镜，确保每一张照片都达到专业级水准。' },
  { icon: 'Sunny', title: '定制化造型设计', desc: '根据新人的五官特征、气质特点和拍摄风格，量身定制专属妆容和造型，使用一线品牌化妆品，打造自然精致的妆容效果。' },
  { icon: 'View', title: '多场景拍摄基地', desc: '拥有超过10000㎡的室内拍摄基地和20+独家外景拍摄场地，涵盖欧式、中式、韩式、森系等多种风格，满足不同新人的拍摄需求。' },
  { icon: 'StarFilled', title: '高端婚纱礼服', desc: '精选国际知名婚纱品牌，拥有超过500套婚纱礼服可供选择，定期更新款式，所有礼服均经过严格消毒，确保卫生安全。' },
  { icon: 'Aim', title: '精细化后期制作', desc: '采用纯手工精修，拒绝模板化修图，保留新人的自然特征和照片的真实质感，修图师一对一沟通，确保修图效果符合新人预期。' },
  { icon: 'Headset', title: '一站式贴心服务', desc: '从前期咨询、拍摄方案定制到后期选片、产品制作，提供全程一对一专属服务，无隐形消费，不满意免费重拍，让新人全程无忧。' }
]

const team = [
  { name: '张明', position: '摄影总监', img: 'https://picsum.photos/id/1005/400/500', desc: '15年婚纱摄影经验，曾获全国婚纱摄影大赛金奖，擅长捕捉新人最自然的情感瞬间，拍摄风格细腻而富有故事感。' },
  { name: '李娜', position: '化妆总监', img: 'https://picsum.photos/id/1006/400/500', desc: '12年化妆造型经验，曾为多位明星定制婚礼造型，擅长根据新人特征打造自然精致的妆容，风格多变且富有创意。' },
  { name: '王浩', position: '后期总监', img: 'https://picsum.photos/id/1010/400/500', desc: '10年后期制作经验，精通各种修图技术，注重细节处理和色彩还原，擅长保留照片的自然质感，拒绝过度修图。' },
  { name: '刘佳', position: '礼服总监', img: 'https://picsum.photos/id/1012/400/500', desc: '8年婚纱礼服搭配经验，熟悉各种身材特点和婚纱款式，擅长根据新人特征推荐最适合的婚纱礼服，打造完美身形比例。' }
]

const honors = [
  { icon: 'Trophy', title: '中国婚纱摄影行业十大品牌', year: '2018-2024 连续7年' },
  { icon: 'Medal', title: '全国婚纱摄影创新金奖', year: '2021年度' },
  { icon: 'Star', title: '消费者最信赖品牌', year: '2019-2023 连续5年' },
  { icon: 'Medal', title: '最佳服务品质奖', year: '2020年度' },
  { icon: 'StarFilled', title: '客户满意度98.7%', year: '2024年度' },
  { icon: 'OfficeBuilding', title: '五星级摄影机构', year: '2022-2024 连续3年' }
]

const stores = [
  { name: '北京总店', img: 'https://picsum.photos/id/1031/600/400', address: '北京市朝阳区建国路88号', phone: '010-88888888', hours: '9:00 - 21:00 (全年无休)' },
  { name: '上海分店', img: 'https://picsum.photos/id/1039/600/400', address: '上海市浦东新区陆家嘴环路1000号', phone: '021-99999999', hours: '9:00 - 21:00 (全年无休)' },
  { name: '广州分店', img: 'https://picsum.photos/id/1040/600/400', address: '广州市天河区天河路385号', phone: '020-77777777', hours: '9:00 - 21:00 (全年无休)' }
]

const culture = [
  { icon: 'Aim', title: '品牌使命', desc: '用专业的技术和贴心的服务，记录每一对新人的幸福瞬间，让每一张婚纱照都成为永恒的记忆，见证爱情的美好与珍贵。' },
  { icon: 'View', title: '品牌愿景', desc: '成为中国最具影响力的婚纱摄影品牌，让每一对新人都能享受到高品质的摄影服务，让唯爱成为爱情与幸福的见证者。' },
  { icon: 'StarFilled', title: '核心价值观', desc: '诚信、专业、创新、用心。以诚信为本，以专业立足，以创新求发展，用心服务每一位客户，成就每一个完美瞬间。' }
]
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  padding: 80px 0 40px;
  text-align: center;
  color: #fff;
  margin-top: 70px;
}
.page-banner h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
}
.breadcrumb-nav a {
  color: rgba(255,255,255,0.8);
  text-decoration: none;
}
.breadcrumb-nav .active {
  color: #fff;
}

/* 品牌故事 */
.brand-story {
  padding: 80px 0;
}
.story-content {
  display: flex;
  gap: 40px;
  align-items: flex-start;
  margin-top: 40px;
}
.story-text {
  flex: 1;
}
.story-text h3 {
  font-size: 1.8rem;
  color: var(--primary-color);
  margin-bottom: 20px;
}
.story-text p {
  line-height: 1.8;
  color: #555;
  margin-bottom: 15px;
}
.story-text .highlight {
  color: var(--primary-color);
  font-style: italic;
  font-size: 1.05rem;
  border-left: 3px solid var(--primary-color);
  padding-left: 15px;
}
.story-image {
  flex: 1;
  position: relative;
}
.story-image img {
  width: 100%;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}
.story-quote {
  background: rgba(255,255,255,0.95);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  position: absolute;
  bottom: -30px;
  left: 20px;
  right: 20px;
}
.story-quote .el-icon {
  color: var(--primary-color);
  font-size: 24px;
  margin-bottom: 8px;
}
.story-quote p {
  font-style: italic;
  color: #555;
  margin-bottom: 8px;
}
.quote-author {
  text-align: right;
  color: var(--primary-color);
  font-size: 0.9rem;
}

/* 时间线 */
:deep(.el-timeline) {
  max-width: 800px;
  margin: 0 auto;
  padding-left: 20px;
}
:deep(.el-timeline-item__timestamp) {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--primary-color);
}
:deep(.el-card) {
  border-radius: 10px;
}
:deep(.el-card h4) {
  color: var(--text-dark);
  margin-bottom: 8px;
  font-size: 1.1rem;
}
:deep(.el-card p) {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

/* 核心优势 */
.advantage-section {
  background: #fdf5f6;
}
.advantage-card {
  background: #fff;
  border-radius: 10px;
  padding: 30px 25px;
  text-align: center;
  height: 100%;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
.advantage-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(241,174,181,0.2);
}
.advantage-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: #fff;
}
.advantage-card h4 {
  font-size: 1.2rem;
  margin-bottom: 12px;
  color: var(--text-dark);
}
.advantage-card p {
  color: #666;
  line-height: 1.7;
  font-size: 0.95rem;
}

/* 团队 */
.team-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}
.team-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(241,174,181,0.2);
}
.team-img img {
  width: 100%;
  height: 280px;
  object-fit: cover;
}
.team-content {
  padding: 20px;
  text-align: center;
}
.team-name {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--text-dark);
}
.team-position {
  color: var(--primary-color);
  font-size: 0.9rem;
  margin: 5px 0 10px;
}
.team-desc {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.6;
  margin-bottom: 15px;
}
.team-social {
  display: flex;
  justify-content: center;
  gap: 15px;
}
.team-social a {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.3s;
}
.team-social a:hover {
  background: var(--primary-color);
  color: #fff;
}

/* 荣誉 */
.honor-section {
  background: linear-gradient(135deg, #2c1a1d, #4a2a30);
  color: #fff;
}
.honor-section .section-title h2,
.honor-section .section-title p {
  color: #fff;
}
.honor-section .section-title h2::after {
  background: var(--primary-color);
}
.honor-gallery {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
}
.honor-item {
  background: rgba(255,255,255,0.1);
  border-radius: 10px;
  padding: 30px 20px;
  text-align: center;
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
}
.honor-item:hover {
  background: rgba(255,255,255,0.2);
  transform: translateY(-5px);
}
.honor-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  color: #fff;
}
.honor-title {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 8px;
}
.honor-year {
  color: rgba(255,255,255,0.7);
  font-size: 0.9rem;
}

/* 门店 */
.store-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}
.store-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(241,174,181,0.2);
}
.store-img img {
  width: 100%;
  height: 220px;
  object-fit: cover;
}
.store-content {
  padding: 20px;
}
.store-name {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--text-dark);
  margin-bottom: 12px;
}
.store-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  margin-bottom: 8px;
  font-size: 0.9rem;
}
.store-info .el-icon {
  color: var(--primary-color);
}

/* 企业文化 */
.culture-section {
  background: #fdf5f6;
}
.culture-card {
  background: #fff;
  border-radius: 10px;
  padding: 30px 25px;
  text-align: center;
  height: 100%;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
.culture-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(241,174,181,0.2);
}
.culture-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: #fff;
}
.culture-card h4 {
  font-size: 1.2rem;
  margin-bottom: 12px;
  color: var(--text-dark);
}
.culture-card p {
  color: #666;
  line-height: 1.7;
  font-size: 0.95rem;
}

</style>
