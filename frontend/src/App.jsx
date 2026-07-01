import React from 'react'
import { Routes, Route, NavLink } from 'react-router-dom'
import AlertsPage from './pages/AlertsPage'
import SearchPage from './pages/SearchPage'
import CaregiversPage from './pages/CaregiversPage'
import styles from './App.module.css'

export default function App() {
  return (
    <div className={styles.app}>
      <header className={styles.header}>
        <div className={styles.headerInner}>
          <div className={styles.brand}>
            <span className={styles.brandIcon}>🐾</span>
            <div>
              <span className={styles.brandName}>AlertaMascota</span>
              <span className={styles.brandSub}>Mascotas perdidas · Búsqueda · Cuidadores</span>
            </div>
          </div>
          <nav className={styles.nav}>
            <NavLink to="/"          className={({isActive}) => isActive ? `${styles.navLink} ${styles.active}` : styles.navLink} end>Alertas</NavLink>
            <NavLink to="/buscar"    className={({isActive}) => isActive ? `${styles.navLink} ${styles.active}` : styles.navLink}>Buscar</NavLink>
            <NavLink to="/cuidadores"className={({isActive}) => isActive ? `${styles.navLink} ${styles.active}` : styles.navLink}>Cuidadores</NavLink>
          </nav>
        </div>
      </header>

      <main className={styles.main}>
        <Routes>
          <Route path="/"           element={<AlertsPage />} />
          <Route path="/buscar"     element={<SearchPage />} />
          <Route path="/cuidadores" element={<CaregiversPage />} />
        </Routes>
      </main>

      <footer className={styles.footer}>
        <div className={styles.footerInner}>
          <span>Patrones aplicados: <b>Observer · Strategy · Chain of Responsibility · Builder · Facade</b></span>
          <span>Spring Boot + React + MongoDB</span>
        </div>
      </footer>
    </div>
  )
}
