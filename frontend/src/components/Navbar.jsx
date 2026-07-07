import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'

function Navbar({ onMenuClick }) {
  const navigate = useNavigate()

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('activeRepoUrl')
    localStorage.removeItem('activeConversationId')
    toast.success('Logged out')
    navigate('/')
  }

  return (
    <div className="flex items-center justify-between px-4 py-3 border-b border-stone-200 bg-white">
      <div className="flex items-center gap-3">
        {onMenuClick && (
          <button
            onClick={onMenuClick}
            className="md:hidden text-stone-600 hover:text-stone-900 transition"
            aria-label="Toggle menu"
          >
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="3" y1="12" x2="21" y2="12" />
              <line x1="3" y1="6" x2="21" y2="6" />
              <line x1="3" y1="18" x2="21" y2="18" />
            </svg>
          </button>
        )}
        <span className="font-semibold text-stone-900">CodeCompass</span>
      </div>
      <button
        onClick={handleLogout}
        className="text-sm font-medium text-white bg-stone-800 hover:bg-stone-900 transition px-4 py-1.5 rounded-lg"
      >
        Log out
      </button>
    </div>
  )
}

export default Navbar