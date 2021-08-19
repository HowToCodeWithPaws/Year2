#define _USE_MATH_DEFINES
#include<iostream>
#include<math.h>
#include<vector>;
#include<exception>;
#include <fstream>
#include <algorithm>

// ��������� �������� ������ ��� ������� � ������������.
std::string order = "cw";//"cc";
std::string format = "plain";//"wkt"
std::string input = "test0.txt";
std::string output = "answer0.txt";
std::string inputData = "";


namespace HW {

	class Point //: IComparable<Point>
	{
	private:
		int x, y;
	public:
		int X() const { return x; }
		int Y() const { return y; }

		Point(int x_, int y_) :x(x_), y(y_) {}

		/// <summary>
		/// ����� ��������� ���� ����� ��� ������
		/// ����� ����� ������ ����� � ������ ������
		/// ���������.
		/// </summary>
		/// <param name="other"></param>
		/// <returns></returns>
		bool CompareTo(Point other)
		{
			if (Y() == other.Y())
			{
				return X() < other.X();
			}
			return Y() < other.Y();
		}

		/// <summary>
		/// ����� ��� ���������� ��������� ���� �����
		/// ������ ������������ � ���������� � ������ ������.
		/// </summary>
		/// <param name="center"></param>
		/// <returns></returns>
		double PolarAngleCC(Point center)
		{
			double res = std::atan2(Y() - center.Y(), X() - center.X());

			if (res < 0) res += 2 * M_PI;

			return res;
		}

		/// <summary>
		/// ����� ��� ���������� ���������� ����� ������
		/// ������������ � ���������� � ������ ������.
		/// </summary>
		/// <param name="other"></param>
		/// <returns></returns>
		double Distance(Point other)
		{
			return std::sqrt((other.X() - X()) * (other.X() - X()) + (other.Y() - Y()) * (other.Y() - Y()));
		}

		/// <summary>
		/// ����������� �����, ������������, �������� �� �������
		/// ����� ����� ������������� ������� �����.
		/// </summary>
		/// <param name="a"></param>
		/// <param name="b"></param>
		/// <param name="c"></param>
		/// <returns></returns>
		static bool LeftTurnCC(Point a, Point b, Point c)
		{
			return (b.X() - a.X()) * (c.Y() - b.Y()) - (b.Y() - a.Y()) * (c.X() - b.X()) > 0;
		}

		std::string ToString()
		{
			return X() + " " + Y();
		}
	};

	class Stack
	{
	private:

		int size = 0;
		int capacity;
		std::vector<Point> points;//Point[] points;
	public:
		// ������������ ��������� ����������� �� ������� according to stackoverflow.
		static constexpr int MAX_SIZE = INT32_MAX;// int.MaxValue;

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
				throw std::exception("Stack if full");
			}

			points[size++] = point;
		}

		Point Pop()
		{
			if (IsEmpty())
			{
				throw std::exception("No elements in stack");
			}

			return points[--size];
		}

		Point Top()
		{
			if (IsEmpty())
			{
				throw std::exception("No elements in stack");
			}
			return points[Size() - 1];
		}

		Point NextToTop()
		{
			if (Size() <= 1)
			{
				throw std::exception("Only one element in stack");
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
		/// ����� ��� ���������� ������������� ������� � 
		/// ������ well known text.
		/// </summary>
		/// <returns></returns>
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
		/// ����� ��� "���������������" �����, ����� ��
		/// �������� ��������� ��� ������ �� ������� �������.
		/// ����� ����������, ��� ��� ���������, �� ��� �����������,
		/// ��� ����������� ��� ��������� � ���������� � �����������.
		/// </summary>
		/// <param name="ccStack"></param>
		/// <returns></returns>
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
	/// ����� ���������, � ����� ����������� �������� ����������.
	/// </summary>
	class Program
	{


	public:

		static int main(std::vector<std::string> args)
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

			// ���������� ������.
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

				inputData += "" + points[i].ToString() + ", ";
			}


			//	string[] data = File.ReadAllLines(input);
	//		Point[] points = new Point[numberOfPoints];

	/*		for (int i = 0; i < numberOfPoints; i++)
			{
				points[i] = new Point(int.Parse(data[i + 1].Split(' ')[0]),
					int.Parse(data[i + 1].Split(' ')[1]));
				inputData += "" + points[i].ToString() + ", ";
			}*/

			// ����� ������ ���������.
			Graham(points);

			// ����� ������ ������ � ����������� �� �������.
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
		/// �����, ��� ���������� ����������, �������� ������
		/// ����� � ��������������� ��� ��������� �������.
		/// </summary>
		/// <param name="points"></param>
		static void Graham(std::vector<Point> points)
		{
			//����� ��� ���� �������� ����. �� �� ��������� ����. 
			//������� �� �� ��� ��������� ����
			//minPoint = points.Min();

			Point minPoint = Point(INT32_MAX, INT32_MAX);
			for (int i = 0; i < points.size(); i++)
			{
				if (minPoint.CompareTo(points[i]) > 0)
				{
					minPoint = points[i];
				}
			}

			// ���������� ����� �� ��������� ���� ������������ ��������� ����������� �����.
			std::sort(points.begin(), points.end(), [minPoint](Point a, Point b)
				{
					if (a.PolarAngleCC(minPoint) == b.PolarAngleCC(minPoint))
					{
						return a.Distance(minPoint) < (b.Distance(minPoint));
					}
					return a.PolarAngleCC(minPoint) < (b.PolarAngleCC(minPoint));
				});

			std::vector<Point> distinctPoints;
			//		List<Point> distinctPoints = new List<Point>();

			distinctPoints.push_back(minPoint);
			distinctPoints.push_back(points[1]);

			// �������� �����, ��� ������� �������� ���� ��������� ��� ������������ � ���������� ������.
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

			for (auto el : distinctPoints)
			{
				std::cout << el.ToString();
			}

			// ������ ��������� � ����������� � ���� ������ ��� �����, ������� �� �������� ����� �������.
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
		/// ����� ��� �������������� � ������ ���������� � ��������������� �������.
		/// </summary>
		static void WellKnownText()
		{
			std::string outData = "MULTIPOINT (" + inputData.substr(0, inputData.size() - 2) +
				")\nPOLYGON ((" + pointStack.WellKnown() + "))";

			std::cout << outData;

			std::ofstream output_file;
			output_file.open(output);

			output_file << outData;
			//	File.WriteAllText(output, outData);
		}

		/// <summary>
		/// ����� ��� �������������� � ������ ���������� � ��������������� �������.
		/// </summary>
		static void Plain()
		{
			std::string outData = pointStack.Size() + "\n" + pointStack.ToString();

			std::cout << outData;

			std::ofstream output_file;
			output_file.open(output);

			output_file << outData;
			//	File.WriteAllText(output, outData);
		}
	};
};



int main(int argc, char** argv) {
	std::vector<std::string> arguments(argv + 1, argv + argc);
	HW::Program::main(arguments);
	return 0;
}
