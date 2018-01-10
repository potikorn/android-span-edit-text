package com.example.potikorn.hightlightedittext

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

typealias onEditorActionListener = () -> Unit
typealias onKeyPreImeListener = () -> Unit
typealias onTextChangeListener = (String) -> Unit

class CustomEditText : AppCompatEditText {

    var editorListener: onEditorActionListener? = null
    var textChangeListener: onTextChangeListener? = null
    var isChange = false
    var keyPreImeListener: onKeyPreImeListener? = null
    private var observable: TextSearchObservable? = null

    constructor(context: Context) : super(context) {
        initInstance()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        initInstance()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttrs: Int) : super(context, attrs, defStyleAttrs) {
        initInstance()
    }

    override fun onEditorAction(actionCode: Int) {
        super.onEditorAction(actionCode)
        if (actionCode == EditorInfo.IME_ACTION_DONE
                || actionCode == EditorInfo.IME_ACTION_GO
                || actionCode == EditorInfo.IME_ACTION_SEARCH
                || actionCode == EditorInfo.IME_ACTION_SEND
                || actionCode == EditorInfo.IME_ACTION_NEXT) {
            editorListener?.invoke()
        }
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            keyPreImeListener?.invoke()
            return false
        }
        return super.dispatchKeyEvent(event)
    }

    private fun initInstance() {
        observable = TextSearchObservable(this)
        Observable.create(observable)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    textChangeListener?.invoke(it)
                }, { Log.d("TEXT CHANGE LISTENER", it.message) })
    }

    fun removeListener() = observable?.listener?.run { removeTextChangedListener(observable?.listener) }
}

class TextSearchObservable(private val editText: EditText) : ObservableOnSubscribe<String> {
    var listener: Listener? = null
    override fun subscribe(e: ObservableEmitter<String>) {
        listener = Listener(e)
        editText.addTextChangedListener(listener)
    }

    inner class Listener(private val observer: ObservableEmitter<String>) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) =
                observer.onNext(charSequence.toString())

        override fun afterTextChanged(editable: Editable) {}
    }

}