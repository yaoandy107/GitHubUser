package me.yaoandy107.githubusers.ui.user

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.yaoandy107.githubusers.R
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import retrofit2.HttpException


class UserFragment : Fragment() {

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

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                handlePagingError(it.error as Exception)
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
            Snackbar.make(requireView(), "使用者名稱請勿留空", Snackbar.LENGTH_LONG)
        }
    }

    private fun handlePagingError(error: Exception) {
        if (error is HttpException) {
            val httpException: HttpException = error
            when (httpException.code()) {
                403 -> {
                    showPagingAlertDialog("超過 API 呼叫次數，請重新嘗試")
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