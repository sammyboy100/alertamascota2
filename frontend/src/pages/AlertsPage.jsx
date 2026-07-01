import React, { useState, useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from 'react-leaflet'
import L from 'leaflet'
import { alertsApi } from '../services/api'
import styles from './AlertsPage.module.css'

// Fix leaflet marker icons
delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

function MapClickHandler({ onSelect }) {
  useMapEvents({ click: e => onSelect(e.latlng) })
  return null
}

const SPECIES = ['Perro', 'Gato', 'Ave', 'Conejo', 'Otro']
const STATUS_LABEL = { ACTIVE: 'Activa', SIGHTED: 'Avistada', RESOLVED: 'Resuelta' }
const STATUS_COLOR = { ACTIVE: '#E03131', SIGHTED: '#F08C00', RESOLVED: '#2F9E44' }

export default function AlertsPage() {
  const [alerts, setAlerts]   = useState([])
  const [tab, setTab]         = useState('list')
  const [loading, setLoading] = useState(false)
  const [msg, setMsg]         = useState('')
  const [coords, setCoords]   = useState(null)
  const [form, setForm]       = useState({ petName:'', species:'', breed:'', description:'', color:'', locationRef:'', ownerName:'', ownerPhone:'' })

  useEffect(() => { loadAlerts() }, [])

  async function loadAlerts() {
    setLoading(true)
    try { setAlerts(await alertsApi.getAll()) }
    finally { setLoading(false) }
  }

  async function handleSubmit(e) {
    e.preventDefault()
    if (!coords) return setMsg('Selecciona una ubicación en el mapa.')
    try {
      await alertsApi.create({ ...form, lat: coords.lat, lng: coords.lng })
      setMsg('Alerta publicada correctamente.')
      setForm({ petName:'', species:'', breed:'', description:'', color:'', locationRef:'', ownerName:'', ownerPhone:'' })
      setCoords(null)
      loadAlerts()
      setTimeout(() => { setMsg(''); setTab('list') }, 1500)
    } catch (err) {
      setMsg(err.response?.data?.message || 'Error al publicar la alerta.')
    }
  }

  return (
    <div>
      <div className={styles.pageHeader}>
        <h1 className={styles.title}>Alertas de Mascotas Perdidas</h1>
        <p className={styles.sub}>Registra una mascota perdida o consulta las alertas activas.</p>
      </div>

      <div className={styles.tabs}>
        {[['list','Ver Alertas'],['form','Reportar Pérdida'],['map','Mapa General']].map(([k,l]) => (
          <button key={k} className={`${styles.tab} ${tab===k ? styles.tabActive : ''}`} onClick={() => setTab(k)}>{l}</button>
        ))}
      </div>

      {/* ── Lista de alertas ── */}
      {tab === 'list' && (
        <div>
          {loading && <p className={styles.loading}>Cargando...</p>}
          {!loading && alerts.length === 0 && <p className={styles.empty}>No hay alertas activas.</p>}
          <div className={styles.grid}>
            {alerts.map(a => (
              <div key={a.id} className={styles.card}>
                <div className={styles.cardTop}>
                  <span className={styles.statusDot} style={{background: STATUS_COLOR[a.status]}} />
                  <span className={styles.statusLabel}>{STATUS_LABEL[a.status]}</span>
                  <span className={styles.species}>{a.species}</span>
                </div>
                <h3 className={styles.petName}>{a.petName}</h3>
                <p className={styles.breed}>{a.breed} {a.color && `· ${a.color}`}</p>
                <p className={styles.desc}>{a.description}</p>
                <div className={styles.cardMeta}>
                  <span>📍 {a.locationRef || `${a.lat?.toFixed(3)}, ${a.lng?.toFixed(3)}`}</span>
                </div>
                {a.status === 'ACTIVE' && (
                  <button className={styles.btnResolve} onClick={async () => { await alertsApi.resolve(a.id); loadAlerts() }}>
                    Marcar como encontrado
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ── Formulario ── */}
      {tab === 'form' && (
        <div className={styles.formWrap}>
          {msg && <div className={styles.msg}>{msg}</div>}
          <form onSubmit={handleSubmit} className={styles.form}>
            <div className={styles.formGrid}>
              <div className={styles.field}>
                <label className={styles.label}>Nombre *</label>
                <input className={styles.input} value={form.petName} onChange={e => setForm({...form, petName: e.target.value})} placeholder="Ej: Luna" required />
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Especie *</label>
                <select className={styles.input} value={form.species} onChange={e => setForm({...form, species: e.target.value})} required>
                  <option value="">Seleccionar</option>
                  {SPECIES.map(s => <option key={s}>{s}</option>)}
                </select>
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Raza</label>
                <input className={styles.input} value={form.breed} onChange={e => setForm({...form, breed: e.target.value})} placeholder="Ej: Labrador" />
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Color</label>
                <input className={styles.input} value={form.color} onChange={e => setForm({...form, color: e.target.value})} placeholder="Ej: Dorado" />
              </div>
              <div className={`${styles.field} ${styles.fullWidth}`}>
                <label className={styles.label}>Descripción</label>
                <textarea className={`${styles.input} ${styles.textarea}`} value={form.description} onChange={e => setForm({...form, description: e.target.value})} placeholder="Señas particulares, collar, etc." />
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Referencia del lugar</label>
                <input className={styles.input} value={form.locationRef} onChange={e => setForm({...form, locationRef: e.target.value})} placeholder="Ej: Parque Kennedy, Miraflores" />
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Tu nombre</label>
                <input className={styles.input} value={form.ownerName} onChange={e => setForm({...form, ownerName: e.target.value})} placeholder="Nombre de contacto" />
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Teléfono *</label>
                <input className={styles.input} value={form.ownerPhone} onChange={e => setForm({...form, ownerPhone: e.target.value})} placeholder="999 000 000" required />
              </div>
            </div>

            <div className={styles.mapSection}>
              <label className={styles.label}>Ubicación en el mapa * <span className={styles.hint}>(haz clic para marcar)</span></label>
              {coords && <p className={styles.coordsOk}>Seleccionado: {coords.lat.toFixed(4)}, {coords.lng.toFixed(4)}</p>}
              <div className={styles.mapWrap}>
                <MapContainer center={[-12.046, -77.043]} zoom={13} style={{height:'100%', width:'100%'}}>
                  <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" attribution="© OpenStreetMap" />
                  <MapClickHandler onSelect={setCoords} />
                  {coords && <Marker position={coords}><Popup>Ubicación seleccionada</Popup></Marker>}
                </MapContainer>
              </div>
            </div>

            <button type="submit" className={styles.btnPrimary}>Publicar Alerta</button>
          </form>
        </div>
      )}

      {/* ── Mapa general ── */}
      {tab === 'map' && (
        <div className={styles.mapFullWrap}>
          <MapContainer center={[-12.046, -77.043]} zoom={13} style={{height:'520px', width:'100%', borderRadius:'12px'}}>
            <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" attribution="© OpenStreetMap" />
            {alerts.filter(a => a.lat && a.lng).map(a => (
              <Marker key={a.id} position={[a.lat, a.lng]}>
                <Popup>
                  <strong>{a.petName}</strong><br/>
                  {a.species} · {a.breed}<br/>
                  {a.description}<br/>
                  <span style={{color: STATUS_COLOR[a.status]}}>{STATUS_LABEL[a.status]}</span>
                </Popup>
              </Marker>
            ))}
          </MapContainer>
        </div>
      )}
    </div>
  )
}
