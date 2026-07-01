import React, { useState } from 'react'
import { alertsApi } from '../services/api'
import styles from './SearchPage.module.css'

const SPECIES = ['Perro', 'Gato', 'Ave', 'Conejo', 'Otro']

export default function SearchPage() {
  const [tipo, setTipo]       = useState('all')
  const [species, setSpecies] = useState('')
  const [breed, setBreed]     = useState('')
  const [results, setResults] = useState([])
  const [searched, setSearched] = useState(false)
  const [loading, setLoading] = useState(false)

  async function handleSearch(e) {
    e.preventDefault()
    setLoading(true)
    try {
      const data = await alertsApi.search(tipo, { species, breed })
      setResults(data)
      setSearched(true)
    } finally { setLoading(false) }
  }

  return (
    <div>
      <div className={styles.pageHeader}>
        <h1 className={styles.title}>Buscar Mascotas Perdidas</h1>
        <p className={styles.sub}>Filtra las alertas activas por especie o raza.</p>
      </div>

      <div className={styles.layout}>
        <aside className={styles.sidebar}>
          <form onSubmit={handleSearch} className={styles.filterCard}>
            <h3 className={styles.filterTitle}>Filtros de búsqueda</h3>

            <div className={styles.field}>
              <label className={styles.label}>Tipo de búsqueda</label>
              <div className={styles.radioGroup}>
                {[['all','Todas las activas'],['species','Por especie'],['breed','Por raza']].map(([v,l]) => (
                  <label key={v} className={`${styles.radioLabel} ${tipo===v ? styles.radioActive : ''}`}>
                    <input type="radio" name="tipo" value={v} checked={tipo===v} onChange={() => setTipo(v)} />
                    {l}
                  </label>
                ))}
              </div>
            </div>

            {tipo === 'species' && (
              <div className={styles.field}>
                <label className={styles.label}>Especie</label>
                <select className={styles.input} value={species} onChange={e => setSpecies(e.target.value)}>
                  <option value="">Seleccionar</option>
                  {SPECIES.map(s => <option key={s}>{s}</option>)}
                </select>
              </div>
            )}

            {tipo === 'breed' && (
              <div className={styles.field}>
                <label className={styles.label}>Raza</label>
                <input className={styles.input} value={breed} onChange={e => setBreed(e.target.value)} placeholder="Ej: Labrador" />
              </div>
            )}

            <button type="submit" className={styles.btnSearch}>
              {loading ? 'Buscando...' : 'Buscar'}
            </button>
          </form>

          <div className={styles.patternNote}>
            <strong>Patrón Strategy</strong>
            <p>El algoritmo de búsqueda cambia según el tipo seleccionado sin modificar el controlador.</p>
          </div>
        </aside>

        <section className={styles.results}>
          {!searched && (
            <div className={styles.placeholder}>
              <p>Selecciona un filtro y presiona Buscar para ver resultados.</p>
            </div>
          )}
          {searched && results.length === 0 && (
            <div className={styles.empty}>No se encontraron alertas con esos criterios.</div>
          )}
          {results.map(a => (
            <div key={a.id} className={styles.resultCard}>
              <div className={styles.resultTop}>
                <span className={styles.tag}>{a.species}</span>
                {a.breed && <span className={styles.tagBreed}>{a.breed}</span>}
              </div>
              <h3 className={styles.petName}>{a.petName}</h3>
              {a.color && <p className={styles.meta}>Color: {a.color}</p>}
              <p className={styles.desc}>{a.description}</p>
              <p className={styles.location}>📍 {a.locationRef || `${a.lat?.toFixed(3)}, ${a.lng?.toFixed(3)}`}</p>
            </div>
          ))}
        </section>
      </div>
    </div>
  )
}
