import Vue from 'vue'
import Router from 'vue-router'
import Processo from '@/components/Processo'
import Mesa from '@/components/Mesa'
import Login from '@/components/Login'
import Sugestoes from '@/components/Sugestoes'
import Sobre from '@/components/Sobre'
import ProcessoBL from '../bl/processo.js'

Vue.use(Router)

export default new Router({
  routes: [{
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/processo/:numero',
    name: 'Processo',
    component: Processo,
    meta: {
      title: route => {
        return (
          'Processo ' +
          ProcessoBL.formatarProcesso(
            ProcessoBL.somenteNumeros(route.params.numero)
          ) +
          '..'
        )
      }
    }
  },
  {
    path: '/mesa',
    name: 'Mesa',
    component: Mesa
  },
  {
    path: '/sugestoes',
    name: 'Sugestões',
    component: Sugestoes
  },
  {
    path: '/sobre',
    name: 'Sobre',
    component: Sobre
  }]
})
