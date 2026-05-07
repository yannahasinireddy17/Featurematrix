import { Info, Code, ShieldCheck, Zap } from 'lucide-react'

export default function About() {
  return (
    <div className="max-w-4xl mx-auto px-4 py-8 animate-in fade-in duration-500">
      <div className="flex items-center gap-3 mb-8">
        <div className="w-12 h-12 rounded-xl bg-purple-500/20 flex items-center justify-center">
          <Info className="w-6 h-6 text-purple-400" />
        </div>
        <h1 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-purple-400 to-fuchsia-400">
          About ProductCompare
        </h1>
      </div>

      <div className="space-y-8">
        <section className="panel card p-6 md:p-8">
          <h2 className="text-2xl font-bold mb-4">The Mission</h2>
          <p className="text-lg text-purple-100/80 leading-relaxed mb-6">
            ProductCompare was built to solve the modern overwhelming e-commerce experience. With endless tabs, confusing spec sheets, and scattered reviews, finding the actual "best deal" is exhausting. 
          </p>
          <p className="text-lg text-purple-100/80 leading-relaxed">
            Our platform acts as your intelligent shopping companion. It aggregates data, compares specifications side-by-side, entirely eliminates guesswork, and firmly directs you towards the best possible purchase decision tailored to pure value.
          </p>
        </section>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="panel card p-6">
            <div className="flex items-center gap-2 mb-4">
              <Code className="w-6 h-6 text-fuchsia-400" />
              <h3 className="text-xl font-bold m-0">The Tech Stack</h3>
            </div>
            <ul className="space-y-3 text-purple-200/80">
              <li><strong className="text-purple-100">Frontend:</strong> React.js, Vite, Tailwind CSS, Lucide Icons</li>
              <li><strong className="text-purple-100">Backend:</strong> Java, Spring Boot, Maven REST APIs</li>
              <li><strong className="text-purple-100">Design System:</strong> Glass-morphism UI with fully responsive layouts</li>
              <li><strong className="text-purple-100">Data Formats:</strong> Strictly typed DTOs ensuring robust data transferring</li>
            </ul>
          </div>

          <div className="panel card p-6">
            <div className="flex items-center gap-2 mb-4">
              <Zap className="w-6 h-6 text-yellow-400" />
              <h3 className="text-xl font-bold m-0">Core Intelligence</h3>
            </div>
            <ul className="space-y-3 text-purple-200/80">
              <li><strong className="text-purple-100">Price Gap Analysis:</strong> Advanced parsing logic computing statistical price gaps</li>
              <li><strong className="text-purple-100">Smart Badging:</strong> Automatically assigning the "Best Price" badge amongst various e-commerce stores</li>
              <li><strong className="text-purple-100">Pros & Cons Generation:</strong> Summarizing lengthy technical specifications into scannable insights</li>
            </ul>
          </div>
        </div>

        <section className="panel card p-6 text-center border border-purple-500/30">
          <ShieldCheck className="w-12 h-12 text-green-400 mx-auto mb-4" />
          <h2 className="text-2xl font-bold mb-2">Designed & Built for Users</h2>
          <p className="text-purple-200/80 max-w-2xl mx-auto">
            From seamless performance to a visually pleasing dark mode interface, every inch of this application focuses on providing the absolute best user experience possible.
          </p>
        </section>
      </div>
    </div>
  )
}