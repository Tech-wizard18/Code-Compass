import { useState, useEffect } from 'react'
import { getConversations } from '../api'

function Sidebar({ onSelectConversation, activeConversationId, onNewChat, onIndexAnotherRepo }) {
  const [conversations, setConversations] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getConversations()
      .then((data) => setConversations(data))
      .catch(() => setConversations([]))
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="w-64 flex-shrink-0 border-r border-stone-200 bg-white h-full overflow-y-auto">
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
              onClick={() => onSelectConversation(conv)}
              className={`px-2 py-2 rounded-lg cursor-pointer transition ${
                conv.id === activeConversationId
                  ? 'bg-indigo-50 border border-indigo-200'
                  : 'hover:bg-stone-100'
              }`}
            >
              <p className="text-sm text-stone-800 truncate">{conv.title}</p>
            </div>
          ))}
      </div>
    </div>
  )
}

export default Sidebar