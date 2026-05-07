import { IndianRupee, Tag, ArrowRight } from 'lucide-react'
import { Link, useNavigate } from 'react-router-dom'

export default function BudgetPicks() {
  const navigate = useNavigate()
  const budgetCategories = [
    { 
      title: "Best Phones Under ₹30K", 
      desc: "Flagship killers and mid-range beasts that offer the best value for money without breaking the bank.",
      products: "Motorola Edge 40 Neo • Poco F5 • Redmi Note 13 Pro • Nothing (2a)",
      productsList: ["Motorola Edge 40 Neo", "Poco F5", "Redmi Note 13 Pro", "Nothing (2a)"]
    },
    { 
      title: "Best Laptops Under ₹60K", 
      desc: "Perfect machines for students, standard office work, and light gaming.",
      products: "ASUS Vivobook 15 • Acer Swift Go • Lenovo IdeaPad • HP Victus",
      productsList: ["ASUS Vivobook 15", "Acer Swift Go", "Lenovo IdeaPad", "HP Victus"]
    },
    { 
      title: "Best TWS Under ₹5K", 
      desc: "Great active noise cancellation and sound without premium price tags.",
      products: "Realme Buds 5 Pro • OnePlus Buds 3 • Oppo Enco Air3 • CMF Buds",
      productsList: ["Realme Buds 5 Pro", "OnePlus Buds 3", "Oppo Enco Air3", "CMF Buds"]
    },
    { 
      title: "Best TVs Under ₹40K", 
      desc: "Crisp 4K panels with great upscaling capabilities and solid smart TV OS.",
      products: "Xiaomi X Series 50\" • Samsung Crystal 43\" • Sony Bravia 43\"",
      productsList: ["Xiaomi X Series 50\"", "Samsung Crystal 43\"", "Sony Bravia 43\""]
    }
  ]

  const handleProductClick = () => {
    // Navigates to home where they can input their selections
    navigate('/')
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 animate-in fade-in duration-500">
      <div className="flex items-center gap-3 mb-8">
        <div className="w-12 h-12 rounded-xl bg-green-500/20 flex items-center justify-center">
          <IndianRupee className="w-6 h-6 text-green-500" />
        </div>
        <h1 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-green-400 to-emerald-500">
          Curated Budget Picks
        </h1>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6">
        {budgetCategories.map((item, idx) => (
          <div key={idx} className="panel card p-6 group">
            <div className="flex items-center gap-2 mb-4">
              <Tag className="w-5 h-5 text-green-400" />
              <h3 className="text-xl font-bold m-0">{item.title}</h3>
            </div>
            <p className="text-sm text-purple-200/70 mb-4 h-12">{item.desc}</p>
            
            <div className="grid grid-cols-2 gap-2 mb-6 text-sm font-semibold text-purple-200">
              {item.productsList.map((prod, pIdx) => (
                <div key={pIdx} onClick={handleProductClick} className="bg-white/5 p-3 text-center rounded-lg border border-white/5 hover:border-green-500/30 hover:bg-green-500/10 cursor-pointer transition-colors">
                  {prod}
                </div>
              ))}
            </div>
            <Link to="/" className="w-full flex items-center justify-center gap-2 bg-white/5 hover:bg-green-500/20 text-green-400 py-3 rounded-lg font-bold transition-all border border-green-500/20 shadow-lg">
              Run Comparisons
            </Link>
          </div>
        ))}
      </div>
    </div>
  )
}