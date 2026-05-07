import { Link, useLocation } from 'react-router-dom'
import { Home, Scale, TrendingUp, IndianRupee, Clock, Info, Search, Moon, Sun } from 'lucide-react'
import { useState, useEffect } from 'react'

export default function Navbar() {
  const location = useLocation()
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'dark')

  useEffect(() => {
    if (theme === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
    localStorage.setItem('theme', theme)
  }, [theme])

  const navLinks = [
    { name: 'Home', path: '/', icon: Home },
    { name: 'Trending', path: '/trending', icon: TrendingUp },
    { name: 'Budget Picks', path: '/budget', icon: IndianRupee },
    { name: 'History', path: '/history', icon: Clock },
  ]

  const isActive = (path) => {
    if (path === '/' && location.pathname !== '/') return false
    return location.pathname.startsWith(path)
  }

  return (
    <nav className="sticky top-0 z-50 w-full backdrop-blur-xl bg-[#0f0a1f]/80 border-b border-white/10 shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 group">
            <div className="bg-gradient-to-br from-purple-600 to-fuchsia-600 p-2 rounded-xl group-hover:shadow-[0_0_15px_rgba(192,38,211,0.5)] transition-all">
              <Scale className="w-5 h-5 text-white" />
            </div>
            <span className="font-extrabold text-xl bg-clip-text text-transparent bg-gradient-to-r from-purple-200 to-fuchsia-200 tracking-tight">
              ProductCompare
            </span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-1">
            {navLinks.map((link) => {
              const Icon = link.icon
              return (
                <Link
                  key={link.name}
                  to={link.path}
                  className={`flex items-center gap-1.5 px-3 py-2 rounded-lg text-sm font-medium transition-all ${
                    isActive(link.path)
                      ? 'bg-purple-500/20 text-purple-300'
                      : 'text-purple-200/70 hover:bg-white/5 hover:text-purple-200'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  {link.name}
                </Link>
              )
            })}
          </div>

          {/* Right Side Tools */}
          <div className="flex items-center gap-4">
            {/* Mock Search Bar */}
            <div className="hidden lg:flex items-center relative group">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Search className="w-4 h-4 text-purple-300/50 group-focus-within:text-fuchsia-400 transition-colors" />
              </div>
              <input
                type="text"
                placeholder="Search products..."
                className="w-48 focus:w-64 transition-all duration-300 bg-white/5 border border-white/10 rounded-full py-1.5 pl-9 pr-4 text-sm text-purple-100 placeholder-purple-300/40 focus:outline-none focus:border-fuchsia-500/50 focus:ring-1 focus:ring-fuchsia-500/50"
              />
            </div>

            {/* Theme Toggle */}
            <button
              onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
              className="p-2 rounded-full hover:bg-white/10 text-purple-200/70 hover:text-purple-200 transition-colors"
              aria-label="Toggle Theme"
            >
              {theme === 'dark' ? <Moon className="w-5 h-5" /> : <Sun className="w-5 h-5" />}
            </button>
          </div>
        </div>
      </div>
    </nav>
  )
}