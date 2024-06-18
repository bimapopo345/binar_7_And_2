package com.gpillaca.upcomingmovies

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.*
import java.io.File
import java.io.FileOutputStream
import android.renderscript.RenderScript
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.ScriptIntrinsicBlur

class ProfileFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var uploadButton: Button
    private val REQUEST_CODE_PICK_IMAGE = 102

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        imageView = view.findViewById(R.id.image_profile)
        uploadButton = view.findViewById(R.id.button_upload)

        uploadButton.setOnClickListener {
            openGallery()
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == android.app.Activity.RESULT_OK && data != null) {
            val imageUri = data.data ?: return
            imageView.setImageURI(imageUri)
            enqueueBlurImageWork(imageUri)
        } else {
            Toast.makeText(context, "You haven't picked an image", Toast.LENGTH_LONG).show()
        }
    }

    private fun enqueueBlurImageWork(imageUri: Uri?) {
        imageUri?.let {
            val data = workDataOf("IMAGE_URI" to it.toString())
            val blurRequest = OneTimeWorkRequestBuilder<BlurImageWorker>()
                .setInputData(data)
                .build()

            WorkManager.getInstance(requireContext()).enqueue(blurRequest)
            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(blurRequest.id)
                .observe(viewLifecycleOwner, { workInfo ->
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val newUri = workInfo.outputData.getString("IMAGE_URI")
                        imageView.setImageURI(Uri.parse(newUri))
                    }
                })
        }
    }

    class BlurImageWorker(
        context: Context,
        workerParams: WorkerParameters
    ) : Worker(context, workerParams) {

        override fun doWork(): Result {
            val resourceUri = inputData.getString("IMAGE_URI") ?: return Result.failure()

            return try {
                val resolver = applicationContext.contentResolver
                val originalBitmap = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(resourceUri))

                // Apply blur using RenderScript
                val blurredBitmap = blurBitmap(originalBitmap, applicationContext)

                // Save the blurred bitmap back to a file or wherever needed
                val uri = saveImage(blurredBitmap)
                val outputData = workDataOf("IMAGE_URI" to uri.toString())

                Result.success(outputData)
            } catch (e: Exception) {
                Result.failure()
            }
        }

        private fun blurBitmap(bitmap: Bitmap, context: Context): Bitmap {
            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(rs, bitmap)
            val output = Allocation.createTyped(rs, input.type)
            val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            script.setRadius(25f)
            script.setInput(input)
            script.forEach(output)
            output.copyTo(bitmap)
            rs.destroy()
            return bitmap
        }

        private fun saveImage(bitmap: Bitmap): Uri {
            val imagesFolder = File(applicationContext.cacheDir, "images")
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs()
            }
            val file = File(imagesFolder, "blurred_image.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            return Uri.fromFile(file)
        }
    }
}
