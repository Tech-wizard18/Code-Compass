import { useState, useEffect } from 'react'
import { getConversations, deleteConversation } from '../api'
import { toast } from 'react-hot-toast'

function Sidebar({ onSelectConversation, activeConversationId, onNewChat, onIndexAnotherRepo, isOpen, onClose, onActiveConversationDeleted }) {
  const [conversations, setConversations] = useState([])
  const [loading, setLoading] = useState(true)
  const [conversationToDelete, setConversationToDelete] = useState(null)

  useEffect(() => {
    getConversations()
      .then((data) => setConversations(data))
      .catch(() => setConversations([]))
      .finally(() => setLoading(false))
  }, [])

  const handleSelect = (conv) => {
    onSelectConversation(conv)
    if (onClose) onClose()
  }

  const handleDeleteClick = (e, conv) => {
    e.stopPropagation()
    setConversationToDelete(conv)
  }

  const confirmDelete = async () => {
    const conv = conversationToDelete
    setConversationToDelete(null)

    try {
      await deleteConversation(conv.id)
      setConversations((prev) => prev.filter((c) => c.id !== conv.id))
      toast.success('Conversation deleted')

      if (conv.id === activeConversationId && onActiveConversationDeleted) {
        onActiveConversationDeleted()
      }
    } catch (err) {
      toast.error(err.message)
    }
  }

  return (
    <>
      {/* Backdrop — only visible on mobile when sidebar is open */}
      {isOpen && (
        <div
          onClick={onClose}
          className="fixed inset-0 bg-black/30 z-20 md:hidden"
        />
      )}

      <div
        className={`
          fixed md:static top-0 left-0 h-full z-30
          w-64 flex-shrink-0 border-r border-stone-200 bg-white overflow-y-auto
          transition-transform duration-200
          ${isOpen ? 'translate-x-0' : '-translate-x-full'}
          md:translate-x-0
        `}
      >
        <div className="p-3">
          {onNewChat && (
            <button
              onClick={onNewChat}
              className="w-full mb-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 transition px-3 py-2 rounded-lg"
            >
              + New Chat
            </button>
          )}

          {onIndexAnotherRepo && (
            <button
              onClick={onIndexAnotherRepo}
              className="w-full mb-3 text-sm font-medium text-stone-700 bg-stone-100 hover:bg-stone-200 transition px-3 py-2 rounded-lg"
            >
              Index Another Repo
            </button>
          )}

          <p className="text-xs font-semibold text-stone-400 uppercase tracking-wide px-2 mb-2">
            History
          </p>

          {loading && (
            <p className="text-sm text-stone-400 px-2">Loading...</p>
          )}

          {!loading && conversations.length === 0 && (
            <p className="text-sm text-stone-400 px-2">No conversations yet</p>
          )}

          {!loading &&
            conversations.map((conv) => (
              <div
                key={conv.id}
                onClick={() => handleSelect(conv)}
                className={`group flex items-center justify-between px-2 py-2 rounded-lg cursor-pointer transition ${
                  conv.id === activeConversationId
                    ? 'bg-indigo-50 border border-indigo-200'
                    : 'hover:bg-stone-100'
                }`}
              >
                <p className="text-sm text-stone-800 truncate flex-1">{conv.title}</p>
                <button
                  onClick={(e) => handleDeleteClick(e, conv)}
                  className="opacity-100 md:opacity-0 md:group-hover:opacity-100 text-stone-400 hover:text-red-600 transition ml-2 flex-shrink-0"
                  aria-label="Delete conversation"
                >
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <polyline points="3 6 5 6 21 6" />
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                  </svg>
                </button>
              </div>
            ))}
        </div>
      </div>

      {/* Delete confirmation modal */}
      {conversationToDelete && (
        <div className="fixed inset-0 bg-black/40 z-40 flex items-center justify-center px-4">
          <div className="bg-white rounded-2xl shadow-lg p-6 w-full max-w-sm">
            <h2 className="text-lg font-semibold text-stone-900 mb-2">
              Delete conversation?
            </h2>
            <p className="text-sm text-stone-500 mb-6">
              "{conversationToDelete.title}" will be permanently deleted. This cannot be undone.
            </p>
            <div className="flex gap-3 justify-end">
              <button
                onClick={() => setConversationToDelete(null)}
                className="px-4 py-2 text-sm font-medium text-stone-700 bg-stone-100 hover:bg-stone-200 rounded-lg transition"
              >
                Cancel
              </button>
              <button
                onClick={confirmDelete}
                className="px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-lg transition"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}

export default Sidebar