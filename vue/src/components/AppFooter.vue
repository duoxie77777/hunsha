<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-grid">
        <div class="footer-about">
          <h3>唯<span>爱</span>婚纱摄影</h3>
          <p>专注婚纱摄影15年，用专业的技术和贴心的服务，为每一对新人记录最美好的瞬间。</p>
          <div class="social-links">
            <a href="#"><el-icon :size="18"><ChatDotRound /></el-icon></a>
            <a href="#"><el-icon :size="18"><Share /></el-icon></a>
            <a href="#"><el-icon :size="18"><Camera /></el-icon></a>
            <a href="#"><el-icon :size="18"><VideoCamera /></el-icon></a>
          </div>
        </div>
        <div class="footer-links">
          <h4>服务项目</h4>
          <ul>
            <li><router-link to="/services">婚纱拍摄</router-link></li>
            <li><router-link to="/services">婚礼跟拍</router-link></li>
            <li><router-link to="/services">婚纱租赁</router-link></li>
            <li><router-link to="/services">化妆造型</router-link></li>
            <li><router-link to="/services">旅拍服务</router-link></li>
          </ul>
        </div>
        <div class="footer-links">
          <h4>关于我们</h4>
          <ul>
            <li><router-link to="/about">公司简介</router-link></li>
            <li><router-link to="/about">团队介绍</router-link></li>
            <li><router-link to="/about">荣誉资质</router-link></li>
            <li><router-link to="/about">新闻动态</router-link></li>
            <li><router-link to="/about">招聘信息</router-link></li>
          </ul>
        </div>
        <div class="footer-links">
          <h4>帮助中心</h4>
          <ul>
            <li><a href="#">预约流程</a></li>
            <li><a href="#">常见问题</a></li>
            <li><a href="#">拍摄须知</a></li>
            <li><a href="#">售后服务</a></li>
            <li><a href="#">投诉建议</a></li>
          </ul>
        </div>
        <div class="footer-links">
          <h4>联系方式</h4>
          <ul>
            <li><a href="#">在线客服</a></li>
            <li><a href="#">门店地址</a></li>
            <li><a href="#">加盟合作</a></li>
            <li><a href="#">商务洽谈</a></li>
            <li><a href="#">隐私政策</a></li>
          </ul>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2026 唯爱婚纱摄影 All Rights Reserved. 京ICP备12345678号</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ChatDotRound, Share, Camera, VideoCamera } from '@element-plus/icons-vue'
</script>

<style scoped>
.app-footer {
  background-color: var(--dark-color);
  color: #fff;
  padding: 80px 0 20px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  gap: 30px;
}

.footer-about h3 {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 20px;
}

.footer-about h3 span {
  color: var(--secondary-color);
}

.footer-about p {
  color: #aaa;
  margin-bottom: 20px;
  line-height: 1.8;
}

.social-links {
  display: flex;
  gap: 10px;
}

.social-links a {
  display: inline-flex;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.1);
  color: #fff;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.social-links a:hover {
  background-color: var(--secondary-color);
  transform: translateY(-5px);
}

.footer-links h4 {
  font-weight: 700;
  margin-bottom: 20px;
  position: relative;
  padding-bottom: 10px;
}

.footer-links h4::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 50px;
  height: 2px;
  background-color: var(--secondary-color);
}

.footer-links ul {
  list-style: none;
  padding: 0;
}

.footer-links ul li {
  margin-bottom: 10px;
}

.footer-links ul li a {
  color: #aaa;
  transition: all 0.3s ease;
}

.footer-links ul li a:hover {
  color: var(--secondary-color);
  padding-left: 5px;
}

.footer-bottom {
  text-align: center;
  padding-top: 30px;
  margin-top: 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: #aaa;
}

@media (max-width: 992px) {
  .footer-grid {
    grid-template-columns: 1fr 1fr;
    gap: 30px;
  }
  .footer-about {
    grid-column: 1 / -1;
  }
}

@media (max-width: 576px) {
  .footer-grid {
    grid-template-columns: 1fr;
  }
}
</style>
