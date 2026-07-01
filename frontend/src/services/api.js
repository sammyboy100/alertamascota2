import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

export const alertsApi = {
  getAll:   ()       => api.get('/alertas').then(r => r.data),
  create:   (data)   => api.post('/alertas', data).then(r => r.data),
  resolve:  (id)     => api.patch(`/alertas/${id}/resolver`).then(r => r.data),
  search:   (tipo, params) => api.get('/alertas/buscar', { params: { tipo, ...params } }).then(r => r.data),
  sighting: (data)   => api.post('/alertas/avistamientos', data).then(r => r.data),
}

export const caregiversApi = {
  getAll:       ()        => api.get('/cuidadores').then(r => r.data),
  getAllAdmin:   ()        => api.get('/cuidadores/todos').then(r => r.data),
  register:     (data)    => api.post('/cuidadores', data).then(r => r.data),
  toggleAlerts: (id, en)  => api.patch(`/cuidadores/${id}/alertas?enabled=${en}`).then(r => r.data),
  verify:       (id)      => api.patch(`/cuidadores/${id}/verificar`).then(r => r.data),
}
