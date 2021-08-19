#include "gdal/gdal.h"
#include "gdal/ogrsf_frmts.h"
#include <boost/geometry/geometry.hpp>
#include <fstream>
#include <iostream>
#include <set>

/// Дисклеймер: оно как-то немного медленно работает,
/// но будет быстрее, если делать через смейк с set(CMAKE_CXX_FLAGS "-O3 -Wall -Wextra")

/// Переменные для аргументов командной строки: директории
/// с данными, путей до входного и выходного файлов,
/// заданы дефолтными параметрами для дебага.
std::string dataPath = "../data";
std::string inputPath = "../input/test1.txt";
std::string outputPath = "../output/temp1.txt";

/// Темплейты, потому что писать руками эти типы невыносимо
using Point = boost::geometry::model::point<double, 2, boost::geometry::cs::cartesian>;
using MBR = boost::geometry::model::box<Point>;
using MBRWithID = std::pair<MBR, int>;
using Rtree = boost::geometry::index::rtree<MBRWithID, boost::geometry::index::quadratic<8, 4>>;

/// Переменные для двух ключевых объектов: дерева, в которое
/// будем класть считываемые из данных полигоны,
/// и прямоугольника, пересечение с которым мы ищем
Rtree tree;
MBR rectangleToIntersect;

namespace HW {

    /// Метод, который получает для передаваемого полигона
    /// MBR, формирует объект из него и его ID, добавляет в дерево.
    static void GetMBR(OGRPolygon *polygon, double OSM_ID) {
        OGREnvelope res;
        polygon->getEnvelope(&res);
        Point minPoint = Point(res.MinX, res.MinY);
        Point maxPoint = Point(res.MaxX, res.MaxY);
        MBRWithID rectangleWID = MBRWithID(MBR(minPoint, maxPoint), OSM_ID);
        tree.insert(rectangleWID);
    }

    /// Метод, с помощью которого мы считываем данные из файла об объектах.
    /// Основан на замечательном примере с семинара.
    static void ReadMBRs() {
        GDALAllRegister();

        std::string data = dataPath + "/building-polygon.shp";

        GDALDataset *dataset = static_cast<GDALDataset *>(GDALOpenEx(
                data.c_str(),
                GDAL_OF_VECTOR,
                nullptr, nullptr, nullptr));


        if (dataset == nullptr) {
            std::cout << "Cannot get data!\n";
            exit(-1);
        }

        for (auto &&layer : dataset->GetLayers()) {
            for (auto &&feature : layer) {
                double OSM_ID = 0;

                for (auto &&field : feature) {
                    OSM_ID = field.GetDouble();
                    break;
                }

                auto *geometry = feature->GetGeometryRef();

                switch (auto geometryType = geometry->getGeometryType()) {

                    case wkbPolygon: {
                        //      std::cout << "This is Polygon!\n";
                        GetMBR(geometry->toPolygon(), OSM_ID);
                        break;
                    }
                    case wkbMultiPolygon: {
                        //            std::cout << "This is MultiPolygon!\n";
                        auto *multiPolygon = geometry->toMultiPolygon();

                        for (auto &&polygon : multiPolygon) {
                            GetMBR(polygon, OSM_ID);
                        }
                        break;
                    }
                    default: {
                        std::cout << "The what you are parsing..... " << geometryType << "\n";
                        exit(1);
                    }
                }
            }
        }
        GDALClose(dataset);
    }

    /// Метод для считывания прямоугольника из
    /// файла входных данных, это тот самый прямоугольник,
    /// с которым мы ищем пересечение.
    static MBR ReadRectangleToIntersect() {

        double xMin, yMin, xMax, yMax;

        std::ifstream input;
        input.open(inputPath);

        input >> xMin >> yMin >> xMax >> yMax;

        input.close();

        return MBR(Point(xMin, yMin), Point(xMax, yMax));
    }

    /// Метод для формирования вектора из MBR в дереве, которые пересекаются
    /// с нашим пересекаемым прямоугольником. Записываем их все в векторы как MBR,
    /// потому что метод R-дерева умеет так, но потом делаем сет для того, чтобы
    /// учитывать только уникальные идентификаторы и сразу их сортировать.
    /// Выводим полученный сет.
    static void Intersect() {
        std::vector<MBRWithID> intersects;
        tree.query(boost::geometry::index::intersects(rectangleToIntersect), std::back_inserter(intersects));

        std::ofstream output;
        output.open(outputPath);

        std::set<int> ids;

        for (MBRWithID mbr : intersects) {
            ids.insert(mbr.second);
        }

        for (int id : ids) {
            output << id << "\n";
            std::cout << id << "\n";
        }

        output.close();
    }

    /// <summary>
    /// Класс программы, где есть мейн и методы для работы с
    /// </summary>
    class Program {
    public:
        /// <summary>
        /// Настоящий метод мейн, который считывает аргументы
        /// командной строки () и вызывает
        /// методы считывания данных о полигонах,
        /// о прямоугольнике, пересечение с которым мы ищем,
        /// метод, который ищет пересекающиеся полигоны.
        /// </summary>
        static void main(std::vector<std::string> args) {
            if (args.size() == 3) {
                dataPath = args[0];
                inputPath = args[1];
                outputPath = args[2];
            } else {
                std::cout << "debugging....\n";
            }

            ReadMBRs();

            rectangleToIntersect = ReadRectangleToIntersect();

            Intersect();

            std::cout << "we are done here\n";
        }
    };
};

/// <summary>
/// Мейн с правильными параметрами для плюсов, который вызывает
/// другой мейн, потому что с# стайл.
/// </summary>
int main(int argc, const char **argv) {
    std::vector<std::string> arguments(argv + 1, argv + argc);
    HW::Program::main(arguments);
    return 0;
}