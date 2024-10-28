package com.example.pomodorotimer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    var pomodoroTimeInMillis: Long = 300000  //5 min
    var restTimeInMillis: Long = 1500000 //25 min
    var timeLeftInMillis: Long = pomodoroTimeInMillis
    var timerRunning = false
    var isPomodoro = true //Pomodoro or rest

    var countDownTimer: CountDownTimer? = null
    var mediaPlayer: MediaPlayer? = null // MediaPlayer for playing sound

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timerTextView = findViewById<TextView>(R.id.timer_text)
        val startPauseButton = findViewById<Button>(R.id.start_pause_button)
        val resetButton = findViewById<Button>(R.id.reset_button)
        val pomodoroInput = findViewById<EditText>(R.id.pomodoro_input)
        val restInput = findViewById<EditText>(R.id.rest_input)

        // timer text when start
        timerTextView.text = updateTimerText(timeLeftInMillis)

        // Start or pause button
        startPauseButton.setOnClickListener {
            if (timerRunning) {
                pauseTimer()
            } else {
                // customisable work or rest time,
                val pomodoroMinutes = pomodoroInput.text.toString().toDoubleOrNull()
                val restMinutes = restInput.text.toString().toDoubleOrNull()

                // change to custom time
                if (pomodoroMinutes != null) {
                    if (pomodoroMinutes >0) {
                        pomodoroTimeInMillis = (pomodoroMinutes * 60 * 1000).toLong()
                    }
                }
                if (restMinutes != null) {
                    if (restMinutes > 0) {
                        restTimeInMillis = (restMinutes * 60 * 1000).toLong()
                    }
                }

                // what to show to timer when rest or work
                if(isPomodoro){
                    timeLeftInMillis = pomodoroTimeInMillis
                }
                else{
                    timeLeftInMillis = restTimeInMillis
                }
                timeLeftInMillis = if (isPomodoro) pomodoroTimeInMillis else restTimeInMillis
                startTimer(timerTextView, startPauseButton)
            }
        }

        // Resetting timer
        resetButton.setOnClickListener {
            resetTimer(timerTextView, startPauseButton)
        }
    }

    // Update the timer txt on tthe screen
    private fun updateTimerText(timeInMillis: Long): String {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        return "$minutes:${if (seconds < 10) "0$seconds" else seconds}" // Simple formatting
    }

    // for starting the timer
    private fun startTimer(timerTextView: TextView, startPauseButton: Button) {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                timerTextView.text = updateTimerText(timeLeftInMillis)
            }

            override fun onFinish() {
                playSound() // Play sound when timer finished countdown
                timerRunning = false
                if (isPomodoro) {
                    isPomodoro = false
                    timeLeftInMillis = restTimeInMillis
                    startPauseButton.text = "Start Rest"
                } else {
                    isPomodoro = true
                    timeLeftInMillis = pomodoroTimeInMillis
                    startPauseButton.text = "Start Pomodoro"
                }
            }
        }.start()

        timerRunning = true
        startPauseButton.text = "Pause"
    }

    // Pausing the timer countdown
    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
    }

    private fun resetTimer(timerTextView: TextView, startPauseButton: Button) {
        timeLeftInMillis = if (isPomodoro) pomodoroTimeInMillis else restTimeInMillis
        timerTextView.text = updateTimerText(timeLeftInMillis)
        countDownTimer?.cancel()
        timerRunning = false
        startPauseButton.text = if (isPomodoro) "Start Pomodoro" else "Start Rest"
    }

    // Play sound when timer finishes
    private fun playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sound) // Sound file placed in res/raw/ding.mp3
        mediaPlayer?.start() // Play the sound
        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release() // Release the media player after playback
            mediaPlayer = null
        }
    }
}
