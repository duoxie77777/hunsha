<template>
  <div id="app">
    <template v-if="isAdminRoute">
      <router-view />
    </template>
    <template v-else>
      <AppHeader />
      <main>
        <router-view />
      </main>
      <AppFooter />
      <LoginDialog />
      <BookingDialog />
      <ChatDialog />
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
import LoginDialog from './components/LoginDialog.vue'
import BookingDialog from './components/BookingDialog.vue'
import ChatDialog from './components/ChatDialog.vue'

const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}
</style>
