// 创建axios实例
const api = axios.create({
  baseURL: '/',
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 排除登录和注册请求
    if (config.url == '/auth/login' || config.url == '/auth/register')
      return config
    const token = localStorage.getItem('token')
    if (token)
      config.headers.Authorization = `Bearer ${token}`
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器: 处理业务逻辑错误和HTTP状态码错误，只有业务成功才返回数据部分，否则都是错误
api.interceptors.response.use(
  response => {
    // 检查业务是否成功
    if (response.data && response.data.code != 0) {
      // 业务失败，显示错误信息
      window.ElementPlus.ElMessage.error(response.data.message || '请求失败')
      return Promise.reject(response.data)
    }
    // 业务成功，直接返回数据部分，方便调用者使用
    return response.data.data
  },
  error => {
    if (error.response) {
      // 处理HTTP状态码错误
      if (error.response.status === 401) {
        window.ElementPlus.ElMessage.error('未登录，请先登录')
        localStorage.removeItem('token')
        window.location.href = 'login.html'
      } else {
        // 其他HTTP错误，使用后端返回的错误信息或者默认提示
        window.ElementPlus.ElMessage.error(error.response.data.message || '请求失败')
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      window.ElementPlus.ElMessage.error('网络请求失败，请检查网络连接')
    } else {
      // 请求配置错误
      window.ElementPlus.ElMessage.error('请求配置错误')
    }
    return Promise.reject(error)
  }
)

// 封装所有API请求
// 认证相关
// 登录
export const login = (data) => {
  return api.post('/auth/login', data)
}

// 注册
export const register = (data) => {
  return api.post('/auth/register', data)
}

// 旅行计划相关
// 获取所有旅行计划
export const getTravelPlans = () => {
  return api.get('/api/travel-plans')
}

// 创建旅行计划
export const createTravelPlan = (data) => {
  return api.post('/api/travel-plans', data)
}

// 更新旅行计划
export const updateTravelPlan = (data) => {
  return api.put('/api/travel-plans', data)
}

// 删除旅行计划
export const deleteTravelPlan = (planId) => {
  return api.delete(`/api/travel-plans/${planId}`)
}

// 获取单个旅行计划
// 用于旅行计划详情页面
// @param {number} planId - 旅行计划ID
// @returns {Promise} - axios请求的Promise
export const getTravelPlanById = (planId) => {
  return api.get(`/api/travel-plans/${planId}`)
}

// 带token的页面重定向
// 用于验证token后跳转页面
// @param {string} url - 要跳转的URL
export const redirectWithToken = (url) => {
  const token = localStorage.getItem('token')
  if (!token) {
    window.ElementPlus.ElMessage.error('未登录，请先登录')
    window.location.href = 'login.html'
    return
  }

  const xhr = new XMLHttpRequest()
  xhr.open('GET', url, true)
  xhr.setRequestHeader('Authorization', 'Bearer ' + token)
  xhr.onload = () => {
    if (xhr.status === 200) {
      window.location.href = url
    } else if (xhr.status === 401) {
      window.ElementPlus.ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      window.location.href = 'login.html'
    } else {
      window.ElementPlus.ElMessage.error('授权失败，请重新登录')
      localStorage.removeItem('token')
      window.location.href = 'login.html'
    }
  }
  xhr.onerror = () => {
    window.ElementPlus.ElMessage.error('网络错误，请稍后重试')
  }
  xhr.send()
}
