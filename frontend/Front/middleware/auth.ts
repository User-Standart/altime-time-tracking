export default defineNuxtRouteMiddleware((to) => {
  if (!process.client) return

  const token = localStorage.getItem('authToken')

  if (!token && to.path !== '/login') {
    return navigateTo('/login')
  }

  if (token && to.path === '/login') {
    return navigateTo('/')
  }
})
