using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Runtime.CompilerServices;

namespace HW2
{
    /// <summary>
    /// Класс для точки, реализующий спецификацию и
    /// дополнительные методы для работы с точками.
    /// </summary>
    public class Point : IComparable<Point>
    {
        private int x, y;

        public int X { get => x; }
        public int Y { get => y; }

        public Point(int x_, int y_)
        {
            x = x_;
            y = y_;
        }

        /// <summary>
        /// Метод сравнения двух точек для поиска
        /// самой левой нижней точки в начале работы
        /// алгоритма.
        /// </summary>
        /// <param name="other"></param>
        /// <returns></returns>
        public int CompareTo(Point other)
        {
            if (this.Y == other.Y)
            {
                return this.X.CompareTo(other.X);
            }
            return this.Y.CompareTo(other.Y);
        }

        /// <summary>
        /// Метод для вычисления полярного угла между
        /// точкой передаваемой в параметрах и данной точкой.
        /// </summary>
        /// <param name="center"></param>
        /// <returns></returns>
        public double PolarAngleCC(Point center)
        {
            double res = Math.Atan2(Y - center.Y, X - center.X);

            if (res < 0) res += 2 * Math.PI;

            return res;
        }

        /// <summary>
        /// Метод для вычисления расстояния между точкой
        /// передаваемой в параметрах и данной точкой.
        /// </summary>
        /// <param name="other"></param>
        /// <returns></returns>
        public double Distance(Point other)
        {
            return Math.Sqrt((other.X - X) * (other.X - X) + (other.Y - Y) * (other.Y - Y));
        }

        /// <summary>
        /// Статический метод, определяющий, является ли поворот
        /// между тремя передаваемыми точками левым.
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <param name="c"></param>
        /// <returns></returns>
        public static bool LeftTurnCC(Point a, Point b, Point c)
        {
            return (b.X - a.X) * (c.Y - b.Y) - (b.Y - a.Y) * (c.X - b.X) > 0;
        }

        public override string ToString()
        {
            return $"{X} {Y}";
        }
    }

    /// <summary>
    /// Реализация стака для точек с требованиями из задания
    /// и также методами для удобства работы с ним.
    /// </summary>
    public class Stack
    {
        private Point[] points;
        private int size = 0;
        private int capacity;

        // Классическое настоящее ограничение на массивы according to stackoverflow.
        public const int MAX_SIZE = int.MaxValue;

        public Stack(int capacity_)
        {
            capacity = capacity_ < MAX_SIZE ? capacity_ : MAX_SIZE;
            points = new Point[capacity_];
        }

        public int Size() { return size; }

        public bool IsEmpty() { return size == 0; }

        public void Push(Point point)
        {
            if (Size() == capacity)
            {
                throw new ArgumentException("Stack if full");
            }

            points[size++] = point;
        }

        public Point Pop()
        {
            if (IsEmpty())
            {
                throw new ArgumentException("No elements in stack");
            }

            return points[--size];
        }

        public Point Top()
        {
            if (IsEmpty())
            {
                throw new ArgumentException("No elements in stack");
            }
            return points[Size() - 1];
        }

        public Point NextToTop()
        {
            if (Size() <= 1)
            {
                throw new ArgumentException("Only one element in stack");
            }
            return points[Size() - 2];
        }

        public override string ToString()
        {
            string output = "";
            for (int i = 0; i < Size(); i++)
            {
                output += points[i].ToString() + "\n";
            }
            return output;
        }

        /// <summary>
        /// Метод для текстового представления массива в 
        /// режиме well known text.
        /// </summary>
        /// <returns></returns>
        public string WellKnown()
        {
            string output = "";
            for (int i = 0; i < Size(); i++)
            {
                output += points[i].ToString() + ", ";
            }

            output += points[0].ToString();

            return output;
        }

        /// <summary>
        /// Метод для "переворачивания" стака, чтобы он
        /// описывал результат при обходе по часовой стрелке.
        /// Может показаться, что это костыльно, но это эффективнее,
        /// чем дублировать код алгоритма с изменением в направлении.
        /// </summary>
        /// <param name="ccStack"></param>
        /// <returns></returns>
        public static Stack MakeCW(Stack ccStack)
        {
            Stack cwStack = new Stack(ccStack.Size());

            int size = ccStack.Size();

            cwStack.Push(ccStack.points[0]);

            for (int i = 1; i < size; i++)
            {
                cwStack.Push(ccStack.Pop());
            }

            return cwStack;
        }
    }

