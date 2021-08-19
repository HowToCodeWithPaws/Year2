/// АиСД-2, 2021, задание 5
/// Зубарева Наталия Дмитриевна, БПИ195
/// Среда разработки CLion 2020.2.3
/// Что сделано: написана программа, сравнивающая 8 сортировок:
/// бинарными вставками, выбором, пузырьком - из итерационных,
/// подсчетом и цифровая - из линейных,
/// быстрая, слиянием и кучей - из рекурсивных.
/// Сравнение проводится на 4 типах массивов (данные генерируются
/// с использованием случайных чисел) 10 размеров (в задании 11, но
/// 100 дублируется, а это наверное не обязательно). Каждая сортировка
/// прогоняется 103 раза на каждом виде каждого размера, время работы
/// замеряется с помощью chrono, усредняется и записывается в таблицу.
/// Также записывается файл с четырьмя сгенерированными типами массивов
/// максимального размера и эталонный файл с ними же, но отсортированными
/// стандартной сортировкой с++. Также записываются файлы с отсортированными
/// массивами наибольшей длины для каждого типа и сортировки в соответствующие
/// файлы.
/// Информация также выводится на экран. Все это очень долго.
/// Что не сделано: я не знаю, возможно вывод в таблицу не самый идеальный, но
/// мне с ней работать вполне нормально.

#include "BinInsertionSort.cpp"
#include "BubbleSort.cpp"
#include "CountSort.cpp"
#include "HeapSort.cpp"
#include "MergeSort.cpp"
#include "QuickSort.cpp"
#include "RadixSort.cpp"
#include "SelectionSort.cpp"
#include "checkSorted.cpp"
#include <chrono>
#include <iostream>

using Sort = AbstractSort *;

/// Метод для вывода в файл input.txt эталонных массивов 4 типов размером 4100
/// и в output.txt их же, отсортированных стандартной сортировкой с++.
void printIO(std::vector<std::vector<int>> &parentArrays,
             std::vector<std::string> arrayNames) {
    std::ofstream inputFile{"input.txt"};
    std::ofstream outputFile{"output.txt"};
    for (size_t j = 0; j < parentArrays.size(); ++j) {
        inputFile << "\n"
                  << arrayNames[j] << "\n";
        for (size_t i = 0; i < parentArrays[j].size(); ++i) {
            inputFile << parentArrays[j][i] << " ";
        }
        inputFile << "\n";

        std::sort(parentArrays[j].begin(), parentArrays[j].end());
        outputFile << "\n"
                   << arrayNames[j] << "\n";
        for (size_t i = 0; i < parentArrays[j].size(); ++i) {
            outputFile << parentArrays[j][i] << " ";
        }
        outputFile << "\n";
    }
}

/// Метод для печати отсортированных больших масиивов всех четырех
/// типов для каждой сортировки. В идеале они все должны быть одинаковые.
void printActual(std::vector<int> &sorted, int size, std::string &info) {
    if (size == 4100) {
        std::ofstream outputFile{info + "output.txt"};
        for (size_t i = 0; i < sorted.size(); ++i) {
            outputFile << sorted[i] << " ";
        }
        outputFile << "\n";
    }
}

/// Метод для проверки того, что на каждом запуске сортировки массив
/// действительно отсортировался. Если нет, происходит выход из программы,
/// потому что этого совершенно не должно происходить.
void check(std::vector<std::vector<int>> arrays, std::string &info) {
    for (size_t i = 0; i < 103; ++i) {
        if (!checkSorted(arrays[i])) {
            std::cout << "Something awful happened at " + info + " and the sort isn't sorting";
            exit(-12);
        }
    }
}

/// Метод собственно запуска теста с замером времени - принимает сортировку, массив
/// требуемого типа размера 4100 элементов, размер требуемого для теста массива и
/// текстовую информацию о сортировке, типе массива и размере.
/// Сначала мы формируем массив требуемого размера, затем копируем его 103 раза
/// (чтобы на каждом запуске мы сортировали неотсортированный массив), три раза прогоняем
/// сортировку вхолостую, затем запускаем таймер, прогоняем сортировку 100 раз,
/// выключаем таймер, проверяем, все ли массивы отсортировались, печатаем результат
/// в файл, если нужно, усредняем время (делим на 100) и возвращаем полученное
/// среднее значение времени на одну сортировку.
long long runTest(Sort sort, std::vector<int> &parentArray, int size, std::string &info) {
    std::vector<int> array(parentArray.begin(), parentArray.begin() + size);
    std::vector<std::vector<int>> arraysX103;
    for (size_t i = 0; i < 103; ++i) {
        arraysX103.emplace_back(array);
    }

    for (size_t i = 0; i < 3; ++i) {
        sort->sort(arraysX103[i]);
    }

    auto start = std::chrono::high_resolution_clock::now();

    for (int i = 3; i < arraysX103.size(); ++i) {
        sort->sort(arraysX103[i]);
    }

    auto elapsed = std::chrono::high_resolution_clock::now() - start;
    long long nanoseconds =
            std::chrono::duration_cast<std::chrono::nanoseconds>(elapsed).count();

    check(arraysX103, info);

    printActual(arraysX103[0], size, info);

    return nanoseconds / 100.0;
}

