package ru.taximer.taxiandroid.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.item_setting.view.setting_checked
import kotlinx.android.synthetic.main.item_setting.view.setting_description
import kotlinx.android.synthetic.main.item_setting.view.setting_image
import kotlinx.android.synthetic.main.item_setting.view.setting_name
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.utils.isVisible

class SettingView: FrameLayout{
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init(){
        inflate(context, R.layout.item_setting, this)
    }

    fun setData(imageRes: Int, nameRes: Int, descriptionRes: Int){
        setting_name.setText(nameRes)
        setting_image.setImageResource(imageRes)
        setting_description.setText(descriptionRes)
    }

    fun setChecked(isChecked: Boolean){
        setting_checked.isVisible = isChecked
    }
}