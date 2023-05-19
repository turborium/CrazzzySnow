package sergeysnow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import static sergeysnow.SergeySnow.snow;

public class SergeySnow {

    public static int clientHeight, clientWidth;
    private static final int SNOWFLAKECOUNT = 300;
    public static snowflake[] snow;                // массив снежинок
    public static Image backgroundImage;           // фоновое изображение
    
    public static void main(String[] args) {
        // Создаем окно
        JFrame f = new JFrame("SergeySnow ver 1.00");
        f.setResizable(false);
        // Создаем компонент, нп котором будем рисовать
        JComponent c = new DrawArea();
        
        // Загрузка фонового изображения из файла
        String fileName = "image.jpg";
        try {
            backgroundImage = ImageIO.read(new File(fileName));
            c.setPreferredSize(new Dimension(backgroundImage.getWidth(null), backgroundImage.getHeight(null)));
            SergeySnow.clientWidth = backgroundImage.getWidth(null);
            SergeySnow.clientHeight = backgroundImage.getHeight(null);
        }
        // Изображения нет на дмске
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Background image read error!");
            c.setPreferredSize(new Dimension(640, 360));
            SergeySnow.clientWidth = 640;
            SergeySnow.clientHeight = 360;
        }
        f.add(c);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        makeSnow();
        
        // Поток для перерисовки снежинок
        Thread DrawEngine = new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(33);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // обновлаяем массив снег
                    updateSnow();
                    // просим систему перерисовать окно
                    c.repaint();
                }
            }
        };
        // Устанавливаем приоретет потока
        DrawEngine.setPriority(DrawEngine.getPriority()+3);
        // Запускаем поток
        DrawEngine.start();
        
        // Отбражаем форму
        f.setVisible(true);
    }
    
    // MakeSnow - создает снежинки
    private static void makeSnow() {
        snow = new snowflake[SNOWFLAKECOUNT];
        for(int i = 0; i < snow.length; i++) {
            snow[i] = new snowflake();
        }
    }
    
    // UpdateSnow - обновление снежинок
    private static void updateSnow() {
        for(int i = 0; i < snow.length; i++) {
            // приращиваем координату по Y
            snow[i].y += snow[i].speed;
            // пересоздаем снежинку, если она упала за границы формы
            if (SergeySnow.clientHeight < snow[i].y) {
                snow[i] = new snowflake();
            }
            // увеличиваем время
            snow[i].time += snow[i].timeDelta;
            
            // вычисляем смещение
            final double T = snow[i].time;
            final double DELTAX = (Math.sin(T * 27) +
                                   Math.sin(T * 21.3) +
                                   3 * Math.sin(T * 18.75) +
                                   7 * Math.sin(T * 7.6) +
                                   10 * Math.sin(T * 5.23)) * 10;
            snow[i].dX = (int)(snow[i].x + DELTAX);
        }
    }
    
}

// snowflake - структура снежинки
class snowflake {
    
    double x;         // координата X
    double y;         // координата Y
    int dX;           // смещение по X
    double speed;     // скорость падения
    int size;         // размер
    double time;      // локальное время
    double timeDelta; // дельта изменения времени
    
    // snowflake() - создает новую снежинку
    snowflake() {
        final int MAXSPEED = 5;
        final int MAXSIZE = 9;
        final int BOUNDS = 30;
        final double MAXTIMEDELTA = 0.0015;
        // задаем случайную координату по X
        this.x = getRandomNumber(-BOUNDS, SergeySnow.clientWidth + BOUNDS);
        // обнуляем Y
        this.y = -MAXSIZE;
        // задаем случайную скорость падения
        this.speed = (1 + Math.random() * MAXSPEED);
        // задаем случайный размер
        this.size = getRandomNumber(2, MAXSIZE);
        // задем время
        this.time = Math.random() * 2 * Math.PI;
        // задаем величину приращивания времени
        this.timeDelta = Math.random() * MAXTIMEDELTA;
    }
    
    // Получение случайного чила в диапазоне от min до max
    private static int getRandomNumber(int min, int max) {
        return (int)((Math.random() * (max - min)) + min);
    }
    
}

class DrawArea extends JComponent{
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        // Улучшение качества графики
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Либо загрузка изображения
        if (SergeySnow.backgroundImage != null) {
            g2.drawImage(SergeySnow.backgroundImage, 0, 0, null);
        }
        else {
            // Либо чёрный прямоугольник
            g2.setPaint(Color.BLACK);
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, SergeySnow.clientWidth, SergeySnow.clientHeight);
        }
        
        // Прорисовка снега
        for (var eachSnowflake : SergeySnow.snow) {
            g2.setPaint(Color.WHITE);
            // рисуем круг по координатам dX, Y, с диаметром Size
            g2.fill(new Ellipse2D.Double(eachSnowflake.dX, (int)eachSnowflake.y, eachSnowflake.size, eachSnowflake.size));
        }
    }

}