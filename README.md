# II-4-13 – Banker's Algorithm (Resource Graph Visualization)

**Хичээл:** F.CSM302 Үйлдлийн систем  
**Багийн код (хуваарилалтын код):** II-4-13  
**Сурах бичгийн бүлэг:** 8-р бүлэг – Deadlocks (Silberschatz, Galvin, Gagne – *Operating System Concepts*)  
**Төслийн нэр:** Programming Project – Banker's Algorithm (Resource-allocation graph UI)

## Багийн гишүүд
| Оюутны код | Нэр |
|---|---|
| B242270128 | Ц.Цэндсүрэн |
| B231940023 | ........... |
| B241910004 | ........... |
| B241940034 | ........... |
| B242270041 | ........... |

## Зорилго
Banker's Algorithm-ийг (Safety Algorithm, Resource-Request Algorithm, Deadlock Detection) хэрэгжүүлж, систем SAFE төлөвт байгаа эсэхийг шалгах, мөн процесс ба нөөцийн холбоог **Resource-allocation graph**-аар алхам алхмаар интерактивээр харуулах Java Swing симулятор.

## Quick Start
Шаардлага: **JDK 17 эсвэл түүнээс дээш** (`javac -version`). Гадаад сан **шаардлагагүй**.

```bash
# 1. Татах
git clone https://github.com/Tsendsvren/csm302_biydaalt.git
cd csm302_biydaalt

# 2. Compile
./build.sh          # Windows: build.bat

# 3. Ажиллуулах (GUI нээгдэнэ)
./run.sh            # Windows: run.bat

# 4. Автомат тест (GUI шаардахгүй)
./test.sh           # Windows: test.bat
```

Гараар: `javac -encoding UTF-8 -d out src/main/java/com/os/*.java` ба `java -cp out com.os.Main`.

## Хавтасны бүтэц
| Зам | Агуулга |
|---|---|
| `src/main/java/com/os/` | Эх код (алгоритм, симуляцийн хөдөлгүүр, Swing UI) |
| `tests/` | `AlgorithmTests.java` (29 автомат тест), `results.txt` (тестийн бодит гаралт) |
| `docs/` | `II-4-13_Manual.pdf`, `II-4-13_BD1_Report.pdf`, `img/` (screenshot) |

## Үндсэн боломжууд
- 10 бэлэн туршилт (сурах бичгийн жишээ, дасгал 8.3 / 8.9 / 8.28, deadlock илрүүлэлт, 10 процесстой систем)
- Алхам алхмаар тоглуулах: ⏮ ◀ ▶ ⏭, seek slider, хурд
- Resource-allocation graph, Allocation/Max/Need матриц, псевдокодын мөр тодруулалт, тайлбар, тоолуур
- Banker's Algorithm асаалттай/унтраалттай харьцуулалт

## Хэрэглэсэн бэлэн код, сан, AI
- Гадаад сан ашиглаагүй (зөвхөн JDK-ийн Swing/AWT).
- Алгоритмын псевдокод нь сурах бичгийн 8-р бүлэгт үндэслэсэн.
- AI: баримт бичиг (Report, Manual, README), автомат тест, build скриптийг бэлтгэхэд **Claude** ашиглан туслалцаа авч өөрөө хийсэн. Алгоритмын кодыг сурах бичгийн жишээг авч gemini, chatgpt ашиглан өөрийн логикт тохируулан бичиж нэмэлт засвар логикийг өөрөө сайжруулсан.

## Мэдэгдэж буй хязгаарлалт
`docs/II-4-13_BD1_Report.pdf`-ийн 9-р бүлгээс харна уу.
