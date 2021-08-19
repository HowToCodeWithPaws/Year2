/// Класс для цифровой сортировки - наследует абстрактную сортировку
/// и реализует требуемый ей метод и еще некоторое количество вещей,
/// потому что радикс - это непросто.

#include "AbstractSort.h"
#include <bits/stdc++.h>//добавим INT_MIN и INT_MAX

class RadixSort : public AbstractSort {
private:
    /// Нам нужен юнион для того, чтобы удобно
    /// было получать разряды числа.
    union UnionNumber {
        uint32_t decimal;
        uint8_t in256[4];
    };

    /// Метод - внебрачный ребенок сортировки подсчетом и радикса, потому
    /// что подсчет осуществляется для заданого ключом разряда чисел в массиве.
    /// Неизбежно используем дополнительную память, но все чистим в конце.
    void radixCountingSort(UnionNumber *unionNumbers, size_t arraySize, size_t key) {
        UnionNumber minUnion, maxUnion;
        minUnion.decimal = INT_MAX;
        maxUnion.decimal = INT_MIN;

        for (size_t i = 0; i < arraySize; ++i) {
            if (unionNumbers[i].in256[key] < minUnion.in256[key]) {
                minUnion = unionNumbers[i];
            }
            if (unionNumbers[i].in256[key] > maxUnion.in256[key]) {
                maxUnion = unionNumbers[i];
            }
        }

        int *count = new int[maxUnion.in256[key] - minUnion.in256[key] + 1];
        UnionNumber *sorted = new UnionNumber[arraySize];

        for (size_t i = 0; i < maxUnion.in256[key] - minUnion.in256[key] + 1; ++i) {
            count[i] = 0;
        }

        UnionNumber zero;
        zero.decimal = 0;

        for (size_t i = 0; i < arraySize; ++i) {
            ++count[unionNumbers[i].in256[key] - minUnion.in256[key]];
            sorted[i] = zero;
        }

        for (size_t i = 1; i < maxUnion.in256[key] - minUnion.in256[key] + 1; ++i) {
            count[i] = count[i] + count[i - 1];
        }

        for (size_t i = arraySize - 1; i + 1 != 0; --i) {
            sorted[count[unionNumbers[i].in256[key] - minUnion.in256[key]] - 1] = unionNumbers[i];
            --count[unionNumbers[i].in256[key] - minUnion.in256[key]];
        }

        for (size_t i = 0; i < arraySize; ++i) {
            unionNumbers[i] = sorted[i];
        }

        delete[] count;
        delete[] sorted;
    }

public:
    /// Основной метод сортировки, входной массив преобразуется в юнионы,
    /// 4 раза (по основанию 256 достаточно для интов) вызывается сортировка
    /// массива юнионов, потом они перегоняются назад в числа.
    void sort(std::vector<int> &unsorted) override {
        UnionNumber *unionNumbers = (UnionNumber *) unsorted.data();

        for (size_t i = 0; i < 4; ++i) {
            radixCountingSort(unionNumbers, unsorted.size(), i);
        }

        for (size_t i = 0; i < unsorted.size(); ++i) {
            unsorted[i] = unionNumbers[i].decimal;
        }
    }
};