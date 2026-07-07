import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'

function Navbar() {
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
      <span className="font-semibold text-stone-900">CodeCompass</span>
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