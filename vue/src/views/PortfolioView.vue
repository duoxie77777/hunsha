<template>
  <div class="portfolio-page">
    <section class="breadcrumb-section">
      <div class="container">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><router-link to="/">首页</router-link></li>
          <li class="breadcrumb-item active">作品展示</li>
        </ol>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <div class="section-title">
          <h2>精选作品展示</h2>
          <p>每一张照片都是一个故事，每一个镜头都记录着幸福的瞬间</p>
        </div>

        <!-- 筛选 -->
        <div class="filter-section">
          <div class="filter-group">
            <span class="filter-label">按风格：</span>
            <el-radio-group v-model="styleFilter" size="small">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="forest">森系清新</el-radio-button>
              <el-radio-button label="retro">复古经典</el-radio-button>
              <el-radio-button label="chinese">中式传统</el-radio-button>
              <el-radio-button label="luxury">轻奢简约</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <!-- 作品展示区 -->
        <div class="gallery-container">
          <div
            class="gallery-item"
            v-for="(item, i) in filteredWorks"
            :key="i"
            @click="previewImg(item.img)"
          >
            <img :src="item.thumb" :alt="item.title" />
            <div class="gallery-overlay">
              <h4>{{ item.title }}</h4>
              <div class="gallery-tags">
                <el-tag v-for="t in item.tags" :key="t" size="small" type="info" effect="plain">{{ t }}</el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 精选作品集 -->
        <div class="section-title" style="margin-top:80px;">
          <h2>精选作品集</h2>
          <p>完整的婚纱摄影作品集，记录新人从相识到相守的美好瞬间</p>
        </div>
        <div class="row">
          <div class="col-lg-4 mb-4" v-for="(c, i) in collections" :key="i">
            <div class="collection-card">
              <div class="collection-img">
                <el-tag class="collection-badge" effect="dark" type="danger">{{ c.badge }}</el-tag>
                <img :src="c.img" :alt="c.title" />
              </div>
              <div class="collection-content">
                <h4>{{ c.title }}</h4>
                <p>{{ c.desc }}</p>
                <div class="collection-stats">
                  <div class="stat-item" v-for="(s, j) in c.stats" :key="j">
                    <div class="stat-number">{{ s.value }}</div>
                    <div class="stat-label">{{ s.label }}</div>
                  </div>
                </div>
                <button class="btn-custom w-100">查看完整作品集</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 预约 -->
    <section class="booking-section">
      <div class="container">
        <div class="booking-content">
          <h3>喜欢我们的作品？立即预约拍摄</h3>
          <p>无论您喜欢哪种风格的婚纱照，我们都能为您量身定制。现在预约，还可享受专属优惠！</p>
          <a href="javascript:void(0)" class="btn-custom" @click="userStore.openBooking()">立即预约</a>
        </div>
      </div>
    </section>

    <el-image-viewer v-if="showViewer" :url-list="[viewerUrl]" @close="showViewer = false" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElImageViewer } from 'element-plus'
import { userStore } from '../stores/user'

const styleFilter = ref('all')
const showViewer = ref(false)
const viewerUrl = ref('')

const works = [
  { thumb: 'https://picsum.photos/id/1011/600/800', img: 'https://picsum.photos/id/1011/1200/800', title: '森系清新外景婚纱照', tags: ['森系', '外景', '暖色系'], category: 'forest' },
  { thumb: 'https://picsum.photos/id/1015/600/800', img: 'https://picsum.photos/id/1015/1200/800', title: '复古经典室内婚纱照', tags: ['复古', '室内', '冷色系'], category: 'retro' },
  { thumb: 'https://picsum.photos/id/1018/600/800', img: 'https://picsum.photos/id/1018/1200/800', title: '中式传统婚纱照', tags: ['中式', '室内', '多彩'], category: 'chinese' },
  { thumb: 'https://picsum.photos/id/1019/600/800', img: 'https://picsum.photos/id/1019/1200/800', title: '轻奢旅拍婚纱照', tags: ['轻奢', '旅拍', '暖色系'], category: 'luxury' },
  { thumb: 'https://picsum.photos/id/1035/600/800', img: 'https://picsum.photos/id/1035/1200/800', title: '森系冷色调外景婚纱照', tags: ['森系', '外景', '冷色系'], category: 'forest' },
  { thumb: 'https://picsum.photos/id/1036/600/800', img: 'https://picsum.photos/id/1036/1200/800', title: '复古外景婚纱照', tags: ['复古', '外景', '多彩'], category: 'retro' },
  { thumb: 'https://picsum.photos/id/1043/600/800', img: 'https://picsum.photos/id/1043/1200/800', title: '中式外景婚纱照', tags: ['中式', '外景', '暖色系'], category: 'chinese' },
  { thumb: 'https://picsum.photos/id/1047/600/800', img: 'https://picsum.photos/id/1047/1200/800', title: '轻奢室内婚纱照', tags: ['轻奢', '室内', '冷色系'], category: 'luxury' },
  { thumb: 'https://picsum.photos/id/1058/600/800', img: 'https://picsum.photos/id/1058/1200/800', title: '森系旅拍婚纱照', tags: ['森系', '旅拍', '多彩'], category: 'forest' }
]

