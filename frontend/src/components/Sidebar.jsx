import { useState, useEffect, useRef } from 'react'
import { getConversations, deleteConversation, renameConversation } from '../api'
import { toast } from 'react-hot-toast'

function Sidebar({ onSelectConversation, activeConversationId, onNewChat, onIndexAnotherRepo, isOpen, onClose, onActiveConversationDeleted }) {
  const [conversations, setConversations] = useState([])
  const [loading, setLoading] = useState(true)
  const [conversationToDelete, setConversationToDelete] = useState(null)
  const [editingId, setEditingId] = useState(null)
  const [editTitle, setEditTitle] = useState('')
  const cancellingRef = useRef(false)

  useEffect(() => {
    getConversations()
      .then((data) => setConversations(data))
      .catch(() => setConversations([]))
      .finally(() => setLoading(false))
  }, [])

  const handleSelect = (conv) => {
    if (editingId === conv.id) return
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

  const handleRenameClick = (e, conv) => {
    e.stopPropagation()
    setEditingId(conv.id)
    setEditTitle(conv.title)
  }

  const saveRename = async (conv) => {
    const trimmed = editTitle.trim()

    if (!trimmed || trimmed === conv.title) {
      setEditingId(null)
      return
    }

    try {
      await renameConversation(conv.id, trimmed)
      setConversations((prev) =>
        prev.map((c) => (c.id === conv.id ? { ...c, title: trimmed } : c))
      )
      toast.success('Conversation renamed')
    } catch (err) {
      toast.error(err.message)
    } finally {
      setEditingId(null)
    }
  }

  const handleTitleKeyDown = (e, conv) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      saveRename(conv)
    } else if (e.key === 'Escape') {
      cancellingRef.current = true
      setEditingId(null)
    }
  }

  const handleTitleBlur = (conv) => {
    if (cancellingRef.current) {
      cancellingRef.current = false
      return
    }
    saveRename(conv)
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
                {editingId === conv.id ? (
                  <input
                    type="text"
                    value={editTitle}
                    onChange={(e) => setEditTitle(e.target.value)}
                    onKeyDown={(e) => handleTitleKeyDown(e, conv)}
                    onBlur={() => handleTitleBlur(conv)}
                    onClick={(e) => e.stopPropagation()}
                    autoFocus
                    className="text-sm text-stone-800 flex-1 bg-white border border-indigo-300 rounded px-1 py-0.5 outline-none focus:ring-1 focus:ring-indigo-400"
                  />
                ) : (
                  <p className="text-sm text-stone-800 truncate flex-1">{conv.title}</p>
                )}

                {editingId !== conv.id && (
                  <div className="flex items-center flex-shrink-0">
                    <button
                      onClick={(e) => handleRenameClick(e, conv)}
                      className="opacity-100 md:opacity-0 md:group-hover:opacity-100 text-stone-400 hover:text-indigo-600 transition ml-2"
                      aria-label="Rename conversation"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                      </svg>
                    </button>
                    <button
                      onClick={(e) => handleDeleteClick(e, conv)}
                      className="opacity-100 md:opacity-0 md:group-hover:opacity-100 text-stone-400 hover:text-red-600 transition ml-2"
                      aria-label="Delete conversation"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <polyline points="3 6 5 6 21 6" />
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                      </svg>
                    </button>
                  </div>
                )}
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