/// Метод для создания массивов входных данных размера 4100
/// четырех типов: со случайными значениями от 0 до 5,
/// со случайными значениями от 0 до 4000, почти отсортированный
/// с значениями от 1 до 4100 и отсортированный в обратном
/// порядке с значениями от 1 до 4100. Все кроме предпоследнего просто
/// заполняются в цикле, для почти отсортированного же мы еще пробегаем
/// и в каждых 50 числах меняем местами одно число из текущего и
/// симметричного с конца блока (выбрано 50, чтобы даже на самом маленьком
/// размере массива его свойство почти отсортированности все равно сохранилось).
/// Возвращаем вектор с этими четырьмя векторами.
void generateArrays(std::vector<std::vector<int>> &arrays) {
    std::vector<int> rand0to5 = std::vector<int>();
    std::vector<int> rand0to4000 = std::vector<int>();
    std::vector<int> almost1to4100 = std::vector<int>();
    std::vector<int> reverse1to4100 = std::vector<int>();

    for (int i = 1; i < 4101; ++i) {
        rand0to5.emplace_back(0 + random() % 6);
        rand0to4000.emplace_back(0 + random() % 4001);
        almost1to4100.emplace_back(i);
        reverse1to4100.emplace_back(4101 - i);
    }

    for (size_t i = 0; i < 4100; i += 50) {
        std::swap(almost1to4100[i + random() % 50],
                  almost1to4100[4050 - i + random() % 50]);
    }

    arrays.emplace_back(rand0to5);
    arrays.emplace_back(rand0to4000);
    arrays.emplace_back(almost1to4100);
    arrays.emplace_back(reverse1to4100);
}

/// Точка входа в программу, здесь мы открываем поток записи в таблицу,
/// формируем массив из указателей на объекты классов сортировок, массив
/// размеров (не дублируем 100), массив названий 4 типов массивов, массив
/// названий сортировок. Вызываем печать массивов входных и эталонных выходных.
/// Далее во вложенных циклах по видам сортировок, типам массивов и размерам
/// формируем строку с информацией, вызываем метод тестирования, выводим результат
/// на экран и в таблицу. В конце не забываем почистить массив указателей.
int main() {
    std::ofstream results{"results.csv"};
    std::vector<Sort> sorts{new SelectionSort(), new BinInsertionSort(), new BubbleSort(),
                            new CountSort(), new RadixSort(),
                            new HeapSort(), new MergeSort(), new QuickSort()};

    std::vector<int> sizes{50, 100, 150, 200, 250, 300, 1100, 2100, 3100, 4100};
    std::vector<std::vector<int>> arrays = std::vector<std::vector<int>>();
    generateArrays(arrays);
    std::vector<std::string> arrayNames{"rand 0 to 5", "rand 0 to 4000",
                                        "almost sorted 1 to 4100", "reverse sorted 1 to 4100"};
    std::vector<std::string> sortNames{"SelectionSort", "BinInsertionSort", "BubbleSort",
                                       "CountSort", "RadixSort",
                                       "HeapSort", "MergeSort", "QuickSort"};

    printIO(arrays, arrayNames);

    for (int i = 0; i < sorts.size(); ++i) {
        for (int j = 0; j < arrays.size(); ++j) {
            for (int k = 0; k < sizes.size(); ++k) {
                std::string info = sortNames[i] + " on " + arrayNames[j] + " of size " + std::to_string(sizes[k]);
                auto time = runTest(sorts[i], arrays[j], sizes[k], info);
                std::cout << info << ": " << time << "\n";
                results << sortNames[i] << ";" << arrayNames[j] << ";" << sizes[k] << ";" << time << "\n";
            }
        }
        std::cout << "\n";
    }

    for (size_t i = 0; i < sorts.size(); ++i) {
        delete sorts[i];
    }

    std::cout << "aaand that's all for today\n";
    return 0;
}
