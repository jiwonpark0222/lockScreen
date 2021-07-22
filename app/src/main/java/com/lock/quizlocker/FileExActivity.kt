package com.lock.quizlocker

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lock.quizlocker.databinding.ActivityFileExBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.jar.Manifest
import kotlin.properties.Delegates

class FileExActivity : AppCompatActivity() {

    private lateinit var fileBinding:ActivityFileExBinding

    // 데이터 저장에 사용할 파일이름름
    val filename = "data.txt"
    val MY_PERMISSION_REQUEST = 999
    var granted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileBinding = ActivityFileExBinding.inflate(layoutInflater)
        val view = fileBinding.root
        setContentView(view)

        val saveButton = fileBinding.saveButton
        val textField  = fileBinding.textField
        val loadButton = fileBinding.loadButton

        checkPermission()

        // 저장 버튼이 클린된 경우
        saveButton.setOnClickListener {
            // textField 의 현재 텍스트를 가져온다.
            val text = textField.text.toString()
            when{
                // 텍스트가 비어있는 경우 오류 메세지를 보여준다.
                TextUtils.isEmpty(text)->{
                    Toast.makeText(applicationContext,"텍스트가 비어있습니다",Toast.LENGTH_LONG).show()
                }
                !isExternalStorageWriteable()->{
                    Toast.makeText(applicationContext, "외부 저장장치가 없습니다", Toast.LENGTH_LONG).show()
                }
                else->{
                    // 내부 저장소 파일에 저장하는 함수 호출
//                    saveToInnerStorage(text, filename)
                    saveToExternalStorage(text, filename)
                }
            }
        }

        // 불러오기 버튼이 클릭된 경우
        loadButton.setOnClickListener {
            try {
//                textField.setText(loadFromInnerStorage(filename))
                textField.setText(loadFromExternalStorage(filename))
            }catch (e: FileNotFoundException){
                Toast.makeText(applicationContext, "저장된 텍스트가 없습니다.", Toast.LENGTH_LONG).show()
            }
        }


    }

    // 내부저장소 파일의 텍스트를 저장한다.
    private fun saveToInnerStorage(text: String, filename: String){

        // 내부 저장소의 전달된 파일이름의 파일 출력 스트림을 가져온다.
        val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)

        // 파일 출력 스트림에 text 를 바이트로 변환하여 write 한다
        fileOutputStream.write(text.toByteArray())

        // 파일 출력 스트림을 닫는다
        fileOutputStream.close()

    }

    // 내부저장소 파일의 텍스트를 불러온다
    private fun loadFromInnerStorage(filename: String): String{
        // 내부저장소의 전달된 파일이름의 파일 입력 스트림을 가져온다
        val fileInputStream = openFileInput(filename)

        // 파일의 저장된 내용을 읽어 String 형태로 불러온다.
        return fileInputStream.reader().readText()
    }

    private fun isExternalStorageWriteable(): Boolean {
        when{
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED -> return true
            else -> return false
        }
    }

    // 외부저장장치에서 앱 전용 데이터로 사용할 파일 객체를 반환하는 함수
    private fun getAppDataFileFromExternalStorage(filename: String): File {
        val dir = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        }else{
            File(Environment.getExternalStorageDirectory().absolutePath+"/Documents")
        }

        dir?.mkdirs()
        return File("${dir?.absolutePath}${File.separator}${filename}")

    }

    // 외부저장소 앱 전용 디렉토리에 파일로 저장하는 함수
    private fun saveToExternalStorage(text: String, filename: String){
        val fileOutputStream = FileOutputStream(getAppDataFileFromExternalStorage(filename))
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
    }

    // 외부저장소 앱 전용 디렉토리에서 파일 데이터를 불러오는 함수
    private fun loadFromExternalStorage(filename: String):String {
        return FileInputStream(getAppDataFileFromExternalStorage(filename)).reader().readText()
    }

    // 권한 체크및 요청 함수
    private fun checkPermission(){
        val permissionCheck = ContextCompat.checkSelfPermission(this@FileExActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        when{
            permissionCheck != PackageManager.PERMISSION_GRANTED -> {
                // 권한을 요청
                ActivityCompat.requestPermissions(this@FileExActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_PERMISSION_REQUEST -> {
                when {
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ->{
                        granted = true
                    }
                    else -> {
                        granted = false
                    }
                }
            }
        }
    }

    private fun saveToExternalCustomDirectory(text: String, filepath: String = "/sdcard/data.txt"){
        when{
            granted -> {
                val fileOutputStream = FileOutputStream(File(filepath))
                fileOutputStream.write(text.toByteArray())
                fileOutputStream.close()
            }
            else -> {
                Toast.makeText(applicationContext, "권한이 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadFromExternalCustomDirectory(filepath: String = "/sdcard/data.txt"): String {
        when{
            granted -> {
                return FileInputStream(File(filepath)).reader().readText()
            }
            else -> {
                Toast.makeText(applicationContext, "권한이 없습니다", Toast.LENGTH_LONG).show()
                return ""
            }
        }
    }

}