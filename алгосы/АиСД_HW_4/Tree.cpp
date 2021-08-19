#include <math.h>
#include <algorithm>
#include <exception>
#include <fstream>
#include <iostream>
#include <random>
#include <string>
#include <vector>

/// <summary>
/// Параметры для Т - количества детей, ключей и тд, 
/// директории для нод, тестов и ответов.
/// </summary>
int parameterT /*= 10*/;
std::string directory /* = "../../tree"*/;
std::string input /*= "../../input/test3.txt"*/;
std::string output /*= "../../output/temp3.txt"*/;

namespace HW {
	/// <summary>
	/// Класс ноды, определение идентификатора ноды как строки.
	/// </summary>
	class Node;
	using NodePtr = std::string;
	class Node {
	private:

		/// <summary>
		/// Метод для создания случайного уникального названия - 
		/// идентификатора ноды, состоящего из символов, которыми можно называть файлы.
		/// </summary>
		NodePtr MakeID() {
			static std::random_device rd;
			static std::mt19937 gen(rd());
			static char rom[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_-1234567890";
			static std::uniform_int_distribution<size_t> dis(0, sizeof(rom) - 2);

			char res[16];
			for (size_t i = 0; i < sizeof(res); i++) {
				res[i] = rom[dis(gen)];
			}
			return std::string(res, 16);
		}

		/// <summary>
		/// Конструктор ноды - создает новые идентификатор и делает новые векторы ключей и детей.
		/// </summary>
		Node() {
			nodeID = MakeID();
			children = std::vector<NodePtr>();
			keys = std::vector<std::pair<int, int>>();
		}

		/// <summary>
		/// Конструктор ноды по параметрам - для десериализации, задает идентификатор, детей и ключи.
		/// </summary>
		Node(NodePtr ID, std::vector<NodePtr> children, std::vector<std::pair<int, int>> keyes) {
			nodeID = ID;
			this->children = children;
			this->keys = keyes;
		}

		/// <summary>
		/// Метод сериаизации - бинарно в файл по номеру идентификатора записывается
		/// информация о ключах и детях ноды, так вот используется вторичная память.
		/// </summary>
		static void Serialize(Node node) {
			std::ofstream node_file;
			node_file.open(directory + "/" + node.nodeID + ".txt", std::ofstream::binary);
			size_t size = node.children.size();
			node_file.write((char*)&size, sizeof(size_t));
			for (NodePtr child : node.children) {
				node_file.write(child.c_str(), 16);
			}
			size = node.keys.size();
			node_file.write((char*)&size, sizeof(size_t));
			for (std::pair<int, int> key : node.keys) {
				node_file.write((char*)&key, sizeof(std::pair<int, int>));
			}
			node_file.close();
		}

		/// <summary>
		/// Метод для разбития ребенка слишком большого размера на два,
		/// создается новая нода, все изменения сериализуются.
		/// </summary>
		Node SplitChild(size_t i, Node& y) {
			Node z = Node();
			for (size_t j = 0; j < parameterT - 1; ++j) {
				z.keys.push_back(y.keys[j + parameterT]);
			}

			if (!y.IsLeaf()) {
				for (size_t j = 0; j < parameterT; ++j) {
					z.children.push_back(y.children[j + parameterT]);
				}
			}

			children.insert(children.begin() + i + 1, z.nodeID);

			keys.insert(keys.begin() + i, y.keys[parameterT - 1]);

			y.keys.resize(parameterT - 1);

			if (!y.children.empty()) {
				y.children.resize(parameterT);
			}

			Serialize(y);
			Serialize(z);
			Serialize(*this);

			return z;
		}

		/// <summary>
		/// Метод, говорящий, является ли нода листом (есть ли у нее дети.)
		/// </summary>
		bool IsLeaf() {
			return children.size() == 0;
		}

		/// <summary>
		/// Метод для возвращения количества ключей.
		/// </summary>
		int KeysCount() {
			return keys.size();
		}

		/// <summary>
		/// Метод вставки ключа - вставляем по разному в зависимости от того,
		/// кем является нода - листом или нет, будет ли переполнение количества ключей.
		/// </summary>
		void Insert(std::pair<int, int> key) {
			int i = KeysCount() - 1;
			if (IsLeaf()) {
				keys.push_back(std::make_pair(INT32_MIN, INT32_MIN));
				while (i >= 0 && key.first < keys[i].first) {
					keys[i + 1] = keys[i];
					i--;
				}
				keys[i + 1] = key;
				Serialize(*this);
			}
			else {
				while (i >= 0 && key.first < keys[i].first) {
					i--;
				}

				i++;

				Node newNode = Deserialize(children[i]);

				if (newNode.KeysCount() == 2 * parameterT - 1) {
					Node temp = SplitChild(i, newNode);

					if (key.first > keys[i].first) {
						i++;
						newNode = temp;
					}
				}

				newNode.Insert(key);
			}
		}

		/// <summary>
		/// Метод для удаления. Оно происходит по разному 
		/// при разных вариантах, как удаление из листа или из
		/// внутренней ноды, в зависимости от недополнения или переполнения 
		/// ключей и детей. Возвращает удаленное значение или наллптр,
		///  если ничего не удалилось.
		/// </summary>
		int* Deletion(int key) {
			int* deleted = nullptr;
			int i = FindKey(key);

			if (i < KeysCount() && keys[i].first == key) {
				if (IsLeaf()) {
					deleted = RemoveFromLeaf(i);
				}
				else {
					deleted = RemoveFromNonLeaf(i);
				}
			}
			else {
				if (IsLeaf()) {
					return deleted;
				}

				bool flag = i == KeysCount();

				Node curr = Deserialize(children[i]);

				if (curr.KeysCount() < parameterT) {
					Fill(i);
				}

				if (flag && i > KeysCount()) {
					deleted = Deserialize(children[i - 1]).Deletion(key);
				}
				else {
					deleted = Deserialize(children[i]).Deletion(key);
				}
			}
			return deleted;
		}

		/// <summary>
		/// Метод для удаления из листа - просто удаляется ключ.
		/// </summary>
		int* RemoveFromLeaf(int i) {
			int ret = keys[i].second;

			for (int j = i + 1; j < KeysCount(); ++j) {
				keys[j - 1] = keys[j];
			}

			keys.pop_back();

			Serialize(*this);

			return new int(ret);
		}

		/// <summary>
		/// Метод для добавления дополнительных эдементов
		/// в ноду, если чего-то не хватает - берет у соседей,
		/// если можно, иначе сливает соседей.
		/// </summary>
		void Fill(int idx) {
			Node node1;
			Node node2;

			if (idx != 0) {
				node1 = Deserialize(children[idx - 1]);
			}
			if (idx != KeysCount()) {
				node2 = Deserialize(children[idx + 1]);
			}

			if (idx != 0 && node1.KeysCount() >= parameterT)
				BorrowFromPrev(idx);

			else if (idx != KeysCount() && node2.KeysCount() >= parameterT)
				BorrowFromNext(idx);

			else {
				if (idx != KeysCount()) {
					Node node = Deserialize(children[idx]);
					Merge(idx, node, node2);
				}
				else {
					Node node = Deserialize(children[idx]);
					Merge(idx - 1, node1, node);
				}
			}

			return;
		}

		/// <summary>
		/// Метод удаления из не листа - рекурсивно удаляем и 
		/// в итоге находим нужный лист.
		/// </summary>
		/// <param name="idx"></param>
		/// <returns></returns>
		int* RemoveFromNonLeaf(int idx) {
			std::pair<int, int> k = keys[idx];
			Node node1 = Deserialize(children[idx]);
			Node node2 = Deserialize(children[idx + 1]);

			if (node1.KeysCount() >= parameterT) {
				std::pair<int, int> pred = GetPredecessor(idx);
				keys[idx] = pred;
				Serialize(*this);
				node1.Deletion(pred.first);
				return new int(k.second);
			}
			else if (node2.KeysCount() >= parameterT) {
				std::pair<int, int> succ = GetSuccessor(idx);
				keys[idx] = succ;
				Serialize(*this);
				node2.Deletion(succ.first);
				return new int(k.second);
			}
			else {
				Merge(idx, node1, node2);
				return node1.Deletion(k.first);
			}

			return nullptr;
		}

		/// <summary>
		/// Метод для получения пары с предыдущим в иерархии значением
		/// </summary>
		std::pair<int, int> GetPredecessor(int idx) {
			Node cur = Deserialize(children[idx]);
			while (!cur.IsLeaf()) {
				cur = Deserialize(cur.children[cur.KeysCount()]);
			}
			if (cur.keys.empty()) {
				exit(1);
			}
			return cur.keys[cur.KeysCount() - 1];
		}

		/// <summary>
		/// Метод для получения пары с следующим в иерархии значением
		/// </summary>
		std::pair<int, int> GetSuccessor(int idx) {
			Node cur = Deserialize(children[idx + 1]);
			while (!cur.IsLeaf()) {
				cur = Deserialize(cur.children[0]);
			}
			if (cur.keys.empty()) {
				exit(1);
			}
			return cur.keys[0];
		}

		/// <summary>
		/// Метод для добавления к ноде разделяющего ключа
		/// и последнего ключа из предыдущей ноды (и ребенка), удаления их,
		/// чтобы не было дублирования. После сериализуем измененные ноды.
		/// </summary>
		void BorrowFromPrev(int idx) {
			Node a = Deserialize(children[idx]);
			Node b = Deserialize(children[idx - 1]);

			a.keys.insert(a.keys.begin(), keys[idx - 1]);
			if (!a.IsLeaf()) a.children.insert(a.children.begin(), b.children[b.children.size() - 1]);
			keys[idx - 1] = b.keys[b.keys.size() - 1];
			b.keys.pop_back();
			if (!b.IsLeaf()) b.children.pop_back();

			Serialize(a);
			Serialize(b);
			Serialize(*this);
			return;
		}

		/// <summary>
		/// Метод для добавления к ноде разделяющего ключа
		/// и первого ключа из следующей ноды (и ребенка), удаления их,
		/// чтобы не было дублирования. После сериализуем измененные ноды.
		/// </summary>
		void BorrowFromNext(int idx) {
			Node a = Deserialize(children[idx]);
			Node b = Deserialize(children[idx + 1]);

			a.keys.push_back(keys[idx]);

			if (!(a.IsLeaf())) {
				a.children.push_back(b.children[0]);
			}

			keys[idx] = b.keys[0];

			b.keys.erase(b.keys.begin());

			if (!b.IsLeaf()) {
				b.children.erase(b.children.begin());
			}

			Serialize(a);
			Serialize(b);
			Serialize(*this);

			return;
		}

		/// <summary>
		/// Метод для слияния двух нод в одну с удалением второй.
		/// Происходит объединение векторов с помощью их методов,
		/// а не по циклам как в псевдокоде, потому что это было больно,
		/// далее сериализуем (сохраняем) измененные ноды.
		/// </summary>
		void Merge(int index, Node& child, Node& sibling) {

			child.keys.push_back(keys[index]);
			child.keys.insert(child.keys.end(), sibling.keys.begin(), sibling.keys.end());
			child.children.insert(child.children.end(), sibling.children.begin(), sibling.children.end());
			keys.erase(keys.begin() + index);
			children.erase(children.begin() + index + 1);

			Serialize(child);
			Serialize(*this);
		}

		/// <summary>
		/// Метод для поиска индекса, где может лежать ключ.
		/// </summary>
		size_t FindKey(int key) {
			size_t i = 0;
			while (i < KeysCount() && keys[i].first < key) {
				++i;
			}
			return i;
		}

		/// <summary>
		/// Метод для поиска ключа по ноде, мы ищем индекс, где ключ может быть,
		/// согласно балансировке, затем ищем по ключам, если нода - лист,
		/// то возвращаем указатель на наллптр, если же нам надо искать по детям,
		/// десериализуем нужную ноду ребенка и ищем там.
		/// </summary>
		/// <param name="key"></param>
		/// <returns></returns>
		int* Find(int key) {
			size_t i = FindKey(key);

			if (i < KeysCount() && key == keys[i].first) {
				return new int(keys[i].second);
			}
			else if (IsLeaf()) {
				return nullptr;
			}

			return Deserialize(children[i]).Find(key);
		}

		/// <summary>
		/// Поля для идентификатора ноды, вектора ключей и детей (хранятся идентификаторы).
		/// </summary>
		NodePtr nodeID;
		std::vector<NodePtr> children;
		std::vector<std::pair<int, int>> keys;
		friend class BTree;

	public:
		/// <summary>
		/// Метод десериализации ноды. Мы читаем бинарный файл и заполняем
		/// ноду ключами и детьми. Принимаем идентификатор ноды, который 
		/// к счастью является и именем файла, который мы дожны читать.
		/// Закрываем потоки, возвращаем новую ноду.
		/// Благодаря этому мы используем вторичную память, и это круто.
		/// </summary>
		static Node Deserialize(std::string path) {
			std::ifstream node_file;
			node_file.open(directory + "/" + path + ".txt", std::ifstream::binary);
			size_t numberOfChildren, numberOfKeyes;
			node_file.read((char*)&numberOfChildren, sizeof(size_t));

			std::vector<NodePtr> children = std::vector<NodePtr>();
			char child[16];
			for (size_t i = 0; i < numberOfChildren; ++i) {
				node_file.read((char*)child, 16);
				children.push_back(std::string(child, 16));
			}

			node_file.read((char*)&numberOfKeyes, sizeof(size_t));

			std::vector<std::pair<int, int>> keyes = std::vector<std::pair<int, int>>();
			std::pair<int, int> key;

			for (size_t i = 0; i < numberOfKeyes; ++i) {
				node_file.read((char*)&key, sizeof(std::pair<int, int>));
				keyes.push_back(key);
			}

			node_file.close();

			return Node(path.c_str(), children, keyes);
		}
	};

