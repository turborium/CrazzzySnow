unit UnitMain;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, Forms, Controls, Graphics, Dialogs, ExtCtrls;

const
  SnowflakeCount = 400;

type
  // TSnowflake - структура снежинки
  TSnowflake = record
    X: Single;// координата X
    Y: Single;// координата Y
    Speed: Single;// скорость падения
    Size: Integer;// размер
    Time: Single;// локальное время
    TimeDelta: Single;// дельта изменения времени
  end;

  { TFormMain }

  TFormMain = class(TForm)
    TimerUpdate: TTimer;
    procedure FormCreate(Sender: TObject);
    procedure FormDestroy(Sender: TObject);
    procedure FormPaint(Sender: TObject);
    procedure TimerUpdateTimer(Sender: TObject);
  private
    // фоновое изображение
    BackgroundImage: TPortableNetworkGraphic;
    // массив снежинок
    Snow: array [0..SnowflakeCount - 1] of TSnowflake;
    // MakeSnowflake - создает новую снежинку
    function MakeSnowflake: TSnowflake;
    // MakeSnow - создает снежинки
    procedure MakeSnow;
    // UpdateSnow - обновление снежинок
    procedure UpdateSnow;
  public

  end;

var
  FormMain: TFormMain;

implementation

uses
  Math;

{$R *.lfm}

{ TFormMain }

procedure TFormMain.FormPaint(Sender: TObject);
var
  X, Y: Integer;
  Size: Integer;
  I: Integer;
  DeltaX, t: Single;
begin
  // заполняем форму черным цветом/фоновым изображением
  if BackgroundImage <> nil then
  begin
    Canvas.Draw(0, 0, BackgroundImage);
    Canvas.Brush.Color := clBlack;
    Canvas.Font.Color := clWhite;
    Canvas.TextOut(10, 10, 'Winter night by prusakov (www.deviantart.com/prusakov/)');
  end else
  begin
    Canvas.Brush.Color := clBlack;
    Canvas.FillRect(0, 0, ClientWidth, ClientHeight);
  end;

  // рисуем снежинку
  Canvas.Pen.Style := psClear;
  Canvas.Brush.Color := clWhite;

  for I := 0 to High(Snow) do
  begin
    // получаем размер
    Size := Snow[I].Size;

    t := Snow[I].Time;

    // вычисляем смещение
    DeltaX := Sin(t * 27) + Sin(t * 21.3) + 3 * Sin(t * 18.75) +
      7 * Sin(t * 7.6) + 10 * Sin(t * 5.23);

    DeltaX := DeltaX * 10;

    // получаем X
    X := Trunc(Snow[I].X + DeltaX);
    // получаем Y
    Y := Trunc(Snow[I].Y);

    // рисуем круг по координатам X, Y, с диаметром Size
    Canvas.Ellipse(X, Y, X + Size, Y + Size);
  end;
end;

procedure TFormMain.FormCreate(Sender: TObject);
const
  BackgroundResName = '_BACKGROUND';// <-- убрать "_" для отображения фона
begin
  // пытаемся загрузить картинку
  if FindResource(HInstance, BackgroundResName, PChar(10)) <> 0 then
  begin
    BackgroundImage := TPortableNetworkGraphic.Create;
    try
      BackgroundImage.LoadFromResourceName(HInstance, BackgroundResName);
      // отключаем изменение размера формы
      BorderStyle := bsSingle;
      // отключаем кнопку развернуть
      BorderIcons := BorderIcons - [biMaximize];
      // задаем ширину и высоту окна
      ClientWidth := BackgroundImage.Width;
      ClientHeight := BackgroundImage.Height;
    except
      // освобождаем BackgroundImage, если загрузка прошла с ошибкой
      FreeAndNil(BackgroundImage);
    end;
  end;
  // создаем снег
  MakeSnow;
end;

procedure TFormMain.FormDestroy(Sender: TObject);
begin
  BackgroundImage.Free;
end;

procedure TFormMain.TimerUpdateTimer(Sender: TObject);
begin
  // обновлаяем снег
  UpdateSnow;
  // просим систему перерисовать окно
  Invalidate;
end;

function TFormMain.MakeSnowflake: TSnowflake;
const
  MaxSpeed = 5;
  MaxSize = 9;
  Bounds = 30;
  MaxTimeDelta = 0.0015;
begin
  // задаем случайную координату по X
  Result.X := RandomRange(-Bounds, ClientWidth + Bounds);
  // обнуляем Y
  Result.Y := -MaxSize;
  // задаем случайную скорость падения
  Result.Speed := 0.5 + Random * MaxSpeed;
  // задаем случайный размер
  Result.Size := RandomRange(2, MaxSize);
  // задем время
  Result.Time := Random * Pi;
  // задаем величину приращивания времени
  Result.TimeDelta := Random * MaxTimeDelta;
end;

procedure TFormMain.MakeSnow;
var
  I: Integer;
begin
  for I := 0 to High(Snow) do
    Snow[I] := MakeSnowflake;
end;

procedure TFormMain.UpdateSnow;
var
  I: Integer;
begin
  for I := 0 to High(Snow) do
  begin
    // приращиваем координату по Y
    Snow[I].Y := Snow[I].Y + Snow[I].Speed;
    // увеличиваем время
    Snow[I].Time := Snow[I].Time + Snow[I].TimeDelta;
    // пересоздаем снежинку, если она упала за границы формы
    if Snow[I].Y > ClientHeight then
      Snow[I] := MakeSnowflake;
  end;
end;

end.