    /// <summary>
    /// Класс программы, в полях сохраняются ключевые переменные.
    /// </summary>
    class Program
    {
        // Дефолтные значения заданы для отладки и тестирования.
        static string order = "cw";//"cc";
        static string format = "plain";//"wkt"
        static string input = "test0.txt";
        static string output = "answer0.txt";
        static string inputData = "";
        static Point minPoint = new Point(int.MaxValue, int.MaxValue);
        static Stack pointStack;

        /// <summary>
        /// В методе происходит считывание и парсинг входных данных,
        /// вызов метода сортировки и алгоритма и вызов метода вывода данных.
        /// </summary>
        /// <param name="args"></param>
        static void Main(string[] args)
        {
            if (args.Length == 4)
            {
                order = args[0];
                format = args[1];
                input = args[2];
                output = args[3];
            }
            else
            {
                Console.WriteLine("debugging....");
            }

            Console.WriteLine("order " + order + " format " + format);

            // Считывание данных.
            string[] data = File.ReadAllLines(input);
            int numberOfPoints = int.Parse(data[0]);

            Point[] points = new Point[numberOfPoints];

            for (int i = 0; i < numberOfPoints; i++)
            {
                points[i] = new Point(int.Parse(data[i + 1].Split(' ')[0]),
                    int.Parse(data[i + 1].Split(' ')[1]));
                inputData += $"({points[i].ToString()}), ";
            }

            // Вызов метода алгоритма.
            Graham(points);

            // Вызов метода вывода в зависимости от формата.
            if (format == "wkt")
            {
                WellKnownText();
            }
            else
            {
                Plain();
            }
        }

        /// <summary>
        /// Метод, где происходит сортировка, удаление лишних
        /// точек и непосредственно ход алгоритма Грехема.
        /// </summary>
        /// <param name="points"></param>
        static void Graham(Point[] points)
        {
            //здесь мог быть красивый линк. но вы запретили линк. 
            //спасибо за то что запретили линк
            //minPoint = points.Min();

            for (int i = 0; i < points.Length; i++)
            {
                if (minPoint.CompareTo(points[i])>0)
                {
                    minPoint = points[i];
                }
            }

            // Сортировка точек по полярному углу относительно найденной минимальной точки.
            Array.Sort(points, delegate (Point a, Point b)
            {
                if (a.PolarAngleCC(minPoint) == b.PolarAngleCC(minPoint))
                {
                    return a.Distance(minPoint).CompareTo(b.Distance(minPoint));
                }
                return a.PolarAngleCC(minPoint).CompareTo(b.PolarAngleCC(minPoint));
            });

            List<Point> distinctPoints = new List<Point>();

            distinctPoints.Add(minPoint);
            distinctPoints.Add(points[1]);

            // Удаление точек, для которых полярный угол дублирует уже существующий и расстояние меньше.
            for (int i = 2; i < points.Length; i++)
            {
                if (points[i].PolarAngleCC(minPoint) != distinctPoints[distinctPoints.Count-1].PolarAngleCC(minPoint))
                {
                    distinctPoints.Add(points[i]);
                }
                else
                {
                    distinctPoints[distinctPoints.Count - 1] = points[i];
                }
            }

            pointStack = new Stack(distinctPoints.Count);

            pointStack.Push(minPoint);
            pointStack.Push(distinctPoints[1]);

            foreach ( var el in distinctPoints)
                {
                    Console.WriteLine(el.ToString());
                }

            // Проход алгоритма с добавлением в стак только тех точек, которые не образуют левый поворот.
            for (int i = 2; i < distinctPoints.Count; i++)
            {
                while (pointStack.Size() > 1 &&
                    !Point.LeftTurnCC(pointStack.NextToTop(), pointStack.Top(), distinctPoints[i]))
                {
                    pointStack.Pop();
                }
                pointStack.Push(distinctPoints[i]);
            }

            if (order == "cw")
            {
                pointStack = Stack.MakeCW(pointStack);
            }
        }

        /// <summary>
        /// Метод для форматирования и вывода результата в соответствующем формате.
        /// </summary>
        static void WellKnownText()
        {
            string outData = "MULTIPOINT (" + inputData.Trim(' ').Trim(',') +
                ")\nPOLYGON ((" + pointStack.WellKnown() + "))";

            Console.WriteLine(outData);
            File.WriteAllText(output, outData);
        }

        /// <summary>
        /// Метод для форматирования и вывода результата в соответствующем формате.
        /// </summary>
        static void Plain()
        {
            string outData = pointStack.Size() + "\n" + pointStack.ToString();

            Console.WriteLine(outData);
            File.WriteAllText(output, outData);
        }
    }
}
