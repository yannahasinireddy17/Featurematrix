import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import './App.css'

const API_BASE = 'http://localhost:8081/api'
const TOKEN_KEY = 'productcompare_token'
const STORES = ['Amazon', 'Myntra', 'Nykaa', 'Flipkart']

const asDisplay = (feature) => {
  if (!feature) return '-'
  const value = (feature.value ?? '').trim()
  const price = (feature.price ?? '').trim()
  if (!value && !price) return '-'
  if (value && price) return `${value} (price: ${price})`
  return value || `price: ${price}`
}

const parseNumeric = (value) => {
  if (!value) return null
  const normalized = String(value).replace(/[^0-9.]/g, ' ').trim()
  if (!normalized) return null
  const first = normalized.split(/\s+/)[0]
  const parsed = Number.parseFloat(first)
  return Number.isFinite(parsed) ? parsed : null
}

const comparePair = (leftValue, rightValue) => {
  const leftNumber = parseNumeric(leftValue)
  const rightNumber = parseNumeric(rightValue)
  if (leftNumber === null || rightNumber === null) return { left: '', right: '' }
  if (leftNumber === rightNumber) return { left: '', right: '' }
  return leftNumber > rightNumber ? { left: 'better', right: 'worse' } : { left: 'worse', right: 'better' }
}

