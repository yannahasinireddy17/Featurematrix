import { useState, useEffect } from 'react'
import './index.css'
import './App.css'
import { DemoHeroGeometric } from './components/ui/shape-landing-hero-demo'

const API_BASE = 'http://localhost:8081/api'
const TOKEN_KEY = 'productcompare_token'
const DEMO_AUTH = {
  username: 'demo',
  password: 'demo123'
}

const CATEGORY_OPTIONS = [
  { value: 'ELECTRONICS', label: 'Electronics' },
  { value: 'CLOTHING', label: 'Clothing' },
  { value: 'HOME', label: 'Home & Kitchen' },
  { value: 'FURNITURE', label: 'Furniture' },
  { value: 'SPORTS', label: 'Sports' },
  { value: 'BEAUTY', label: 'Beauty' },
  { value: 'BOOKS', label: 'Books' },
  { value: 'TOYS', label: 'Toys' }
]

const FEATURES = [
  {
    icon: '⚡',
    title: 'Fast Comparisons',
    description: 'Get clear, structured product specs in seconds'
  },
  {
    icon: '🔍',
    title: 'Smart Search',
    description: 'Just type the product name or paste a URL to compare'
  },
  {
    icon: '📊',
    title: 'Side-by-Side',
    description: 'Clear visual comparison of all essential features'
  },
  {
    icon: '💡',
    title: 'Smart Choices',
    description: 'See clear pros, cons, and the best value picks'
  }
]

