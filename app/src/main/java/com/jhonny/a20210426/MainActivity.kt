package com.jhonny.a20210426

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.jhonny.a20210426.ml.Shapes
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.support.image.TensorImage


class MainActivity : AppCompatActivity() {
    fun classifyDrawing(bitmap : Bitmap) {
        val model = Shapes.newInstance(this)

        val image = TensorImage.fromBitmap(bitmap)
        /*
        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList
        */
        val outputs = model.process(image)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }.take(2)
        var Result:String = ""
        for (output in outputs) {
            when (output.label) {
                "circle" -> Result += "Circle"
                "square" -> Result += "Square"
                "star" -> Result += "Star"
                "triangle" -> Result += "Triangle"
            }
            Result += ": " + String.format("%.1f%%", output.score * 100.0f)+","
        }
        model.close()
        Toast.makeText(this, Result.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handv.setOnTouchListener(object:View.OnTouchListener{
            override fun onTouch(p0: View?, event: MotionEvent): Boolean {
                var xPos = event.getX()
                var yPos = event.getY()
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> handv.path.moveTo(xPos, yPos)
                    MotionEvent.ACTION_MOVE -> handv.path.lineTo(xPos, yPos)
                    MotionEvent.ACTION_UP -> {
                        val b = Bitmap.createBitmap(handv.measuredWidth, handv.measuredHeight,
                            Bitmap.Config.ARGB_8888)
                        val c = Canvas(b)
                        handv.draw(c)
                        classifyDrawing(b)
                    }
                }
                handv.invalidate()
                return true
            }
        })
        
        btn.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                handv.path.reset()
                handv.invalidate()
            }
        })
    }

}