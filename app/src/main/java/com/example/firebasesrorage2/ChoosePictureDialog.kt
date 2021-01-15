package com.example.firebasesrorage2

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import kotlinx.android.synthetic.main.dialog_choose_photo_source.*

class ChoosePictureDialog(context: Context, var dialogClickListener: IDialog) :
    AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_choose_photo_source)

        this.setTitle("Photo")

        textViewGallery.setOnClickListener {
            dialogClickListener.chooseImage()
            dismiss()
        }

        textViewTakePhoto.setOnClickListener {
            dialogClickListener.takePhoto()
            dismiss()
        }
    }
}