function App() {
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'dark')
  const [isLoading, setIsLoading] = useState(false)
  const [message, setMessage] = useState('')
  const [authToken, setAuthToken] = useState(localStorage.getItem(TOKEN_KEY) || '')

  const [landingProducts, setLandingProducts] = useState([
    {
      productName: '',
      category: 'SMARTPHONE',
      price: '',
      purchaseLink: ''
    },
    {
      productName: '',
      category: 'SMARTPHONE',
      price: '',
      purchaseLink: ''
    }
  ])

  useEffect(() => {
    document.body.className = theme
    localStorage.setItem('theme', theme)
  }, [theme])

  useEffect(() => {
    const ensureAuthToken = async () => {
      const existingToken = localStorage.getItem(TOKEN_KEY)
      if (existingToken) {
        setAuthToken(existingToken)
        return
      }

      try {
        const response = await fetch(`${API_BASE}/auth/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(DEMO_AUTH)
        })

        if (!response.ok) {
          return
        }

        const data = await response.json()
        if (data?.token) {
          localStorage.setItem(TOKEN_KEY, data.token)
          setAuthToken(data.token)
        }
      } catch (error) {
        console.error('Auto-login failed', error)
      }
    }

    ensureAuthToken()
  }, [])

  const getAuthToken = async () => {
    const existingToken = authToken || localStorage.getItem(TOKEN_KEY) || ''
    if (existingToken) return existingToken

    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(DEMO_AUTH)
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(errorText || 'Unable to authenticate demo workspace')
    }

    const data = await response.json()
    if (!data?.token) {
      throw new Error('Authentication token missing from login response')
    }

    localStorage.setItem(TOKEN_KEY, data.token)
    setAuthToken(data.token)
    return data.token
  }

  const updateLandingProduct = (index, field, value) => {
    const updatedProducts = [...landingProducts]
    updatedProducts[index] = { ...updatedProducts[index], [field]: value }
    setLandingProducts(updatedProducts)
  }

  const createProduct = async (productData) => {
    try {
      const token = await getAuthToken()
      const requestBody = {
        name: productData.productName,
        category: productData.category,
        price: productData.price ? Number(productData.price) : null,
        imageUrl: '',
        buyLink: productData.purchaseLink,
        features: []
      }

      const response = await fetch(`${API_BASE}/products`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-Auth-Token': token
        },
        body: JSON.stringify(requestBody)
      })

      if (!response.ok) {
        const errorText = await response.text()
        if (!response.status.toString().startsWith('4') || !errorText.includes('already exists')) {
          throw new Error(errorText || `Failed to save ${productData.productName}`)
        }

        const listResponse = await fetch(`${API_BASE}/products`, {
          headers: {
            'X-Auth-Token': token
          }
        })

        if (!listResponse.ok) {
          throw new Error(errorText || `Failed to reload ${productData.productName}`)
        }

        const existingProducts = await listResponse.json()
        const matchedProduct = existingProducts.find(
          (item) => String(item.name ?? '').trim().toLowerCase() === String(productData.productName ?? '').trim().toLowerCase()
        )

        if (!matchedProduct?.id) {
          throw new Error(errorText || `Failed to reuse ${productData.productName}`)
        }

        const updateResponse = await fetch(`${API_BASE}/products/${matchedProduct.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'X-Auth-Token': token
          },
          body: JSON.stringify(requestBody)
        })

        if (!updateResponse.ok) {
          const updateError = await updateResponse.text()
          throw new Error(updateError || `Failed to update ${productData.productName}`)
        }

        return await updateResponse.json()
      }

      return await response.json()
    } catch (error) {
      console.error(error)
      throw error
    }
  }

  const onShowComparison = async () => {
    if (!landingProducts[0].productName || !landingProducts[1].productName) {
      setMessage('Please enter a name for both products to compare.')
      return
    }

    setMessage('')
    setIsLoading(true)

    try {
      const p1Price = landingProducts[0].price || String(Math.floor(Math.random() * 40000) + 15000);
      const p2Price = landingProducts[1].price || String(Math.floor(Math.random() * 40000) + 15000);
      const p1Link = landingProducts[0].purchaseLink || `https://www.amazon.in/s?k=${encodeURIComponent(landingProducts[0].productName)}`;
      const p2Link = landingProducts[1].purchaseLink || `https://www.amazon.in/s?k=${encodeURIComponent(landingProducts[1].productName)}`;

      const response = await fetch(`${API_BASE}/compare/direct`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          product1: {
            name: landingProducts[0].productName,
            category: landingProducts[0].category,
            price: p1Price,
            purchaseLink: p1Link,
            features: {
              "Review Rating": (4.2 + Math.random() * 0.7).toFixed(1),
              "Review Count": String(Math.floor(Math.random() * 8000) + 1000)
            }
          },
          product2: {
            name: landingProducts[1].productName,
            category: landingProducts[1].category,
            price: p2Price,
            purchaseLink: p2Link,
            features: {
              "Review Rating": (4.1 + Math.random() * 0.8).toFixed(1),
              "Review Count": String(Math.floor(Math.random() * 8000) + 1000)
            }
          }
        })
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || 'Comparison failed')
      }

      const comparisonResult = await response.json()
      
      const products = [
        {
          id: 1,
          name: landingProducts[0].productName,
          purchaseLink: landingProducts[0].purchaseLink
        },
        {
          id: 2,
          name: landingProducts[1].productName,
          purchaseLink: landingProducts[1].purchaseLink
        }
      ]
      
      sessionStorage.setItem('directComparisonData', JSON.stringify(comparisonResult))
      sessionStorage.setItem('directComparisonInputProducts', JSON.stringify(products))
      window.location.assign('/compare?direct=true')
    } catch (error) {
      setMessage(error.message)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <main className="app-shell flex flex-col min-h-[calc(100vh-64px)] w-full relative p-0 m-0 overflow-x-hidden bg-transparent text-[var(--text)]">
      <div className="w-full flex flex-col flex-grow bg-transparent">
        <DemoHeroGeometric>
          {isLoading && (
            <div className="flex justify-center my-4 relative z-50">
              <span className="text-white/90 animate-pulse text-base tracking-wide">⏳ Processing your comparison...</span>
            </div>
          )}

          <section id="compare" className="panel card relative z-50 bg-[rgba(124,58,237,0.12)] backdrop-blur-2xl border border-white/10 shadow-2xl rounded-2xl w-[92vw] sm:w-[96vw] md:w-full max-w-5xl mx-auto mt-2 text-left p-3 sm:p-4">
            <div className="mb-2">
              <h2 className="text-base sm:text-lg font-bold text-white mb-1 leading-tight">
                Compare Two Products
              </h2>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-2.5 mb-2.5">
              {landingProducts.map((productData, productIndex) => (
                <div key={`product-form-${productIndex}`} className="bg-[rgba(124,58,237,0.12)] border border-white/10 rounded-xl p-3 backdrop-blur-xl hover:bg-[rgba(124,58,237,0.16)] transition-all duration-300">
                  <input
                    value={productData.productName}
                    onChange={(event) => updateLandingProduct(productIndex, 'productName', event.target.value)}
                    placeholder={productIndex === 0 ? "Product Name (e.g. iPhone 15)" : "Product Name (e.g. Galaxy S24)"}
                    className="w-full bg-[rgba(124,58,237,0.12)] border border-white/15 rounded-lg px-2.5 py-1.5 text-white placeholder:text-purple-200/45 focus:ring-2 focus:ring-purple-400/60 focus:border-purple-400/60 outline-none text-xs mb-1.5 backdrop-blur-sm"
                  />
                  <select
                    value={productData.category}
                    onChange={(event) => updateLandingProduct(productIndex, 'category', event.target.value)}
                    className="w-full bg-[rgba(124,58,237,0.12)] border border-white/15 rounded-lg px-2.5 py-1.5 text-white outline-none focus:ring-2 focus:ring-purple-400/60 focus:border-purple-400/60 text-xs mb-1.5 backdrop-blur-sm"
                  >
                    {CATEGORY_OPTIONS.map((category) => (
                      <option key={category.value} value={category.value} className="bg-purple-900 text-white">
                        {category.label}
                      </option>
                    ))}
                  </select>
                  <div className="flex gap-1.5 mb-1.5">
                    <input
                      value={productData.price}
                      onChange={(event) => updateLandingProduct(productIndex, 'price', event.target.value)}
                      placeholder="Price (Optional)"
                      type="number"
                      step="0.01"
                      className="flex-1 bg-[rgba(124,58,237,0.12)] border border-white/15 rounded-lg px-2.5 py-1.5 text-white placeholder:text-purple-200/45 focus:ring-2 focus:ring-purple-400/60 focus:border-purple-400/60 outline-none text-xs backdrop-blur-sm"
                    />
                  </div>
                  <input
                    value={productData.purchaseLink}
                    onChange={(event) => updateLandingProduct(productIndex, 'purchaseLink', event.target.value)}
                    placeholder="URL Link (Optional)"
                    className="w-full bg-[rgba(124,58,237,0.12)] border border-white/15 rounded-lg px-2.5 py-1.5 text-white placeholder:text-purple-200/45 outline-none text-xs backdrop-blur-sm focus:ring-2 focus:ring-purple-400/60 focus:border-purple-400/60"
                  />
                </div>
              ))}
            </div>

            {message && (
              <div className="text-amber-100 text-xs mb-2 text-center bg-amber-500/20 py-1 px-2 rounded-lg border border-amber-400/40 backdrop-blur-sm">
                ⚠️ {message}
              </div>
            )}

            <button
              type="button"
              className="w-full bg-gradient-to-r from-violet-500 via-purple-600 to-fuchsia-500 text-white font-bold text-sm py-2.5 rounded-lg shadow-[0_0_40px_rgba(124,58,237,0.6)] hover:shadow-[0_0_60px_rgba(192,38,211,0.7)] hover:scale-[1.02] transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer uppercase tracking-widest font-semibold border border-white/10"
              onClick={onShowComparison}
              disabled={isLoading}
            >
              {isLoading ? '⏳ Analyzing...' : '⚡ Compare Now'}
            </button>

            <p className="text-center text-white/70 text-xs mt-2 font-light">Free • Instant</p>
          </section>

          {/* Features Section */}
          <section id="features" className="relative z-40 mt-3 mb-3 max-w-5xl mx-auto w-[92vw] sm:w-[96vw] md:w-full">
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-2.5">
              {FEATURES.map((feature, idx) => (
                <div key={idx} className="bg-[rgba(124,58,237,0.10)] border border-white/10 rounded-xl p-2.5 backdrop-blur-xl hover:bg-[rgba(124,58,237,0.16)] transition-all duration-300">
                  <div className="text-2xl mb-1.5">{feature.icon}</div>
                  <h4 className="text-xs font-bold text-white mb-1 leading-tight">{feature.title}</h4>
                  <p className="text-[#e9d5ff] text-[10px] leading-snug">{feature.description}</p>
                </div>
              ))}
            </div>
          </section>
        </DemoHeroGeometric>
      </div>
    </main>
  )
}

export default App
