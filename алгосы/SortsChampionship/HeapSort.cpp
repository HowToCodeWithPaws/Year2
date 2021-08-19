/// Класс сортировки кучей - наследует абстрактную сортировку и
/// реализует требуемую функцию, кроме того имеет вспомогательные
/// методы для работы алгоритма.

#include "AbstractSort.h"
#include <vector>

class HeapSort : public AbstractSort {
private:
    /// Рекурсивный метод для образования кучи с вершиной в переданном индексе,
    /// гарантирует выполнение условия кучи. Если что-то не выполнено, вершины
    /// меняются местами и для измененного ребенка вызывается рекурсия метода.
    int heapify(std::vector<int> &unsorted, int arraySize, int index) {
        int largest;
        int left = 2 * (index + 1) - 1;
        int right = 2 * (index + 1);

        if (left <= arraySize - 1 && unsorted[left] > unsorted[index]) {
            largest = left;
        } else {
            largest = index;
        }

        if (right <= arraySize - 1 && unsorted[right] > unsorted[largest]) {
            largest = right;
        }

        if (largest != index) {
            std::swap(unsorted[index], unsorted[largest]);
            if (largest < arraySize)
                return heapify(unsorted, arraySize, largest);
        }

        if (left >= arraySize)
            return -1;
        return 0;
    }

    /// Метод для построения кучи из переданного массива
    /// - для половины вершин (потому что родителей половина)
    /// вызываем метод условия кучи.
    void buildHeap(std::vector<int> &unsorted, int arraySize) {
        for (int i = (arraySize - 1) / 2; i + 1 != 0; --i) {
            heapify(unsorted, arraySize, i);
        }
    }

public:
    /// Основной метод сортировки кучей - строим кучу,
    /// затем для упорядочивания меняем местами элементы
    /// и вызываем метод условия кучи для новой вершины.
    void sort(std::vector<int> &unsorted) override {
        buildHeap(unsorted, unsorted.size());

        int heapSize = unsorted.size();

        for (int i = unsorted.size() - 1; i > 0; --i) {
            std::swap(unsorted[0], unsorted[i]);

            --heapSize;
            heapify(unsorted, heapSize, 0);
        }
    }
};