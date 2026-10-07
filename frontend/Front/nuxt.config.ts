// nuxt.config.ts
export default defineNuxtConfig({
  compatibilityDate: '2025-04-26',
  devtools: { enabled: true },
  modules: ['@nuxt/ui', '@nuxt/icon'],
  colorMode: {
    preference: 'light',
  },
  components: [
    {
      path: '~/components',
      pathPrefix: false,
    },
  ],
  router: {
  },
});
