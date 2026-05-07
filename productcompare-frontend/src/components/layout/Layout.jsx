import { Outlet, Link } from 'react-router-dom'
import Navbar from './Navbar'

export default function Layout() {
  return (
    <div className="min-h-screen flex flex-col w-full relative">
      <Navbar />
      <div className="flex-grow w-full relative">
        <Outlet />
      </div>
      
      <footer className="mt-auto py-6 border-t border-purple-500/20 bg-black/10 backdrop-blur-sm z-40">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-4">
          <p className="text-white/50 text-sm font-sans tracking-wide">
            ProductCompare © 2026
          </p>
          <div className="flex items-center gap-4">
            <Link 
              to="/about" 
              className="text-purple-300/80 hover:text-purple-200 text-sm font-medium transition-colors"
            >
              About
            </Link>
          </div>
        </div>
      </footer>
    </div>
  )
}