export default function Compare() {
  const [searchParams] = useSearchParams()
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')
  const [products, setProducts] = useState([])
  const [storesByProductId, setStoresByProductId] = useState({})
  const [recommendation, setRecommendation] = useState(null)

  const token = localStorage.getItem(TOKEN_KEY) ?? ''
  const productIds = useMemo(() => {
    return (searchParams.get('ids') ?? '')
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean)
      .slice(0, 2)
  }, [searchParams])

  useEffect(() => {
    if (!token) {
      setError('Please login first.')
      setIsLoading(false)
      return
    }

    if (productIds.length < 2) {
      setError('Two product IDs are required in query params.')
      setIsLoading(false)
      return
    }

    const load = async () => {
      setIsLoading(true)
      setError('')
      try {
        const headers = {
          'X-Auth-Token': token
        }

        const [combined, recommendationResponse] = await Promise.all([
          Promise.all(
          productIds.map(async (id) => {
            const [productResponse, storesResponse] = await Promise.all([
              fetch(`${API_BASE}/products/${id}`, { headers }),
              fetch(`${API_BASE}/products/${id}/stores`, { headers })
            ])

            if (!productResponse.ok) {
              const text = await productResponse.text()
              throw new Error(text || `Failed to load product ${id}`)
            }

            if (!storesResponse.ok) {
              const text = await storesResponse.text()
              throw new Error(text || `Failed to load stores for product ${id}`)
            }

            const product = await productResponse.json()
            const stores = await storesResponse.json()
            return { product, stores }
          })
          ),
          fetch(`${API_BASE}/compare/recommendation?productA=${productIds[0]}&productB=${productIds[1]}`, {
            headers
          })
        ])

        if (!recommendationResponse.ok) {
          const text = await recommendationResponse.text()
          throw new Error(text || 'Failed to load recommendation')
        }

        const recommendationData = await recommendationResponse.json()

        setProducts(combined.map((item) => item.product))
        setStoresByProductId(
          Object.fromEntries(combined.map((item) => [String(item.product.id), item.stores ?? []]))
        )
        setRecommendation(recommendationData)
      } catch (loadError) {
        setError(loadError.message)
      } finally {
        setIsLoading(false)
      }
    }

    load()
  }, [token, productIds])

  const comparisonRows = useMemo(() => {
    if (products.length !== 2) return []

    const [productOne, productTwo] = products
    const p1Map = new Map((productOne.features ?? []).map((feature) => [feature.name, feature]))
    const p2Map = new Map((productTwo.features ?? []).map((feature) => [feature.name, feature]))

    const allFeatureNames = Array.from(new Set([...p1Map.keys(), ...p2Map.keys()]))

    return allFeatureNames.map((name) => {
      const left = asDisplay(p1Map.get(name))
      const right = asDisplay(p2Map.get(name))
      return {
        featureName: name,
        left,
        right,
        different: left !== right
      }
    })
  }, [products])

  const availableOn = useMemo(() => {
    return products.map((product) => {
      const productStores = storesByProductId[String(product?.id)] ?? []
      const normalized = productStores.map((store) => ({
        storeName: store.storeName,
        price: store.price,
        buyLink: store.buyLink
      }))

      const known = new Set(normalized.map((store) => (store.storeName ?? '').trim().toLowerCase()))
      for (const storeName of STORES) {
        if (!known.has(storeName.toLowerCase())) {
          normalized.push({
            storeName,
            price: null,
            buyLink: ''
          })
        }
      }

      return {
        productId: product?.id,
        productName: product?.name ?? 'Product',
        stores: normalized
      }
    })
  }, [products, storesByProductId])

  const recommendedProduct = useMemo(() => {
    if (!recommendation?.recommendedProductId) return null
    return products.find((product) => Number(product.id) === Number(recommendation.recommendedProductId)) ?? null
  }, [products, recommendation])

  const goHome = () => {
    window.location.assign('/')
  }

  if (isLoading) {
    return (
      <main className="app-shell">
        <section className="panel">
          <p className="muted">Loading comparison...</p>
        </section>
      </main>
    )
  }

  if (error) {
    return (
      <main className="app-shell">
        <section className="panel">
          <h2>Comparison Error</h2>
          <p className="status-text">{error}</p>
          <button type="button" className="ghost" onClick={goHome}>
            Back to Home
          </button>
        </section>
      </main>
    )
  }

  const [productOne, productTwo] = products

  return (
    <main className="app-shell">
      <section className="panel card compare-page-panel">
        <div className="compare-header-row">
          <h2>Product Comparison</h2>
          <button type="button" className="ghost" onClick={goHome}>
            Back
          </button>
        </div>

        <div className="compare-name-row">
          <div className="compare-name-card">{productOne?.name ?? 'Product 1'}</div>
          <div className="compare-name-card">{productTwo?.name ?? 'Product 2'}</div>
        </div>

        <div className="table-wrapper">
          <table className="compare-table">
            <thead>
              <tr>
                <th>Feature</th>
                <th>{productOne?.name ?? 'Product 1'}</th>
                <th>{productTwo?.name ?? 'Product 2'}</th>
              </tr>
            </thead>
            <tbody>
              {comparisonRows.length === 0 ? (
                <tr>
                  <td colSpan={3}>No features available for these products.</td>
                </tr>
              ) : (
                comparisonRows.map((row) => {
                  const rating = comparePair(row.left, row.right)
                  const leftClass = [row.different ? 'compare-different' : '', rating.left].filter(Boolean).join(' ')
                  const rightClass = [row.different ? 'compare-different' : '', rating.right].filter(Boolean).join(' ')
                  return (
                    <tr key={row.featureName}>
                      <td>{row.featureName}</td>
                      <td className={leftClass}>{row.left}</td>
                      <td className={rightClass}>{row.right}</td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>

        {recommendedProduct && (
          <div className="recommendation-card">
            <h3>
              🏆 Recommended Pick: {recommendedProduct.name}{' '}
              <span className="badge-best">Best Pick</span>
            </h3>
            <p>{recommendation?.reason || 'Balanced overall value across compared specs.'}</p>
          </div>
        )}

        <div className="available-on-wrap">
          <h3>Available On</h3>
          <div className="available-on-grid">
            {availableOn.map((entry) => (
              <div key={entry.productName} className="available-on-card card">
                <h4>{entry.productName}</h4>
                <div className="store-cards-grid">
                  {entry.stores.map((storeRow) => {
                    const prices = entry.stores
                      .map((store) => parseNumeric(store.price))
                      .filter((value) => value !== null)
                    const lowestPrice = prices.length ? Math.min(...prices) : null
                    const currentPrice = parseNumeric(storeRow.price)
                    const isLowestPrice =
                      lowestPrice !== null && currentPrice !== null && currentPrice === lowestPrice

                    return (
                      <div key={`${entry.productName}-${storeRow.storeName}`} className="store-card card">
                        <div>
                          <strong>{storeRow.storeName}</strong>
                          <div>{storeRow.price ? `₹${storeRow.price}` : '-'}</div>
                        </div>
                        <div>
                          {isLowestPrice && <span className="badge-best">Best Price</span>}
                          <a
                            href={storeRow.buyLink || '#'}
                            target="_blank"
                            rel="noreferrer"
                            className={`buy-now-btn mini btn-primary ${!storeRow.buyLink ? 'disabled' : ''}`}
                            onClick={(event) => {
                              if (!storeRow.buyLink) event.preventDefault()
                            }}
                          >
                            Buy
                          </a>
                        </div>
                      </div>
                    )
                  })}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>
    </main>
  )
}
