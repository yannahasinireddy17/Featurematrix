import { TrendingUp, Flame, ArrowRight } from 'lucide-react'
import { Link, useNavigate } from 'react-router-dom'

export default function Trending() {
  const navigate = useNavigate()

  const trendingComparisons = [
    { title: "iPhone 15 vs Galaxy S24", category: "Smartphones", tag: "Hot 🔥" },
    { title: "MacBook Air M3 vs Dell XPS 13", category: "Laptops", tag: "Popular 🚀" },
    { title: "Sony WH-1000XM5 vs Bose QC Ultra", category: "Headphones", tag: "Trending 📈" },
    { title: "AirPods Pro 2 vs Pixel Buds", category: "Earbuds", tag: "Hot 🔥" },
    { title: "PS5 vs Xbox Series X", category: "Gaming", tag: "Classic ⚔️" },
    { title: "iPad Air vs Galaxy Tab S9", category: "Tablets", tag: "Popular 🚀" },
  ]

  const handleCompare = (item) => {
    // Quick mock payload for presentation demo
    const [p1, p2] = item.title.split(' vs ')
    
    // Assign mock image URLs based on category
    let mockImg1 = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80'
    let mockImg2 = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80'
    
    // Generate realistic feature comparisons where the newer/Pro product usually wins despite price
    let rows = []
    
    if (item.category === 'Smartphones') {
      mockImg1 = 'https://images.unsplash.com/photo-1605236453806-6ff368528d70?w=800&q=80'
      mockImg2 = 'https://images.unsplash.com/photo-1598327105666-5b89351cb31b?w=800&q=80'
      rows = [
        { featureId: 1, featureName: "Price", cells: [{ value: "₹59,900" }, { value: "₹1,34,900" }] },
        { featureId: 4, featureName: "RAM", cells: [{ value: "6 GB" }, { value: "8 GB" }] },
        { featureId: 5, featureName: "Review Rating", cells: [{ value: "4.3 / 5" }, { value: "4.8 / 5" }] },
        { featureId: 6, featureName: "Camera Megapixels", cells: [{ value: "48 MP" }, { value: "48 MP + 5x Optical" }] },
        { featureId: 7, featureName: "Refresh Rate", cells: [{ value: "60 Hz" }, { value: "120 Hz ProMotion" }] }
      ]
    } else if (item.category === 'Laptops') {
      mockImg1 = 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80'
      mockImg2 = 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=800&q=80'
      rows = [
        { featureId: 1, featureName: "Price", cells: [{ value: "₹1,14,900" }, { value: "₹1,44,900" }] },
        { featureId: 4, featureName: "RAM", cells: [{ value: "8 GB Unified" }, { value: "16 GB" }] },
        { featureId: 5, featureName: "Review Rating", cells: [{ value: "4.7 / 5" }, { value: "4.5 / 5" }] },
        { featureId: 6, featureName: "Battery Life", cells: [{ value: "18 hours" }, { value: "14 hours" }] }
      ]
    } else if (item.category === 'Headphones' || item.category === 'Earbuds') {
      mockImg1 = 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80'
      mockImg2 = 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800&q=80'
      rows = [
        { featureId: 1, featureName: "Price", cells: [{ value: "₹24,900" }, { value: "₹29,900" }] },
        { featureId: 4, featureName: "Battery Life", cells: [{ value: "30 hours" }, { value: "24 hours" }] },
        { featureId: 5, featureName: "Review Rating", cells: [{ value: "4.6 / 5" }, { value: "4.8 / 5" }] }
      ]
    } else if (item.category === 'Gaming') {
      mockImg1 = 'https://images.unsplash.com/photo-1605901309584-818e25960b8f?w=800&q=80'
      mockImg2 = 'https://images.unsplash.com/photo-1622297845775-5ff3fef71d13?w=800&q=80'
      rows = [
        { featureId: 1, featureName: "Price", cells: [{ value: "₹54,990" }, { value: "₹54,990" }] },
        { featureId: 4, featureName: "Storage", cells: [{ value: "825 GB" }, { value: "1000 GB" }] },
        { featureId: 5, featureName: "Review Rating", cells: [{ value: "4.8 / 5" }, { value: "4.7 / 5" }] }
      ]
    } else if (item.category === 'Tablets') {
      mockImg1 = 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&q=80'
      mockImg2 = 'https://images.unsplash.com/photo-1589739900266-43b2844f2ff8?w=800&q=80'
      rows = [
        { featureId: 1, featureName: "Price", cells: [{ value: "₹59,900" }, { value: "₹72,999" }] },
        { featureId: 4, featureName: "Storage", cells: [{ value: "64 GB" }, { value: "128 GB" }] },
        { featureId: 5, featureName: "Review Rating", cells: [{ value: "4.7 / 5" }, { value: "4.6 / 5" }] }
      ]
    }

    const products = [
      { id: 1, name: p1?.trim() || 'Product 1', imageUrl: mockImg1 },
      { id: 2, name: p2?.trim() || 'Product 2', imageUrl: mockImg2 }
    ]

    const data = {
      productItems: products,
      rows: rows
    }
    
    sessionStorage.setItem('directComparisonData', JSON.stringify(data))
    sessionStorage.setItem('directComparisonInputProducts', JSON.stringify(products))
    navigate('/compare?direct=true')
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 animate-in fade-in duration-500">
      <div className="flex items-center gap-3 mb-8">
        <div className="w-12 h-12 rounded-xl bg-orange-500/20 flex items-center justify-center">
          <TrendingUp className="w-6 h-6 text-orange-500" />
        </div>
        <h1 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-orange-400 to-red-500">
          Trending Comparisons
        </h1>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {trendingComparisons.map((item, idx) => (
          <div key={idx} onClick={() => handleCompare(item)} className="panel card p-6 group cursor-pointer hover:-translate-y-1 transition-transform">
            <div className="flex justify-between items-start mb-4">
              <span className="text-xs font-bold uppercase tracking-wider text-purple-400/80">{item.category}</span>
              <span className="bg-red-500/10 text-red-400 px-2 py-1 rounded text-xs font-bold">{item.tag}</span>
            </div>
            <h3 className="text-xl font-bold mb-4">{item.title}</h3>
            <div className="flex items-center gap-2 text-fuchsia-400 font-semibold group-hover:text-fuchsia-300">
              Compare Now <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}