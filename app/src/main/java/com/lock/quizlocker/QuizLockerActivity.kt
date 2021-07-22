package com.lock.quizlocker

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.lock.quizlocker.databinding.ActivityQuizLockerBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class QuizLockerActivity : AppCompatActivity() {

    private lateinit var quizBinding: ActivityQuizLockerBinding
    var quiz:JSONObject? = null

    // 정답횟수 저장 SharedPreference
    val wrongAnswerPref by lazy { getSharedPreferences("wrongAnswer", Context.MODE_PRIVATE) }
    val correctAnswerPref by lazy { getSharedPreferences("correctAnswer", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizLockerBinding.inflate(layoutInflater)
        val view = quizBinding.root
        val quizLabel = quizBinding.quizLabel
        val choice1 = quizBinding.choice1
        val choice2 = quizBinding.choice2
        val seekBar = quizBinding.seekBar
        val leftImageView = quizBinding.leftImageView
        val rightImageView = quizBinding.rightImageView
        val correctCountLabel = quizBinding.correctCountLabel
        val wrongCountLabel = quizBinding.wrongCountLabel


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            Log.d("QuizLockerActivity","이리옴니다.")

            setShowWhenLocked(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
            Log.d("QuizLockerActivity","다실행했습니다.")
        }else{
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)

            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(view)

        val json = assets.open("capital.json").reader().readText()
        val quizArray = JSONArray(json)

        quiz = quizArray.getJSONObject(Random().nextInt(quizArray.length()))
        quizLabel.text = quiz?.getString("question")
        choice1.text = quiz?.getString("choice1")
        choice2.text = quiz?.getString("choice2")

        // 정답횟수 오답횟수를 보여준다.
        val id = quiz?.getInt("id").toString() ?: ""
        correctCountLabel.text = "정답횟수: ${correctAnswerPref.getInt(id, 0)}"
        wrongCountLabel.text = "오답횟수: ${wrongAnswerPref.getInt(id, 0)}"


        //SeekBar 의 값이 변경될때 불리는 리스너
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when{
                    //SeekBar 의 우측끝으로 가면 choice2 를 선택한 것으로 간주한다
                    progress > 95 -> {
                        leftImageView.setImageResource(R.drawable.padlock)
                        rightImageView.setImageResource(R.drawable.unlock)
                    }
                    //SeekBar 의 좌측 끝으로 가면 choice1 를 선택한 것으로 간주한다
                    progress < 5 -> {
                        leftImageView.setImageResource(R.drawable.unlock)
                        rightImageView.setImageResource(R.drawable.padlock)
                    }
                    else ->{
                        leftImageView.setImageResource(R.drawable.padlock)
                        rightImageView.setImageResource(R.drawable.padlock)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            // 터치 조작을 끝낸 경우 불리는 함수
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress?:50
                when {
                    progress > 95 -> checkChoice(quiz?.getString("choice2")?:"",leftImageView,rightImageView,seekBar,correctCountLabel,wrongCountLabel)
                    progress < 5 -> checkChoice(quiz?.getString("choice1")?:"",leftImageView,rightImageView,seekBar,correctCountLabel,wrongCountLabel)
                }

            }

        })


    }

    private fun checkChoice(choice: String, leftImageView:ImageView, rightImageView: ImageView, seekBar: SeekBar?, correctAnswerLabel:TextView, wrongAnswerLabel:TextView){
        quiz?.let {
            when {
                choice == it.getString("answer") -> {
                    // 정답인 경우 정답횟수 증가
                    val id = it.getInt("id").toString()
                    var count = correctAnswerPref.getInt(id, 0)
                    count++
                    correctAnswerPref.edit().putInt(id, count).apply()
                    correctAnswerLabel.text = "정답횟수: ${count}"

                    finish()
                }
                else -> {

                    // 오답횟수 증가
                    val id = it.getInt("id").toString()
                    var count = wrongAnswerPref.getInt(id, 0)
                    count++
                    wrongAnswerPref.edit().putInt(id, count).apply()
                    wrongAnswerLabel.text = "오답횟수: ${count}"

                    // 정답이 아닌경우 UI 초기화
                    leftImageView.setImageResource(R.drawable.padlock)
                    rightImageView.setImageResource(R.drawable.padlock)
                    seekBar?.progress = 50

                    // 정답이 아닌경우 진동알림 추가
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                    if(Build.VERSION.SDK_INT >= 26){
                        vibrator.vibrate(VibrationEffect.createOneShot(1000,100))
                    }else{
                        vibrator.vibrate(1000)
                    }


                }
            }
        }
    }

}