	/// <summary>
	/// Класс для дерева, в котором есть функции поиска, вставки и удаления (вау)!
	/// </summary>
	class BTree {
	public:
		BTree() {}

		/// <summary>
		/// Метод вставки в дерево - сначала проверяет,
		/// есть ли уже этот ключ (так мы делать не можем, возвращаем
		/// фолс), иначе двоим дерево, если надо, и делаем вставку в корень.
		/// Возвращаем тру, если все удалось и фолс иначе.
		/// </summary>
		bool Insert(int key, int value) {
			int* res = Find(key);

			if (res != nullptr) {
				delete res;
				return false;
			}

			Node r = root;

			if (root.KeysCount() == 2 * parameterT - 1) {
				Node s = Node();
				s.children.push_back(root.nodeID);
				s.SplitChild(0, root);
				root = s;
			}

			root.Insert(std::make_pair(key, value));
			delete res;
			return true;
		}

		/// <summary>
		/// Метод для поиска ключа в дереве - вызывает поиск от корня.
		/// Возвращает либо значение, которое мы нашли, либо наллптр.
		/// </summary>
		int* Find(int key) {
			return root.Find(key);
		}

		/// <summary>
		/// Метод для удаления ключа из дерева - если корня нет,
		/// возвращаем пустоту, иначе вызываем удаление от корня,
		/// если у нас меняется структура корня - делаем корень из 
		/// считываемого ребенка. Возвращается удаленное значение,
		/// либо указатель на налл, если не удалено ничего.
		/// </summary>
		int* Deletion(int key) {
			if (&root == nullptr) {
				return nullptr;
			}

			int* res = root.Deletion(key);
			if (root.KeysCount() == 0 && !root.IsLeaf()) {
				root = Node::Deserialize(root.children[0]);
			}

			return res;
		}

