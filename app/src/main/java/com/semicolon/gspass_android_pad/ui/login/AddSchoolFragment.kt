package com.semicolon.gspass_android_pad.ui.login

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.semicolon.gspass_android_pad.R
import com.semicolon.gspass_android_pad.adapter.GetSchoolsAdapter
import com.semicolon.gspass_android_pad.base.BaseFragment
import com.semicolon.gspass_android_pad.databinding.FragmentAddSchoolBinding
import com.semicolon.gspass_android_pad.model.GetSchoolResponse
import com.semicolon.gspass_android_pad.viewmodel.AddSchoolViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class AddSchoolFragment : BaseFragment<FragmentAddSchoolBinding>(R.layout.fragment_add_school) {

    private val vm: AddSchoolViewModel by viewModel()

    private val adapter: GetSchoolsAdapter by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.vm = vm
        val setLayoutManager = LinearLayoutManager(context)
        setLayoutManager.orientation = RecyclerView.VERTICAL
        binding.schoolGetRv.layoutManager = setLayoutManager
        binding.schoolGetRv.adapter = adapter
        observeInputText()
        observeChooseSchool()
    }

    private val textSource = Observable.create<String> {
        binding.schoolGetEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                it.onNext(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private lateinit var observer: Disposable
    private fun observeInputText() {
        observer =
            textSource.debounce(500, TimeUnit.MILLISECONDS).subscribe {
                    vm.loadSchools()
                }
    }

    private val dialog by lazy {
        AlertDialog.Builder(activity, R.style.myDialog)
    }

    private fun observeChooseSchool() {
        binding.vm?.chooseSchool?.observe(viewLifecycleOwner) {
            if (it != null) {
                dialog.setTitle("확인해주세요").setMessage("${it.name}(이)가 맞습니까?")
                    .setPositiveButton("네") { _, _ ->
                        startLogin(it)
                    }.setNegativeButton("아니요") { _, _ ->
                        Toast.makeText(context, "학교를 다시 선택해주세요", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }
    }

    private fun startLogin(school: GetSchoolResponse) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.main_container, LoginFragment())
    }

    override fun onDetach() {
        super.onDetach()
        observer.dispose()
    }
}
