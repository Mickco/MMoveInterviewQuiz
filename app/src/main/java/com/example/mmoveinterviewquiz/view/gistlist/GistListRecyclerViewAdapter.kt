package com.example.mmoveinterviewquiz.view.gistlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.mmoveinterviewquiz.databinding.ItemGistBinding
import com.example.mmoveinterviewquiz.databinding.ItemUserInfoBinding
import com.example.mmoveinterviewquiz.viewmodel.GistListUIItem

class GistListRecyclerViewAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_NULL = -1
        private const val TYPE_GIST_ITEM = 0
        private const val TYPE_GIST_USER_INFO = 1
    }

    var uiList: List<GistListUIItem> = listOf()
        set(value) {
            field = value
            // TODO: Use difftool later
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_GIST_ITEM -> GistItemViewHolder(ItemGistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_GIST_USER_INFO -> UserInfoItemViewHolder(ItemUserInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> GistItemViewHolder(ItemGistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = uiList.getOrNull(position)
        val context = holder.itemView.context
        val isNextItemUserInfo = uiList.getOrNull(position + 1) is GistListUIItem.UserInfo
        when {
            holder is GistItemViewHolder && item is GistListUIItem.Gist-> {
                holder.binding.apply {
                    gistItemIdText.text = item.idText.getString(context)
                    gistItemUrlText.text = item.url.getString(context)
                    gistItemFileNameText.text = item.csvFileName.getString(context)
                    gistItemFavoriteText.text = item.isFavorite.toString()
                    gistItemDivider.isGone = isNextItemUserInfo
                }
            }
            holder is UserInfoItemViewHolder && item is GistListUIItem.UserInfo -> {
                holder.binding.apply {
                    userInfoItemText.text = item.info.getString(context)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = uiList.getOrNull(position)
        return when (item) {
            is GistListUIItem.Gist -> TYPE_GIST_ITEM
            is GistListUIItem.UserInfo -> TYPE_GIST_USER_INFO
            else -> TYPE_NULL
        }
    }

    override fun getItemCount(): Int {
        return uiList.size
    }


    inner class GistItemViewHolder(val binding: ItemGistBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class UserInfoItemViewHolder(val binding: ItemUserInfoBinding) :
        RecyclerView.ViewHolder(binding.root)
}