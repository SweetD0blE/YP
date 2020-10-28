package com.example.yp3;

import android.graphics.Color;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

public class DrawingView extends View { //Создаем класс DrawingView
    public DrawingView(Context context, AttributeSet attrs) {//Добавляем метод конструктора в класс
        super(context, attrs);
        setupDrawing();
    }

    private Path drawPath;//Путь рисования
    private Paint drawPaint, canvasPaint;//Рисование и холст
    private int paintColor =0xFF660000;//Первоначальный цвет
    private Canvas drawCanvas;//Холст
    private Bitmap canvasBitmap;//Растровое изображения холста
    private float brushSize, lastBrushSize;//Добавляем в класс две переменных экземпляра
    private boolean erase=false;//Добавим стирание в приложение. В пользовательском классе чертежного вида добавим логическую переменную экземпляра, которая будет действовать как флаг для того, стирает ли пользователь в данный момент или нет
    private void setupDrawing() {//Экземпляр пользовательского представления.Вспомогательный метод

    drawPath = new Path();//Экземпляры переменных, для настройки класса для рисования
    drawPaint = new Paint();//Экземпляры переменных, для настройки класса для рисования
    drawPaint.setColor(paintColor);//Начальный цвет
        //Начальные св-ва пути
    drawPaint.setAntiAlias(true);
    drawPaint.setStrokeWidth(20);
    drawPaint.setStyle(Paint.Style.STROKE);
    drawPaint.setStrokeJoin(Paint.Join.ROUND);
    drawPaint.setStrokeCap(Paint.Cap.ROUND);
    canvasPaint = new Paint(Paint.DITHER_FLAG);//Создание экземпляра объекта холста
    brushSize = getResources().getInteger(R.integer.medium_size);//Начальный размер кисти - средний
    lastBrushSize = brushSize;
    drawPaint.setStrokeWidth(brushSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//Метод, который будет вызван, когда пользовательскому представлению будет присвоен размер
        super.onSizeChanged(w, h, oldw, oldh);//Метод суперкласса
        canvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);//Экземпляр холста чертежа и растрового изображения, исп. значения ширины и высоты
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {//Метод, чтобы позволить классу функционировать как пользовательский вид чертежа
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);//Холст и путь рисования
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//Когда чертежный вид находится на экране приложения, мы хотим, чтобы прикосновения пользователя к нему регистрировались как операции рисования.
        float touchX= event.getX();//Касания пользователя по X и Y
        float touchY = event.getY();
        switch (event.getAction()){//Действия, которые мы заинтересованы в реализации чертежа down, move и up есть. Добавляем оператор switch в метод, чтобы ответить на каждый из них
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();//После оператора switch завершаем метод, сделав представление недействительным и вернув истинное значение:
        return true;
    }

    public void setColor(String newColor){//Создаем пользовательский класс представления для установки цвета.
        invalidate();//Начинаем с аннулирования представления
        paintColor = Color.parseColor(newColor);//Устанавливаем цвет для рисования
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize){//Добавляем в класс следующий метод для установки размера кисти
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());//Внутри метода обновляем размер кисти с помощью переданного значения
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }
    //Мы будем передавать значение из файла измерений при вызове этого метода, поэтому нам нужно вычислить его значение измерения.
    // Мы обновляем переменную и объект Paint, чтобы использовать новый размер.
    // Теперь добавляем методы, чтобы получить и установить другую переменную размера, которую мы создали
    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }

    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setErase(boolean isErase){//Изначально будем считать, что пользователь рисует, а не стирает. Добавьте в класс следующий метод
        erase=isErase;//Обновляем переменную флага
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));//Теперь изменяем объект краски, чтобы стереть или переключиться обратно на рисование
        else drawPaint.setXfermode(null);
    }

    public void startNew() {//Добавляем метод для запуска нового чертежа. Этот метод просто очищает холст и обновляет дисплей
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