	private:
		/// <summary>
		/// Поле для корневой ноды.
		/// </summary>
		Node root;
		friend class Node;
	};

	/// <summary>
	/// Почти глобальная переменная для дерева.
	/// </summary>
	BTree tree;

	/// <summary>
	/// Класс программы, где есть мейн и методы для работы с деревом извне.
	/// </summary>
	class Program {
	public:
		/// <summary>
		/// Метод для проверки на налл, потому что с++ не может так
		/// просто взять и вывести налл, поэтому мы выводим строку
		/// каждый раз, когда встречаем нулевой указатель.
		/// </summary>
		static void OutputNull(int* res, std::ofstream* output_file) {
			if (res == nullptr) {
				*output_file << "null\n";
			}
			else {
				*output_file << *res << "\n";
			}
		}

		/// <summary>
		/// Метод, который свитчится по названию команды
		/// без использования свитч кейса для строк, которого нет в с++.
		/// При каждой правильной команде мы считываем дополнительные параметры,
		/// вызываем ее метод от дерева, выводим результат в файл.
		/// записываем 
		/// </summary>
		static void InsteadOfSwitch(std::string command,
			std::ifstream* input_file, std::ofstream* output_file) {

			int key, value;

			if (command == "insert") {
				*input_file >> key >> value;
				bool res = tree.Insert(key, value);
				*output_file << std::boolalpha << res << "\n";
			}
			else if (command == "find") {
				*input_file >> key;
				int* res = tree.Find(key);
				OutputNull(res, output_file);
				delete res;
			}
			else if (command == "delete") {
				*input_file >> key;
				int* res = tree.Deletion(key);
				OutputNull(res, output_file);
				delete res;
			}
			else {
				std::cout << "you're doing it wrong";
			}
		}

