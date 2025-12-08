import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN'
import en from './en'

// 获取浏览器语言设置
const getBrowserLanguage = () => {
  const language = navigator.language || navigator.userLanguage
  // 如果是中文，返回 zh-CN，否则返回 en
  return language.startsWith('zh') ? 'zh-CN' : 'en'
}

// 从本地存储获取语言设置，如果没有则使用浏览器语言
const getStoredLanguage = () => {
  return localStorage.getItem('language') || getBrowserLanguage()
}

const i18n = createI18n({
  legacy: false, // 使用 Composition API 模式
  locale: getStoredLanguage(),
  fallbackLocale: 'en',
  messages: {
    'zh-CN': zhCN,
    en: en
  }
})

// 切换语言
export const setLanguage = (lang) => {
  i18n.global.locale.value = lang
  localStorage.setItem('language', lang)
  document.documentElement.lang = lang
}

// 获取当前语言
export const getLanguage = () => {
  return i18n.global.locale.value
}

export default i18n
