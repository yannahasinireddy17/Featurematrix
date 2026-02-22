import { useCallback, useEffect, useState } from 'react'
import './App.css'

const API_BASE = 'http://localhost:8081/api'
const THEME_KEY = 'productcompare_theme'
const TOKEN_KEY = 'productcompare_token'
const USER_KEY = 'productcompare_user'

const createLandingProduct = () => ({
  productName: '',
  category: 'electronic',
  price: '',
  purchaseLink: ''
})

const normalizeUrl = (value) => {
  const raw = (value ?? '').trim()
  if (!raw || raw === '-') return ''
  if (raw.startsWith('http://') || raw.startsWith('https://')) return raw
  if (raw.startsWith('www.')) return `https://${raw}`
  return ''
}

function App() {
  const [theme, setTheme] = useState(localStorage.getItem(THEME_KEY) ?? 'light')

  const [token, setToken] = useState(localStorage.getItem(TOKEN_KEY) ?? '')
  const [username, setUsername] = useState(localStorage.getItem(USER_KEY) ?? '')
  const [authMode, setAuthMode] = useState('login')
  const [authUsername, setAuthUsername] = useState('')
  const [authPassword, setAuthPassword] = useState('')

  const [features, setFeatures] = useState([])
  const [isLoading, setIsLoading] = useState(false)
  const [message, setMessage] = useState('')

  const [landingProducts, setLandingProducts] = useState([
    createLandingProduct(),
    createLandingProduct()
  ])

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme)
    localStorage.setItem(THEME_KEY, theme)
  }, [theme])

  const apiCall = useCallback(async (path, options = {}, authToken) => {
    const effectiveToken = authToken ?? token
    const response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers: {
        ...(options.body !== undefined ? { 'Content-Type': 'application/json' } : {}),
        ...(effectiveToken ? { 'X-Auth-Token': effectiveToken } : {}),
        ...(options.headers || {})
      }
    })

    if (!response.ok) {
      const text = await response.text()
      throw new Error(text || 'Request failed')
    }

    if (response.status === 204) return null
    return response.json()
  }, [token])

  const refreshComparison = useCallback(async (authToken) => {
    const effectiveToken = authToken ?? token
    if (!effectiveToken) {
      setFeatures([])
      return
    }

    setIsLoading(true)
    try {
      const data = await apiCall('/comparison', {}, effectiveToken)
      setFeatures(data.features ?? [])
    } catch (error) {
      setMessage(error.message)
    } finally {
      setIsLoading(false)
    }
  }, [apiCall, token])

  useEffect(() => {
    if (!token) {
      setFeatures([])
      return
    }

    ;(async () => {
      try {
        const me = await apiCall('/auth/me', {}, token)
        setUsername(me.username)
        localStorage.setItem(USER_KEY, me.username)
        await refreshComparison(token)
      } catch {
        localStorage.removeItem(TOKEN_KEY)
        localStorage.removeItem(USER_KEY)
        setToken('')
        setUsername('')
      }
    })()
  }, [apiCall, refreshComparison, token])

  const onAuthSubmit = async (event) => {
    event.preventDefault()
    if (!authUsername.trim() || !authPassword.trim()) {
      setMessage('Username and password are required')
      return
    }

    try {
      const endpoint = authMode === 'register' ? '/auth/register' : '/auth/login'
      const data = await apiCall(
        endpoint,
        {
          method: 'POST',
          body: JSON.stringify({
            username: authUsername.trim(),
            password: authPassword.trim()
          })
        },
        ''
      )

      setToken(data.token)
      setUsername(data.username)
      localStorage.setItem(TOKEN_KEY, data.token)
      localStorage.setItem(USER_KEY, data.username)
      setAuthPassword('')
      setMessage(authMode === 'register' ? 'Account created successfully' : 'Login successful')
      await refreshComparison(data.token)
    } catch (error) {
      setMessage(error.message)
    }
  }

  const onLogout = async () => {
    try {
      if (token) {
        await apiCall('/auth/logout', { method: 'POST' }, token)
      }
    } catch {
      // ignore
    } finally {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
      setToken('')
      setUsername('')
      setMessage('Logged out')
    }
  }

  const updateLandingProduct = (index, field, value) => {
    setLandingProducts((current) =>
      current.map((item, itemIndex) => (itemIndex === index ? { ...item, [field]: value } : item))
    )
  }

  const ensureFeature = async (featureName, featureCache) => {
    const key = featureName.trim().toLowerCase()
    if (!key) return null

    if (featureCache.has(key)) return featureCache.get(key)

    try {
      const created = await apiCall('/features', {
        method: 'POST',
        body: JSON.stringify({ name: featureName.trim() })
      })
      featureCache.set(key, created.id)
      return created.id
    } catch {
      const latest = await apiCall('/features')
      const found = (latest ?? []).find((item) => item.name.trim().toLowerCase() === key)
      if (!found) throw new Error(`Unable to save feature: ${featureName}`)
      featureCache.set(key, found.id)
      return found.id
    }
  }

  const onShowComparison = async () => {
    if (landingProducts.some((product) => !product.productName.trim())) {
      setMessage('Product name is required in both forms')
      return
    }

    try {
      const featureCache = new Map(features.map((feature) => [feature.name.trim().toLowerCase(), feature.id]))
      const productIds = []

      for (const productData of landingProducts) {
        const numericPrice = (productData.price ?? '').toString().trim()
        const created = await apiCall('/products', {
          method: 'POST',
          body: JSON.stringify({
            name: productData.productName.trim(),
            category: productData.category,
            imageUrl: null,
            price: numericPrice ? Number(numericPrice) : null
          })
        })

        productIds.push(String(created.id))

        if (productData.purchaseLink.trim()) {
          const url = normalizeUrl(productData.purchaseLink)
          if (!url) {
            throw new Error(`Invalid purchase link: ${productData.productName}`)
          }

          const purchaseLinkFeatureId = await ensureFeature('Purchase Link', featureCache)
          await apiCall(`/products/${created.id}/features/${purchaseLinkFeatureId}/value`, {
            method: 'PUT',
            body: JSON.stringify({ value: url })
          })
        }

      }

      await refreshComparison()
      setMessage('Products submitted. Showing comparison now.')
      window.location.assign(`/compare?ids=${productIds.join(',')}`)
    } catch (error) {
      setMessage(error.message)
    }
  }

  return (
    <main className="app-shell">
      <header className="app-header">
        <div>
          <h1>Personal Product Comparison</h1>
          <p>
            {token
              ? `Welcome ${username}. Add two products and compare all features side by side.`
              : 'Create an account or sign in to access your private comparison workspace.'}
          </p>
        </div>
        <div className="header-actions">
          <button type="button" className="ghost" onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}>
            {theme === 'light' ? 'Dark Mode' : 'Light Mode'}
          </button>
          {token && (
            <button type="button" className="ghost" onClick={onLogout}>
              Logout
            </button>
          )}
        </div>
      </header>

      {!token ? (
        <section className="auth-wrap">
          <form className="panel card auth-panel" onSubmit={onAuthSubmit}>
            <h2>{authMode === 'register' ? 'Create Account' : 'Login'}</h2>
            <p className="panel-subtitle">Your dashboard is private to your account.</p>
            <input
              value={authUsername}
              onChange={(event) => setAuthUsername(event.target.value)}
              placeholder="Username"
              autoComplete="username"
            />
            <input
              value={authPassword}
              onChange={(event) => setAuthPassword(event.target.value)}
              placeholder="Password"
              type="password"
              autoComplete={authMode === 'register' ? 'new-password' : 'current-password'}
            />
            <button type="submit" className="btn-primary">
              {authMode === 'register' ? 'Register and Continue' : 'Login'}
            </button>
            <button
              type="button"
              className="ghost"
              onClick={() => setAuthMode(authMode === 'register' ? 'login' : 'register')}
            >
              {authMode === 'register' ? 'Already have an account? Login' : 'No account? Register'}
            </button>
          </form>
        </section>
      ) : (
        <>
          {isLoading && (
            <section className="panel card">
              <p className="muted">Loading your workspace...</p>
            </section>
          )}

          <section className="panel card">
            <h2>Add Two Products</h2>
            <p className="panel-subtitle">Enter core fields only. Full specifications are loaded from system demo data.</p>

              <div className="home-forms-grid">
                {landingProducts.map((productData, productIndex) => (
                  <div key={`product-form-${productIndex}`} className="home-product-form card">
                    <h3>Product {productIndex + 1}</h3>
                    <input
                      value={productData.productName}
                      onChange={(event) => updateLandingProduct(productIndex, 'productName', event.target.value)}
                      placeholder="Product name"
                    />
                    <select
                      value={productData.category}
                      onChange={(event) => updateLandingProduct(productIndex, 'category', event.target.value)}
                    >
                      <option value="electronic">Electronic</option>
                      <option value="non-electronic">Non-electronic</option>
                    </select>
                    <input
                      value={productData.price}
                      onChange={(event) => updateLandingProduct(productIndex, 'price', event.target.value)}
                      placeholder="Price (₹)"
                      type="number"
                      step="0.01"
                    />
                    <input
                      value={productData.purchaseLink}
                      onChange={(event) => updateLandingProduct(productIndex, 'purchaseLink', event.target.value)}
                      placeholder="Purchase link (URL)"
                      className="input-url"
                    />
                  </div>
                ))}
              </div>

            <button type="button" className="show-comparison-btn btn-primary" onClick={onShowComparison}>
              Show Comparison
            </button>
          </section>
        </>
      )}

      {message && <p className="status-text">{message}</p>}

      <footer className="app-footer">ProductCompare • Built for clear side-by-side product decisions</footer>
    </main>
  )
}

export default App
