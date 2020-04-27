package mx.edu.ittepic.ladm_u3_practica1_arturolarios.Utils

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.Toast
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor

class Utils
{
    companion object
    {
        fun toastMessageLong(message : String, context : Context)
        {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun toastMessageShort(message : String, context : Context)
        {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun alertMessage(message : String, title : String, context: Context)
        {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK"){_,_->}
                .show()
        }

        fun getByteArray(bitMap : Bitmap) : ByteArray
        {
            val baos = ByteArrayOutputStream(20480)
            bitMap.compress(Bitmap.CompressFormat.PNG, 0, baos)
            return baos.toByteArray()
        }

        fun getBitMap(blob : ByteArray?) : Bitmap?
        {
            val bais = ByteArrayInputStream(blob)
            return BitmapFactory.decodeStream(bais)
        }

        fun getBitMap(uri : Uri?, contentResolver : ContentResolver) : Bitmap
        {
            var parcelFileDescriptor: ParcelFileDescriptor?= null

            uri?.let {
                parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            }

            val fileDescriptor: FileDescriptor?= parcelFileDescriptor?.fileDescriptor
            val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
            return image
        }
    }
}