package me.yaoandy107.githubusers.ui.user

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.yaoandy107.githubusers.R
import me.yaoandy107.githubusers.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import retrofit2.HttpException


class UserFragment : BaseFragment() {

    private var searchJob: Job? = null
    private var query: String? = null
    private val userViewModel: UserViewModel by viewModel { parametersOf(query) }
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
        rv_user.adapter = adapter
        rv_user.layoutManager = LinearLayoutManager(context)
        adapter.addLoadStateListener { loadState ->
            rv_user.isVisible = loadState.refresh is LoadState.NotLoading
            progress_bar.isVisible = loadState.refresh is LoadState.Loading
            empty_view.visibility = View.GONE

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                handlePagingError(it.error as Exception)
            }
        }
        @OptIn(ExperimentalPagingApi::class)
        adapter.addDataRefreshListener {
            empty_view.visibility = (if (adapter.itemCount == 0) View.VISIBLE else View.GONE)
        }
    }

    private fun initSearch() {
        search_user.setOnEditorActionListener { v, actionId, event ->
            search(v.text.trim().toString())
            hideSoftKeyboard()
            true
        }

        lifecycleScope.launch {
            @OptIn(ExperimentalPagingApi::class)
            adapter.dataRefreshFlow.collect {
                rv_user.scrollToPosition(0)
            }
        }
    }

    private fun search(query: String) {
        if (query.isNotBlank()) {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                userViewModel.searchUsers(query).collectLatest {
                    adapter.submitData(it)
                }
            }
        } else {
            showSnackBar("使用者名稱請勿留空")
        }
    }

    private fun handlePagingError(error: Exception) {
        if (error is HttpException) {
            val httpException: HttpException = error
            when (httpException.code()) {
                403 -> {
                    showPagingAlertDialog("API 請求過於頻繁，請稍後重新嘗試")
                }
            }
        }
    }

    private fun showPagingAlertDialog(message: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(message)
        builder.setPositiveButton("重試") { _: DialogInterface, _: Int ->
            adapter.retry()
        }
        builder.setNegativeButton("取消") { _, _ -> }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
}