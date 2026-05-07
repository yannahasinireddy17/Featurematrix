import { HeroGeometric } from "@/components/ui/shape-landing-hero"

function DemoHeroGeometric({ children }) {
    return <HeroGeometric 
            badge="🎯 Your Ultimate Shopping Companion"
            title1="Discover the Perfect Pick"
            title2="Without the Guesswork"
            children={children} />
}

export { DemoHeroGeometric }