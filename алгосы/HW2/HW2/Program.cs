using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Runtime.CompilerServices;

namespace HW2
{
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

        public int CompareTo(Point other)
        {
            if (this.Y == other.Y)
            {
                return this.X.CompareTo(other.X);
            }
            return this.Y.CompareTo(other.Y);
        }

        public double PolarAngleCC(Point center)
        {
            double res = Math.Atan2(Y - center.Y, X - center.X);

            if (res < 0) res += 2 * Math.PI;

            return res;
        }

        public double Distance(Point other)
        {
            return Math.Sqrt((other.X - X) * (other.X - X) + (other.Y - Y) * (other.Y - Y));
        }

        public override string ToString()
        {
            return $"{X} {Y}";
        }

        public static bool LeftTurnCC(Point a, Point b, Point c)
        {
            return (b.X - a.X) * (c.Y - b.Y) - (b.Y - a.Y) * (c.X - b.X) > 0;
        }
    }

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

        public string WellKnown()
        {
            string output = "";
            for (int i = 0; i < Size(); i++)
            {
                output += points[i].ToString() + ", ";
            }

            output +=points[0].ToString();

            return output;
        }

        public static Stack MakeCW(Stack ccStack) {
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

    class Program
    {
        static string order = "cw";//"cc";
        static string format = "plain";//"wkt"
        static string input = "test0.txt";
        static string output = "answer0.txt";
        static string inputData = "";
        static Point minPoint;
        static Stack pointStack;


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

            Console.WriteLine("order "+order+" format "+format);

            string[] data = File.ReadAllLines(input);
            int numberOfPoints = int.Parse(data[0]);

            Point[] points = new Point[numberOfPoints];

            for (int i = 0; i < numberOfPoints; i++)
            {
                points[i] = new Point(int.Parse(data[i + 1].Split(' ')[0]),
                    int.Parse(data[i + 1].Split(' ')[1]));
                inputData += $"({points[i].ToString()}), ";
            }

            Graham(points);

            if (format == "wkt")
            {
                WellKnownText();
            }
            else
            {
                Plain();
            }
        }

        static void Graham(Point[] points)
        {
            minPoint = points.Min();

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

            for (int i = 2; i < points.Length; i++)
            {
                if (points[i].PolarAngleCC(minPoint) != distinctPoints.Last().PolarAngleCC(minPoint))
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

        static void WellKnownText()
        {
            string outData = "MULTIPOINT (" + inputData.Trim(' ').Trim(',') +
                ")\nPOLYGON ((" + pointStack.WellKnown()+"))" ;

            Console.WriteLine(outData);
            File.WriteAllText(output, outData);
        }

        static void Plain()
        {
            string outData = pointStack.Size() + "\n" + pointStack.ToString();

            Console.WriteLine(outData);
            File.WriteAllText(output, outData);
        }
    }
}
