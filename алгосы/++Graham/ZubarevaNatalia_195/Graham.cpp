#define _USE_MATH_DEFINES
#include <iostream>
#include <math.h>
#include <vector>
#include <exception>
#include <fstream>
#include <algorithm>

// Дефолтные значения заданы для отладки и тестирования.
std::string order = "cw";//"cc";
std::string format = "plain";//"wkt"
std::string input = "test0.txt";
std::string output = "answer0.txt";
std::string inputData = "";


namespace HW {

	class Point
	{
	private:
		int x, y;
	public:
		int X() const { return x; }
		int Y() const { return y; }

		// Конструктор без параметров для того чтобы оно не ломалось.
		Point() {}

		Point(int x_, int y_) :x(x_), y(y_) {}

		/// <summary>
		/// Метод сравнения двух точек для поиска
		/// самой левой нижней точки в начале работы
		/// алгоритма.
		/// </summary>
		bool CompareTo(Point other)
		{
			if (Y() == other.Y())
			{
				return X() < other.X();
			}
			return Y() < other.Y();
		}

		/// <summary>
		/// Метод для вычисления полярного угла между
		/// точкой передаваемой в параметрах и данной точкой.
		/// </summary>
		double PolarAngleCC(Point center)
		{
			double res = std::atan2(Y() - center.Y(), X() - center.X());

			if (res < 0) res += 2 * M_PI;

			return res;
		}

		/// <summary>
		/// Метод для вычисления расстояния между точкой
		/// передаваемой в параметрах и данной точкой.
		/// </summary>
		double Distance(Point other)
		{
			return std::sqrt((other.X() - X()) * (other.X() - X()) + (other.Y() - Y()) * (other.Y() - Y()));
		}

		/// <summary>
		/// Статический метод, определяющий, является ли поворот
		/// между тремя передаваемыми точками левым.
		/// </summary>
		static bool LeftTurnCC(Point a, Point b, Point c)
		{
			return (b.X() - a.X()) * (c.Y() - b.Y()) - (b.Y() - a.Y()) * (c.X() - b.X()) > 0;
		}

		std::string ToString()
		{
			return std::to_string(X()) + " " + std::to_string(Y());
		}
	};

	class Stack
	{
	private:
		int size = 0;
		int capacity;

		std::vector<Point> points;
	public:
		// Классическое настоящее ограничение на массивы according to stackoverflow.
		static constexpr int MAX_SIZE = INT32_MAX;

		Stack() {}

		Stack(int capacity_)
		{
			capacity = capacity_ < MAX_SIZE ? capacity_ : MAX_SIZE;
			points = std::vector<Point>(capacity_);
		}

		int Size() { return size; }

		bool IsEmpty() { return size == 0; }

		void Push(Point point)
		{
			if (Size() == capacity)
			{
				throw std::out_of_range("Stack if full");
			}

			points[size++] = point;
		}

		Point Pop()
		{
			if (IsEmpty())
			{
				throw std::out_of_range("No elements in stack");
			}

			return points[--size];
		}

		Point Top()
		{
			if (IsEmpty())
			{
				throw std::out_of_range("No elements in stack");
			}
			return points[Size() - 1];
		}

		Point NextToTop()
		{
			if (Size() <= 1)
			{
				throw std::out_of_range("Only one element in stack");
			}
			return points[Size() - 2];
		}

		std::string ToString()
		{
			std::string output = "";
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
		std::string WellKnown()
		{
			std::string output = "";
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
		static Stack MakeCW(Stack ccStack)
		{
			Stack cwStack = Stack(ccStack.Size());

			int size = ccStack.Size();

			cwStack.Push(ccStack.points[0]);

			for (int i = 1; i < size; i++)
			{
				cwStack.Push(ccStack.Pop());
			}

			return cwStack;
		}
	};

	Point minPoint;
	Stack pointStack;

	/// <summary>
	/// Класс программы, в полях сохраняются ключевые переменные.
	/// </summary>
	class Program
	{
	public:

		static void main(std::vector<std::string> args)
		{
			if (args.size() == 4)
			{
				order = args[0];
				format = args[1];
				input = args[2];
				output = args[3];
			}
			else
			{
				std::cout << "debugging....\n";
			}

			std::cout << "order " + order + " format " + format + "\n";

			// Считывание данных.
			std::ifstream input_file;
			input_file.open(input);

			int numberOfPoints;
			input_file >> numberOfPoints;

			std::vector<Point> points = std::vector<Point>(numberOfPoints);

			for (size_t i = 0; i < numberOfPoints; i++)
			{
				int x, y;
				input_file >> x >> y;
				points[i] = Point(x, y);

				inputData += "(" + points[i].ToString() + "), ";
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
		static void Graham(std::vector<Point> points)
		{
			Point minPoint = points[0];
			for (int i = 0; i < points.size(); i++)
			{
				if (points[i].CompareTo(minPoint))
				{
					minPoint = points[i];
				}
			}

			// Сортировка точек по полярному углу относительно найденной минимальной точки.
			std::sort((points).begin(), (points).end(), [minPoint](Point a, Point b)
				{
					if (a.PolarAngleCC(minPoint) == b.PolarAngleCC(minPoint))
					{
						return a.Distance(minPoint) < (b.Distance(minPoint));
					}
					return a.PolarAngleCC(minPoint) < (b.PolarAngleCC(minPoint));
				});

			std::vector<Point> distinctPoints;
			distinctPoints.push_back(minPoint);
			distinctPoints.push_back(points[1]);

			// Удаление точек, для которых полярный угол дублирует уже существующий и расстояние меньше.
			for (int i = 2; i < points.size(); i++)
			{
				if (points[i].PolarAngleCC(minPoint) != distinctPoints[distinctPoints.size() - 1].PolarAngleCC(minPoint))
				{
					distinctPoints.push_back(points[i]);
				}
				else
				{
					distinctPoints[distinctPoints.size() - 1] = points[i];
				}
			}

			pointStack = Stack(distinctPoints.size());
			pointStack.Push(minPoint);
			pointStack.Push(distinctPoints[1]);

			// Проход алгоритма с добавлением в стак только тех точек, которые не образуют левый поворот.
			for (int i = 2; i < distinctPoints.size(); i++)
			{
				while (pointStack.Size() > 1 &&
					!(HW::Point::LeftTurnCC(pointStack.NextToTop(), pointStack.Top(), distinctPoints[i])))
				{
					pointStack.Pop();
				}
				pointStack.Push(distinctPoints[i]);
			}

			if (order == "cw")
			{
				pointStack = HW::Stack::MakeCW(pointStack);
			}
		}

		/// <summary>
		/// Метод для форматирования и вывода результата в соответствующем формате.
		/// </summary>
		static void WellKnownText()
		{
			std::string outData = "MULTIPOINT (" + inputData.substr(0, inputData.size() - 2) +
				")\nPOLYGON ((" + pointStack.WellKnown() + "))";

			std::cout << "\nWKT\n" + outData;

			std::ofstream output_file;
			output_file.open(output);

			output_file << outData;
		}

		/// <summary>
		/// Метод для форматирования и вывода результата в соответствующем формате.
		/// </summary>
		static void Plain()
		{
			std::string outData = std::to_string(pointStack.Size()) + "\n" + pointStack.ToString();

			std::cout << "\nPLAIN\n" + outData;

			std::ofstream output_file;
			output_file.open(output);

			output_file << outData;
		}
	};
};

// Точка входа, перенаправляющая в другой метод мейн.
int main(int argc, const char** argv) {
	std::vector<std::string> arguments(argv + 1, argv + argc);
	HW::Program::main(arguments);
	return 0;
}
