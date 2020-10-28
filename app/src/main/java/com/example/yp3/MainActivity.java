package com.example.yp3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {//Реализовываем интерфейс OnClickListener

    private DrawingView drawView;//Экземпляр пользовательского представления,добавленного в макет
    private ImageButton currPaint;//Ещё одна переменная экземпляра для представления кнопки paint color в палитре:
    private float smallBrush, mediumBrush, largeBrush;//Добавляем в класс следующие переменные экземпляра для хранения значений трех измерений, определенных нами в прошлый раз
    private ImageButton drawBtn;
    private ImageButton eraseBtn;
    private ImageButton newBtn;
    private Canvas drawCanvas;
    private ImageButton saveBtn;
    private Object imgSaved;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = (DrawingView) findViewById(R.id.drawing);//Создаем экземпляр этой переменной, получив ссылку на нее из макета
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);//Сначала извлекаем линейный макет, в котором он содержится
        currPaint = (ImageButton) paintLayout.getChildAt(0);//Получаем первую кнопку и сохраняем ее как переменную экземпляра
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));//Используем другое рисоваемое изображение на кнопке, чтобы показать, что оно в данный момент выбрано
        smallBrush = getResources().getInteger(R.integer.small_size);//Экземпляр
        mediumBrush = getResources().getInteger(R.integer.medium_size);//Экземпляр
        largeBrush = getResources().getInteger(R.integer.large_size);//Экземпляр
        drawBtn = (ImageButton) findViewById(R.id.draw_btn);//Извлекаем ссылку на кнопку из макета
        drawBtn.setOnClickListener(this);//Устанавливаем класс в качестве прослушивателя щелчка для кнопки
        drawView.setBrushSize(mediumBrush);//Устанавливаем начальный размер кисти - средний
        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);//Извлекаем ссылку на кнопку
        eraseBtn.setOnClickListener(this);//Настраиваем класс для проверки щелчков
        newBtn = (ImageButton) findViewById(R.id.new_btn);//Создаем экземпляр с ссылкой на кнопку
        newBtn.setOnClickListener(this);//Настраиваем класс для проверки щелчков
        saveBtn = (ImageButton) findViewById(R.id.save_btn);//Создаем экземпляр
        saveBtn.setOnClickListener(this);//Настраиваем класс для проверки щелчков
    }

    public void paintClicked(View view) {//Позволяем пользователю выбирать цвета

        drawView.setErase(false);

        if (view != currPaint) {//Убеждаемся, что пользователь нажал на цвет краски, который не является выбранным в данный момент
            ImageButton imgView = (ImageButton) view;//Внутри if блока извлекаем тег, который мы установили для каждой кнопки в макете, представляющий выбранный цвет
            String color = view.getTag().toString();
            drawView.setColor(color);//Вызываем новый метод для объекта пользовательского вида чертежа:
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));//Теперь обновляем пользовательский интерфейс, чтобы отразить новую выбранную краску, и устанавливаем предыдущую обратно в нормальное состояние
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.draw_btn) {//Внутри метода проверяем наличие щелчков по кнопке рисования
            final Dialog brushDialog = new Dialog(this);//Когда пользователь нажимает на кнопку, мы выводим диалоговое окно, представляющее ему три размера кнопок.
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            //Мы устанавливаем размер с помощью методов, добавленных в пользовательский класс представления
            //Как только пользователь нажимает кнопку размера кисти, а затем немедленно закрываем диалоговое окно.
            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();//Завершаем раздел кнопки рисования отображением диалогового окна
        } else if (v.getId() == R.id.erase_btn) {//Добавляем условный оператор для кнопки в onClick после условного оператора для кнопки draw
            final Dialog brushDialog = new Dialog(this);//Мы позволим пользователю выбрать размер ластика из диалогового окна. Внутри условного блока для кнопки стирания создаем и подготавливаем диалоговое окно, как и раньше
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            //Создаем макет, такой же, как и диалоговое окно кнопки рисования, только с ластиком.
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();//Метод, чтобы установить размер кисти, как с помощью кнопки draw, на этот раз сначала установив флаг erase в true.
            drawView.setErase(false);//Когда пользователь нажимает кнопку рисования и выбирает размер кисти, нам нужно вернуться к рисованию, если они ранее были стерты.
            // В трех прослушивателях щелчков, добавленных для маленьких, средних и больших кнопок в разделе draw button onClick,
            // вызываем метод erase с параметром false - добавляем его в каждый onClick перед вызовом dismiss для объекта brushDialog
            drawView.setBrushSize(drawView.getLastBrushSize());//Устанавливаем размер кисти обратно на последний, используемый при рисовании, а не стирании

        } else if (v.getId() == R.id.new_btn) {//Создаем условный блок для новой кнопки
            //Убедимся, что пользователь определенно хочет начать новый чертеж
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    drawView.startNew();
                    dialog.dismiss();
                }
            });

            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if(v.getId()==R.id.save_btn){//Добавляем условное обозначение
            //Воспользуемся алгоритмом, аналогичным тому, который мы использовали для создания новых чертежей, чтобы проверить, хочет ли пользователь идти вперед и сохранять
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                public void onClick(DialogInterface dialog, int which){

                    drawView.setDrawingCacheEnabled(true);//Если пользователь решит продолжить и сохранить, нам нужно вывести отображаемое в данный момент представление в виде изображения. Включаем кэш чертежей на пользовательском представлении
                    String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),//Теперь попробуем записать изображение в файл
                            UUID.randomUUID().toString()+".png", "drawing");
                    //Мы используем метод insertImage, чтобы попытаться записать изображение в хранилище мультимедиа для изображений на устройстве, которое должно сохранить его в пользовательской галерее.
                    //Мы передаем решатель содержимого, кэш чертежей для отображаемого вида, случайно сгенерированную строку UUID для имени файла с расширением PNG и краткое описание.
                    //Метод возвращает URL-адрес созданного изображения или null, если операция была неудачной - это позволяет нам дать обратную связь пользователю:
                    if(imgSaved!=null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                }else{
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                    drawView.destroyDrawingCache();//Уничтожаем кэш чертежей, чтобы все сохраненные чертежи в будущем не использовали существующий кэш:
                }

                }
            });

            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });

            saveDialog.show();
            String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(), drawView.getDrawingCache(),
                    UUID.randomUUID().toString()+".png", "drawing");
        }
    }
}