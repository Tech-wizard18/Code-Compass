import { useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'

function Home() {
  const navigate = useNavigate()

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      navigate('/chat')
    }
  }, [navigate])

  return (
    <div className="min-h-screen bg-stone-50 flex items-center justify-center px-4">
      <div className="text-center max-w-lg">
        <h1 className="text-4xl font-bold text-stone-900 mb-3">CodeCompass</h1>
        <p className="text-stone-500 text-lg mb-8">
          Explore any public GitHub repository through natural language chat —
          get answers grounded in the actual code, with exact file and line citations.
        </p>

        <div className="flex gap-3 justify-center">
          <Link
            to="/login"
            className="bg-indigo-600 text-white font-medium px-6 py-2.5 rounded-lg hover:bg-indigo-700 transition"
          >
            Log In
          </Link>
          <Link
            to="/register"
            className="bg-white text-stone-700 font-medium px-6 py-2.5 rounded-lg border border-stone-300 hover:bg-stone-100 transition"
          >
            Sign Up
          </Link>
        </div>
      </div>
    </div>
  )
}

export default Home