import React, { useState, useEffect } from 'react'
import { caregiversApi } from '../services/api'
import styles from './CaregiversPage.module.css'

const ROLES = { SOLIDARIO: 'Solidario', PROFESIONAL: 'Profesional', ESPECIALIZADO: 'Especializado' }
const SPECIES = ['Perro', 'Gato', 'Ave', 'Conejo', 'Otro']
const SIZES   = ['Pequeño', 'Mediano', 'Grande']

export default function CaregiversPage() {
  const [caregivers, setCaregivers] = useState([])
  const [tab, setTab] = useState('list')
  const [all, setAll] = useState([])
  const [form, setForm] = useState({ name:'', role:'SOLIDARIO', bio:'', district:'', species:[], sizes:[] })
  const [msg, setMsg] = useState('')

  useEffect(() => { load() }, [])

  async function load() {
    const [verified, todos] = await Promise.all([caregiversApi.getAll(), caregiversApi.getAllAdmin()])
    setCaregivers(verified)
    setAll(todos)
  }

  function toggleArr(arr, val) {
    return arr.includes(val) ? arr.filter(x => x !== val) : [...arr, val]
  }

  async function handleRegister(e) {
    e.preventDefault()
    try {
      await caregiversApi.register(form)
      setMsg('Solicitud enviada. Pendiente de verificación.')
      setForm({ name:'', role:'SOLIDARIO', bio:'', district:'', species:[], sizes:[] })
      load()
      setTimeout(() => setMsg(''), 3000)
    } catch { setMsg('Error al registrar.') }
  }

  async function handleToggle(id, current) {
    await caregiversApi.toggleAlerts(id, !current)
    load()
  }

  async function handleVerify(id) {
    await caregiversApi.verify(id)
    load()
  }

  return (
    <div>
      <div className={styles.pageHeader}>
        <h1 className={styles.title}>Red de Cuidadores</h1>
        <p className={styles.sub}>Cuidadores verificados disponibles para ayudar.</p>
      </div>

      <div className={styles.tabs}>
        {[['list','Directorio'],['register','Registrarme'],['admin','Administración']].map(([k,l]) => (
          <button key={k} className={`${styles.tab} ${tab===k ? styles.tabActive : ''}`} onClick={() => setTab(k)}>{l}</button>
        ))}
      </div>

      {/* ── Directorio ── */}
      {tab === 'list' && (
        <div className={styles.grid}>
          {caregivers.length === 0 && <p className={styles.empty}>No hay cuidadores verificados aún.</p>}
          {caregivers.map(c => (
            <div key={c.id} className={styles.card}>
              <div className={styles.cardHeader}>
                <div>
                  <h3 className={styles.name}>{c.name}</h3>
                  <span className={`${styles.roleBadge} ${styles['role_'+c.role]}`}>{ROLES[c.role] || c.role}</span>
                </div>
                {c.rating > 0 && (
                  <div className={styles.rating}>
                    <span className={styles.stars}>{'★'.repeat(Math.round(c.rating))}</span>
                    <span className={styles.ratingNum}>{c.rating}</span>
                  </div>
                )}
              </div>
              {c.district && <p className={styles.district}>📍 {c.district}</p>}
              {c.bio && <p className={styles.bio}>{c.bio}</p>}
              {c.species?.length > 0 && (
                <div className={styles.tags}>
                  {c.species.map(s => <span key={s} className={styles.tag}>{s}</span>)}
                </div>
              )}
              <div className={styles.toggleRow}>
                <span className={styles.toggleLabel}>Recibe alertas</span>
                <button
                  className={`${styles.toggle} ${c.alertsEnabled ? styles.toggleOn : ''}`}
                  onClick={() => handleToggle(c.id, c.alertsEnabled)}
                >
                  <span className={styles.toggleThumb} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* ── Registro ── */}
      {tab === 'register' && (
        <div className={styles.formWrap}>
          {msg && <div className={styles.msg}>{msg}</div>}
          <form onSubmit={handleRegister} className={styles.form}>
            <h3 className={styles.formTitle}>Solicitud de registro</h3>
            <div className={styles.formGrid}>
              <div className={styles.field}>
                <label className={styles.label}>Nombre completo *</label>
                <input className={styles.input} value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Rol *</label>
                <select className={styles.input} value={form.role} onChange={e => setForm({...form, role: e.target.value})}>
                  {Object.entries(ROLES).map(([v,l]) => <option key={v} value={v}>{l}</option>)}
                </select>
              </div>
              <div className={styles.field}>
                <label className={styles.label}>Distrito</label>
                <input className={styles.input} value={form.district} onChange={e => setForm({...form, district: e.target.value})} placeholder="Ej: Miraflores" />
              </div>
              <div className={`${styles.field} ${styles.fullWidth}`}>
                <label className={styles.label}>Sobre ti</label>
                <textarea className={`${styles.input} ${styles.textarea}`} value={form.bio} onChange={e => setForm({...form, bio: e.target.value})} placeholder="Describe tu experiencia con mascotas..." />
              </div>
              <div className={`${styles.field} ${styles.fullWidth}`}>
                <label className={styles.label}>Especies que atiendes</label>
                <div className={styles.chips}>
                  {SPECIES.map(s => (
                    <button type="button" key={s}
                      className={`${styles.chip} ${form.species.includes(s) ? styles.chipActive : ''}`}
                      onClick={() => setForm({...form, species: toggleArr(form.species, s)})}>
                      {s}
                    </button>
                  ))}
                </div>
              </div>
              <div className={`${styles.field} ${styles.fullWidth}`}>
                <label className={styles.label}>Tamaños aceptados</label>
                <div className={styles.chips}>
                  {SIZES.map(s => (
                    <button type="button" key={s}
                      className={`${styles.chip} ${form.sizes.includes(s) ? styles.chipActive : ''}`}
                      onClick={() => setForm({...form, sizes: toggleArr(form.sizes, s)})}>
                      {s}
                    </button>
                  ))}
                </div>
              </div>
            </div>
            <div className={styles.infoBox}>
              Tu perfil quedará pendiente hasta que un administrador valide tu identidad.
            </div>
            <button type="submit" className={styles.btnPrimary}>Enviar solicitud</button>
          </form>
        </div>
      )}

      {/* ── Admin ── */}
      {tab === 'admin' && (
        <div className={styles.adminWrap}>
          <h3 className={styles.formTitle}>Panel de verificación</h3>
          <p className={styles.adminSub}>Verifica la identidad de los cuidadores para publicar sus perfiles.</p>
          <div className={styles.adminList}>
            {all.map(c => (
              <div key={c.id} className={styles.adminRow}>
                <div>
                  <strong>{c.name}</strong>
                  <span className={`${styles.roleBadge} ${styles['role_'+c.role]}`}>{ROLES[c.role] || c.role}</span>
                  <span className={c.verified ? styles.verifiedBadge : styles.pendingBadge}>
                    {c.verified ? 'Verificado' : 'Pendiente'}
                  </span>
                </div>
                {!c.verified && (
                  <button className={styles.btnVerify} onClick={() => handleVerify(c.id)}>Verificar</button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