const filteredWorks = computed(() => {
  if (styleFilter.value === 'all') return works
  return works.filter(w => w.category === styleFilter.value)
})

const previewImg = (url) => {
  viewerUrl.value = url
  showViewer.value = true
}

const collections = [
  { badge: '三亚旅拍', img: 'https://picsum.photos/id/1069/800/600', title: '三亚浪漫海景婚纱照', desc: '碧海蓝天，沙滩白浪，在三亚的海边记录属于你们的浪漫时刻。', stats: [{ value: 45, label: '精修照片' }, { value: 8, label: '拍摄场景' }, { value: 6, label: '服装造型' }] },
  { badge: '复古系列', img: 'https://picsum.photos/id/1074/800/600', title: '复古欧式宫廷婚纱照', desc: '经典的欧式宫廷风格，华丽的服饰搭配精致的妆容。', stats: [{ value: 38, label: '精修照片' }, { value: 5, label: '拍摄场景' }, { value: 7, label: '服装造型' }] },
  { badge: '中式系列', img: 'https://picsum.photos/id/1080/800/600', title: '新中式国潮婚纱照', desc: '融合传统与现代的新中式风格，展现中式婚礼的独特韵味。', stats: [{ value: 42, label: '精修照片' }, { value: 6, label: '拍摄场景' }, { value: 5, label: '服装造型' }] }
]
</script>

<style scoped>
.filter-section { background: #fff; padding: 20px 30px; border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.05); margin-bottom: 40px; }
.filter-group { display: flex; align-items: center; flex-wrap: wrap; gap: 10px; }
.filter-label { font-weight: 600; color: var(--dark-color); }
:deep(.el-radio-button__inner) { border-radius: 20px !important; }

.gallery-container { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; margin-bottom: 60px; }
.gallery-item { border-radius: 10px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.08); position: relative; height: 400px; cursor: pointer; transition: all 0.5s ease; }
.gallery-item:hover { transform: translateY(-10px); box-shadow: 0 15px 30px rgba(241,174,181,0.2); }
.gallery-item img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.gallery-item:hover img { transform: scale(1.05); }
.gallery-overlay { position: absolute; bottom: 0; left: 0; width: 100%; background: linear-gradient(transparent, rgba(0,0,0,0.7)); padding: 20px; opacity: 0; transition: opacity 0.3s; }
.gallery-item:hover .gallery-overlay { opacity: 1; }
.gallery-overlay h4 { color: #fff; font-weight: 600; margin-bottom: 10px; }
.gallery-tags { display: flex; gap: 5px; }

.collection-card { border-radius: 15px; overflow: hidden; background: #fff; box-shadow: 0 10px 30px rgba(0,0,0,0.08); }
.collection-img { height: 300px; position: relative; }
.collection-img img { width: 100%; height: 100%; object-fit: cover; }
.collection-badge { position: absolute; top: 20px; left: 20px; }
.collection-content { padding: 30px; }
.collection-content h4 { font-weight: 700; margin-bottom: 15px; }
.collection-content > p { color: #666; margin-bottom: 20px; line-height: 1.8; }
.collection-stats { display: flex; justify-content: space-between; margin-bottom: 20px; }
.stat-item { text-align: center; }
.stat-number { font-weight: 700; color: var(--secondary-color); font-size: 1.5rem; }
.stat-label { font-size: 0.9rem; color: #666; }

.booking-section { padding: 80px 0; background: #fff; }
.booking-content { max-width: 700px; margin: 0 auto; text-align: center; }
.booking-content h3 { font-weight: 700; margin-bottom: 20px; font-size: 2rem; }
.booking-content p { color: #666; margin-bottom: 30px; line-height: 1.8; }

@media (max-width: 768px) {
  .gallery-container { grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); }
  .gallery-item { height: 300px; }
}
</style>
