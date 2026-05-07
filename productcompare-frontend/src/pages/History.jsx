import { Clock, History as HistoryIcon, Trash2 } from 'lucide-react'
import { Link, useNavigate } from 'react-router-dom'
import { useState, useEffect } from 'react'

export default function History() {
  const navigate = useNavigate()
  const [history, setHistory] = useState([])

  useEffect(() => {
    // Let's pretend to fetch from localStorage or just use a nice placeholder for the demo
    setHistory([
      { id: 1, p1: "iPhone 15", p2: "iPhone 17 Pro", date: "Today, 10:45 AM" },
      { id: 2, p1: "Dell XPS 15", p2: "MacBook Pro 14", date: "Yesterday, 3:20 PM" },
      { id: 3, p1: "Sony A7IV", p2: "Canon R6 Mark II", date: "May 1, 2026" },
      { id: 4, p1: "Samsung S24 Ultra", p2: "Pixel 8 Pro", date: "April 29, 2026" },
      { id: 5, p1: "Nothing Ear 2", p2: "Sony WF-1000XM5", date: "April 25, 2026" }
    ])
  }, [])

  const handleRecompare = (item) => {
    // Quick mock payload for presentation demo
    const products = [
      { id: 1, name: item.p1 },
      { id: 2, name: item.p2 }
    ]
    const data = {
      productItems: products,
      featureItems: [{ id: 1, name: "Price" }, { id: 2, name: "Review Rating" }, { id: 3, name: "Feature Score" }],
      rows: [
        { featureId: 1, featureName: "Price", cells: [{ productId: 1, value: "₹49,999", isBetter: true }, { productId: 2, value: "₹65,999", isBetter: false }] },
        { featureId: 2, featureName: "Review Rating", cells: [{ productId: 1, value: "4.7", isBetter: true }, { productId: 2, value: "4.6", isBetter: false }] },
        { featureId: 3, featureName: "Feature Score", cells: [{ productId: 1, value: "85/100", isBetter: false }, { productId: 2, value: "92/100", isBetter: true }] }
      ]
    }
    sessionStorage.setItem('directComparisonData', JSON.stringify(data))
    sessionStorage.setItem('directComparisonInputProducts', JSON.stringify(products))
    navigate('/compare?direct=true')
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 animate-in fade-in duration-500">
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-xl bg-blue-500/20 flex items-center justify-center">
            <Clock className="w-6 h-6 text-blue-500" />
          </div>
          <h1 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-cyan-400">
            Recent Comparisons
          </h1>
        </div>
        <button 
          onClick={() => setHistory([])} 
          className="text-sm font-semibold text-red-400 hover:text-red-300 flex items-center gap-2 px-4 py-2 rounded-lg hover:bg-red-500/10 transition-colors"
        >
          <Trash2 className="w-4 h-4" /> Clear History
        </button>
      </div>

      <div className="space-y-4">
        {history.map((item) => (
          <div key={item.id} className="panel card p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-4 group">
            <div className="flex items-center gap-3">
              <HistoryIcon className="w-5 h-5 text-purple-400/50" />
              <div>
                <h3 className="font-bold text-lg">{item.p1} <span className="text-purple-400/50 italic mx-2">vs</span> {item.p2}</h3>
                <p className="text-sm text-purple-200/50">{item.date}</p>
              </div>
            </div>
            <button 
              onClick={() => handleRecompare(item)}
              className="px-5 py-2 rounded-lg border border-purple-500/30 text-purple-300 font-semibold hover:bg-purple-500/20 transition-colors whitespace-nowrap text-center"
            >
              Re-Compare
            </button>
          </div>
        ))}
        {history.length === 0 && (
          <div className="text-center p-12 panel border-dashed">
            <p className="text-purple-200/50">No comparisons made yet.</p>
          </div>
        )}
      </div>
    </div>
  )
}