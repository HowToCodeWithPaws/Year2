/// Класс для быстрой сортировки - наследует абстрактную
/// сортировку и реализует требуемый метод, тут также есть
/// собственно рекурсивный метод.

#include "AbstractSort.h"
#include <vector>
class QuickSort : public AbstractSort {
private:
    /// Рекурсивный метод быстрой сортировки со средним опорным элементом.
    /// Выбираем его, затем проходим в две стороны по массиву и меняем
    /// местами элементы не на своих местах по отношению к опорному, вызываем рекурсию.
    void recursiveQuick(std::vector<int> &unordered, int left, int right) {
        if (left < right && right < unordered.size()) {

            int pivot = unordered[(left + right) / 2];

            int i = left;
            int j = right;

            while (true) {
                while (unordered[i] < pivot) {
                    ++i;
                }

                while (unordered[j] > pivot) {
                    --j;
                }

                if (i > j) {
                    break;
                }

                std::swap(unordered[i], unordered[j]);

                ++i;
                --j;
            }

            if (j > left && j < unordered.size())
                recursiveQuick(unordered, left, j);

            if (right > i && right < unordered.size())
                recursiveQuick(unordered, i, right);
        }
    }

public:
    /// Основной метод с требуемой сигнатурой, не делает
    /// ничего кроме вызова рекурсивного метода сортировки
    /// от верхнего уровня - размеров массива целиком.
    void sort(std::vector<int> &unordered) override {
        recursiveQuick(unordered, 0, unordered.size() - 1);
    }
};