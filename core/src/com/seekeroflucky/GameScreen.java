package com.seekeroflucky;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import static com.badlogic.gdx.Gdx.app;

public class GameScreen implements Screen {
    public static final int VIEWPORT_WIDTH = 300;
    public static final int VIEWPORT_HEIGHT = 168;
    public static final int LIFES = 10;
    private final BitmapFont font;
    private final Texture cointImage1;


    SpriteBatch batch;
    OrthographicCamera camera;
    Array<Rectangle> raindrops;
    long lastDropTime;
    Texture background;


    private Array<Texture> dropImageFruitsOn;
    private Array<Texture> dropImageFruitsOF;
    private boolean showDropImageFruitsOF;
    private int speed = 400;
    private long soruse;
    private int lifes;
    private float touchX;
    private float touchY;


    public GameScreen(Drop drop) {
        font = new BitmapFont();

        lifes = LIFES;
        // загрузка изображений для капли и ведра, 64x64 пикселей каждый
        cointImage1 = new Texture(Gdx.files.internal("alien2.png"));


        FileHandle dir = Gdx.files.internal("img");


        background = new Texture(Gdx.files.internal("background.png"));

        // загрузка звукового эффекта падающей капли и фоновой "музыки" дождя


        // сразу же воспроизводиться музыка для фона


        // создается камера и SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        batch = new SpriteBatch();

        // создает массив капель и возрождает первую
        raindrops = new Array<Rectangle>();

        spawnRaindrop();
    }


    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.y = MathUtils.random(0, VIEWPORT_WIDTH / 3);
        raindrop.x = VIEWPORT_HEIGHT / cointImage1.getHeight();
        raindrop.width = cointImage1.getWidth();
        raindrop.height = cointImage1.getHeight();
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }


    @Override
    public void dispose() {
        // высвобождение всех нативных ресурсов
        cointImage1.dispose();
        batch.dispose();
    }

    @Override
    public void show() {
        System.err.print("SHOW");

    }

    @Override
    public void render(float delta) {
        // очищаем экран темно-синим цветом.
        // Аргументы для glClearColor красный, зеленый
        // синий и альфа компонент в диапазоне [0,1]
        // цвета используемого для очистки экрана.
        batch.begin();
        batch.draw(background, 0, 0);

        font.setColor(Color.RED);
        font.getData().setScale(3);
        if (lifes > 0) {
            font.draw(batch, String.format("Speed: %s \n Points: %s " + "\n" + "Lives left: %s",speed, soruse, lifes ), VIEWPORT_WIDTH / 3, VIEWPORT_HEIGHT - 200);


            // сообщает камере, что нужно обновить матрицы
            camera.update();

            // сообщаем SpriteBatch о системе координат
            // визуализации указанной для камеры.
            batch.setProjectionMatrix(camera.combined);

            // начинаем новую серию, рисуем ведро и
            // все капли
            //batch.begin();


            drowCoints();
        } else {
            font.draw(batch, String.format(" GAME OVER! \n Speed: %s \n  Points: %s \n Tap anywhere to restart!",speed, soruse), VIEWPORT_WIDTH / 3, VIEWPORT_HEIGHT - 200);

        }
        batch.end();

        Vector3 temp = new Vector3(); // временный вектор для хранения входных координат
        mufeFruts(temp);


        if (Gdx.input.justTouched()) {
            if (lifes < 0) {
                soruse = 0;
                lifes = LIFES;
            }
        }
    }


    private void mufeFruts(Vector3 temp) {
        // движение капли, удаляем все капли выходящие за границы экрана
        // или те, что попали в ведро. Воспроизведение звукового эффекта
        // при попадании.
        Iterator<Rectangle> iter = raindrops.iterator();

        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.x += speed * Gdx.graphics.getDeltaTime();
            float v = raindrop.x + cointImage1.getWidth();
            System.out.print("raindrop.x +  dropImage1.getWidth() = " + v);
            app.log("MyTag", "raindrop.x +  dropImage1.getWidth() = " + v);
            if (v > 1030) {
                iter.remove();
                app.log("MyTag", " remove raindrop.x +  dropImage1.getWidth() = " + v);
                lifes--;
            }

            if (Gdx.input.justTouched()) {
                if (lifes < 0) {
                    soruse = 0;
                    lifes = LIFES;
                }
                temp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                // получаем координаты касания
                // относительно области просмотра нашей "камеры"
                camera.unproject(temp);
                touchX = temp.x;
                touchY = temp.y;
                if (handleimage(raindrop, touchX, touchY)) {
                    speed++;
                    lifes++;
                    soruse ++;

                    showDropImageFruitsOF = true;

                    iter.remove();

                }
            }
        }
    }

    private void drowCoints() {
        for (Rectangle raindrop : raindrops) {
            batch.draw(cointImage1, raindrop.x + 20, raindrop.y);

        }

        // проверка, нужно ли создавать новыюйк фрукт
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRaindrop();
        }
    }

    private static boolean handleimage(Rectangle raindrop, float touchX, float touchY) {

        // Проверяем, находятся ли координаты касания экрана
        if ((touchX >= raindrop.x) && touchX <= (raindrop.x + raindrop.width) && (touchY >= raindrop.y) && touchY <= (raindrop.y + raindrop.height)) {


            return true;
        }
        return false;
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }


}