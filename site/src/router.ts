import { createRouter, createWebHistory } from 'vue-router'
import CaseDetailView from './views/CaseDetailView.vue'
import HomeView from './views/HomeView.vue'
import NoteDetailView from './views/NoteDetailView.vue'

export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/cases/:slug',
      name: 'case-detail',
      component: CaseDetailView
    },
    {
      path: '/notes/:slug',
      name: 'note-detail',
      component: NoteDetailView
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ],
  scrollBehavior(to) {
    if (to.hash) {
      return {
        el: to.hash,
        top: 24,
        behavior: 'smooth'
      }
    }

    return {
      top: 0,
      behavior: 'smooth'
    }
  }
})
