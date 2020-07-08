package me.yaoandy107.githubusers.ui.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.yaoandy107.githubusers.R


class UserFragment : Fragment() {

    private var searchJob: Job? = null
    private val userViewModel: UserViewModel by viewModels()
    private val adapter = UserAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initSearch()
    }

    private fun initAdapter() {
        rv_user.apply {
            setHasFixedSize(true)
            adapter = this@UserFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
        adapter.addLoadStateListener { loadState ->
            rv_user.isVisible = loadState.refresh is LoadState.NotLoading
            progress_bar.isVisible = loadState.refresh is LoadState.Loading

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Snackbar
                    .make(requireView(), "", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun initSearch() {
        search_user.setOnEditorActionListener { v, actionId, event ->
            search(v.text.trim().toString())
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            true
        }
    }

    private fun search(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            userViewModel.searchUsers(query).collectLatest {
                adapter.submitData(it)
            }
        }
    }
}