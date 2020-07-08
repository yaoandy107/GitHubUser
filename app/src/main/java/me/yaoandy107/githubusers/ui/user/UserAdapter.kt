package me.yaoandy107.githubusers.ui.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_user.view.*
import me.yaoandy107.githubusers.R
import me.yaoandy107.githubusers.model.User

class UserAdapter : PagingDataAdapter<User, UserAdapter.ViewHolder>(
    USER_COMPARATOR
) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val username = view.tv_username

        fun bind(user: User) {
            username.text = user.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.bind(repoItem)
        }
    }

    companion object {
        val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.name == newItem.name
        }
    }
}