		/// <summary>
		/// Метод, осуществляющий работу: открываются потоки для
		/// входных и выходных данных, создается дерево, задается максимальное
		/// число команд и номер строки, в цикле считывается команда и вызывается
		/// метод, выполняющий ее, затем потоки закрываются.
		/// </summary>
		static void DoTheReading() {
			std::ifstream input_file;
			input_file.open(input, std::ifstream::binary);

			std::ofstream output_file;
			output_file.open(output, std::ofstream::binary);

			size_t maxNumberOfCommands = 1000000000;
			size_t line = 0;
			tree = BTree();

			while (input_file && maxNumberOfCommands > 0) {
				std::string command;

				input_file >> command;
				if (!input_file) {
					break;
				}

				InsteadOfSwitch(command, &input_file, &output_file);

				--maxNumberOfCommands;
				++line;
			}

			input_file.close();
			output_file.close();
		}

		/// <summary>
		/// Настоящий метод мейн, который считывает аргументы
		/// командной строки (параметр т, директорию для сохранения
		/// файлов нод, входных и выходных файлов) и вызывает
		/// метод, который делает остальную работу.
		/// </summary>
		static void main(std::vector<std::string> args) {
			if (args.size() == 4) {
				parameterT = std::stoi(args[0]);
				directory = args[1];
				input = args[2];
				output = args[3];
			}
			else {
				std::cout << "debugging....\n";
			}

			DoTheReading();
		}
	};
};

/// <summary>
/// Мейн с правильными параметрами для плюсов, который вызывает
/// другой мейн, потому что с# стайл.
/// </summary>
int main(int argc, const char** argv) {
	std::vector<std::string> arguments(argv + 1, argv + argc);
	HW::Program::main(arguments);
	return 0;
}