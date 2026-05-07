import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import './index.css'
import App from './App.jsx'
import Compare from './Compare.jsx'
import Layout from './components/layout/Layout.jsx'
import Trending from './pages/Trending.jsx'
import BudgetPicks from './pages/BudgetPicks.jsx'
import History from './pages/History.jsx'
import About from './pages/About.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<App />} />
          <Route path="/compare" element={<Compare />} />
          <Route path="/trending" element={<Trending />} />
          <Route path="/budget" element={<BudgetPicks />} />
          <Route path="/history" element={<History />} />
          <Route path="/about" element={<About />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)
