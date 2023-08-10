from tkinter import *
from tkinter import ttk
from random import randrange, uniform
from math import sin, pi

# задаем ширину и высоту окна
ClientWidth  = 720
ClientHeight = 480

# число снежинок на полотне
SnowflakeCount = 400

# массив снежинок
SnowflakeArray = []

for i in range(0, SnowflakeCount):
    SnowflakeArray.append("")

class TSnowflake:
    #TSnowflake - структура снежинки
    def __init__(self, x, y, speed, size, id, time, TimeDelta):
        self.x = x                  # координата X
        self.y = y                  # координата Y
        self.speed = speed          # скорость падения
        self.size = size            # размер
        self.id = id                # идентификатор объекта
        self.time = time            # локальное время
        self.TimeDelta = TimeDelta  # дельта изменения времени

def MakeSnowflake():
    """MakeSnowflake - создает новую снежинку"""
    MaxSpeed = 5
    MaxSize  = 9
    Bounds = 30
    MaxTimeDelta = 0.0015 
    
    # задаем случайную координату по X
    x = randrange(-Bounds, ClientWidth + Bounds)
    
    # обнуляем Y
    y = -MaxSize
    
    # задаем случайную скорость падения
    speed = uniform(0.5, 5.5)
    
    #задаем случайный размер
    size = randrange(2, 9)
    
    # задем время 
    time = uniform(0, 2 * pi)
    
    # задаем величину приращивания времени  
    TimeDelta = uniform(0, MaxTimeDelta)
    
    # рисуем круг по координатам X, Y, с диаметром Size
    # outline можно заменить на "blue", или любой другой цвет для получения красивых границ снежинки
    id = Canvas.create_oval(x, y, x+size, y+size, fill="white", outline='white')
    
    return TSnowflake(x, y, speed, size, id, time, TimeDelta)
   
def UpdateSnow():
    """UpdateSnow - обновление снежинок"""
    for i in range(0, SnowflakeCount):
        speed = SnowflakeArray[i].speed
        id = SnowflakeArray[i].id
        coords = Canvas.coords(id)
        
        # получаем X
        X = SnowflakeArray[i].x = coords[0]
        # получаем Y
        SnowflakeArray[i].y = coords[1]
        
        # увеличиваем время
        time = SnowflakeArray[i].time + SnowflakeArray[i].TimeDelta
        
        # вычисляем смещение
        DeltaX = (sin(time * 27) + sin(time * 21.3) + 3 * sin(time * 18.75)
        + 7 * sin(time * 7.6) + 10 * sin(time * 5.23))
        
        # приращиваем координату по Y 
        Y = SnowflakeArray[i].y + speed
        # приращиваем координату по X
        X = X + DeltaX * 0.15
        
        Canvas.moveto(id, X, Y) 
        
        # пересоздаем снежинку, если она упала за границы формы
        if SnowflakeArray[i].y > ClientHeight:
            Canvas.delete(SnowflakeArray[i].id) # удаляем объект снежинки для предотвращения утечки памяти
            SnowflakeArray[i] = MakeSnowflake()
              
def MakeSnow():
    """MakeSnow - создает снежинки"""
    for i in range(0, SnowflakeCount):
        SnowflakeArray[i] = MakeSnowflake()

def Invalidate():
    UpdateSnow()

    Canvas.after(33, Invalidate)
        
# создаём и заполняем форму

if __name__ == "__main__":
    FormMain = Tk()
    FormMain.title("DanielSnow")
    FormMain.resizable(False, False)
    
    Canvas = Canvas(FormMain, 
                    width=ClientWidth, 
                    height=ClientHeight,
                    background="black")
    
    Canvas.grid()

    # Раскомментируйте для отрисовки изображения.
    
    # background = PhotoImage(file="background.png")
    # Canvas.create_image(1, 1, image=background, anchor="nw")
    # Canvas.create_text(235, 10, text="Winter night by prusakov (www.deviantart.com/prusakov/)", fill="red")
    
    MakeSnow()                
    Invalidate()

    FormMain.mainloop()